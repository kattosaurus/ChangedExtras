package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ChangedExtras.MODID);

    public static final RegistryObject<EntityType<ConeKatMaleEntity>> CONEKAT_MALE = REGISTRY.register("conekat/male",
            () -> EntityType.Builder.of(ConeKatMaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("conekat/male"));

    public static final RegistryObject<EntityType<ConeKatFemaleEntity>> CONEKAT_FEMALE = REGISTRY.register("conekat/female",
            () -> EntityType.Builder.of(ConeKatFemaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("conekat/female"));

    public static final RegistryObject<EntityType<WhiteCatEntity>> WHITE_CAT = REGISTRY.register("latex_white_cat",
            () -> EntityType.Builder.of(WhiteCatEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("latex_white_cat"));

    public static final RegistryObject<EntityType<KattEntity>> KATT = REGISTRY.register("latex_katto",
            () -> EntityType.Builder.of(KattEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("latex_katto"));

    public static final RegistryObject<EntityType<JammerEntity>> JAMMER = REGISTRY.register("latex_jammer",
            () -> EntityType.Builder.of(JammerEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("latex_jammer"));

    public static final RegistryObject<EntityType<ProtoBeeEntity>> PROTO_BEE = REGISTRY.register("proto_bee",
            () -> EntityType.Builder.of(ProtoBeeEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("proto_bee"));

    public static final RegistryObject<EntityType<FurredLatexTigerSharkEntity>> FURRED_LATEX_TIGER_SHARK = REGISTRY.register("furred_latex_tiger_shark",
            () -> EntityType.Builder.of(FurredLatexTigerSharkEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("furred_latex_tiger_shark"));

    public static final RegistryObject<EntityType<FluffedUpLatexSnowLeopardMaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE = REGISTRY.register("fluffed_up_latex_snow_leopard/male",
            () -> EntityType.Builder.of(FluffedUpLatexSnowLeopardMaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("fluffed_up_latex_snow_leopard/male"));

    public static final RegistryObject<EntityType<FluffedUpLatexSnowLeopardFemaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE = REGISTRY.register("fluffed_up_latex_snow_leopard/female",
            () -> EntityType.Builder.of(FluffedUpLatexSnowLeopardFemaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("fluffed_up_latex_snow_leopard/female"));

    public static final RegistryObject<EntityType<ArtistEntity>> ARTIST = REGISTRY.register("latex_artist",
            () -> EntityType.Builder.of(ArtistEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(12)
                    .sized(0.7F, 1.93F)
                    .build("latex_artist"));

    public static final RegistryObject<EntityType<Scp009Entity>> SCP_009 = REGISTRY.register("scp_009",
            () -> EntityType.Builder.of(Scp009Entity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("scp_009"));

    public static final RegistryObject<EntityType<LatexHazzyEntity>> LATEX_HAZZY = REGISTRY.register("latex_hazzy",
            () -> EntityType.Builder.of(LatexHazzyEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("latex_hazzy"));

    public static final RegistryObject<EntityType<LatexCatteEntity>> LATEX_CATTE = REGISTRY.register("latex_catte",
            () -> EntityType.Builder.of(LatexCatteEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("latex_catte"));

    public static final RegistryObject<EntityType<LatexHakuEntity>> LATEX_HAKU = REGISTRY.register("latex_haku",
            () -> EntityType.Builder.of(LatexHakuEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(1.25F, 2.0F)
                    .build("latex_haku"));

}
