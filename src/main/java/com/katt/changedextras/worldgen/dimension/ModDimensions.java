package com.katt.changedextras.worldgen.dimension;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.biome.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<Level> DM_KEY = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(ChangedExtras.MODID, "idle_death_gamble")
    );
    public static final ResourceKey<DimensionType> DM_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            new ResourceLocation(ChangedExtras.MODID, "idle_death_gamble")
    );
    public static final ResourceKey<LevelStem> DM_STEM = ResourceKey.create(
            Registries.LEVEL_STEM,
            new ResourceLocation(ChangedExtras.MODID, "idle_death_gamble")
    );

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(DM_TYPE, new DimensionType(
                OptionalLong.of(18000L),
                true,
                false,
                false,
                false,
                1.0D,
                false,
                false,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.END_EFFECTS,
                1.0F,
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        ));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        Holder<Biome> whiteVoidBiome = biomes.getOrThrow(ModBiomes.WHITE_VOID);

        FlatLevelGeneratorSettings flatSettings = new FlatLevelGeneratorSettings(
                Optional.empty(),
                whiteVoidBiome,
                List.of()
        );

        FlatLevelSource chunkGenerator = new FlatLevelSource(flatSettings);
        context.register(DM_STEM, new LevelStem(
                dimensionTypes.getOrThrow(DM_TYPE),
                chunkGenerator
        ));
    }
}
