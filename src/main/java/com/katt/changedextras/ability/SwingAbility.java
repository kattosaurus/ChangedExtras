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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SwingAbility extends SimpleAbility {
    private static final double RADIUS = 4.0D;

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.swing.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.swing.description"));
    }

    @Override
    public AbstractAbility.UseType getUseType(IAbstractChangedEntity entity) {
        return AbstractAbility.UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 80;
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
        AABB area = user.getBoundingBox().inflate(RADIUS);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, target -> target.isAlive() && target != user)) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SQUID_INK, user.getX(), user.getY(0.7D), user.getZ(), 24, 1.4D, 0.5D, 1.4D, 0.02D);
        }

        level.playSound(null, user.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, user.getSoundSource(), 1.0F, 0.8F);
    }
}
