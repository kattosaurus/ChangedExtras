package com.katt.changedextras.worldgen.biome;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ModBiomes {
    public static final ResourceKey<Biome> WHITE_VOID = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(ChangedExtras.MODID, "white_void")
    );
}
