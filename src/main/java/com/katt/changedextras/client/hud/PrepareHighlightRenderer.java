package com.katt.changedextras.client.hud;

import com.katt.changedextras.client.renderer.PrepareRenderTypes;
import com.katt.changedextras.client.tracker.ClientPrepareTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class PrepareHighlightRenderer {
    private static final float RED = 1.0F;
    private static final float GREEN = 0.85F;
    private static final float BLUE = 0.05F;
    private static final float MAX_ALPHA = 0.4F;

    private PrepareHighlightRenderer() {
    }

    public static <T extends LivingEntity> void render(T entity, EntityModel<T> model, PoseStack poseStack,
                                                       MultiBufferSource buffer, int packedLight) {
        if (!ClientPrepareTracker.isActive() || !entity.isAlive() || ClientPrepareTracker.isCaster(entity)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || entity == minecraft.getCameraEntity()) {
            return;
        }

        Entity caster = minecraft.level.getEntity(ClientPrepareTracker.getCasterId());
        if (caster == null) {
            return;
        }

        double radius = minecraft.options.renderDistance().get() * 16.0D;
        if (!caster.getBoundingBox().inflate(radius).intersects(entity.getBoundingBox())) {
            return;
        }

        float alpha = ClientPrepareTracker.getAlpha() * MAX_ALPHA;
        if (alpha <= 0.0F) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(PrepareRenderTypes.prepareOverlay());
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                RED, GREEN, BLUE, alpha);
    }
}
