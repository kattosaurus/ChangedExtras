package com.katt.changedextras.client.death;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class DelayedDeathScreen extends Screen {
    private static final int MESSAGE_FADE_TICKS = 10;
    private static final int RED_TINT_START_TICKS = 4;
    private static final int RED_TINT_FADE_TICKS = 32;
    private static final int DEATH_SCREEN_START_TICKS = 28;
    private static final int DEATH_SCREEN_FADE_TICKS = 28;
    private static final int OPEN_VANILLA_SCREEN_TICKS = 58;

    private final Component causeOfDeath;
    private final Component overlayMessage;
    private final boolean hardcore;
    private int ticks;

    public DelayedDeathScreen(Component causeOfDeath, Component overlayMessage, boolean hardcore) {
        super(Component.empty());
        this.causeOfDeath = causeOfDeath;
        this.overlayMessage = overlayMessage;
        this.hardcore = hardcore;
    }

    @Override
    public void tick() {
        ticks++;

        if (minecraft == null || minecraft.player == null) {
            return;
        }

        if (!minecraft.player.isDeadOrDying()) {
            onClose();
            return;
        }

        if (ticks >= OPEN_VANILLA_SCREEN_TICKS) {
            DeathSequenceClient.openVanillaDeathScreen(causeOfDeath, overlayMessage, hardcore);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float messageAlpha = Mth.clamp((ticks + partialTick) / MESSAGE_FADE_TICKS, 0.0F, 1.0F);
        float tintAlpha = Mth.clamp((ticks + partialTick - RED_TINT_START_TICKS) / RED_TINT_FADE_TICKS, 0.0F, 1.0F);
        float deathUiAlpha = Mth.clamp((ticks + partialTick - DEATH_SCREEN_START_TICKS) / DEATH_SCREEN_FADE_TICKS, 0.0F, 1.0F);

        int redAlpha = (int)(tintAlpha * 120.0F) << 24;
        int blackAlpha = (int)(deathUiAlpha * 170.0F) << 24;
        guiGraphics.fill(0, 0, width, height, redAlpha | 0x550000);
        guiGraphics.fill(0, 0, width, height, blackAlpha);

        int overlayMessageColor = ((int)(messageAlpha * 255.0F) << 24) | 0xFFFFFF;
        guiGraphics.drawCenteredString(
                font,
                overlayMessage,
                width / 2,
                height - 68,
                overlayMessageColor
        );

        if (deathUiAlpha <= 0.0F) {
            return;
        }

        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(width / 2.0F, height / 4.0F, 0.0F);
        pose.scale(2.0F, 2.0F, 1.0F);
        guiGraphics.drawCenteredString(
                font,
                Component.translatable("deathScreen.title"),
                0,
                0,
                ((int)(deathUiAlpha * 255.0F) << 24) | 0xFF5555
        );
        pose.popPose();

        if (causeOfDeath != null) {
            guiGraphics.drawCenteredString(
                    font,
                    causeOfDeath,
                    width / 2,
                    height / 4 + 52,
                    ((int)(deathUiAlpha * 220.0F) << 24) | 0xFFFFFF
            );
        }
    }
}
