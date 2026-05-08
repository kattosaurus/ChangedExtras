package com.katt.changedextras.mixin;

import com.katt.changedextras.common.ExoskeletonVisorRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.accessory.WornExoskeletonRenderer;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WornExoskeletonRenderer.class)
public abstract class WornExoskeletonRendererMixin {
    @Shadow @Final private Cacheable<ExoskeletonModel.VisorModel> suitVisorModel;

    @Redirect(
            method = "render(Lnet/ltxprogrammer/changed/data/AccessorySlotContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/MultiBufferSource;IFFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/client/renderer/model/ExoskeletonModel$VisorModel;getTexture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            remap = false,
            require = 0
    )
    private <T extends LivingEntity, M extends EntityModel<T>> net.minecraft.resources.ResourceLocation changedextras$redirectWornTexture(ExoskeletonModel.VisorModel visorModel, LivingEntity wearer, net.minecraft.world.item.ItemStack stack, AccessorySlotContext<T> context, PoseStack poseStack, RenderLayerParent<T, M> parent, MultiBufferSource buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        return ExoskeletonVisorRenderHelper.resolveWornTexture(wearer, stack, visorModel.getTexture(wearer, stack));
    }

    @ModifyArgs(
            method = "render(Lnet/ltxprogrammer/changed/data/AccessorySlotContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/MultiBufferSource;IFFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/client/renderer/model/ExoskeletonModel$VisorModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            remap = false,
            require = 0
    )
    private <T extends LivingEntity, M extends EntityModel<T>> void changedextras$tintWornVisor(Args args, AccessorySlotContext<T> context, PoseStack poseStack, RenderLayerParent<T, M> parent, MultiBufferSource buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        int color = ExoskeletonVisorRenderHelper.getPrimaryTint(context.wearer(), context.stack());
        args.set(4, ((color >> 16) & 0xFF) / 255.0F);
        args.set(5, ((color >> 8) & 0xFF) / 255.0F);
        args.set(6, (color & 0xFF) / 255.0F);

        // Force the vertex consumer to use the resolved texture (hypnosis) when applicable.
        try {
            ExoskeletonModel.VisorModel visorModel = this.suitVisorModel.getOrThrow();
            java.util.ResourceBundle rb = null; // no-op to keep try block happy
            net.minecraft.resources.ResourceLocation resolved = ExoskeletonVisorRenderHelper.resolveWornTexture(context.wearer(), context.stack(), visorModel.getTexture(context.wearer(), context.stack()));
            args.set(1, buffer.getBuffer(visorModel.renderType(resolved)));
        } catch (Throwable ignored) {
        }
    }

    @Inject(
            method = "render(Lnet/ltxprogrammer/changed/data/AccessorySlotContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/MultiBufferSource;IFFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            ),
            remap = false,
            require = 0
    )
    private <T extends LivingEntity, M extends EntityModel<T>> void changedextras$renderWornSecondaryLayer(AccessorySlotContext<T> context, PoseStack poseStack, RenderLayerParent<T, M> parent, MultiBufferSource buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!ExoskeletonVisorRenderHelper.shouldRenderSecondaryLayer(context.wearer(), context.stack())) {
            return;
        }

        ExoskeletonModel.VisorModel visorModel = this.suitVisorModel.getOrThrow();
        visorModel.matchParentAnim(((WornExoskeletonRenderer)(Object)this).getModel());
        int color = ExoskeletonVisorRenderHelper.getSecondaryTint(context.wearer(), context.stack());
        visorModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(visorModel.renderType(ExoskeletonVisorRenderHelper.HYPNO_LAYER2)),
                packedLight,
                LivingEntityRenderer.getOverlayCoords(context.wearer(), 0.0F),
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                1.0F
        );
    }
}