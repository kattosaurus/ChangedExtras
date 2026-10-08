package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexHakuEntity;
import com.katt.changedextras.entity.model.LatexHakuEntityModel;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurLowerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleTaurUpperModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelPicker;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class LatexHakuRenderer extends AdvancedHumanoidRenderer<LatexHakuEntity, LatexHakuEntityModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_haku/latex_haku.png");

    public LatexHakuRenderer(EntityRendererProvider.Context context) {
        super(context,
                new LatexHakuEntityModel(context.bakeLayer(LatexHakuEntityModel.LAYER_LOCATION)),
                ArmorModelPicker.centaur(
                        context.getModelSet(),
                        ArmorLatexMaleTaurUpperModel.MODEL_SET,
                        ArmorLatexCentaurLowerModel.MODEL_SET_WITH_TORSO),
                0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, this.getModel()));
        this.addLayer(CustomEyesLayer.<LatexHakuEntityModel, LatexHakuEntity>builder(this, context.getModelSet())
                .withIris(CustomEyesLayer.fixedColor(Color3.fromInt(0x8B0000)))
                .build());
        this.addLayer(new SaddleLayer<>(this, this.getModel(), Changed.modResource("textures/white_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.shortCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexHakuEntity entity) {
        return TEXTURE;
    }
}
