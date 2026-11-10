package com.katt.changedextras.client.renderer;

import com.katt.changedextras.client.model.JammerAnimation;
import com.katt.changedextras.client.tracker.ClientJamTracker;
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

/**
 * Plays the Jammer's looping Jam animation on the {@link AdvancedHumanoidModel} while the
 * player is jamming. Mirrors {@link ParryAnimationHelper}.
 */
public class JamAnimationHelper {
    private static final Vector3f ANIMATION_CACHE = new Vector3f();

    @SuppressWarnings("rawtypes")
    private static final JamDriver DRIVER = new JamDriver();

    public static <T extends ChangedEntity> boolean applyAnimation(AdvancedHumanoidModel<T> model, UUID uuid) {
        if (uuid == null) return false;
        if (!ClientJamTracker.isJamming(uuid)) return false;

        try {
            long elapsed = ClientJamTracker.getJamElapsedMillis(uuid);
            DRIVER.setModel(model);
            // The definition is looping, so the elapsed time wraps every 0.75 seconds.
            KeyframeAnimations.animate(DRIVER, JammerAnimation.Jam, elapsed, 1.0F, ANIMATION_CACHE);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static class JamDriver<T extends ChangedEntity> extends HierarchicalModel<T> {
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
                case "LeftLowerLeg" -> {
                    // Jammer-style legs optionally have a lower leg bone under the thigh;
                    // on models without it the channel is skipped harmlessly.
                    ModelPart leg = currentModel.getLeg(HumanoidArm.LEFT);
                    yield (leg != null && leg.hasChild("LeftLowerLeg")) ? leg.getChild("LeftLowerLeg") : null;
                }
                case "Head" -> ((HeadedModel) currentModel).getHead();
                case "Torso" -> ((TorsoedModel) currentModel).getTorso();
                case "RightArm" -> currentModel.getArm(HumanoidArm.RIGHT);
                case "LeftArm" -> currentModel.getArm(HumanoidArm.LEFT);
                default -> null;
            };
            return Optional.ofNullable(part);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
    }
}
