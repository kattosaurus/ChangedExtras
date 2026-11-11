package com.katt.changedextras.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * A target-selector goal for the Latex Panther that prevents it from seeing
 * or pursuing players who are standing in direct sunlight.
 *
 * <p>Extends {@link NearestAttackableTargetGoal} and overrides
 * {@link #canUse()} and {@link #canContinueToUse()} to inject the extra
 * sunlight check.  The check is also wired into the goal's targeting predicate
 * so a sunlit player is never picked up in the first place.</p>
 *
 * <p>A player is considered "in sunlight" when all of the following hold:</p>
 * <ul>
 *   <li>The level has a sky (Overworld-like dimension).</li>
 *   <li>It is not raining (rain blocks sunburn).</li>
 *   <li>The day-time is within the daytime window (ticks 1000–13000).</li>
 *   <li>The block above the player's eye position can see the sky.</li>
 *   <li>The sky-light level at that position exceeds the shade threshold.</li>
 * </ul>
 */
public class AvoidSunlitPlayerGoal extends NearestAttackableTargetGoal<Player> {

    /** Sky-light level above which a position is considered "in sunlight". */
    private static final int SHADE_THRESHOLD = 4;

    /**
     * @param mob   the Latex Panther entity
     * @param range detection range in blocks (passed to the parent goal)
     */
    public AvoidSunlitPlayerGoal(Mob mob, double range) {
        super(mob, Player.class, 10, true, false,
                buildPredicate(mob, range));
    }

    /**
     * Builds a targeting predicate that rejects players who are in sunlight or
     * are crouching (sneaking hides them from the panther).
     */
    private static Predicate<LivingEntity> buildPredicate(Mob mob, double range) {
        double rangeSq = range * range;
        return living -> {
            if (!(living instanceof Player player)) return false;
            // Crouching players are invisible to the panther.
            if (player.isCrouching()) return false;
            // Range check.
            if (mob.distanceToSqr(player) > rangeSq) return false;
            // Reject sunlit players.
            return !isPlayerInSunlight(player);
        };
    }

    // ── Goal lifecycle ────────────────────────────────────────────────────────

    @Override
    public boolean canUse() {
        // Re-check the current target before the parent's canUse logic runs.
        if (this.mob.getTarget() != null && isPlayerInSunlight(this.mob.getTarget())) {
            this.mob.setTarget(null);
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        // Drop the target immediately if the player steps into sunlight.
        if (this.mob.getTarget() instanceof Player player && isPlayerInSunlight(player)) {
            this.mob.setTarget(null);
            return false;
        }
        return super.canContinueToUse();
    }

    // ── Sunlight detection ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code entity} is standing in direct sunlight.
     * Accepts any {@link LivingEntity} so it can be called generically; only
     * meaningful for players.
     */
    public static boolean isPlayerInSunlight(@Nullable LivingEntity entity) {
        if (entity == null) return false;

        Level level = entity.level();

        // Dimension must have a sky.
        if (!level.dimensionType().hasSkyLight()) return false;

        // Rain cancels sunlight.
        if (level.isRaining()) return false;

        // Daytime only (ticks 1000–13000 within the 24 000-tick cycle).
        long dayTime = level.getDayTime() % 24000L;
        if (dayTime < 1000L || dayTime > 13000L) return false;

        // Check sky visibility from just above the entity's eye level.
        BlockPos eyePos = BlockPos.containing(
                entity.getX(),
                entity.getEyeY(),
                entity.getZ());

        if (!level.canSeeSky(eyePos)) return false;

        return level.getBrightness(LightLayer.SKY, eyePos) > SHADE_THRESHOLD;
    }
}
