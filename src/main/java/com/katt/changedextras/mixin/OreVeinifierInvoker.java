package com.katt.changedextras.mixin;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OreVeinifier.class)
public interface OreVeinifierInvoker {
    @Invoker("create")
    static NoiseChunk.BlockStateFiller changedextras$create(
            DensityFunction veinToggle,
            DensityFunction veinRidged,
            DensityFunction veinGap,
            PositionalRandomFactory oreRandom
    ) {
        throw new AssertionError();
    }
}
