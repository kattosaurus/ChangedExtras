package com.katt.changedextras.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

/**
 * Makes the Latex Panther actively seek shade when it is standing in direct
 * sunlight during daytime.
 *
 * <p>Logic overview:</p>
 * <ol>
 *   <li>The goal is only active when the panther is in the Overworld, it is
 *       daytime, and the block the panther stands on receives sky-light
 *       (i.e. it is not raining).</li>
 *   <li>Each time the goal is activated (or re-evaluated) it searches in an
 *       expanding ring around the panther for the nearest block that is
 *       {@linkplain #isShaded shaded} — meaning sky-light level ≤ 4 or the
 *       panther cannot see the sky from there.</li>
 *   <li>The panther navigates to that block at a brisk trot.  Once it arrives,
 *       or if it can no longer find shade, the goal stops and will not run
 *       again for a short cooldown.</li>
 * </ol>
 *
 * <p>Priority note: register this at a <em>lower</em> number than wandering
 * goals (higher priority) so it overrides random strolling, but at a
 * <em>higher</em> number than combat goals so the panther will fight if
 * something enters its reach even while seeking shade.</p>
 */
public class SeekShadowGoal extends Goal {

    // ── Tuning ────────────────────────────────────────────────────────────────

    /** Search radius (blocks) for a shaded spot. */
    private static final int SEARCH_RADIUS = 16;

    /** Sky-light level at or below which a block is considered "shaded". */
    private static final int SHADE_THRESHOLD = 4;

    /** Navigation speed multiplier while seeking shade. */
    private static final double SEEK_SPEED = 0.9;

    /**
     * How close the panther must get to its shade target before the goal
     * considers itself done (squared, in blocks).
     */
    private static final double ARRIVE_DIST_SQ = 2.25; // 1.5 blocks

    /** Ticks to wait before re-searching for a new shade spot. */
    private static final int SEARCH_COOLDOWN = 80;

    /** Ticks to wait after reaching shade before this goal can run again. */
    private static final int REST_COOLDOWN = 60;

    // ── State ─────────────────────────────────────────────────────────────────

    private final Mob mob;

    private BlockPos shadeTarget = null;
    private int searchCooldown   = 0;
    private int restCooldown     = 0;

    // ── Constructor ───────────────────────────────────────────────────────────

    public SeekShadowGoal(Mob mob) {
        this.mob = mob;
        // Shares movement and look flags so it co-operates with other move goals.
        setFlags(EnumSet.of(Flag.MOVE));
    }

    // ── Goal lifecycle ────────────────────────────────────────────────────────

    @Override
    public boolean canUse() {
        if (restCooldown > 0) {
            restCooldown--;
            return false;
        }
        // Only active when the panther itself is standing in sunlight.
        return isInSunlight(mob.blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if the panther is no longer in sunlight (e.g. cloud passed over,
        // it stepped under a tree, etc.) or if the target was cleared.
        if (!isInSunlight(mob.blockPosition())) return false;
        if (shadeTarget == null) return false;
        // Stop if we have arrived at the target.
        if (mob.distanceToSqr(
                shadeTarget.getX() + 0.5,
                shadeTarget.getY(),
                shadeTarget.getZ() + 0.5) <= ARRIVE_DIST_SQ) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        searchCooldown = 0;
        shadeTarget = findNearestShade();
        if (shadeTarget != null) {
            navigateTo(shadeTarget);
        }
    }

    @Override
    public void tick() {
        searchCooldown--;

        // Periodically re-evaluate the shade target in case a better one
        // becomes available or the path becomes stale.
        if (searchCooldown <= 0) {
            searchCooldown = SEARCH_COOLDOWN;
            BlockPos newTarget = findNearestShade();
            if (newTarget != null && !newTarget.equals(shadeTarget)) {
                shadeTarget = newTarget;
                navigateTo(shadeTarget);
            } else if (newTarget == null) {
                shadeTarget = null;
                mob.getNavigation().stop();
            }
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        shadeTarget = null;
        restCooldown = REST_COOLDOWN;
    }

    // ── Shade helpers ─────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when {@code pos} is in direct sunlight: the
     * dimension has a sky, it is daytime (no moon-only phase), the block can
     * see the sky, the sky-light is above the threshold, and it is not raining
     * (rain cancels sunlight burning in vanilla).
     */
    private boolean isInSunlight(BlockPos pos) {
        Level level = mob.level();
        if (!level.dimensionType().hasSkyLight()) return false;
        if (level.isRaining()) return false;

        long dayTime = level.getDayTime() % 24000L;
        // Daytime is roughly ticks 1000-13000; outside this is night/dawn/dusk.
        if (dayTime < 1000L || dayTime > 13000L) return false;

        if (!level.canSeeSky(pos.above())) return false;
        return level.getBrightness(LightLayer.SKY, pos) > SHADE_THRESHOLD;
    }

    /**
     * Returns {@code true} when {@code pos} is considered shaded: either the
     * sky cannot be seen from directly above, or the sky-light level is low
     * enough that sunlight cannot reach it.
     */
    private boolean isShaded(BlockPos pos) {
        Level level = mob.level();
        // Must be a solid-ish floor: the block itself must not be inside a wall.
        if (!level.getBlockState(pos).isAir()) return false;
        // The block above the candidate must block sky.
        if (level.canSeeSky(pos.above())) return false;
        // Low sky-light confirms shade.
        return level.getBrightness(LightLayer.SKY, pos) <= SHADE_THRESHOLD;
    }

    /**
     * Scans the area around the panther in a spiral/ring pattern and returns
     * the nearest block position that qualifies as shaded and is navigable.
     *
     * @return the closest shaded {@link BlockPos}, or {@code null} if none found
     */
    private BlockPos findNearestShade() {
        BlockPos origin = mob.blockPosition();
        Level level     = mob.level();

        BlockPos best     = null;
        double   bestDist = Double.MAX_VALUE;

        // Search in a square bounding box, checking each column.
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                // Quick reject: only check inside a circle, not a square.
                if (dx * dx + dz * dz > SEARCH_RADIUS * SEARCH_RADIUS) continue;

                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;

                // Scan a vertical range close to the mob's Y so we do not
                // send it into deep caves.
                for (int dy = -4; dy <= 8; dy++) {
                    BlockPos candidate = new BlockPos(x, origin.getY() + dy, z);

                    // Must be an air block with a solid floor below.
                    if (!level.getBlockState(candidate).isAir()) continue;
                    if (!level.getBlockState(candidate.below()).isSolid()) continue;

                    if (!isShaded(candidate)) continue;

                    double dist = candidate.distSqr(origin);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best     = candidate;
                    }
                    // Only need the best for each column — stop scanning vertically.
                    break;
                }
            }
        }

        return best;
    }

    private void navigateTo(BlockPos target) {
        Path path = mob.getNavigation().createPath(target, 1);
        if (path != null) {
            mob.getNavigation().moveTo(path, SEEK_SPEED);
        }
    }
}
