package us.kattosaur.changedextras.client.renderer;

import us.kattosaur.changedextras.Config;
import us.kattosaur.changedextras.client.model.GlobalAnimations;
import us.kattosaur.changedextras.common.combat.AttackComboTracker;
import us.kattosaur.changedextras.ability.GrabReachAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Replays the shared {@link GlobalAnimations} on bipedal (anthro) latex models.
 *
 * <p>The clips are applied after the model's preset animator has run, and only the bones
 * touched by a clip are reset first. That means these definitions <em>replace</em> the
 * preset idle/attack/grab/tail motion for those bones, while special states
 * (crouching, swimming, riding, sleeping, flying) are left entirely to the presets.
 * Non-bipedal shapes (taurs, nagas, merfolk, ferals) only receive tail animations, since
 * their body rigs are incompatible with the shared bipedal clips.</p>
 *
 * <p>Selection:</p>
 * <ul>
 *     <li>Grabbing: {@code GrabIdle} while actively holding someone; it stops once the
 *     target is suited/absorbed. {@code Attack2} plays while reaching.</li>
 *     <li>Attacking: the combo cycles {@code Attack}, {@code Attack1}, {@code Attack2}.</li>
 *     <li>Moving: {@code IdleBody} for the upper body, leaving the arms and legs to the
 *     preset's vanilla walk animation; otherwise {@code Idle} for the whole upper body.
 *     Neither clip has arm position channels, so no global layer moves the arms vertically.</li>
 *     <li>Tail: {@code changed_addon:no_tail_anim} variants keep their tail at rest;
 *     {@code changed_addon:cat_like} and {@code changed_addon:leopard_like} use
 *     {@code CatTail}; everything else (including {@code changed_addon:wolf_like} and
 *     untagged variants) uses {@code WolfTail}.</li>
 * </ul>
 */
public final class GlobalAnimationHelper {
    private static final Vector3f ANIMATION_CACHE = new Vector3f();

