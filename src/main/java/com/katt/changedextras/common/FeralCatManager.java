package com.katt.changedextras.common;

import com.katt.changedextras.network.FeralCatStatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class FeralCatManager {
    public static final String FERAL_TAG = "changedextras:feral_cat";

    public static boolean isFeral(Player player) {
        if (player == null) return false;
        return player.getPersistentData().getBoolean(FERAL_TAG);
    }

    public static void setFeral(Player player, boolean feral) {
        if (player == null) return;
        player.getPersistentData().putBoolean(FERAL_TAG, feral);
        player.refreshDimensions();

        if (player.level() instanceof ServerLevel serverLevel) {
            FeralCatStatePacket.broadcast(serverLevel, player.getUUID(), feral);
        }
    }
}
