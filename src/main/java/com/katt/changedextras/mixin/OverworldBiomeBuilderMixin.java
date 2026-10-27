package com.katt.changedextras.mixin;

import com.katt.changedextras.worldgen.biome.ModBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraftforge.data.loading.DatagenModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public abstract class OverworldBiomeBuilderMixin {
    @ModifyArg(
            method = "addUndergroundBiomes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/OverworldBiomeBuilder;addBottomBiome(Ljava/util/function/Consumer;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;FLnet/minecraft/resources/ResourceKey;)V"
            ),
            index = 5,
            require = 1
    )
    private Climate.Parameter changedextras$splitDeepDarkWeirdness(Climate.Parameter weirdness) {
        if (DatagenModLoader.isRunningDataGen()) {
            return weirdness;
        }
        return Climate.Parameter.span(-1.0F, 0.0F);
    }

    @Inject(method = "addUndergroundBiomes", at = @At("TAIL"), require = 1)
    private void changedextras$addBlueCave(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, CallbackInfo ci) {
        if (DatagenModLoader.isRunningDataGen()) {
            return;
        }
        consumer.accept(Pair.of(Climate.parameters(
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-0.8F, -0.375F),
                Climate.Parameter.span(0.2F, 1.1F),
                Climate.Parameter.span(0.0F, 0.5F),
                0.0F
        ), ModBiomes.BLUE_CAVE));
    }
}
