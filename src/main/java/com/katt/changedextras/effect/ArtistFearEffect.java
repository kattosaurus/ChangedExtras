package com.katt.changedextras.effect;

import com.katt.changedextras.init.ChangedExtrasSounds;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ArtistFearEffect extends MobEffect {
    public ArtistFearEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);
        if (entity instanceof ServerPlayer player) {
            player.playNotifySound(ChangedExtrasSounds.HEARTBEAT.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundStopSoundPacket(
                    ChangedExtrasSounds.HEARTBEAT.getId(), SoundSource.MASTER));
        }
    }
}