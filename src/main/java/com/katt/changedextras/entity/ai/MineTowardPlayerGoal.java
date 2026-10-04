package com.katt.changedextras.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Makes latex creatures path toward the nearest non-sneaking player,
 * mine obstructing blocks, and attempt critical hits by jumping before
 * attacking — exactly the way a player scores a crit (falling + hit).
 *
 * Sneaking hides the nametag, so a sneaking player is invisible to this goal.
 */
@SuppressWarnings("deprecation")
public class MineTowardPlayerGoal extends Goal {

    // ── Tuning constants ──────────────────────────────────────────────────────
    private static final double DETECTION_RANGE  = 24.0;
    private static final double ATTACK_RANGE_SQ  = 4.0;   // ~2 blocks
    private static final double MINING_RANGE     = 2.5;
    private static final int    MINE_TICKS       = 40;    // ~2 s per block
    private static final int    PATH_RETRY_TICKS = 20;

    /** Ticks between normal attacks (20 = 1 hit/s). */
    private static final int    ATTACK_COOLDOWN  = 20;
    /**
     * How many ticks before landing we commit the attack.
     * The mob jumps, waits this many ticks while falling, then swings.
     * A genuine crit in Minecraft requires the entity to be falling
     * (deltaMovement.y < 0) at the moment of the hit.
     */
    private static final int    CRIT_APEX_TICKS  = 7;
    private static final int    CRIT_COOLDOWN    = 40;   // ticks between crit attempts

    // ── State ─────────────────────────────────────────────────────────────────
    private final Mob    mob;
    private final double speedMod;

    private Player   target;
    private Path     path;
    private int      pathRetryTimer  = 0;
    private int      mineTimer       = 0;
    private BlockPos mineTarget      = null;

    // Attack state
    private int  attackTimer    = 0;
    private int  critTimer      = 0;   // counts up to CRIT_COOLDOWN before next crit attempt
    private int  critApexTimer  = -1;  // -1 = not in a crit jump; >=0 = ticks since jump
    private boolean jumpedForCrit = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    public MineTowardPlayerGoal(Mob mob, double speedMod) {
        this.mob      = mob;
        this.speedMod = speedMod;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    // ── Goal lifecycle ────────────────────────────────────────────────────────

    @Override
    public boolean canUse() {
        return findTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) return false;
        if (target.isCrouching()) return false;
        if (mob.distanceToSqr(target) > DETECTION_RANGE * DETECTION_RANGE * 4) return false;
        return true;
    }

    @Override
    public void start() {
        target = findTarget();
        attackTimer   = 0;
        critTimer     = CRIT_COOLDOWN / 2; // slight delay before first crit attempt
        critApexTimer = -1;
        jumpedForCrit = false;
        tryNavigate();
    }

    @Override
    public void stop() {
        target        = null;
        path          = null;
        mineTarget    = null;
        mineTimer     = 0;
        critApexTimer = -1;
        jumpedForCrit = false;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distSq = mob.distanceToSqr(target);

        // ── Mining ────────────────────────────────────────────────────────────
        if (mineTarget != null) {
            tickMining();
            return;
        }

        // ── Navigation ────────────────────────────────────────────────────────
        pathRetryTimer--;
        if (pathRetryTimer <= 0) {
            pathRetryTimer = PATH_RETRY_TICKS;
            tryNavigate();
        }

        if (mob.getNavigation().isDone() && distSq > ATTACK_RANGE_SQ * 4) {
            BlockPos obstacle = findObstacleBlock();
            if (obstacle != null) {
                mineTarget = obstacle;
                mineTimer  = 0;
                return;
            }
        }

        // ── Combat ────────────────────────────────────────────────────────────
        if (distSq <= ATTACK_RANGE_SQ) {
            mob.getNavigation().stop();
            tickCombat();
        }

        attackTimer--;
        critTimer++;
    }

    // ── Mining tick ───────────────────────────────────────────────────────────

    private void tickMining() {
        BlockState state = mob.level().getBlockState(mineTarget);
        if (state.isAir() || !state.isSolid()) {
            mineTarget = null;
            mineTimer  = 0;
            tryNavigate();
            return;
        }

        mineTimer++;
        if (mineTimer % 5 == 0 && mob.level() instanceof ServerLevel sv) {
            sv.destroyBlockProgress(mob.getId(), mineTarget, (int)((float) mineTimer / MINE_TICKS * 9));
        }

        if (mineTimer >= MINE_TICKS) {
            mob.level().destroyBlock(mineTarget, true, mob);
            mineTarget = null;
            mineTimer  = 0;
            tryNavigate();
        }
    }

