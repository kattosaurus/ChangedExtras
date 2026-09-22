package com.katt.changedextras.client;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.inventory.PillBottleMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PillBottleScreen extends AbstractContainerScreen<PillBottleMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/gui/container/pill_bottle.png");

    public PillBottleScreen(PillBottleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 182;
        this.imageHeight = 126;
    }

    @Override
    protected void init() {
        super.init();
        this.topPos -= 9;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // No labels (removes title and inventory labels)
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos + 4;
        guiGraphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
    }
}
