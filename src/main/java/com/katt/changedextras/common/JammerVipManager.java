package com.katt.changedextras.common;

import com.katt.changedextras.entity.beasts.JammerEntity;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.SyncJammerVipPacket;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class JammerVipManager {
    private static final Set<UUID> CLIENT_VIP_PLAYERS = ConcurrentHashMap.newKeySet();

    private JammerVipManager() {
    }

    public static boolean isClientVip(UUID uuid) {
        return uuid != null && CLIENT_VIP_PLAYERS.contains(uuid);
    }

    public static void setClientVip(UUID uuid, boolean vip) {
        if (uuid == null) return;
        if (vip) {
            CLIENT_VIP_PLAYERS.add(uuid);
        } else {
            CLIENT_VIP_PLAYERS.remove(uuid);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Player player = mc.level.getPlayerByUUID(uuid);
            if (player != null) {
                TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
                if (variant != null && variant.getChangedEntity() instanceof JammerEntity jammer) {
                    jammer.setVip(vip);
                }
            }
        }
    }

    public static boolean isServerVip(Player player) {
        if (player == null) return false;
        return player.getPersistentData().getBoolean(JammerEntity.VIP_TAG);
    }

    public static void setVip(ServerPlayer player, boolean vip) {
        if (player == null) return;
        player.getPersistentData().putBoolean(JammerEntity.VIP_TAG, vip);

        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null && variant.getChangedEntity() instanceof JammerEntity jammer) {
            jammer.setVip(vip);
        }

        SyncJammerVipPacket packet = new SyncJammerVipPacket(player.getUUID(), vip);
        ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
    }

    public static void syncTo(ServerPlayer player, ServerPlayer recipient) {
        if (player == null || recipient == null) return;
        boolean vip = isServerVip(player);
        SyncJammerVipPacket packet = new SyncJammerVipPacket(player.getUUID(), vip);
        ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> recipient), packet);
    }
}
