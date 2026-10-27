package com.katt.changedextras.client.renderer.entity;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelSet;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ConeKatRenderer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AdvancedHumanoidRenderer<T, M> {
    private static final ResourceLocation CONEKAT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/conekat/conekat.png");
    private final ResourceLocation texture;

    public ConeKatRenderer(EntityRendererProvider.Context context, M model, ArmorModelSet<? super T, ? extends LatexHumanoidArmorModel<? super T, ?>> armorModelSet) {
        this(context, model, armorModelSet, CONEKAT_TEXTURE);
    }

    public ConeKatRenderer(EntityRendererProvider.Context context, M model, ArmorModelSet<? super T, ? extends LatexHumanoidArmorModel<? super T, ?>> armorModelSet, ResourceLocation texture) {
        super(context, model, armorModelSet, 0.5f);
        this.texture = texture;
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.<M, T>builder(this, context.getModelSet())
                .withSclera((entity, bpi) -> CustomEyesLayer.ColorData.ofEmissiveColor(CustomEyesLayer.scleraColor(entity, bpi).color))
                .withLeftIris(CustomEyesLayer::glowingIrisColorLeft)
                .withRightIris(CustomEyesLayer::glowingIrisColorRight)
                .build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