    // ── Combat tick ──────────────────────────────────────────────────────────

    /**
     * Combat logic — called every tick when within attack range.
     *
     * Critical-hit strategy (mirrors vanilla player crit):
     *   1. Entity must be on the ground and not in water.
     *   2. We jump, giving the entity an upward velocity.
     *   3. We count CRIT_APEX_TICKS ticks (entity rises then starts falling).
     *   4. On that tick deltaMovement.y should be negative → we attack.
     *      The DamageSource and the falling flag together tell the game
     *      this is a crit, applying 1.5× damage.
     */
    private void tickCombat() {
        // ── In-progress crit jump ─────────────────────────────────────────────
        if (jumpedForCrit) {
            critApexTimer++;

            if (critApexTimer >= CRIT_APEX_TICKS) {
                // Attack while falling for the critical hit
                Vec3 motion = mob.getDeltaMovement();
                // Force a downward component so isFalling() returns true
                if (motion.y >= 0) {
                    mob.setDeltaMovement(motion.x, -0.1, motion.z);
                }
                performAttack(true);
                jumpedForCrit = false;
                critApexTimer = -1;
                critTimer     = 0;
                attackTimer   = ATTACK_COOLDOWN;
            }
            return;
        }

        // ── Attempt a crit if cooldown is ready ───────────────────────────────
        if (critTimer >= CRIT_COOLDOWN && mob.onGround() && !mob.isInWater() && !mob.isInLava()) {
            // Jump to set up the crit
            mob.setDeltaMovement(mob.getDeltaMovement().x, 0.42, mob.getDeltaMovement().z);
            mob.hasImpulse = true;
            jumpedForCrit  = true;
            critApexTimer  = 0;
            // Play a little whoosh so it feels telegraphed
            mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.5F, 1.2F);
            return;
        }

        // ── Normal attack ─────────────────────────────────────────────────────
        if (attackTimer <= 0) {
            performAttack(false);
            attackTimer = ATTACK_COOLDOWN;
        }
    }

    /**
     * Deals damage to the target.
     *
     * For a critical hit we replicate what vanilla does:
     *   • The entity must be falling (deltaMovement.y < 0) — enforced above.
     *   • We call doHurtTarget which internally checks isFallFlying / sprinting /
     *     falling and applies the crit multiplier (1.5×) automatically.
     *   • We also spawn the crit particles manually so the visual is clear.
     */
    private void performAttack(boolean isCrit) {
        if (target == null) return;

        // Swing arm for visual feedback
        mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);

        // doHurtTarget handles the actual damage; vanilla crit logic lives inside
        // LivingEntity.attack() which doHurtTarget delegates to.
        mob.doHurtTarget(target);

        if (isCrit && mob.level() instanceof ServerLevel sv) {
            // Spawn crit particles on the server — the client will display them
            sv.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.CRIT,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5,
                    target.getZ(),
                    8,   // count
                    0.3, 0.3, 0.3,  // spread
                    0.1  // speed
            );
        }
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    private Player findTarget() {
        Player p = mob.level().getNearestPlayer(mob, DETECTION_RANGE);
        return (p != null && !p.isCrouching()) ? p : null;
    }

    private void tryNavigate() {
        if (target == null) return;
        path = mob.getNavigation().createPath(target, 0);
        if (path != null) mob.getNavigation().moveTo(path, speedMod);
    }

    private BlockPos findObstacleBlock() {
        if (target == null) return null;

        double dx = target.getX() - mob.getX();
        double dy = (target.getY() + target.getEyeHeight()) - (mob.getY() + mob.getEyeHeight());
        double dz = target.getZ() - mob.getZ();
        double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len < 0.001) return null;
        dx /= len; dy /= len; dz /= len;

        for (double d = 1.0; d <= MINING_RANGE + 1; d += 0.5) {
            BlockPos bp = BlockPos.containing(
                    mob.getX() + dx * d,
                    mob.getY() + mob.getEyeHeight() + dy * d,
                    mob.getZ() + dz * d);
            BlockState bs = mob.level().getBlockState(bp);
            if (bs.isSolid() && !bs.isAir()) {
                if (mob.blockPosition().distSqr(bp) <= MINING_RANGE * MINING_RANGE) return bp;
            }
        }
        return null;
    }
}
