package com.katt.changedextras.client.renderer.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.model.LatexCarnelineEntityModel;
import com.katt.changedextras.entity.beasts.LatexCarnelineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LatexCarnelineRenderer extends AdvancedHumanoidRenderer<LatexCarnelineEntity, LatexCarnelineEntityModel<LatexCarnelineEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_carneline/latex_carneline.png");
    private static final ResourceLocation GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_carneline/latex_carneline_glow.png");

    public LatexCarnelineRenderer(EntityRendererProvider.Context context, LatexCarnelineEntityModel<LatexCarnelineEntity> model) {
        super(context, model, ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new EyesLayer<LatexCarnelineEntity, LatexCarnelineEntityModel<LatexCarnelineEntity>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW_TEXTURE);
            }

            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LatexCarnelineEntity entity,
                               float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                               float netHeadYaw, float headPitch) {
                getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE)),
                        15728640, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }
        });
        this.addLayer(CustomEyesLayer.<LatexCarnelineEntityModel<LatexCarnelineEntity>, LatexCarnelineEntity>builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0x00EDFF)))
                .withLeftIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xFF075B)))
                .withRightIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xFF075B)))
                .build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    public LatexCarnelineRenderer(EntityRendererProvider.Context context) {
        this(context, new LatexCarnelineEntityModel<>(context.bakeLayer(LatexCarnelineEntityModel.LAYER_LOCATION)));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCarnelineEntity entity) {
        return TEXTURE;
    }
}
