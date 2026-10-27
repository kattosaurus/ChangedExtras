package com.katt.changedextras.client.death;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.Config;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.RespawnChoicePacket;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, value = Dist.CLIENT)
public final class DeathSequenceClient {
    private static final int HUMAN_DEATH_MESSAGE_COUNT = 7;
    private static final int INFECTED_DEATH_MESSAGE_COUNT = 7;
    private static boolean allowNextVanillaDeathScreen;

    private DeathSequenceClient() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !minecraft.player.isDeadOrDying()) {
            return;
        }

        if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
            minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    @SubscribeEvent
    public static void onOpenScreen(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof DeathScreen) || allowNextVanillaDeathScreen) {
            allowNextVanillaDeathScreen = false;
            return;
        }

        if (!Config.useCustomDeathScreen) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Component causeOfDeath = minecraft.player.getCombatTracker().getDeathMessage();
        boolean hardcore = minecraft.level.getLevelData().isHardcore();
        event.setNewScreen(new DelayedDeathScreen(causeOfDeath, getRandomDeathMessage(), hardcore));
    }

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof DeathScreen deathScreen) || event.getScreen() instanceof StyledDeathScreen) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.level.getLevelData().isHardcore()) {
            return;
        }

        if (!ClientDeathRespawnTracker.shouldShowButton(mc.player)) {
            return;
        }

        TransfurVariant<?> variant = ClientDeathRespawnTracker.getActiveTransfurVariant(mc.player);
        if (variant == null) {
            return;
        }

        Component transfurName = variant.getEntityType().getDescription();
        Component transfurBtnText = Component.translatable("changedextras.death_screen.respawn_as_transfur", transfurName);

        int textWidth = mc.font.width(transfurBtnText);
        int btnWidth = Math.max(200, Math.min(deathScreen.width - 40, textWidth + 24));
        int x = deathScreen.width / 2 - btnWidth / 2;
        int y = deathScreen.height / 4 + 48;

        Button transfurBtn = Button.builder(transfurBtnText, button -> {
            ChangedExtrasNetwork.INSTANCE.sendToServer(new RespawnChoicePacket(true));
            mc.player.respawn();
            mc.setScreen(null);
        }).bounds(x, y, btnWidth, 20).build();

        event.addListener(transfurBtn);
    }

    public static void openVanillaDeathScreen(Component causeOfDeath, Component overlayMessage, boolean hardcore) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof DelayedDeathScreen)) {
            return;
        }

        allowNextVanillaDeathScreen = true;
        minecraft.setScreen(new StyledDeathScreen(causeOfDeath, overlayMessage, hardcore));
    }

    private static Component getRandomDeathMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return Component.empty();
        }

        boolean infected = ProcessTransfur.isPlayerTransfurred(minecraft.player);
        String keyPrefix = infected ? "message.changedextras.death_overlay.infected." : "message.changedextras.death_overlay.human.";
        int salt = infected ? 0x1FEC7ED : 0x0A11CE;
        int count = infected ? INFECTED_DEATH_MESSAGE_COUNT : HUMAN_DEATH_MESSAGE_COUNT;

        long seed = minecraft.level.getGameTime();
        seed ^= minecraft.player.getUUID().getMostSignificantBits();
        seed ^= minecraft.player.getUUID().getLeastSignificantBits();
        seed ^= salt;

        return Component.translatable(keyPrefix + (Math.floorMod(seed, count) + 1));
    }
}
