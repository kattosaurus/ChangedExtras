package com.katt.changedextras.worldgen.feature;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public final class ModFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_STONE_CRYSTAL_PATCH_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation(ChangedExtras.MODID, "blue_stone_crystal_patch")
    );

    public static final ResourceKey<PlacedFeature> BLUE_STONE_CRYSTAL_PATCH_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            new ResourceLocation(ChangedExtras.MODID, "blue_stone_crystal_patch")
    );

    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_STONE_PILLAR_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation(ChangedExtras.MODID, "blue_stone_pillar")
    );

    public static final ResourceKey<PlacedFeature> BLUE_STONE_PILLAR_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            new ResourceLocation(ChangedExtras.MODID, "blue_stone_pillar")
    );

    private ModFeatures() {
    }

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(
                BLUE_STONE_CRYSTAL_PATCH_KEY,
                new ConfiguredFeature<>(ChangedExtras.BLUE_STONE_CRYSTAL_PATCH.get(), NoneFeatureConfiguration.INSTANCE)
        );
        context.register(
                BLUE_STONE_PILLAR_KEY,
                new ConfiguredFeature<>(ChangedExtras.BLUE_STONE_PILLAR.get(), NoneFeatureConfiguration.INSTANCE)
        );
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        Holder<ConfiguredFeature<?, ?>> patchFeature = configuredFeatures.getOrThrow(BLUE_STONE_CRYSTAL_PATCH_KEY);
        List<PlacementModifier> patchModifiers = List.of(
                CountPlacement.of(16),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(64)),
                BiomeFilter.biome()
        );
        context.register(BLUE_STONE_CRYSTAL_PATCH_PLACED_KEY, new PlacedFeature(patchFeature, patchModifiers));

        Holder<ConfiguredFeature<?, ?>> pillarFeature = configuredFeatures.getOrThrow(BLUE_STONE_PILLAR_KEY);
        List<PlacementModifier> pillarModifiers = List.of(
                CountPlacement.of(10),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(64)),
                BiomeFilter.biome()
        );
        context.register(BLUE_STONE_PILLAR_PLACED_KEY, new PlacedFeature(pillarFeature, pillarModifiers));
    }
}
