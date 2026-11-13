package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.*;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Since Changed 0.16.0, abilities are no longer assigned through {@link TransfurVariant.Builder}.
 * They are granted by ability-tree nodes instead. The default abilities for every variant in this
 * class live in {@code data/changedextras/ability/trees} and {@code data/changedextras/ability/nodes}.
 */
public class ModTransfurVariants {
    public static final DeferredRegister<TransfurVariant<?>> REGISTRY =
            ChangedRegistry.TRANSFUR_VARIANT.createDeferred(ChangedExtras.MODID);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> CONEKAT_MALE =
            REGISTRY.register("conekat/male", () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_MALE)
                    .nightVision()
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> CONEKAT_FEMALE =
            REGISTRY.register("conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.CONEKAT_FEMALE)
                            .nightVision()
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(CONEKAT_MALE, CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> MINT_CONEKAT_MALE =
            REGISTRY.register("mint_conekat/male", () -> TransfurVariant.Builder.of(ModEntities.MINT_CONEKAT_MALE)
                    .nightVision()
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> MINT_CONEKAT_FEMALE =
            REGISTRY.register("mint_conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.MINT_CONEKAT_FEMALE)
                            .nightVision()
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> MINT_CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(MINT_CONEKAT_MALE, MINT_CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> CHOCO_CONEKAT_MALE =
            REGISTRY.register("choco_conekat/male", () -> TransfurVariant.Builder.of(ModEntities.CHOCO_CONEKAT_MALE)
                    .nightVision()
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> CHOCO_CONEKAT_FEMALE =
            REGISTRY.register("choco_conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.CHOCO_CONEKAT_FEMALE)
                            .nightVision()
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> CHOCO_CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(CHOCO_CONEKAT_MALE, CHOCO_CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> STRAWBERRY_CONEKAT_MALE =
            REGISTRY.register("strawberry_conekat/male", () -> TransfurVariant.Builder.of(ModEntities.STRAWBERRY_CONEKAT_MALE)
                    .nightVision()
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> STRAWBERRY_CONEKAT_FEMALE =
            REGISTRY.register("strawberry_conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.STRAWBERRY_CONEKAT_FEMALE)
                            .nightVision()
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> STRAWBERRY_CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(STRAWBERRY_CONEKAT_MALE, STRAWBERRY_CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<ConeKatMaleEntity>> NEAPOLITAN_CONEKAT_MALE =
            REGISTRY.register("neapolitan_conekat/male", () -> TransfurVariant.Builder.of(ModEntities.NEAPOLITAN_CONEKAT_MALE)
                    .nightVision()
                    .build());

    public static final RegistryObject<TransfurVariant<ConeKatFemaleEntity>> NEAPOLITAN_CONEKAT_FEMALE =
            REGISTRY.register("neapolitan_conekat/female",
                    () -> TransfurVariant.Builder.of(ModEntities.NEAPOLITAN_CONEKAT_FEMALE)
                            .nightVision()
                            .build());

    public static final GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> NEAPOLITAN_CONEKATS =
            ChangedTransfurVariants.Gendered.registerPair(NEAPOLITAN_CONEKAT_MALE, NEAPOLITAN_CONEKAT_FEMALE);

    public static final RegistryObject<TransfurVariant<WhiteCatEntity>> WHITE_CAT =
            REGISTRY.register("latex_white_cat",
                    () -> TransfurVariant.Builder.of(ModEntities.WHITE_CAT)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<KattEntity>> KATT =
            REGISTRY.register("latex_katto",
                    () -> TransfurVariant.Builder.of(ModEntities.KATT)
                            .nightVision()
                            .reducedFall(true)
                            .extraJumps(4)
                            .transfurMode(TransfurMode.REPLICATION)
                            .build());

    public static final RegistryObject<TransfurVariant<JammerEntity>> JAMMER =
            REGISTRY.register("latex_jammer",
                    () -> TransfurVariant.Builder.of(ModEntities.JAMMER)
                            .nightVision()
                            .build());

    public static final RegistryObject<TransfurVariant<ProtoBeeEntity>> PROTO_BEE =
            REGISTRY.register("proto_bee",
                    () -> TransfurVariant.Builder.of(ModEntities.PROTO_BEE)
                            .nightVision()
                            .transfurMode(TransfurMode.ABSORPTION)
                            .absorbing()
                            .reducedFall(true)
                            .extraJumps(3)
                            .build());

    public static final RegistryObject<TransfurVariant<ArtistEntity>> ARTIST =
            REGISTRY.register("latex_artist",
                    () -> TransfurVariant.Builder.of(ModEntities.ARTIST)
                            .nightVision()
                            .build());

    public static final RegistryObject<TransfurVariant<Scp009Entity>> SCP_009 =
            REGISTRY.register("scp_009",
                    () -> TransfurVariant.Builder.of(ModEntities.SCP_009)
                            .nightVision()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexHazzyEntity>> LATEX_HAZZY =
            REGISTRY.register("latex_hazzy",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_HAZZY)
                            .nightVision()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexCatteEntity>> LATEX_CATTE =
            REGISTRY.register("latex_catte",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_CATTE)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexHakuEntity>> LATEX_HAKU =
            REGISTRY.register("latex_haku",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_HAKU)
                            .cameraZOffset(0.4375f)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexThorniiiEntity>> LATEX_THORNIII =
            REGISTRY.register("latex_thorniii",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_THORNIII)
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexCarnelineEntity>> LATEX_CARNELINE =
            REGISTRY.register("latex_carneline",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_CARNELINE)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexPantherEntity>> LATEX_PANTHER =
            REGISTRY.register("latex_panther",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_PANTHER)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

    public static final RegistryObject<TransfurVariant<LatexKaijuEntity>> LATEX_KAIJU =
            REGISTRY.register("latex_kaiju",
                    () -> TransfurVariant.Builder.of(ModEntities.LATEX_KAIJU)
                            .nightVision()
                            .transfurMode(TransfurMode.REPLICATION)
                            .replicating()
                            .build());

}
