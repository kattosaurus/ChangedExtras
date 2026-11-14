package us.kattosaur.changedextras.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Shown once per game launch when both Changed Extras and the standalone
 * animations addon are installed. The two mods animate the same eyes and
 * humanoid models, so the player is warned about the overlap but may still
 * continue.
 */
public class AnimationConflictWarningScreen extends Screen {
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HINT_COLOR = 0xAAAAAA;
    private static final int TITLE_COLOR = 0xFFAA00;
    private static final int MARGIN = 40;
    private static final int MAX_TEXT_WIDTH = 420;

    private final Screen parent;

    public AnimationConflictWarningScreen(Screen parent) {
        super(Component.translatable("changedextras.animation_conflict.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 160;
        int buttonHeight = 20;
        int gap = 8;
        int totalWidth = buttonWidth * 2 + gap;
        int x = this.width / 2 - totalWidth / 2;
        int y = this.height - 40;

        this.addRenderableWidget(Button.builder(
                Component.translatable("changedextras.animation_conflict.continue"),
                button -> this.onClose()
        ).bounds(x, y, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("changedextras.animation_conflict.quit"),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.stop();
                    }
                }
        ).bounds(x + buttonWidth + gap, y, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        int textWidth = Math.min(this.width - MARGIN * 2, MAX_TEXT_WIDTH);
        int y = 60;

        graphics.drawCenteredString(this.font, this.title, centerX, y, TITLE_COLOR);
        y += 34;

        List<FormattedCharSequence> message = this.font.split(
                Component.translatable("changedextras.animation_conflict.message"), textWidth);
        for (FormattedCharSequence line : message) {
            graphics.drawString(this.font, line, centerX - this.font.width(line) / 2, y, TEXT_COLOR, false);
            y += 12;
        }

        y += 12;
        List<FormattedCharSequence> hint = this.font.split(
                Component.translatable("changedextras.animation_conflict.hint"), textWidth);
        for (FormattedCharSequence line : hint) {
            graphics.drawString(this.font, line, centerX - this.font.width(line) / 2, y, HINT_COLOR, false);
            y += 12;
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
