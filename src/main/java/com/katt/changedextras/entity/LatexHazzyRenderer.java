package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexHazzyEntity;
import com.katt.changedextras.entity.model.LatexHazzyEntityModel;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexHazzyRenderer extends AdvancedHumanoidRenderer<LatexHazzyEntity, LatexHazzyEntityModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_hazzy/latex_hazzy.png");

    public LatexHazzyRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexHazzyEntityModel(context.bakeLayer(LatexHazzyEntityModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.<LatexHazzyEntityModel, LatexHazzyEntity>builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColor(Color3.fromInt(0xFFFF75)))
                .withLeftIris(CustomEyesLayer.fixedColor(Color3.fromInt(0xFF5858)))
                .withRightIris(CustomEyesLayer.fixedColor(Color3.fromInt(0xFF5858)))
                .build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexHazzyEntity entity) {
        return TEXTURE;
    }
}
