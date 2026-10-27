package com.katt.changedextras.worldgen.biome;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.feature.ModFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModBiomes {
    public static final ResourceKey<Biome> WHITE_VOID = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(ChangedExtras.MODID, "white_void")
    );
    public static final ResourceKey<Biome> BLUE_CAVE = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(ChangedExtras.MODID, "blue_cave")
    );

    public static void bootstrap(BootstapContext<Biome> context) {
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .skyColor(0xFFFFFF)
                .fogColor(0xFFFFFF)
                .waterColor(0xFFFFFF)
                .waterFogColor(0xFFFFFF)
                .foliageColorOverride(0xFFFFFF)
                .grassColorOverride(0xFFFFFF)
                .ambientLoopSound(SoundEvents.AMBIENT_CAVE)
                .build();

        context.register(WHITE_VOID, new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.5F)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build());

        registerBlueCave(context);
    }

    private static void registerBlueCave(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        BiomeGenerationSettings generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, carvers)
                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                .addCarver(GenerationStep.Carving.AIR, Carvers.CANYON)
                .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placedFeatures.getOrThrow(ModFeatures.BLUE_STONE_PILLAR_PLACED_KEY))
                .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placedFeatures.getOrThrow(ModFeatures.BLUE_STONE_CRYSTAL_PATCH_PLACED_KEY))
                .build();

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .skyColor(0x0A1B2A)
                .fogColor(0x0A1B2A)
                .waterColor(0x1B3A5C)
                .waterFogColor(0x0A1A2A)
                .ambientLoopSound(SoundEvents.AMBIENT_CAVE)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
                .build();

        context.register(BLUE_CAVE, new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects(effects)
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(generationSettings)
                .build());
    }
}
