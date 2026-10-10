package com.katt.changedextras.effect;

import com.katt.changedextras.client.ClientEffectHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ArtistFearEffect extends MobEffect {
    public ArtistFearEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> ClientEffectHandler.startHeartbeat(entity, this));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}