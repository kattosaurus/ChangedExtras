package com.katt.changedextras.worldgen;

import com.katt.changedextras.worldgen.biome.ModBiomes;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class BlueCaveOreVeinFilter {
    private static final Map<NoiseChunk, ChunkAccess> CHUNKS = Collections.synchronizedMap(new WeakHashMap<>());

    private BlueCaveOreVeinFilter() {
    }

    public static void track(NoiseChunk noiseChunk, ChunkAccess chunk) {
        CHUNKS.put(noiseChunk, chunk);
    }

    public static NoiseChunk.BlockStateFiller wrap(NoiseChunk.BlockStateFiller original) {
        return context -> {
            if (context instanceof NoiseChunk noiseChunk) {
                ChunkAccess chunk = CHUNKS.get(noiseChunk);
                if (chunk != null && isBlueCave(chunk, context)) {
                    return null;
                }
            }

            return original.calculate(context);
        };
    }

    private static boolean isBlueCave(ChunkAccess chunk, DensityFunction.FunctionContext context) {
        return chunk.getNoiseBiome(
                QuartPos.fromBlock(context.blockX()),
                QuartPos.fromBlock(context.blockY()),
                QuartPos.fromBlock(context.blockZ())
        ).is(ModBiomes.BLUE_CAVE);
    }
}
