package com.katt.changedextras.client.renderer.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.model.LatexKaijuEntityModel;
import com.katt.changedextras.entity.beasts.LatexKaijuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LatexKaijuRenderer extends AdvancedHumanoidRenderer<LatexKaijuEntity, LatexKaijuEntityModel<LatexKaijuEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_kaiju/latex_kaiju.png");
    private static final ResourceLocation GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_kaiju/latex_kaiju_glow.png");

    public LatexKaijuRenderer(EntityRendererProvider.Context context, LatexKaijuEntityModel<LatexKaijuEntity> model) {
        super(context, model, ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new EyesLayer<LatexKaijuEntity, LatexKaijuEntityModel<LatexKaijuEntity>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW_TEXTURE);
            }

            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LatexKaijuEntity entity,
                               float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                               float netHeadYaw, float headPitch) {
                getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE)),
                        15728640, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }
        });
        this.addLayer(CustomEyesLayer.<LatexKaijuEntityModel<LatexKaijuEntity>, LatexKaijuEntity>builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0x30376D)))
                .withLeftIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0x80BEFF)))
                .withRightIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0x80BEFF)))
                .build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    public LatexKaijuRenderer(EntityRendererProvider.Context context) {
        this(context, new LatexKaijuEntityModel<>(context.bakeLayer(LatexKaijuEntityModel.LAYER_LOCATION)));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexKaijuEntity entity) {
        return TEXTURE;
    }
}