    private static final ResourceKey<Registry<TransfurVariant<?>>> VARIANT_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("changed", "latex_variant"));
    private static final TagKey<TransfurVariant<?>> CAT_LIKE =
            TagKey.create(VARIANT_REGISTRY, ResourceLocation.fromNamespaceAndPath("changed_addon", "cat_like"));
    private static final TagKey<TransfurVariant<?>> LEOPARD_LIKE =
            TagKey.create(VARIANT_REGISTRY, ResourceLocation.fromNamespaceAndPath("changed_addon", "leopard_like"));
    private static final TagKey<TransfurVariant<?>> WOLF_LIKE =
            TagKey.create(VARIANT_REGISTRY, ResourceLocation.fromNamespaceAndPath("changed_addon", "wolf_like"));
    private static final TagKey<TransfurVariant<?>> NO_TAIL_ANIM =
            TagKey.create(VARIANT_REGISTRY, ResourceLocation.fromNamespaceAndPath("changed_addon", "no_tail_anim"));

    private static final GlobalDriver DRIVER = new GlobalDriver();

    private static final String[] TAIL_PARTS = {"TailPrimary", "TailSecondary", "TailTertiary", "TailQuaternary"};

    private static final String[] LEG_PARTS = {
            "RightLowerLeg", "RightFoot", "RightPad", "LeftLowerLeg", "LeftFoot", "LeftPad"
    };

    private static final float ATTACK_LENGTH_SECONDS = 0.2083F;
    private static final float ATTACK1_LENGTH_SECONDS = 0.2083F;
    private static final float ATTACK2_LENGTH_SECONDS = 0.5F;

    /**
     * Response time of the grab body aim. The extra body yaw eases toward the
     * look direction instead of snapping, so looking around while holding a grab
     * stays smooth.
     */
    private static final float GRAB_AIM_RESPONSE_SECONDS = 0.08F;

    private static final Map<UUID, GrabAim> GRAB_AIM_STATES = new HashMap<>();

    /** Below this movement speed the entity counts as idle and gets the full idle clip. */
    private static final float MOVING_SPEED_THRESHOLD = 0.05F;

    private GlobalAnimationHelper() {
    }

    /**
     * @param legacyCatTail retained for call-site compatibility; tail selection is now
     *                      driven by the entity's variant tags instead
     */
    public static <T extends ChangedEntity> void apply(AdvancedHumanoidModel<T> model, T entity,
                                                       float limbSwing, float limbSwingAmount, float ageInTicks,
                                                       float netHeadYaw, float headPitch, boolean legacyCatTail) {
        if (!Config.globalAnimations) {
            return;
        }

        HumanoidAnimator<T, ?> animator = model.getAnimator(entity);
        if (animator == null) {
            return;
        }

        if (!isBipedalGlobal(entity)) {
            applyTailOnly(model, entity, ageInTicks);
            return;
        }

        if (isSpecialState(model, entity, animator)) {
            return;
        }

        try {
            DRIVER.setModel(model);

            List<Clip> clips = new ArrayList<>(3);
            long elapsedMillis = (long) (ageInTicks * 50.0F);
            boolean headFollowsLook = false;
            boolean grabbing = false;
            boolean moving = false;

            GrabEntityAbilityInstance grab = AbstractAbility.getAbilityInstance(
                    entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            boolean holding = false;
            boolean reaching = false;
            long grabElapsed = 0L;
            boolean reworkedCombat = Config.latexReworkedCombat;

            if (grab != null) {
                // Changed keeps grabbedEntity set after the target is suited/absorbed,
                // but its own animator only holds the grab pose while this is true.
                holding = grab.shouldAnimateArms();
                if (!holding && reworkedCombat) {
                    if (grab instanceof GrabReachAccess reach) {
                        if (reach.changedextras$isGrabReaching()) {
                            reaching = true;
                            int remaining = reach.changedextras$getGrabReachTicks();
                            float progress = 1.0F - (float) remaining / (float) GrabReachAccess.REACH_TICKS;
                            grabElapsed = Math.max(0L, (long) (Mth.clamp(progress, 0.0F, 1.0F)
                                    * ATTACK2_LENGTH_SECONDS * 1000.0F));
                        }
                    } else if (animator.grabState == AdvancedHumanoidModel.GrabState.REACH) {
                        reaching = true;
                        grabElapsed = (long) (animator.reachOut * ATTACK2_LENGTH_SECONDS * 1000.0F);
                    }
                }
            } else if (animator.grabState == AdvancedHumanoidModel.GrabState.HOLD) {
                holding = true;
            } else if (animator.grabState == AdvancedHumanoidModel.GrabState.REACH && reworkedCombat) {
                reaching = true;
                grabElapsed = (long) (animator.reachOut * ATTACK2_LENGTH_SECONDS * 1000.0F);
            }

            if (holding) {
                clips.add(new Clip(GlobalAnimations.GrabIdle, 0L));
                grabbing = true;
            } else if (reaching) {
                // The reach uses the Attack2 strike so grabbing matches the finisher.
                clips.add(new Clip(GlobalAnimations.Attack2, Math.max(0L, grabElapsed)));
                grabbing = true;
            } else {
                boolean bareHanded = entity.getMainHandItem().isEmpty();
                AttackComboTracker.ClientAttack attack = bareHanded && reworkedCombat
                        ? AttackComboTracker.clientAttack(entity, ageInTicks)
                        : null;

                if (attack != null && attack.spam()) {
                    // Spam clicking falls back to the preset's vanilla animations and
                    // never reaches the boosted finisher.
                    return;
                }

                if (attack != null
                        && attack.combo() == AttackComboTracker.COMBO_SIZE - 1
                        && attack.elapsedMillis() < (long) (ATTACK2_LENGTH_SECONDS * 1000.0F)) {
                    // The finisher is longer than the vanilla swing, so it keeps playing
                    // from its own timer instead of attackTime.
                    clips.add(new Clip(GlobalAnimations.Attack2, attack.elapsedMillis()));
                } else if (model.attackTime > 0.0F) {
                    if (!bareHanded || !reworkedCombat) {
                        // Item swings (and every swing when the reworked combat config
                        // is off) use the preset's own animation. Only the upper body
                        // is layered here so the preset arm swing stays visible.
                        clips.add(new Clip(GlobalAnimations.IdleBody, elapsedMillis));
                        headFollowsLook = true;
                    } else if (attack != null && attack.combo() == 1) {
                        clips.add(new Clip(GlobalAnimations.Attack1, scaledMillis(model.attackTime, ATTACK1_LENGTH_SECONDS)));
                    } else {
                        clips.add(new Clip(GlobalAnimations.Attack, scaledMillis(model.attackTime, ATTACK_LENGTH_SECONDS)));
                    }
                } else {
                    moving = Math.abs(limbSwingAmount) > MOVING_SPEED_THRESHOLD;
                    clips.add(new Clip(moving ? GlobalAnimations.IdleBody : GlobalAnimations.Idle,
                            elapsedMillis));
                    headFollowsLook = true;
                }
            }

            TailAnimation tail = selectTailAnimation(entity);
            if (tail != TailAnimation.NONE) {
                clips.add(new Clip(tail == TailAnimation.CAT ? GlobalAnimations.CatTail : GlobalAnimations.WolfTail,
                        elapsedMillis));
            }

            // Reset every bone that any selected clip touches before replaying them, so the
            // clips always start from the model's rest pose instead of stacking on each other.
            // Preserve preset positions for bones that no selected clip moves positionally, to
            // avoid snapping them (e.g. arms) away from the model/animator's intended placement.
            Map<ModelPart, Boolean> resetParts = new HashMap<>();
            for (Clip clip : clips) {
                for (Map.Entry<String, List<AnimationChannel>> entry : clip.definition.boneAnimations().entrySet()) {
                    boolean hasPosition = hasPositionChannel(entry.getValue());
                    DRIVER.getAnyDescendantWithName(entry.getKey()).ifPresent(part ->
                            resetParts.merge(part, hasPosition, Boolean::logicalOr));
                }
            }
            if (tail == TailAnimation.NONE) {
                // No tail clip means the preset tail motion must be cleared too, otherwise
                // the tail would still swing from the model's own animator.
                for (String name : TAIL_PARTS) {
                    DRIVER.getAnyDescendantWithName(name).ifPresent(part -> resetParts.putIfAbsent(part, false));
                }
            }
            for (Map.Entry<ModelPart, Boolean> entry : resetParts.entrySet()) {
                ModelPart part = entry.getKey();
                float x = part.x;
                float y = part.y;
                float z = part.z;
                part.resetPose();
                if (!entry.getValue()) {
                    part.x = x;
                    part.y = y;
                    part.z = z;
                }
            }

            for (Clip clip : clips) {
                KeyframeAnimations.animate(DRIVER, clip.definition, clip.elapsedMillis, 1.0F, ANIMATION_CACHE);
            }

            if (headFollowsLook) {
                ModelPart head = model.getHead();
                if (head != null) {
                    head.yRot += (float) Math.toRadians(netHeadYaw);
                    head.xRot += (float) Math.toRadians(headPitch);
                }
            }

            if (grabbing) {
                // The renderer turns the whole body toward the look direction while
                // grabbing, so the head must not reapply its yaw or the model would
                // turn twice and still miss the target.
                ModelPart head = model.getHead();
                if (head != null) {
                    head.yRot = 0.0F;
                }
            }
        } catch (Throwable ignored) {
            // Rendering must never crash because of an optional animation layer.
        }
    }

    private static <T extends ChangedEntity> void applyTailOnly(AdvancedHumanoidModel<T> model, T entity,
                                                                float ageInTicks) {
        try {
            DRIVER.setModel(model);
            TailAnimation tail = selectTailAnimation(entity);
            if (tail == TailAnimation.NONE) {
                for (String name : TAIL_PARTS) {
                    DRIVER.getAnyDescendantWithName(name).ifPresent(part -> resetPartPose(part, false));
                }
                return;
            }

            AnimationDefinition tailClip = tail == TailAnimation.CAT ? GlobalAnimations.CatTail : GlobalAnimations.WolfTail;
            for (Map.Entry<String, List<AnimationChannel>> entry : tailClip.boneAnimations().entrySet()) {
                boolean hasPosition = hasPositionChannel(entry.getValue());
                DRIVER.getAnyDescendantWithName(entry.getKey()).ifPresent(part -> resetPartPose(part, hasPosition));
            }
            long elapsedMillis = (long) (ageInTicks * 50.0F);
            KeyframeAnimations.animate(DRIVER, tailClip, elapsedMillis, 1.0F, ANIMATION_CACHE);
        } catch (Throwable ignored) {
            // Rendering must never crash because of an optional animation layer.
        }
    }

    private static void resetPartPose(ModelPart part, boolean keepAnimatedPosition) {
        float x = part.x;
        float y = part.y;
        float z = part.z;
        part.resetPose();
        if (!keepAnimatedPosition) {
            part.x = x;
            part.y = y;
            part.z = z;
        }
    }

    private static boolean isBipedalGlobal(ChangedEntity entity) {
        if (entity == null) {
            return false;
        }
        return entity.getEntityShape() == EntityShape.ANTHRO;
    }

    /**
     * Applies the shared animation layer to any Changed model, including models from the
     * main Changed mod and other addons. The tail clip follows the entity variant's tags.
     */
    public static <T extends ChangedEntity> void applyGlobal(AdvancedHumanoidModel<T> model, T entity,
                                                             float limbSwing, float limbSwingAmount, float ageInTicks,
                                                             float netHeadYaw, float headPitch) {
        apply(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, false);
    }

    /** Whether the model already applies this layer itself (a model from this mod). */
    public static boolean isManagedModel(AdvancedHumanoidModel<?> model) {
        return model.getClass().getName().startsWith("us.kattosaur.changedextras.");
    }

    /**
     * Whether the entity is currently reaching out to grab or actively holding a
     * grabbed entity. Once the target is suited/absorbed this is false, so the renderer
     * stops aiming the whole body and the head looks normally again.
     */
    public static boolean isGrabAiming(ChangedEntity entity) {
        if (entity == null) {
            return false;
        }

        GrabEntityAbilityInstance grab = AbstractAbility.getAbilityInstance(
                entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grab == null) {
            return false;
        }
        if (grab.shouldAnimateArms()) {
            return true;
        }
        return grab instanceof GrabReachAccess reach && reach.changedextras$isGrabReaching();
    }

    /**
     * Turns the whole model toward the entity's head yaw while grabbing. The extra
     * yaw eases in so start/stop and fast mouse movement stay smooth instead of
     * snapping. The head itself has its yaw cleared by {@link #apply} so this turn
     * is not applied twice.
     *
     * <p>The aim is skipped whenever the shared layer is suppressed for a special
     * pose (crouching, swimming, riding, sleeping, flying), because in those states
     * the preset animator already applies the head yaw and aiming the body as well
     * would turn the head twice.</p>
     */
    public static void applyGrabBodyAim(AdvancedHumanoidModel<?> model, ChangedEntity entity, PoseStack poseStack,
                                        float bodyYaw, float partialTick) {
        if (entity == null) {
            return;
        }

        HumanoidAnimator<?, ?> animator = grabAimAnimator(model, entity);
        if (!Config.globalAnimations || !isBipedalGlobal(entity) || animator == null
                || isSpecialState(model, entity, animator) || !isGrabAiming(entity)) {
            GRAB_AIM_STATES.remove(entity.getUUID());
            return;
        }

        float headYaw = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float target = Mth.wrapDegrees(headYaw - bodyYaw);

        long now = System.nanoTime();
        GrabAim state = GRAB_AIM_STATES.computeIfAbsent(entity.getUUID(), ignored -> new GrabAim(now));
        float elapsed = Mth.clamp((now - state.updatedAt) / 1.0E9F, 0.0F, 0.1F);
        state.updatedAt = now;

        float alpha = 1.0F - (float) Math.exp(-elapsed / GRAB_AIM_RESPONSE_SECONDS);
        state.yaw += Mth.wrapDegrees(target - state.yaw) * alpha;

        // Vanilla's setupRotations uses (180 - bodyYaw); adding -yaw turns the
        // model from its body yaw toward the head/look yaw.
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yaw));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static HumanoidAnimator<?, ?> grabAimAnimator(AdvancedHumanoidModel<?> model, ChangedEntity entity) {
        return model == null ? null : ((AdvancedHumanoidModel) model).getAnimator(entity);
    }

    private static final class GrabAim {
        private float yaw;
        private long updatedAt;

        private GrabAim(long updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    /**
     * Tail selection is tag-driven. {@code changed_addon:no_tail_anim} takes priority so
     * variants like the kaiju keep their tail still; then {@code cat_like} and
     * {@code leopard_like} get the feline tail, while {@code wolf_like} and untagged
     * variants get the canine tail.
     */
    private static TailAnimation selectTailAnimation(ChangedEntity entity) {
        TransfurVariant<?> variant = entity == null ? null : entity.getSelfVariant();
        if (variant == null) {
            return TailAnimation.WOLF;
        }
        if (variant.is(NO_TAIL_ANIM)) {
            return TailAnimation.NONE;
        }
        if (variant.is(LEOPARD_LIKE) || variant.is(CAT_LIKE)) {
            return TailAnimation.CAT;
        }
        if (variant.is(WOLF_LIKE)) {
            return TailAnimation.WOLF;
        }
        return TailAnimation.WOLF;
    }

    private enum TailAnimation {
        NONE, CAT, WOLF
    }

    private static boolean hasPositionChannel(List<AnimationChannel> channels) {
        for (AnimationChannel channel : channels) {
            if (channel.target() == AnimationChannel.Targets.POSITION) {
                return true;
            }
        }
        return false;
    }

    private static long scaledMillis(float progress, float lengthSeconds) {
        return Math.max(0L, (long) (Math.min(progress, 1.0F) * lengthSeconds * 1000.0F));
    }

    private static boolean isSpecialState(AdvancedHumanoidModel<?> model, ChangedEntity entity,
                                          HumanoidAnimator<?, ?> animator) {
        return model.crouching
                || model.swimAmount > 0.0F
                || model.riding
                || entity.getPose() == Pose.SLEEPING
                || animator.flyAmount > 0.0F
                || animator.fallFlyingAmount > 0.0F
                || entity.isFallFlying();
    }

    private record Clip(AnimationDefinition definition, long elapsedMillis) {
    }

    private static final class GlobalDriver extends HierarchicalModel<ChangedEntity> {
        private AdvancedHumanoidModel<?> currentModel;

        void setModel(AdvancedHumanoidModel<?> model) {
            this.currentModel = model;
        }

        @Override
        public ModelPart root() {
            return currentModel != null ? currentModel.getTorso() : null;
        }

        @Override
        public Optional<ModelPart> getAnyDescendantWithName(String name) {
            if (currentModel == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(findPart(name));
        }

        private ModelPart findPart(String name) {
            return switch (name) {
                case "Head" -> currentModel.getHead();
                case "Torso" -> currentModel.getTorso();
                case "RightArm" -> currentModel.getArm(HumanoidArm.RIGHT);
                case "LeftArm" -> currentModel.getArm(HumanoidArm.LEFT);
                case "RightLeg" -> currentModel.getLeg(HumanoidArm.RIGHT);
                case "LeftLeg" -> currentModel.getLeg(HumanoidArm.LEFT);
                default -> {
                    ModelPart head = currentModel.getHead();
                    if (head != null && head.hasChild(name)) {
                        yield head.getChild(name);
                    }
                    ModelPart torso = currentModel.getTorso();
                    if (torso != null && torso.hasChild("Tail")) {
                        ModelPart tail = findInTail(torso.getChild("Tail"), name);
                        if (tail != null) {
                            yield tail;
                        }
                    }
                    ModelPart rightLeg = findInLeg(currentModel.getLeg(HumanoidArm.RIGHT), name);
                    yield rightLeg != null ? rightLeg : findInLeg(currentModel.getLeg(HumanoidArm.LEFT), name);
                }
            };
        }

        private static ModelPart findInLeg(ModelPart leg, String name) {
            if (leg == null) {
                return null;
            }
            if (leg.hasChild(name)) {
                return leg.getChild(name);
            }
            for (String childName : LEG_PARTS) {
                if (leg.hasChild(childName)) {
                    ModelPart found = findInLeg(leg.getChild(childName), name);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        private static ModelPart findInTail(ModelPart tail, String name) {
            if (tail.hasChild(name)) {
                return tail.getChild(name);
            }
            for (String childName : TAIL_PARTS) {
                if (tail.hasChild(childName)) {
                    ModelPart found = findInTail(tail.getChild(childName), name);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        @Override
        public void setupAnim(ChangedEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                              float netHeadYaw, float headPitch) {
        }
    }
}
