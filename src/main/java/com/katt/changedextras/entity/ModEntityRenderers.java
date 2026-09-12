package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.renderer.ProtoBeeRenderer;
import com.katt.changedextras.entity.model.ConeKatFemaleEntityModel;
import com.katt.changedextras.entity.model.ConeKatMaleEntityModel;
import com.katt.changedextras.entity.model.ArtistEntityModel;
import com.katt.changedextras.entity.model.FluffedUpLatexSnowLeopardFemaleEntityModel;
import com.katt.changedextras.entity.model.FluffedUpLatexSnowLeopardMaleEntityModel;
import com.katt.changedextras.entity.model.FurredLatexTigerSharkEntityModel;
import com.katt.changedextras.entity.model.KattEntityModel;
import com.katt.changedextras.entity.model.WhiteCatEntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import static net.ltxprogrammer.changed.init.ChangedEntityRenderers.registerHumanoid;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        registerHumanoid(event, ModEntities.CONEKAT_MALE.get(),
                context -> new ModEntityRenderer<>(context, new ConeKatMaleEntityModel(context.bakeLayer(ConeKatMaleEntityModel.LAYER_LOCATION))));
        registerHumanoid(event, ModEntities.CONEKAT_FEMALE.get(),
                context -> new ModEntityRenderer<>(context, new ConeKatFemaleEntityModel(context.bakeLayer(ConeKatFemaleEntityModel.LAYER_LOCATION))));
        registerHumanoid(event, ModEntities.WHITE_CAT.get(),
                context -> new WhiteCatRenderer<>(context, new WhiteCatEntityModel<>(context.bakeLayer(WhiteCatEntityModel.LAYER_LOCATION))));
        registerHumanoid(event, ModEntities.ARTIST.get(),
                ArtistRenderer::new);
        registerHumanoid(event, ModEntities.KATT.get(),
                KattRenderer::new);
        registerHumanoid(event, ModEntities.JAMMER.get(),
                JammerRenderer::new);
        registerHumanoid(event, ModEntities.PROTO_BEE.get(),
                ProtoBeeRenderer::new);
        registerHumanoid(event, ModEntities.FURRED_LATEX_TIGER_SHARK.get(),
                context -> new ModEntityRenderer<>(context,
                        new FurredLatexTigerSharkEntityModel(context.bakeLayer(FurredLatexTigerSharkEntityModel.LAYER_LOCATION)),
                        ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/furred_latex_tiger_shark/furred_latex_tiger_shark.png")));
        registerHumanoid(event, ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE.get(),
                context -> new ModEntityRenderer<>(context,
                        new FluffedUpLatexSnowLeopardMaleEntityModel(context.bakeLayer(FluffedUpLatexSnowLeopardMaleEntityModel.LAYER_LOCATION)),
                        ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/fluffed_up_latex_snow_leopard_male/fluffed_up_latex_snow_leopard_male.png")));
        registerHumanoid(event, ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE.get(),
                context -> new ModEntityRenderer<>(context,
                        new FluffedUpLatexSnowLeopardFemaleEntityModel(context.bakeLayer(FluffedUpLatexSnowLeopardFemaleEntityModel.LAYER_LOCATION)),
                        ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/entity/fluffed_up_latex_snow_leopard_female/fluffed_up_latex_snow_leopard_female.png")));
    }
}
