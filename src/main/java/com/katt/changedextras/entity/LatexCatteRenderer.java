package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCatteRenderer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AdvancedHumanoidRenderer<T, M> {
    private static final ResourceLocation CATTE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/latex_catte/latex_catte.png");
    private final ResourceLocation texture;

    public LatexCatteRenderer(EntityRendererProvider.Context context, M model) {
        this(context, model, CATTE_TEXTURE);
    }

    public LatexCatteRenderer(EntityRendererProvider.Context context, M model, ResourceLocation texture) {
        super(context, model, ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.texture = texture;
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
