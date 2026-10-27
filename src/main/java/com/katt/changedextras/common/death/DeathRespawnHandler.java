package com.katt.changedextras.common.death;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.Config;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.SyncDeathRespawnPacket;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DeathRespawnHandler {
    private static final String RESPAWN_AS_TRANSFUR_TAG = "changedextras.respawn_as_transfur";
    private static final String DEATH_VARIANT_TAG = "changedextras.death_variant";
    private static final String DO_RESPAWN_HUMAN_TAG = "changedextras.do_respawn_human";
    private static final String DO_RESPAWN_TRANSFUR_TAG = "changedextras.do_respawn_transfur";

    private DeathRespawnHandler() {
    }

    public static void setRespawnChoice(ServerPlayer player, boolean asTransfur) {
        player.getPersistentData().putBoolean(RESPAWN_AS_TRANSFUR_TAG, asTransfur);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean configEnabled = Config.respawnAsTransfurInsteadOfKeepForm;
        boolean keepForm = player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM);
        ChangedExtrasNetwork.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncDeathRespawnPacket(configEnabled, keepForm, null)
        );
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
        ResourceLocation variantId = variantInstance != null ? variantInstance.getParent().getFormId() : null;

        if (variantId != null) {
            player.getPersistentData().putString(DEATH_VARIANT_TAG, variantId.toString());
        } else {
            player.getPersistentData().remove(DEATH_VARIANT_TAG);
        }
        player.getPersistentData().remove(RESPAWN_AS_TRANSFUR_TAG);

        boolean configEnabled = Config.respawnAsTransfurInsteadOfKeepForm;
        boolean keepForm = player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM);
        ChangedExtrasNetwork.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncDeathRespawnPacket(configEnabled, keepForm, variantId)
        );
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer) || !(event.getEntity() instanceof ServerPlayer newPlayer)) {
            return;
        }

        boolean configEnabled = Config.respawnAsTransfurInsteadOfKeepForm;
        boolean keepForm = oldPlayer.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM);
        boolean hadVariant = oldPlayer.getPersistentData().contains(DEATH_VARIANT_TAG);

        if (configEnabled && keepForm && hadVariant) {
            boolean choseTransfur = oldPlayer.getPersistentData().getBoolean(RESPAWN_AS_TRANSFUR_TAG);
            if (choseTransfur) {
                newPlayer.getPersistentData().putBoolean(DO_RESPAWN_TRANSFUR_TAG, true);
                newPlayer.getPersistentData().putString(DEATH_VARIANT_TAG, oldPlayer.getPersistentData().getString(DEATH_VARIANT_TAG));
            } else {
                newPlayer.getPersistentData().putBoolean(DO_RESPAWN_HUMAN_TAG, true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.getPersistentData().getBoolean(DO_RESPAWN_HUMAN_TAG)) {
            player.getPersistentData().remove(DO_RESPAWN_HUMAN_TAG);
            player.getPersistentData().remove(DEATH_VARIANT_TAG);
            player.getPersistentData().remove(RESPAWN_AS_TRANSFUR_TAG);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0F);
        } else if (player.getPersistentData().getBoolean(DO_RESPAWN_TRANSFUR_TAG)) {
            player.getPersistentData().remove(DO_RESPAWN_TRANSFUR_TAG);
            player.getPersistentData().remove(RESPAWN_AS_TRANSFUR_TAG);
            String variantIdStr = player.getPersistentData().getString(DEATH_VARIANT_TAG);
            player.getPersistentData().remove(DEATH_VARIANT_TAG);

            if (!ProcessTransfur.isPlayerTransfurred(player) && variantIdStr != null && !variantIdStr.isEmpty()) {
                ResourceLocation variantId = ResourceLocation.tryParse(variantIdStr);
                if (variantId != null && ChangedRegistry.TRANSFUR_VARIANT.get() != null) {
                    TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(variantId);
                    if (variant != null) {
                        ProcessTransfur.setPlayerTransfurVariant(player, variant);
                    }
                }
            }
        }
    }
}
