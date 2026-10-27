package com.katt.changedextras.mixin;

import com.katt.changedextras.worldgen.biome.BlueCaveSurfaceRules;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, index = 5, require = 1)
    private static SurfaceRules.RuleSource changedextras$addBlueCaveSurfaceRule(SurfaceRules.RuleSource original) {
        return BlueCaveSurfaceRules.wrap(original);
    }
}
