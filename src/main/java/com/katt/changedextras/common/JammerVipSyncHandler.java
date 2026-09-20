package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JammerVipSyncHandler {
    private JammerVipSyncHandler() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer trackedPlayer && event.getEntity() instanceof ServerPlayer trackingPlayer) {
            JammerVipManager.syncTo(trackedPlayer, trackingPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JammerVipManager.syncTo(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JammerVipManager.syncTo(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JammerVipManager.syncTo(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTransfur(ProcessTransfur.EntityVariantAssigned event) {
        if (event.livingEntity instanceof ServerPlayer player && event.variant != null) {
            if (event.variant == ModTransfurVariants.JAMMER.get() || JammerVipManager.isServerVip(player)) {
                JammerVipManager.setVip(player, JammerVipManager.isServerVip(player));
            }
        }
    }
}
