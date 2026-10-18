package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.SnepEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Renders the Snep as a flat camera-facing billboard of {@code sneppng.png}, two blocks tall.
 * The source texture is 166x239, so width is derived from that aspect ratio.
 */
public class SnepRenderer extends EntityRenderer<SnepEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/sneppng.png");
    private static final float HEIGHT = 2.0F;
    private static final float WIDTH = HEIGHT * (166.0F / 239.0F);

    public SnepRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SnepEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float halfWidth = WIDTH / 2.0F;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        vertex(consumer, pose, normal, -halfWidth, 0.0F, 0.0F, 1.0F, packedLight);
        vertex(consumer, pose, normal, halfWidth, 0.0F, 1.0F, 1.0F, packedLight);
        vertex(consumer, pose, normal, halfWidth, HEIGHT, 1.0F, 0.0F, packedLight);
        vertex(consumer, pose, normal, -halfWidth, HEIGHT, 0.0F, 0.0F, packedLight);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float u, float v, int packedLight) {
        consumer.vertex(pose, x, y, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SnepEntity entity) {
        return TEXTURE;
    }
}
