package com.katt.changedextras.client;

import com.katt.changedextras.init.ChangedExtrasSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeartbeatLoopSound extends AbstractTickableSoundInstance {
    private final LivingEntity entity;
    private final MobEffect effect;

    public HeartbeatLoopSound(LivingEntity entity, MobEffect effect) {
        super(ChangedExtrasSounds.HEARTBEAT.get(), SoundSource.MASTER, RandomSource.create());
        this.entity = entity;
        this.effect = effect;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.relative = true;
        this.attenuation = Attenuation.NONE;
    }

    @Override
    public void tick() {
        if (!entity.isAlive() || !entity.hasEffect(effect)) {
            this.stop();
        }
    }
}