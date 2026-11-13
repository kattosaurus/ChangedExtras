package com.katt.changedextras.ability;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PunctureAbility extends SimpleAbility {
    private static final double DASH_DISTANCE = 5.0D;

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.puncture.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.puncture.description"));
    }

    @Override
    public AbstractAbility.UseType getUseType(IAbstractChangedEntity entity) {
        return AbstractAbility.UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 120;
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
        Vec3 start = user.position();
        Vec3 end = start.add(user.getLookAngle().scale(DASH_DISTANCE));
        BlockHitResult hit = level.clip(new ClipContext(start.add(0.0D, user.getBbHeight() * 0.5D, 0.0D), end.add(0.0D, user.getBbHeight() * 0.5D, 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        Vec3 dashEnd = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation().subtract(user.getLookAngle().scale(0.75D));

        damageAlongPath(user, start, dashEnd);
        moveUser(user, dashEnd);

        if (level instanceof ServerLevel serverLevel) {
            Vec3 delta = dashEnd.subtract(start);
            serverLevel.sendParticles(ParticleTypes.CRIT, start.x, start.y + 0.8D, start.z, 10, 0.15D, 0.15D, 0.15D, 0.05D);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, start.x + delta.x * 0.5D, start.y + 0.8D + delta.y * 0.5D, start.z + delta.z * 0.5D, 2, 0.1D, 0.1D, 0.1D, 0.0D);
        }

        level.playSound(null, user.blockPosition(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, user.getSoundSource(), 1.0F, 0.9F);
    }

    private static void damageAlongPath(LivingEntity user, Vec3 start, Vec3 end) {
        AABB pathBox = user.getBoundingBox().expandTowards(end.subtract(start)).inflate(0.75D);
        for (LivingEntity target : user.level().getEntitiesOfClass(LivingEntity.class, pathBox, target -> target.isAlive() && target != user)) {
            target.hurt(user.damageSources().mobAttack(user), 6.0F);
        }
    }

    private static void moveUser(LivingEntity user, Vec3 end) {
        if (user instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(end.x, end.y, end.z, user.getYRot(), user.getXRot());
        } else {
            user.teleportTo(end.x, end.y, end.z);
        }
        user.hasImpulse = true;
        user.setDeltaMovement(user.getLookAngle().scale(0.25D));
    }
}
