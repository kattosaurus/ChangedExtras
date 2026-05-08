package com.katt.changedextras.mixin;

import com.katt.changedextras.common.ExoskeletonVisorRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.ExoskeletonRenderer;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ExoskeletonRenderer.VisorLayer.class)
public class ExoskeletonVisorLayerMixin {
    @Shadow @Final private ExoskeletonModel.VisorModel model;

    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/robot/Exoskeleton;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/client/renderer/model/ExoskeletonModel$VisorModel;getTexture(Lnet/ltxprogrammer/changed/entity/robot/Exoskeleton;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            remap = false,
            require = 0
    )
    private net.minecraft.resources.ResourceLocation changedextras$redirectEntityTexture(ExoskeletonModel.VisorModel visorModel, Exoskeleton exoskeleton) {
        return ExoskeletonVisorRenderHelper.resolveEntityTexture(exoskeleton, visorModel.getTexture(exoskeleton));
    }

    @ModifyArgs(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/robot/Exoskeleton;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/client/renderer/model/ExoskeletonModel$VisorModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            remap = false,
            require = 0
    )
    private void changedextras$tintEntityVisor(Args args, Exoskeleton exoskeleton) {
        int color = ExoskeletonVisorRenderHelper.getPrimaryTint(exoskeleton);
        args.set(4, ((color >> 16) & 0xFF) / 255.0F);
        args.set(5, ((color >> 8) & 0xFF) / 255.0F);
        args.set(6, (color & 0xFF) / 255.0F);
    }

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/robot/Exoskeleton;FFFFFF)V",
            at = @At("TAIL"),
            remap = false,
            require = 0
    )
    private void changedextras$renderEntitySecondaryLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Exoskeleton exoskeleton, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!ExoskeletonVisorRenderHelper.shouldRenderSecondaryLayer(exoskeleton)) {
            return;
        }

        int color = ExoskeletonVisorRenderHelper.getSecondaryTint(exoskeleton);
        this.model.renderToBuffer(
                poseStack,
                buffer.getBuffer(this.model.renderType(ExoskeletonVisorRenderHelper.HYPNO_LAYER2)),
                packedLight,
                LivingEntityRenderer.getOverlayCoords(exoskeleton, 0.0F),
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                1.0F
        );
    }
}
