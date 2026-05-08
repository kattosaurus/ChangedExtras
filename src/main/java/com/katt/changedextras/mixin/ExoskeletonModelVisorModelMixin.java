package com.katt.changedextras.mixin;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.ExoskeletonVisorRenderHelper;
import com.katt.changedextras.common.ExoskeletonVisorStyle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Provide a client-side short-circuit so that when our mod marks a player as
 * 'fooled' or their persistent visorStyle is 'hypnosis', the visor texture
 * resolves to the hypnosis textures immediately. This avoids relying on
 * ProcessTransfur state being in perfect sync.
 */
@Mixin(targets = "net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel$VisorModel")
public class ExoskeletonModelVisorModelMixin {

    @Inject(method = "getTexture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void changedextras$overrideWornTexture(LivingEntity wearer, ItemStack stack, CallbackInfoReturnable<ResourceLocation> cir) {
        try {
            if (wearer != null) {
                boolean fooled = wearer.getPersistentData().getBoolean("changedextras.fooled_wolf");
                String style = wearer.getPersistentData().getString(ExoskeletonVisorStyle.PATTERN_TAG);
                if (fooled || "hypnosis".equalsIgnoreCase(style)) {
                    ChangedExtras.LOGGER.info("VisorModel mixin: override worn={} style={}", fooled, style);
                    int tick = wearer.tickCount;
                    ResourceLocation tex = (tick % 12 < 6) ? ExoskeletonVisorRenderHelper.HYPNO_LAYER1 : ExoskeletonVisorRenderHelper.HYPNO_LAYER2;
                    cir.setReturnValue(tex);
                }
            }
        } catch (Throwable ignored) {
            // don't crash rendering
        }
    }

    @Inject(method = "getTexture(Lnet/ltxprogrammer/changed/entity/robot/Exoskeleton;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void changedextras$overrideEntityTexture(Exoskeleton exoskeleton, CallbackInfoReturnable<ResourceLocation> cir) {
        try {
            if (exoskeleton != null) {
                // Exoskeleton entities may carry the visor style in their changedextras holder
                boolean fooled = false;
                try {
                    fooled = exoskeleton.getPersistentData().getBoolean("changedextras.fooled_wolf");
                } catch (Throwable ignored) {}
                String style = exoskeleton.getPersistentData().getString(ExoskeletonVisorStyle.PATTERN_TAG);
                if (fooled || "hypnosis".equalsIgnoreCase(style)) {
                    ChangedExtras.LOGGER.info("VisorModel mixin: override entity={} style={}", fooled, style);
                    int tick = exoskeleton.tickCount;
                    ResourceLocation tex = (tick % 12 < 6) ? ExoskeletonVisorRenderHelper.HYPNO_LAYER1 : ExoskeletonVisorRenderHelper.HYPNO_LAYER2;
                    cir.setReturnValue(tex);
                }
            }
        } catch (Throwable ignored) {
            // swallow
        }
    }
}
