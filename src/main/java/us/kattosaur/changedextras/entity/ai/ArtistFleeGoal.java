package us.kattosaur.changedextras.entity.ai;

import us.kattosaur.changedextras.entity.beasts.ArtistEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

/**
 * End-state goal for the Experiment 012 boss fight: the beast will not fight any
 * more. It runs away from nearby players and pathfinds to a covered hiding spot.
 */
public class ArtistFleeGoal extends Goal {
    /** How far the beast looks for a hiding spot. */
    private static final int SEARCH_RADIUS = 24;
    /** Players inside this radius are actively avoided. */
    private static final double AVOID_RADIUS = 16.0D;
    /** Recalculate the escape route this often. */
    private static final int REPLAN_TICKS = 20;
    /** Navigation speed while fleeing (multiplied by the entity's speed attribute). */
    private static final double FLEE_NAV_SPEED = 1.5D;
    /** Distance from a hiding spot at which the goal considers itself done. */
    private static final double ARRIVE_DIST_SQR = 2.25D;

    private final ArtistEntity artist;
    private BlockPos hideTarget;
    private int replanCooldown;

    public ArtistFleeGoal(ArtistEntity artist) {
        this.artist = artist;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return artist.getFightPhase() == ArtistEntity.FightPhase.FLEEING && !artist.isFleePaused();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        replanCooldown = 0;
        hideTarget = findHidingSpot();
        navigate();
    }

    @Override
    public void stop() {
        artist.getNavigation().stop();
        hideTarget = null;
    }

    @Override
    public void tick() {
        if (--replanCooldown > 0) {
            return;
        }
        replanCooldown = REPLAN_TICKS;
        hideTarget = findHidingSpot();
        navigate();
    }

    private void navigate() {
        if (hideTarget == null) {
            return;
        }
        Path path = artist.getNavigation().createPath(hideTarget, 1);
        if (path != null) {
            artist.getNavigation().moveTo(path, FLEE_NAV_SPEED);
        }
    }

    /**
     * Picks a reachable air block with a solid floor, as far from the nearest
     * player as possible, strongly preferring spots with a roof over them.
     */
    private BlockPos findHidingSpot() {
        Level level = artist.level();
        BlockPos origin = artist.blockPosition();
        ServerPlayer nearest = nearestPlayer();
        if (nearest == null) {
            return null;
        }

        BlockPos best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                if (dx * dx + dz * dz > SEARCH_RADIUS * SEARCH_RADIUS) {
                    continue;
                }
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                for (int dy = -3; dy <= 6; dy++) {
                    BlockPos candidate = new BlockPos(x, origin.getY() + dy, z);
                    if (!level.getBlockState(candidate).isAir()) {
                        continue;
                    }
                    if (!level.getBlockState(candidate.below()).isSolid()) {
                        continue;
                    }
                    if (candidate.distSqr(origin) < ARRIVE_DIST_SQR) {
                        continue;
                    }
                    double distanceToPlayer = Math.sqrt(candidate.distSqr(nearest.blockPosition()));
                    double score = distanceToPlayer * 2.0D;
                    if (!level.canSeeSky(candidate.above())) {
                        score += 30.0D; // cover to hide behind
                    }
                    score += artist.getRandom().nextFloat() * 2.0D;
                    if (score > bestScore) {
                        bestScore = score;
                        best = candidate;
                    }
                    break; // one candidate per column is enough
                }
            }
        }
        return best;
    }

    private ServerPlayer nearestPlayer() {
        ServerPlayer nearest = null;
        double bestDistance = AVOID_RADIUS * AVOID_RADIUS;
        for (ServerPlayer player : artist.level().getEntitiesOfClass(ServerPlayer.class,
                artist.getBoundingBox().inflate(AVOID_RADIUS))) {
            if (player.isSpectator() || player.isCreative()) {
                continue;
            }
            double distance = artist.distanceToSqr(player);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = player;
            }
        }
        return nearest;
    }
}
