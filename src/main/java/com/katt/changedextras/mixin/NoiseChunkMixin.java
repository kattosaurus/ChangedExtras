package com.katt.changedextras.mixin;

import com.katt.changedextras.worldgen.BlueCaveOreVeinFilter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunk.class)
public abstract class NoiseChunkMixin {
    @Inject(method = "forChunk", at = @At("RETURN"), require = 1)
    private static void changedextras$trackChunk(
            ChunkAccess chunk,
            RandomState randomState,
            DensityFunctions.BeardifierOrMarker beardifier,
            NoiseGeneratorSettings settings,
            Aquifer.FluidPicker fluidPicker,
            Blender blender,
            CallbackInfoReturnable<NoiseChunk> cir
    ) {
        BlueCaveOreVeinFilter.track(cir.getReturnValue(), chunk);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/OreVeinifier;create(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;)Lnet/minecraft/world/level/levelgen/NoiseChunk$BlockStateFiller;"
            ),
            require = 1
    )
    private NoiseChunk.BlockStateFiller changedextras$filterOreVeins(
            DensityFunction veinToggle,
            DensityFunction veinRidged,
            DensityFunction veinGap,
            PositionalRandomFactory oreRandom
    ) {
        return BlueCaveOreVeinFilter.wrap(OreVeinifierInvoker.changedextras$create(
                veinToggle,
                veinRidged,
                veinGap,
                oreRandom
        ));
    }
}
