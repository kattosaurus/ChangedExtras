package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexThorniiiEntity;
import com.katt.changedextras.entity.model.LatexThorniiiEntityModel;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSquidDogModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexThorniiiRenderer extends AdvancedHumanoidRenderer<LatexThorniiiEntity, LatexThorniiiEntityModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ChangedExtras.MODID, "textures/entity/latex_thorniii/latex_thorniii.png");

    public LatexThorniiiRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexThorniiiEntityModel(context.bakeLayer(LatexThorniiiEntityModel.LAYER_LOCATION)),
                ArmorLatexMaleSquidDogModel.MODEL_SET, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexThorniiiEntity entity) {
        return TEXTURE;
    }
}