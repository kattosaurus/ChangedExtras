package com.katt.changedextras.client.renderer.layers;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.ProtoBeeEntity;
import com.katt.changedextras.entity.model.ProtoBeeEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ProtoBeeDisplayLayer extends RenderLayer<ProtoBeeEntity, ProtoBeeEntityModel> {
    private static final ResourceLocation SCREENS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/proto_bee/proto_bee_screens.png");
    private static final ResourceLocation DISPLAY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/proto_bee/proto_bee_display.png");

    public ProtoBeeDisplayLayer(RenderLayerParent<ProtoBeeEntity, ProtoBeeEntityModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ProtoBeeEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        // Layer 2: ProtoBeeScreens (Visor screens background)
        VertexConsumer screensConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(SCREENS_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, screensConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        // Layer 3: ProtoBeeDisplay (Emissive LEDs / Visor face & cheek sigils)
        VertexConsumer displayConsumer = buffer.getBuffer(RenderType.eyes(DISPLAY_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, displayConsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
