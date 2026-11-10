package com.katt.changedextras.network;

import com.katt.changedextras.client.tracker.ClientJackpotTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ClientParryHandler {
    public static void handleProgress(boolean triggerReadyScreenShake) {
        if (!triggerReadyScreenShake) return;

        ClientJackpotTracker.triggerScreenShake(35, 1.8f);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
        }
    }
}