package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.*;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTransfurVariants {
    public static final DeferredRegister<TransfurVariant<?>> REGISTRY =
            ChangedRegistry.TRANSFUR_VARIANT.createDeferred(ChangedExtras.MODID);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> CONEKAT_MALE =
            REGISTRY.register("conekat/male", () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_MALE)
                    .nightVision()
                    .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                    .addAbility(ChangedAbilities.SWITCH_GENDER)
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> CONEKAT_FEMALE =
            REGISTRY.register("conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_FEMALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(CONEKAT_MALE, CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<WhiteCatEntity>> WHITE_CAT =
            REGISTRY.register("latex_white_cat",
                    () -> TransfurVariant.Builder.of(ModEntities.WHITE_CAT)
                            .nightVision()
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .transfurMode(TransfurMode.REPLICATION)
                            .addAbility(ChangedExtrasAbilities.TURN_FERAL)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<KattEntity>> KATT =
            REGISTRY.register("latex_katto",
                    () -> TransfurVariant.Builder.of(ModEntities.KATT)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedExtrasAbilities.PARRY)
                            .addAbility(ChangedExtrasAbilities.TURN_FERAL)
                            .reducedFall(true)
                            .extraJumps(4)
                            .transfurMode(TransfurMode.REPLICATION)
                            .build());

    public static final RegistryObject<TransfurVariant<JammerEntity>> JAMMER =
            REGISTRY.register("latex_jammer",
                    () -> TransfurVariant.Builder.of(ModEntities.JAMMER)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .build());

    public static final RegistryObject<TransfurVariant<ProtoBeeEntity>> PROTO_BEE =
            REGISTRY.register("proto_bee",
                    () -> TransfurVariant.Builder.of(ModEntities.PROTO_BEE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAddonAbilities.POLLEN_CARRY)
                            .transfurMode(TransfurMode.ABSORPTION)
                            .absorbing()
                            .reducedFall(true)
                            .extraJumps(3)
                            .build());

    public static final RegistryObject<TransfurVariant<FurredLatexTigerSharkEntity>> FURRED_LATEX_TIGER_SHARK =
            REGISTRY.register("furred_latex_tiger_shark",
                    () -> TransfurVariant.Builder.of(ModEntities.FURRED_LATEX_TIGER_SHARK)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .build());

    public static final RegistryObject<TransfurVariant<FluffedUpLatexSnowLeopardMaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE =
            REGISTRY.register("fluffed_up_latex_snow_leopard/male",
                    () -> TransfurVariant.Builder.of(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<FluffedUpLatexSnowLeopardFemaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE =
            REGISTRY.register("fluffed_up_latex_snow_leopard/female",
                    () -> TransfurVariant.Builder.of(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .build());

    public static final GenderedPair<FluffedUpLatexSnowLeopardMaleEntity, FluffedUpLatexSnowLeopardFemaleEntity> FLUFFED_UP_LATEX_SNOW_LEOPARDS =
            ChangedTransfurVariants.Gendered.registerPair(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE, FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE);

    public static final RegistryObject<TransfurVariant<ArtistEntity>> ARTIST =
            REGISTRY.register("latex_artist",
                    () -> TransfurVariant.Builder.of(ModEntities.ARTIST)
                            .nightVision()
                            .addAbility(ChangedExtrasAbilities.PAINT_BALL)
                            .addAbility(ChangedExtrasAbilities.SWING)
                            .addAbility(ChangedExtrasAbilities.PUNCTURE)
                            .build());

    public static final RegistryObject<TransfurVariant<Scp009Entity>> SCP_009 =
            REGISTRY.register("scp_009",
                    () -> TransfurVariant.Builder.of(ModEntities.SCP_009)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .build());

    public static final RegistryObject<TransfurVariant<LatexHazzyEntity>> LATEX_HAZZY =
            REGISTRY.register("latex_hazzy",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_HAZZY)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .build());

    public static final RegistryObject<TransfurVariant<LatexCatteEntity>> LATEX_CATTE =
            REGISTRY.register("latex_catte",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_CATTE)
                            .nightVision()
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexHakuEntity>> LATEX_HAKU =
            REGISTRY.register("latex_haku",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_HAKU)
                            .quadrupedal()
                            .cameraZOffset(0.4375f)
                            .rideable()
                            .nightVision()
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

}
