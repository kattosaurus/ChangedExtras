package com.katt.changedextras.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ChangedExtrasGuideScreen extends Screen {
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath("changedextras", "textures/gui/book.png");
    private static final ResourceLocation COVER_TEXTURE = ResourceLocation.fromNamespaceAndPath("changedextras", "textures/gui/guide.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int BOOK_SOURCE_WIDTH = 164;
    private static final int BOOK_SOURCE_HEIGHT = 182;
    private static final int SCALED_BOOK_WIDTH = 230;
    private static final int SCALED_BOOK_HEIGHT = 255;
    static final int TEXT_WIDTH = 168;
    static final int TEXT_HEIGHT = 196;
    private static final int TEXT_LEFT_OFFSET = 43;
    private static final int TEXT_TOP_OFFSET = 25;
    private static final int ARROW_RIGHT_OFFSET = 12;
    private static final int ARROW_TOP_OFFSET = 262;
    private static final int SEPARATOR_MARGIN = 24;
    private static final int ACCENT_COLOR = 0xFF555555;
    private static final int QUOTE_BAR_LEFT = 1;
    private static final int QUOTE_BAR_WIDTH = 2;

    private final List<List<GuideLine>> pages;
    private final boolean hasCover;
    private int page;
    private Button previousButton;
    private Button nextButton;

    public ChangedExtrasGuideScreen(List<List<GuideLine>> pages) {
        super(Component.translatable("item.changedextras.changed_extras_guide"));
        this.pages = pages.isEmpty() ? List.of(List.of()) : pages;
        this.hasCover = Minecraft.getInstance().getResourceManager().getResource(COVER_TEXTURE).isPresent();
        if (!this.hasCover) {
            this.page = 1;
        }
    }

    @Override
    protected void init() {
        int left = (this.width - SCALED_BOOK_WIDTH) / 2;
        int top = (this.height - SCALED_BOOK_HEIGHT) / 2;
        this.previousButton = addRenderableWidget(new PageTurnButton(left + 44 + ARROW_RIGHT_OFFSET, top + ARROW_TOP_OFFSET, 24, 20,
                Component.literal("<"), button -> {
            if (this.page > firstPage()) {
                this.page--;
                updateButtons();
            }
        }));
        this.nextButton = addRenderableWidget(new PageTurnButton(left + 162 + ARROW_RIGHT_OFFSET, top + ARROW_TOP_OFFSET, 24, 20,
                Component.literal(">"), button -> {
            if (this.page < this.pages.size()) {
                this.page++;
                updateButtons();
            }
        }));
        updateButtons();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        int left = (this.width - SCALED_BOOK_WIDTH) / 2;
        int top = (this.height - SCALED_BOOK_HEIGHT) / 2;

        if (isCover()) {
            graphics.blit(COVER_TEXTURE, left, top, SCALED_BOOK_WIDTH, SCALED_BOOK_HEIGHT, 0.0F, 0.0F, BOOK_SOURCE_WIDTH, BOOK_SOURCE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        graphics.blit(BOOK_TEXTURE, left, top, SCALED_BOOK_WIDTH, SCALED_BOOK_HEIGHT, 0.0F, 0.0F, BOOK_SOURCE_WIDTH, BOOK_SOURCE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int textX = left + TEXT_LEFT_OFFSET;
        int textY = top + TEXT_TOP_OFFSET;
        int lineHeight = this.font.lineHeight;
        int bottom = textY + TEXT_HEIGHT;
        int y = textY;
        for (GuideLine line : this.pages.get(this.page - 1)) {
            if (y >= bottom) {
                break;
            }

            switch (line.kind()) {
                case BLANK -> y += lineHeight;
                case SEPARATOR -> {
                    int separatorY = y + lineHeight / 2;
                    graphics.fill(textX + SEPARATOR_MARGIN, separatorY, textX + TEXT_WIDTH - SEPARATOR_MARGIN, separatorY + 1, ACCENT_COLOR);
                    y += lineHeight;
                }
                case QUOTE -> {
                    graphics.fill(textX + QUOTE_BAR_LEFT, y, textX + QUOTE_BAR_LEFT + QUOTE_BAR_WIDTH, y + lineHeight, ACCENT_COLOR);
                    graphics.drawString(this.font, line.text(), textX + line.indent(), y, 0, false);
                    y += lineHeight;
                }
                case HEADING -> {
                    float scale = line.scale();
                    graphics.pose().pushPose();
                    graphics.pose().translate(textX + line.indent(), y, 0.0F);
                    graphics.pose().scale(scale, scale, 1.0F);
                    graphics.drawString(this.font, line.text(), 0, 0, 0, false);
                    graphics.pose().popPose();
                    y += Math.round(lineHeight * scale);
                }
                case TEXT -> {
                    graphics.drawString(this.font, line.text(), textX + line.indent(), y, 0, false);
                    y += lineHeight;
                }
            }
        }

        Component pageIndicator = Component.translatable("book.pageIndicator", this.page, this.pages.size());
        int pageIndicatorWidth = this.font.width(pageIndicator);
        graphics.drawString(this.font, pageIndicator, textX + (TEXT_WIDTH - pageIndicatorWidth) / 2, textY + TEXT_HEIGHT + 4, 0, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isCover() {
        return this.hasCover && this.page == 0;
    }

    private int firstPage() {
        return this.hasCover ? 0 : 1;
    }

    private void updateButtons() {
        this.previousButton.visible = this.page > firstPage();
        this.nextButton.visible = this.page < this.pages.size();
    }

    private static final class PageTurnButton extends Button {
        PageTurnButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        public void playDownSound(SoundManager manager) {
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }
}
