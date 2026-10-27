package com.katt.changedextras.client.renderer;

import com.katt.changedextras.client.tracker.ClientDomainTracker;
import com.katt.changedextras.client.tracker.ClientParryTracker;
import com.katt.changedextras.client.model.KattAnimation;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class ParryAnimationHelper {
    private static final Vector3f ANIMATION_CACHE = new Vector3f();

    @SuppressWarnings("rawtypes")
    private static final ParryDriver DRIVER = new ParryDriver();

    public static <T extends ChangedEntity> boolean applyAnimation(AdvancedHumanoidModel<T> model, UUID uuid) {
        if (uuid == null) return false;

        try {
            if (ClientParryTracker.isCounterattacking(uuid)) {
                long elapsed = ClientParryTracker.getCounterattackElapsedMillis(uuid);
                DRIVER.setModel(model);
                KeyframeAnimations.animate(DRIVER, KattAnimation.parry_successful, elapsed, 1.0F, ANIMATION_CACHE);
                return true;
            } else if (ClientParryTracker.isParrying(uuid)) {
                long elapsed = ClientParryTracker.getParryElapsedMillis(uuid);
                DRIVER.setModel(model);
                KeyframeAnimations.animate(DRIVER, KattAnimation.parry_start, elapsed, 1.0F, ANIMATION_CACHE);
                return true;
            }
        } catch (Throwable ignored) {}

        return false;
    }

    /**
     * Plays the caster's domain-open animation while the domain intro is running. The animation
     * runs once (1s) and then holds its final frame until the intro ends (the white overlay is
     * dismissed and everyone is unfrozen).
     */
    public static <T extends ChangedEntity> boolean applyDomainAnimation(AdvancedHumanoidModel<T> model, UUID uuid) {
        if (uuid == null) return false;
        if (!ClientDomainTracker.isDomainActive()) return false;
        if (!ClientDomainTracker.isShowOverlay()) return false;

        UUID caster = ClientDomainTracker.getCasterUUID();
        if (caster == null || !caster.equals(uuid)) return false;

        try {
            long elapsed = ClientDomainTracker.getDomainAnimationElapsedMs();
            DRIVER.setModel(model);
            KeyframeAnimations.animate(DRIVER, KattAnimation.domain_open, elapsed, 1.0F, ANIMATION_CACHE);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static class ParryDriver<T extends ChangedEntity> extends HierarchicalModel<T> {
        private AdvancedHumanoidModel<T> currentModel;

        public void setModel(AdvancedHumanoidModel<T> model) {
            this.currentModel = model;
        }

        @Override
        public ModelPart root() {
            return currentModel != null ? ((TorsoedModel) currentModel).getTorso() : null;
        }

        @Override
        public Optional<ModelPart> getAnyDescendantWithName(String name) {
            if (currentModel == null) return Optional.empty();
            ModelPart part = switch (name) {
                case "RightLeg" -> currentModel.getLeg(HumanoidArm.RIGHT);
                case "LeftLeg" -> currentModel.getLeg(HumanoidArm.LEFT);
                case "Head" -> ((HeadedModel) currentModel).getHead();
                case "Torso" -> ((TorsoedModel) currentModel).getTorso();
                case "RightArm" -> currentModel.getArm(HumanoidArm.RIGHT);
                case "LeftArm" -> currentModel.getArm(HumanoidArm.LEFT);
                case "Sleeve" -> {
                    ModelPart arm = currentModel.getArm(HumanoidArm.RIGHT);
                    yield (arm != null && arm.hasChild("Sleeve")) ? arm.getChild("Sleeve") : null;
                }
                default -> null;
            };
            return Optional.ofNullable(part);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
    }
}
