package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.model.ArtistEntityModel;
import com.katt.changedextras.entity.model.ConeKatFemaleEntityModel;
import com.katt.changedextras.entity.model.ConeKatMaleEntityModel;
import com.katt.changedextras.entity.model.FluffedUpLatexSnowLeopardFemaleEntityModel;
import com.katt.changedextras.entity.model.FluffedUpLatexSnowLeopardMaleEntityModel;
import com.katt.changedextras.entity.model.FurredLatexTigerSharkEntityModel;
import com.katt.changedextras.entity.model.JammerEntityModel;
import com.katt.changedextras.entity.model.KattEntityModel;
import com.katt.changedextras.entity.model.ProtoBeeEntityModel;
import com.katt.changedextras.entity.model.Scp009EntityModel;
import com.katt.changedextras.entity.model.WhiteCatEntityModel;
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
        event.registerLayerDefinition(FurredLatexTigerSharkEntityModel.LAYER_LOCATION, FurredLatexTigerSharkEntityModel::createBodyLayer);
        event.registerLayerDefinition(FluffedUpLatexSnowLeopardMaleEntityModel.LAYER_LOCATION, FluffedUpLatexSnowLeopardMaleEntityModel::createBodyLayer);
        event.registerLayerDefinition(FluffedUpLatexSnowLeopardFemaleEntityModel.LAYER_LOCATION, FluffedUpLatexSnowLeopardFemaleEntityModel::createBodyLayer);
        event.registerLayerDefinition(Scp009EntityModel.LAYER_LOCATION, Scp009EntityModel::createBodyLayer);
    }
}
