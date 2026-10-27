package com.katt.changedextras.client.renderer.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.client.model.KattEntityModel;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class KattRenderer extends AdvancedHumanoidRenderer<KattEntity, KattEntityModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/katt/katt_texture.png");
    private static final ResourceLocation GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/katt/katt_glowing.png");
    private static final ResourceLocation JACKPOT_GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/katt/katt_jackpot_glowing.png");

    public KattRenderer(EntityRendererProvider.Context context) {
        super(context,
                new KattEntityModel(context.bakeLayer(KattEntityModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel.MODEL_SET,
                0.5f);

        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));

        // Body glow layer added FIRST so it renders underneath the eyes
        this.addLayer(new EyesLayer<KattEntity, KattEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW_TEXTURE);
            }

            @Override
            public void render(
                    com.mojang.blaze3d.vertex.PoseStack poseStack,
                    net.minecraft.client.renderer.MultiBufferSource buffer,
                    int packedLight,
                    KattEntity entity,
                    float limbSwing, float limbSwingAmount,
                    float partialTick, float ageInTicks,
                    float netHeadYaw, float headPitch) {

                RenderType rt = entity.isJackpot()
                        ? RenderType.eyes(JACKPOT_GLOW_TEXTURE)
                        : RenderType.eyes(GLOW_TEXTURE);

                var vertexConsumer = buffer.getBuffer(rt);
                getParentModel().renderToBuffer(poseStack, vertexConsumer,
                        15728640, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                        1f, 1f, 1f, 1f);
            }
        });

        // CustomEyesLayer added LAST so it renders on top of the body glow
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer::scleraColor)
                .withLeftIris((entity, bpi) -> CustomEyesLayer.ColorData.ofColor(Color3.fromInt(0x8AFFFD)))
                .withRightIris((entity, bpi) -> CustomEyesLayer.ColorData.ofColor(Color3.fromInt(0x8AFFFD)))
                .build());
    }

    @Override
    public ResourceLocation getTextureLocation(KattEntity entity) {
        return TEXTURE;
    }
}