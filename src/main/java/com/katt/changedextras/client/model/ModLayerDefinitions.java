package com.katt.changedextras.client.model;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.model.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModLayerDefinitions {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ConeKatMaleEntityModel.LAYER_LOCATION, ConeKatMaleEntityModel::createBodyLayer);
        event.registerLayerDefinition(ConeKatFemaleEntityModel.LAYER_LOCATION, ConeKatFemaleEntityModel::createBodyLayer);
        event.registerLayerDefinition(WhiteCatEntityModel.LAYER_LOCATION, WhiteCatEntityModel::createBodyLayer);
        event.registerLayerDefinition(ArtistEntityModel.LAYER_LOCATION, ArtistEntityModel::createBodyLayer);
        event.registerLayerDefinition(KattEntityModel.LAYER_LOCATION, KattEntityModel::createBodyLayer);
        event.registerLayerDefinition(JammerEntityModel.LAYER_LOCATION, JammerEntityModel::createBodyLayer);
        event.registerLayerDefinition(JammerEntityModel.OUTLINE_LAYER_LOCATION, JammerEntityModel::createOutlineLayer);
        event.registerLayerDefinition(ProtoBeeEntityModel.LAYER_LOCATION, ProtoBeeEntityModel::createBodyLayer);
        event.registerLayerDefinition(Scp009EntityModel.LAYER_LOCATION, Scp009EntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexHazzyEntityModel.LAYER_LOCATION, LatexHazzyEntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexCatteEntityModel.LAYER_LOCATION, LatexCatteEntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexHakuEntityModel.LAYER_LOCATION, LatexHakuEntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexThorniiiEntityModel.LAYER_LOCATION, LatexThorniiiEntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexCarnelineEntityModel.LAYER_LOCATION, LatexCarnelineEntityModel::createBodyLayer);
        event.registerLayerDefinition(LatexPantherEntityModel.LAYER_LOCATION, LatexPantherEntityModel::createBodyLayer);
    }
}
