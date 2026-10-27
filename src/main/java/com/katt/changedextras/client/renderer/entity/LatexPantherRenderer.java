package com.katt.changedextras.client.renderer.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.model.LatexPantherEntityModel;
import com.katt.changedextras.entity.beasts.LatexPantherEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPantherRenderer extends AdvancedHumanoidRenderer<LatexPantherEntity, LatexPantherEntityModel<LatexPantherEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_panther/latex_panther.png");

    public LatexPantherRenderer(EntityRendererProvider.Context context, LatexPantherEntityModel<LatexPantherEntity> model) {
        super(context, model, ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.<LatexPantherEntityModel<LatexPantherEntity>, LatexPantherEntity>builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xCD2F11)))
                .withLeftIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xF8EA02)))
                .withRightIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xF8EA02)))
                .build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    public LatexPantherRenderer(EntityRendererProvider.Context context) {
        this(context, new LatexPantherEntityModel<>(context.bakeLayer(LatexPantherEntityModel.LAYER_LOCATION)));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPantherEntity entity) {
        return TEXTURE;
    }
}
