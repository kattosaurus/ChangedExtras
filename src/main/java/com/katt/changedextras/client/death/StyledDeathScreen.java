package com.katt.changedextras.client.death;

import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.RespawnChoicePacket;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class StyledDeathScreen extends Screen {
    private static final int BUTTON_FADE_START_TICKS = 8;
    private static final int BUTTON_FADE_DURATION_TICKS = 16;
    private static final int FINAL_RED_TINT = 0x78550000;
    private static final int FINAL_BLACK_TINT = 0xAA000000;

    private final Component causeOfDeath;
    private final Component overlayMessage;
    private final boolean hardcore;
    private int ticks;
    private boolean showTransfurButton;

    public StyledDeathScreen(Component causeOfDeath, Component overlayMessage, boolean hardcore) {
        super(Component.empty());
        this.causeOfDeath = causeOfDeath;
        this.overlayMessage = overlayMessage;
        this.hardcore = hardcore;
    }

    @Override
    protected void init() {
        clearWidgets();

        int centerX = width / 2;
        int buttonHeight = 22;

        showTransfurButton = !hardcore && ClientDeathRespawnTracker.shouldShowButton(minecraft != null ? minecraft.player : null);

        if (showTransfurButton) {
            TransfurVariant<?> variant = ClientDeathRespawnTracker.getActiveTransfurVariant(minecraft != null ? minecraft.player : null);
            Component transfurName = variant != null ? variant.getEntityType().getDescription() : Component.empty();
            Component transfurBtnText = Component.translatable("changedextras.death_screen.respawn_as_transfur", transfurName);

            int textWidth = font.width(transfurBtnText);
            int buttonWidth = Math.max(170, Math.min(width - 40, textWidth + 24));
            int buttonGap = 26;
            int transfurY = height - 104;
            int respawnY = transfurY + buttonGap;
            int exitY = respawnY + buttonGap;

            FlatButton transfurButton = addRenderableWidget(new FlatButton(
                    centerX - buttonWidth / 2,
                    transfurY,
                    buttonWidth,
                    buttonHeight,
                    transfurBtnText,
                    button -> handleRespawnAsTransfur()
            ));

            FlatButton primaryButton = addRenderableWidget(new FlatButton(
                    centerX - buttonWidth / 2,
                    respawnY,
                    buttonWidth,
                    buttonHeight,
                    Component.translatable("deathScreen.respawn"),
                    button -> handleRespawnAsHuman()
            ));

            FlatButton exitButton = addRenderableWidget(new FlatButton(
                    centerX - buttonWidth / 2,
                    exitY,
                    buttonWidth,
                    buttonHeight,
                    Component.translatable("deathScreen.titleScreen"),
                    button -> exitToTitleScreen()
            ));

            exitButton.setTooltip(Tooltip.create(Component.translatable("deathScreen.quit.confirm")));

            transfurButton.active = false;
            transfurButton.visible = false;
            primaryButton.active = false;
            primaryButton.visible = false;
            exitButton.active = false;
            exitButton.visible = false;
        } else {
            int buttonWidth = 170;
            int firstY = height - 88;

            FlatButton primaryButton = addRenderableWidget(new FlatButton(
                    centerX - buttonWidth / 2,
                    firstY,
                    buttonWidth,
                    buttonHeight,
                    hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn"),
                    button -> handleRespawn()
            ));

            FlatButton exitButton = addRenderableWidget(new FlatButton(
                    centerX - buttonWidth / 2,
                    firstY + 28,
                    buttonWidth,
                    buttonHeight,
                    Component.translatable("deathScreen.titleScreen"),
                    button -> exitToTitleScreen()
            ));

            if (!hardcore) {
                exitButton.setTooltip(Tooltip.create(Component.translatable("deathScreen.quit.confirm")));
            }

            primaryButton.active = false;
            primaryButton.visible = false;
            exitButton.active = false;
            exitButton.visible = false;
        }
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

        float alpha = getButtonAlpha(0.0F);
        boolean buttonsReady = alpha > 0.0F;
        for (var renderable : renderables) {
            if (renderable instanceof FlatButton button) {
                button.visible = buttonsReady;
                button.active = buttonsReady;
                button.setAlpha(alpha);
            }
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
        guiGraphics.fill(0, 0, width, height, FINAL_RED_TINT);
        guiGraphics.fill(0, 0, width, height, FINAL_BLACK_TINT);

        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(width / 2.0F, height / 4.0F, 0.0F);
        pose.scale(2.0F, 2.0F, 1.0F);
        guiGraphics.drawCenteredString(
                font,
                Component.translatable("deathScreen.title"),
                0,
                0,
                0xFF5555
        );
        pose.popPose();

        if (causeOfDeath != null) {
            guiGraphics.drawCenteredString(font, causeOfDeath, width / 2, height / 4 + 52, 0xFFFFFF);
        }

        int messageY = showTransfurButton ? height - 124 : height - 124;
        guiGraphics.drawCenteredString(font, overlayMessage, width / 2, messageY, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private float getButtonAlpha(float partialTick) {
        return Mth.clamp((ticks + partialTick - BUTTON_FADE_START_TICKS) / BUTTON_FADE_DURATION_TICKS, 0.0F, 1.0F);
    }

    private void handleRespawnAsTransfur() {
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        ChangedExtrasNetwork.INSTANCE.sendToServer(new RespawnChoicePacket(true));
        minecraft.player.respawn();
        minecraft.setScreen(null);
    }

    private void handleRespawnAsHuman() {
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        ChangedExtrasNetwork.INSTANCE.sendToServer(new RespawnChoicePacket(false));
        minecraft.player.respawn();
        minecraft.setScreen(null);
    }

    private void handleRespawn() {
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        if (showTransfurButton) {
            handleRespawnAsHuman();
            return;
        }

        minecraft.player.respawn();
        minecraft.setScreen(null);
    }

    private void exitToTitleScreen() {
        if (minecraft == null) {
            return;
        }

        if (minecraft.level != null) {
            minecraft.level.disconnect();
        }

        minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        minecraft.setScreen(new TitleScreen());
    }

    private static class FlatButton extends Button {
        public FlatButton(int x, int y, int width, int height, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int alphaInt = Mth.clamp((int)(alpha * 255.0F), 0, 255);
            int background = isHoveredOrFocused() ? 0xAA8A1F1F : 0xAA3A0E0E;
            int textColor = ((alphaInt & 0xFF) << 24) | (active ? 0xF4EDED : 0x9E8C8C);

            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, (alphaInt << 24) | (background & 0x00FFFFFF));

            guiGraphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    getMessage(),
                    getX() + width / 2,
                    getY() + (height - 8) / 2,
                    textColor
            );
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
