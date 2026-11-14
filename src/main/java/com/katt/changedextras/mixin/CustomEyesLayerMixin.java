package com.katt.changedextras.mixin;

import com.katt.changedextras.Config;
import com.katt.changedextras.client.renderer.ArtistTintManager;
import com.katt.changedextras.client.renderer.EyeBlink;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(CustomEyesLayer.class)
public class CustomEyesLayerMixin {
    /** One model pixel in pose-stack units. Model-part vertices are baked as pixels / 16. */
    private static final float MODEL_PIXEL = 1.0F / 16.0F;

    /**
     * Maximum distance the iris can slide across the sclera, in model pixels.
     * The sclera next to the iris is one pixel wide, so 0.85 pixels keeps the
     * iris within the eye socket without popping out past the outer edge.
     */
    private static final float PUPIL_MAX_HORIZONTAL = 0.85F;
    private static final float PUPIL_YAW_RANGE = 45.0F;

    /**
     * Extra forward offset in blocks along the head front-face normal to eliminate
     * iris-sclera z-fighting by ensuring the iris renders strictly in front of the sclera.
     */
    private static final float IRIS_Z_OFFSET = -0.002F;

    @Shadow(remap = false)
    @Final
    private Map<CustomEyesLayer.HeadShape, ModelPart> shapedHeads;

    @Shadow(remap = false)
    private CustomEyesLayer.HeadShape headShape;

    /** Eye Y scale for the layer currently being rendered. */
    @Unique
    private float changedextras$eyeScaleY = 1.0F;

    /** Scale of the eye head part before this render pass, restored afterwards. */
    @Unique
    private float changedextras$previousYScale = 1.0F;

    /** Whether the pose stack was pushed with a blink centering translation for this renderHead call. */
    @Unique
    private boolean changedextras$pushedBlinkPose = false;

    /** Eye style of the entity currently being rendered. */
    @Unique
    private EyeStyle changedextras$currentEyeStyle = EyeStyle.V2;

    /** Pose of the eye layer currently being rendered, used by the pupil redirects. */
    @Unique
    private PoseStack changedextras$poseStack;

    /** Head-local pupil offset in model pixels, computed from the look direction. */
    @Unique
    private float changedextras$pupilX;

    @Unique
    private float changedextras$getEyeCenterY() {
        boolean tall = this.changedextras$currentEyeStyle == EyeStyle.TALL;
        if (this.headShape == CustomEyesLayer.HeadShape.PUP || this.headShape == CustomEyesLayer.HeadShape.PUP_CONCAVE) {
            return tall ? 0.0F : 0.5F;
        }
        return tall ? -4.0F : -3.5F;
    }

