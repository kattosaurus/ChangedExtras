package us.kattosaur.changedextras.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.ltxprogrammer.changed.util.Color3;
import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.client.model.VoidCatEntityModel;
import us.kattosaur.changedextras.client.renderer.JammerRenderTypes;
import us.kattosaur.changedextras.entity.beasts.VoidCatEntity;

public class VoidCatRenderer extends AdvancedHumanoidRenderer<VoidCatEntity, VoidCatEntityModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/void_cat/void_cat.png");
    private static final ResourceLocation OUTLINE = ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/void_cat/outline.png");
    private final VoidCatEntityModel outlineModel;

    public VoidCatRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidCatEntityModel(context.bakeLayer(VoidCatEntityModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.shadowRadius = 0.0F;
        outlineModel = new VoidCatEntityModel(context.bakeLayer(VoidCatEntityModel.OUTLINE_LAYER_LOCATION));
        addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
        outlineModel.hideDecorationsForOutline();
        addLayer(new GlowAndOutlineLayer(this, outlineModel));
        addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x000000))
                .withIris(Color3.fromInt(0xFFFFFF))
                .build());
    }

    @Override public ResourceLocation getTextureLocation(VoidCatEntity entity) { return TEXTURE; }

    @Override
    protected int getBlockLightLevel(VoidCatEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected RenderType getRenderType(VoidCatEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (translucent)
            return RenderType.itemEntityTranslucentCull(TEXTURE);
        if (bodyVisible)
            return RenderType.entityTranslucent(TEXTURE);
        if (glowing)
            return RenderType.outline(TEXTURE);
        return null;
    }

    private static class GlowAndOutlineLayer extends RenderLayer<VoidCatEntity, VoidCatEntityModel> {
        private final VoidCatEntityModel outlineModel;
        GlowAndOutlineLayer(VoidCatRenderer parent, VoidCatEntityModel outlineModel) { super(parent); this.outlineModel = outlineModel; }
        @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, VoidCatEntity entity,
                float limbSwing, float limbAmount, float partialTick, float age, float yaw, float pitch) {
            getParentModel().renderToBuffer(pose, buffers.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE)),
                    15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            outlineModel.prepareMobModel(entity, limbSwing, limbAmount, partialTick);
            outlineModel.setupAnim(entity, limbSwing, limbAmount, age, yaw, pitch);
            outlineModel.renderToBuffer(pose, buffers.getBuffer(JammerRenderTypes.frontCulledEmissive(OUTLINE)), 15728640,
                    OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }
    }
}
