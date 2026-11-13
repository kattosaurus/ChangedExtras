package com.katt.changedextras.ability;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PaintBallAbility extends SimpleAbility {
    private static final double RANGE = 20.0D;

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.paint_ball.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.paint_ball.description"));
    }

    @Override
    public AbstractAbility.UseType getUseType(IAbstractChangedEntity entity) {
        return AbstractAbility.UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 100;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getEntity() instanceof LivingEntity;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        Level level = entity.getLevel();
        if (level.isClientSide()) {
            return;
        }

        LivingEntity user = (LivingEntity) entity.getEntity();
        Vec3 start = user.getEyePosition();
        Vec3 end = start.add(user.getLookAngle().scale(RANGE));
        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        Vec3 targetEnd = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                user,
                start,
                targetEnd,
                new AABB(start, targetEnd).inflate(1.0D),
                target -> target instanceof LivingEntity living && living.isAlive() && target != user,
                start.distanceToSqr(targetEnd)
        );

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            target.hurt(user.damageSources().mobAttack(user), 4.0F);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
            targetEnd = entityHit.getLocation();
        }

        if (level instanceof ServerLevel serverLevel) {
            spawnTrail(serverLevel, start, targetEnd);
        }

        level.playSound(null, user.blockPosition(), SoundEvents.SNOWBALL_THROW, user.getSoundSource(), 0.8F, 0.9F + user.getRandom().nextFloat() * 0.2F);
    }

    private static void spawnTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        int particles = Math.max(6, (int) (delta.length() * 3.0D));
        for (int i = 0; i <= particles; i++) {
            double progress = i / (double) particles;
            Vec3 point = start.add(delta.scale(progress));
            level.sendParticles(ParticleTypes.ITEM_SLIME, point.x, point.y, point.z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }
}
