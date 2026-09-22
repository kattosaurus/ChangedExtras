package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.ConeKatFemaleEntity;
import com.katt.changedextras.entity.beasts.ConeKatMaleEntity;
import com.katt.changedextras.entity.beasts.FluffedUpLatexSnowLeopardFemaleEntity;
import com.katt.changedextras.entity.beasts.FluffedUpLatexSnowLeopardMaleEntity;
import com.katt.changedextras.entity.beasts.FurredLatexTigerSharkEntity;
import com.katt.changedextras.entity.beasts.JammerEntity;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import com.katt.changedextras.entity.beasts.ProtoBeeEntity;
import com.katt.changedextras.entity.beasts.Scp009Entity;
import com.katt.changedextras.entity.beasts.WhiteCatEntity;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTransfurVariants {
    public static final DeferredRegister<TransfurVariant<?>> REGISTRY =
            ChangedRegistry.TRANSFUR_VARIANT.createDeferred(ChangedExtras.MODID);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> CONEKAT_MALE =
            REGISTRY.register("conekat_male", () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_MALE)
                    .nightVision()
                    .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                    .addAbility(ChangedAbilities.SWITCH_GENDER)
                    .addAbility(ChangedExtrasAbilities.CLAWS)
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> CONEKAT_FEMALE =
            REGISTRY.register("conekat_female",
                    () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_FEMALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(CONEKAT_MALE, CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<WhiteCatEntity>> WHITE_CAT =
            REGISTRY.register("white_cat",
                    () -> TransfurVariant.Builder.of(ModEntities.WHITE_CAT)
                            .nightVision()
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<KattEntity>> KATT =
            REGISTRY.register("katt",
                    () -> TransfurVariant.Builder.of(ModEntities.KATT)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .addAbility(ChangedExtrasAbilities.PARRY)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .addAbility(ChangedAbilities.HYPNOSIS)
                            .reducedFall(true)
                            .extraJumps(2)
                            .transfurMode(TransfurMode.REPLICATION)
                            .build());

    public static final RegistryObject<TransfurVariant<JammerEntity>> JAMMER =
            REGISTRY.register("jammer",
                    () -> TransfurVariant.Builder.of(ModEntities.JAMMER)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .build());

    public static final RegistryObject<TransfurVariant<ProtoBeeEntity>> PROTO_BEE =
            REGISTRY.register("proto_bee",
                    () -> TransfurVariant.Builder.of(ModEntities.PROTO_BEE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAddonAbilities.POLLEN_CARRY)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
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
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .build());

    public static final RegistryObject<TransfurVariant<FluffedUpLatexSnowLeopardMaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE =
            REGISTRY.register("fluffed_up_latex_snow_leopard_male",
                    () -> TransfurVariant.Builder.of(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .addAbility(ChangedAbilities.SWITCH_TRANSFUR_MODE)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<FluffedUpLatexSnowLeopardFemaleEntity>> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE =
            REGISTRY.register("fluffed_up_latex_snow_leopard_female",
                    () -> TransfurVariant.Builder.of(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE)
                            .nightVision()
                            .addAbility(ChangedAbilities.GRAB_ENTITY_ABILITY)
                            .addAbility(ChangedAbilities.TOGGLE_NIGHT_VISION)
                            .addAbility(ChangedAbilities.SWITCH_GENDER)
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .build());

    public static final GenderedPair<FluffedUpLatexSnowLeopardMaleEntity, FluffedUpLatexSnowLeopardFemaleEntity> FLUFFED_UP_LATEX_SNOW_LEOPARDS =
            ChangedTransfurVariants.Gendered.registerPair(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE, FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE);

    public static final RegistryObject<TransfurVariant<ArtistEntity>> ARTIST =
            REGISTRY.register("artist",
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
                            .addAbility(ChangedExtrasAbilities.CLAWS)
                            .build());

}
