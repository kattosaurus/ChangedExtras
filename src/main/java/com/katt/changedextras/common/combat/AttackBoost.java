package com.katt.changedextras.common.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Applies the forward lunge that goes with the {@code Attack2} animation.
 *
 * <p>The boost runs on the server for both players and variants so the movement
 * is authoritative. Callers are expected to have already decided that an
 * {@code Attack2} is playing; this class only performs the push.</p>
 */
public final class AttackBoost {
    public static final double ATTACK2_FORWARD_BOOST = 0.65D;

    private AttackBoost() {
    }

    public static void applyForwardBoost(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }

        Vec3 look = entity.getLookAngle();
        double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
        if (horizontal < 1.0E-4D) {
            return;
        }

        entity.push(look.x / horizontal * ATTACK2_FORWARD_BOOST, 0.0D,
                look.z / horizontal * ATTACK2_FORWARD_BOOST);
        entity.hurtMarked = true;
    }
}
