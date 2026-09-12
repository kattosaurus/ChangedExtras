package com.katt.changedextras.mixin;

import com.katt.changedextras.client.ClientParryTracker;
import com.katt.changedextras.common.LatexCuddleHelper;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AdvancedHumanoidModel.class)
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> {
    @Inject(method = "setupAnim", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$applyCuddlePose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!LatexCuddleHelper.shouldCuddle(entity)) {
            return;
        }

        try {
            AdvancedHumanoidModel<T> model = (AdvancedHumanoidModel<T>)(Object)this;
            ModelPart head = ((HeadedModel)model).getHead();
            ModelPart torso = ((TorsoedModel)model).getTorso();
            ModelPart rightArm = model.getArm(HumanoidArm.RIGHT);
            ModelPart leftArm = model.getArm(HumanoidArm.LEFT);
            ModelPart rightLeg = model.getLeg(HumanoidArm.RIGHT);
            ModelPart leftLeg = model.getLeg(HumanoidArm.LEFT);
            if (head == null || torso == null || rightArm == null || leftArm == null || rightLeg == null || leftLeg == null) {
                return;
            }

            float rollWave = (float)Math.sin(ageInTicks * 0.22F) * 0.08F;
            torso.zRot = 1.15F + rollWave;
            torso.xRot = 0.2F;
            torso.yRot = 0.0F;

            head.xRot = 0.35F;
            head.yRot = -0.18F + rollWave * 0.3F;
            head.zRot = 1.05F + rollWave * 0.5F;

            rightArm.xRot = -1.4F;
            rightArm.yRot = -0.2F;
            rightArm.zRot = 0.45F;
            leftArm.xRot = -1.15F;
            leftArm.yRot = 0.3F;
            leftArm.zRot = -0.3F;

            rightLeg.xRot = -1.25F + rollWave;
            rightLeg.yRot = 0.1F;
            rightLeg.zRot = 0.22F;
            leftLeg.xRot = -1.45F - rollWave;
            leftLeg.yRot = -0.08F;
            leftLeg.zRot = -0.18F;
        } catch (ClassCastException | NullPointerException ignored) {
        }
    }

    @Inject(method = "setupAnim", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$applyParryPose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity == null) {
            return;
        }

        UUID uuid = entity.getUnderlyingPlayer() != null ? entity.getUnderlyingPlayer().getUUID() : entity.getUUID();
        if (uuid == null) {
            return;
        }

        boolean counterattacking = ClientParryTracker.isCounterattacking(uuid);
        boolean parrying = ClientParryTracker.isParrying(uuid);

        if (!counterattacking && !parrying) {
            return;
        }

        try {
            AdvancedHumanoidModel<T> model = (AdvancedHumanoidModel<T>)(Object)this;
            ModelPart rightArm = model.getArm(HumanoidArm.RIGHT);
            ModelPart leftArm = model.getArm(HumanoidArm.LEFT);
            if (rightArm == null || leftArm == null) {
                return;
            }

            if (counterattacking) {
                // Left fist counter punch forward
                leftArm.xRot = -1.55F;
                leftArm.yRot = 0.25F;
                leftArm.zRot = -0.1F;

                // Right arm tucked back
                rightArm.xRot = -0.7F;
                rightArm.yRot = -0.3F;
                rightArm.zRot = 0.4F;
            } else if (parrying) {
                // Right fist raised in front for blocking guard
                rightArm.xRot = -1.45F;
                rightArm.yRot = -0.55F;
                rightArm.zRot = 0.55F;

                // Left arm lower guard
                leftArm.xRot = -0.35F;
                leftArm.yRot = 0.35F;
                leftArm.zRot = -0.2F;
            }
        } catch (Throwable ignored) {
        }
    }
}
