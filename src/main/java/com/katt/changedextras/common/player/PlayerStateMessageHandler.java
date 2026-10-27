package com.katt.changedextras.common.player;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class PlayerStateMessageHandler {
    private static final int TRANSFUR_MESSAGE_COUNT = 7;
    private static final int HUMAN_DEATH_MESSAGE_COUNT = 7;
    private static final int INFECTED_DEATH_MESSAGE_COUNT = 7;
    private static final Style BOLD_STYLE = Style.EMPTY.withBold(true);

    private PlayerStateMessageHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTransfur(ProcessTransfur.EntityVariantAssigned event) {
        if (!(event.livingEntity instanceof ServerPlayer player) || event.variant == null || event.isRedundant()) {
            return;
        }

        player.displayClientMessage(getRandomTransfurMessage(player), true);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
    }

    public static Component getRandomTransfurMessage(ServerPlayer player) {
        return Component.translatable(
                "message.changedextras.transfur_overlay." + selectMessageIndex(player, 0x51A7F0, TRANSFUR_MESSAGE_COUNT)
        );
    }

    public static Component getRandomDeathMessage(ServerPlayer player) {
        boolean infected = ProcessTransfur.isPlayerTransfurred(player);
        String keyPrefix = infected ? "message.changedextras.death_overlay.infected." : "message.changedextras.death_overlay.human.";
        int salt = infected ? 0x1FEC7ED : 0x0A11CE;
        int messageCount = infected ? INFECTED_DEATH_MESSAGE_COUNT : HUMAN_DEATH_MESSAGE_COUNT;
        return Component.translatable(keyPrefix + selectMessageIndex(player, salt, messageCount)).setStyle(BOLD_STYLE);
    }

    private static int selectMessageIndex(ServerPlayer player, int salt, int count) {
        long seed = player.level().getGameTime();
        seed ^= player.getUUID().getMostSignificantBits();
        seed ^= player.getUUID().getLeastSignificantBits();
        seed ^= salt;
        return Math.floorMod(seed, count) + 1;
    }
}
