package com.katt.changedextras.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class ClientEffectHandler {
    private static HeartbeatLoopSound current;

    public static void startHeartbeat(LivingEntity entity, MobEffect effect) {
        Minecraft mc = Minecraft.getInstance();
        if (entity != mc.player) return;

        if (current == null || current.isStopped()) {
            current = new HeartbeatLoopSound(entity, effect);
            mc.getSoundManager().play(current);
        }
    }
}