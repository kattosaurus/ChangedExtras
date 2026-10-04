package com.katt.changedextras.events;

import com.katt.changedextras.common.FeralCatManager;
import com.katt.changedextras.network.FeralCatStatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "changedextras")
@SuppressWarnings("removal")
public class FeralCatEvents {

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player && FeralCatManager.isFeral(player)) {
            event.setNewSize(EntityDimensions.scalable(0.6F, 0.7F), false);
            event.setNewEyeHeight(0.45F);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && FeralCatManager.isFeral(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && FeralCatManager.isFeral(player)) {
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CAT_HURT,
                    SoundSource.PLAYERS,
                    1.0F, 1.0F
            );
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player targetPlayer && FeralCatManager.isFeral(targetPlayer)) {
            if (event.getEntity() instanceof ServerPlayer tracker && targetPlayer.level() instanceof ServerLevel) {
                com.katt.changedextras.network.ChangedExtrasNetwork.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> tracker),
                        new FeralCatStatePacket(targetPlayer.getUUID(), true)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel serverLevel) {
            boolean feral = FeralCatManager.isFeral(player);
            if (feral) {
                FeralCatStatePacket.broadcast(serverLevel, player.getUUID(), true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player != null) {
            FeralCatManager.setFeral(player, false);
        }
    }
}
