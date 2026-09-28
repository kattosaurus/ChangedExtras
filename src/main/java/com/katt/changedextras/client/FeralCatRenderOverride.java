package com.katt.changedextras.client;

import com.katt.changedextras.common.FeralCatManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.RenderOverride;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class FeralCatRenderOverride implements RenderOverride.Override {
    private final EntityModelSet modelSet;

    public FeralCatRenderOverride(EntityModelSet modelSet) {
        this.modelSet = modelSet;
    }

    @Override
    public boolean requireVariant() {
        return false;
    }

    @Override
    public boolean wantToOverride(Player player, TransfurVariantInstance<?> variant) {
        if (player == null) return false;
        return ClientFeralCatTracker.isFeral(player.getUUID()) || FeralCatManager.isFeral(player);
    }

    @Override
    public void render(Player player, TransfurVariantInstance<?> variant, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
        FeralCatClientRenderer.renderFeralCat(player, poseStack, buffer, packedLight, partialTick);
    }
}
