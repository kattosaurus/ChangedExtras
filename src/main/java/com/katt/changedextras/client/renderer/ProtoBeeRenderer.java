package com.katt.changedextras.client.renderer;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.renderer.layers.ProtoBeeDisplayLayer;
import com.katt.changedextras.entity.beasts.ProtoBeeEntity;
import com.katt.changedextras.entity.model.ProtoBeeEntityModel;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProtoBeeRenderer extends AdvancedHumanoidRenderer<ProtoBeeEntity, ProtoBeeEntityModel> {
    private static final ResourceLocation BASE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/proto_bee/proto_bee.png");

    public ProtoBeeRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new ProtoBeeEntityModel(context.bakeLayer(ProtoBeeEntityModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel.MODEL_SET,
                0.5F
        );
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(
                this,
                context.getModelSet(),
                CustomEyesLayer::noRender,
                CustomEyesLayer::noRender,
                CustomEyesLayer::noRender,
                CustomEyesLayer::noRender,
                CustomEyesLayer::noRender));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
        this.addLayer(new ProtoBeeDisplayLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ProtoBeeEntity entity) {
        return BASE_TEXTURE;
    }
}