    @Inject(method = "render", at = @At("HEAD"), remap = false, require = 0)
    private void changedextras$beginEyes(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ChangedEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        this.changedextras$currentEyeStyle = EyeStyle.V2;
        if (entity != null) {
            BasicPlayerInfo info = entity.getBasicPlayerInfo();
            if (info != null) {
                if (entity instanceof ArtistEntity && info.getEyeStyle() != EyeStyle.TALL) {
                    info.setEyeStyle(EyeStyle.TALL);
                }
                this.changedextras$currentEyeStyle = info.getEyeStyle();
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float targetPupilX = 0.0F;
        if (Config.pupilTracking) {
            // netHeadYaw describes how far the head is turned away from the body.
            // Shifting the iris in that same direction makes the pupils lead the
            // head turn, like a real eye looking toward the target.
            float yaw = Mth.clamp(netHeadYaw, -PUPIL_YAW_RANGE, PUPIL_YAW_RANGE) / PUPIL_YAW_RANGE;
            targetPupilX = -yaw * PUPIL_MAX_HORIZONTAL;
        }

        EyeBlink.EyeResult result = EyeBlink.update(entity, ageInTicks, targetPupilX);
        this.changedextras$eyeScaleY = result.scaleY;
        this.changedextras$pupilX = result.pupilX;

        this.changedextras$poseStack = poseStack;
    }

    @Inject(method = "render", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$endEyes(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ChangedEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (this.changedextras$pushedBlinkPose) {
            this.changedextras$pushedBlinkPose = false;
            poseStack.popPose();
        }

        ModelPart head = this.shapedHeads.get(this.headShape);
        if (head != null && this.changedextras$previousYScale != 0.0F) {
            head.yScale = this.changedextras$previousYScale;
        }

        this.changedextras$eyeScaleY = 1.0F;
        this.changedextras$pupilX = 0.0F;
        this.changedextras$poseStack = null;
        this.changedextras$currentEyeStyle = EyeStyle.V2;

        Integer tint = ArtistTintManager.getTint(entity.getId());
        if (tint == null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }

        float red = ((tint >> 16) & 0xFF) / 255.0F;
        float green = ((tint >> 8) & 0xFF) / 255.0F;
        float blue = (tint & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }

    /**
     * Skips Changed's hard texture-swap blink while the smooth Y-scale blink is active;
     * the smooth blink replaces it for every entity. When the client option is disabled
     * this falls back to Changed's normal blink.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/entity/ChangedEntity;isBlinking()Z"), remap = false, require = 0)
    private boolean changedextras$suppressTextureBlink(ChangedEntity entity) {
        return !Config.smoothBlinking && entity.isBlinking();
    }

    /**
     * Centers the vertical blink scale on the eye row. Scaling head.yScale alone
     * squashes toward the neck pivot (y = 0), pulling the eyes 3 to 4 pixels down
     * the face toward the chin as they close. Translating the pose stack by
     * {@code eyeCenterY * (1 - scale)} counteracts this downward drift and closes
     * the eyelid in place.
     */
    @Inject(method = "renderHead", at = @At("HEAD"), remap = false, require = 0)
    private void changedextras$applyEyeBlink(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Color3 color, float alpha, CallbackInfo ci) {
        if (this.changedextras$eyeScaleY >= 0.999F) {
            return;
        }

        ModelPart head = this.shapedHeads.get(this.headShape);
        if (head == null) {
            return;
        }

        float eyeCenterY = this.changedextras$getEyeCenterY();
        float blinkYShift = eyeCenterY * (1.0F - this.changedextras$eyeScaleY) * MODEL_PIXEL;
        Vector3f blinkOffset = new Vector3f(0.0F, blinkYShift, 0.0F)
                .rotateX(head.xRot)
                .rotateY(head.yRot)
                .rotateZ(head.zRot);

        poseStack.pushPose();
        poseStack.translate(blinkOffset.x, blinkOffset.y, blinkOffset.z);
        this.changedextras$pushedBlinkPose = true;

        this.changedextras$previousYScale = head.yScale;
        head.yScale = this.changedextras$previousYScale * this.changedextras$eyeScaleY;
    }

    @Inject(method = "renderHead", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$resetEyeBlink(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Color3 color, float alpha, CallbackInfo ci) {
        if (this.changedextras$pushedBlinkPose) {
            this.changedextras$pushedBlinkPose = false;
            poseStack.popPose();

            ModelPart head = this.shapedHeads.get(this.headShape);
            if (head != null) {
                head.yScale = this.changedextras$previousYScale;
            }
        }
    }

    /**
     * The left iris is rendered through the first of the two iris {@code ifPresent}
     * calls in {@code render}. Wrapping that call lets the iris box slide while the
     * sclera, brows and lashes stay pinned to the head.
     */
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", ordinal = 2),
            remap = false, require = 0)
    private void changedextras$shiftLeftPupil(Optional<?> optional, Consumer<?> consumer) {
        this.changedextras$renderIris(optional, consumer, true);
    }

    /** Mirrors {@link #changedextras$shiftLeftPupil} for the right iris call. */
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", ordinal = 3),
            remap = false, require = 0)
    private void changedextras$shiftRightPupil(Optional<?> optional, Consumer<?> consumer) {
        this.changedextras$renderIris(optional, consumer, false);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void changedextras$renderIris(Optional optional, Consumer consumer, boolean isLeft) {
        ModelPart head = this.shapedHeads.get(this.headShape);
        PoseStack pose = this.changedextras$poseStack;
        if (head == null || pose == null) {
            optional.ifPresent(consumer);
            return;
        }

        // Clamp the horizontal travel for each iris so it can ONLY slide across its
        // own sclera, never crossing the inner boundary onto the bridge of the nose
        // or exceeding the outer sclera edge.
        float clampedX = isLeft
                ? Mth.clamp(this.changedextras$pupilX, 0.0F, PUPIL_MAX_HORIZONTAL)
                : Mth.clamp(this.changedextras$pupilX, -PUPIL_MAX_HORIZONTAL, 0.0F);

        // Rotate the head-local offset into model space so it still slides across
        // the face when the head itself is turned or tilted. Moving slightly forward
        // along the front face normal (-Z) prevents z-fighting with the sclera.
        Vector3f offset = new Vector3f(clampedX * MODEL_PIXEL, 0.0F, IRIS_Z_OFFSET)
                .rotateX(head.xRot)
                .rotateY(head.yRot)
                .rotateZ(head.zRot);

        pose.pushPose();
        try {
            pose.translate(offset.x, offset.y, offset.z);
            optional.ifPresent(consumer);
        } finally {
            pose.popPose();
        }
    }
}
