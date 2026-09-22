package com.katt.changedextras.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class HypoxemiaEffect extends MobEffect {
    public HypoxemiaEffect() {
        super(MobEffectCategory.HARMFUL, 0x2C3E50);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide) {
            // Apply Weakness II (amplifier 1) and Slowness II (amplifier 1)
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, true));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false, true));

            // Poison 0.5: Deals 1 damage every 50 ticks (50% speed of standard Poison I which is every 25 ticks)
            // Does not drop health below 1.0F, matching vanilla poison behaviour
            if (livingEntity.tickCount % 50 == 0) {
                if (livingEntity.getHealth() > 1.0F) {
                    livingEntity.hurt(livingEntity.damageSources().magic(), 1.0F);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
