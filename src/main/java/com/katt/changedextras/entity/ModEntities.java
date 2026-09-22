package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import com.katt.changedextras.entity.beasts.ConeKatFemaleEntity;
import com.katt.changedextras.entity.beasts.ConeKatMaleEntity;
import com.katt.changedextras.entity.beasts.FluffedUpLatexSnowLeopardFemaleEntity;
import com.katt.changedextras.entity.beasts.FluffedUpLatexSnowLeopardMaleEntity;
import com.katt.changedextras.entity.beasts.FurredLatexTigerSharkEntity;
import com.katt.changedextras.entity.beasts.JammerEntity;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.entity.beasts.ProtoBeeEntity;
import com.katt.changedextras.entity.beasts.Scp009Entity;
import com.katt.changedextras.entity.beasts.WhiteCatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ChangedExtras.MODID);

    public static final RegistryObject<EntityType<ConeKatMaleEntity>> CONEKAT_MALE = REGISTRY.register("conekat_male",
            () -> EntityType.Builder.of(ConeKatMaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("conekat_male"));

    public static final RegistryObject<EntityType<ConeKatFemaleEntity>> CONEKAT_FEMALE = REGISTRY.register("conekat_female",
            () -> EntityType.Builder.of(ConeKatFemaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("conekat_female"));

    public static final RegistryObject<EntityType<WhiteCatEntity>> WHITE_CAT = REGISTRY.register("white_cat",
            () -> EntityType.Builder.of(WhiteCatEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("white_cat"));

    public static final RegistryObject<EntityType<KattEntity>> KATT = REGISTRY.register("katt",
            () -> EntityType.Builder.of(KattEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("katt"));

    public static final RegistryObject<EntityType<JammerEntity>> JAMMER = REGISTRY.register("jammer",
            () -> EntityType.Builder.of(JammerEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("jammer"));

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

    public static final RegistryObject<EntityType<FluffedUpLatexSnowLeopardMaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE = REGISTRY.register("fluffed_up_latex_snow_leopard_male",
            () -> EntityType.Builder.of(FluffedUpLatexSnowLeopardMaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("fluffed_up_latex_snow_leopard_male"));

    public static final RegistryObject<EntityType<FluffedUpLatexSnowLeopardFemaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE = REGISTRY.register("fluffed_up_latex_snow_leopard_female",
            () -> EntityType.Builder.of(FluffedUpLatexSnowLeopardFemaleEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("fluffed_up_latex_snow_leopard_female"));

    public static final RegistryObject<EntityType<ArtistEntity>> ARTIST = REGISTRY.register("artist",
            () -> EntityType.Builder.of(ArtistEntity::new, MobCategory.MONSTER)
                    .clientTrackingRange(12)
                    .sized(0.7F, 1.93F)
                    .build("artist"));

    public static final RegistryObject<EntityType<Scp009Entity>> SCP_009 = REGISTRY.register("scp_009",
            () -> EntityType.Builder.of(Scp009Entity::new, MobCategory.MONSTER)
                    .clientTrackingRange(10)
                    .sized(0.7F, 1.93F)
                    .build("scp_009"));
}
