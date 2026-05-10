package com.katt.changedextras.common.ai;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.world.entity.player.Player;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LatexMobAIHandler {
    private static final double MIN_GLOBAL_LATEX_MOVE_SPEED = 0.27D;

    private static final Set<ChangedEntity> INSTALLED_MOBS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private LatexMobAIHandler() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ChangedEntity mob)) return;
        if (mob instanceof ArtistEntity artist) {
            if (artist.isNoAi()) {
                artist.setNoAi(false);
            }
            return;
        }
        if (ChangedExtrasGameRules.isSmartLatexAiEnabled(event.getLevel().getGameRules())) {
            ensureSmartAiInstalled(mob);
        } else {
            installDefaultGoals(mob);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ChangedEntity mob)) return;
        if (mob.level().isClientSide()) return;
        if (mob instanceof ArtistEntity artist) {
            if (artist.isNoAi()) {
                artist.setNoAi(false);
            }
            return;
        }
        boolean smartAiEnabled = ChangedExtrasGameRules.isSmartLatexAiEnabled(mob.level().getGameRules());
        if (smartAiEnabled) {
            if (LatexAiUtil.isSmartAiExcluded(mob)) {
                INSTALLED_MOBS.remove(mob);
                LatexMindStore.forget(mob);
                return;
            }

            ensureSmartAiInstalled(mob);
            LatexMind mind = LatexMindStore.get(mob);
            mind.tick(mob);
        } else {
            if (INSTALLED_MOBS.contains(mob)) {
                INSTALLED_MOBS.remove(mob);
                LatexMindStore.forget(mob);
                installDefaultGoals(mob);
                mob.setCanPickUpLoot(false);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ChangedEntity mob && !event.getLevel().isClientSide()) {
            INSTALLED_MOBS.remove(mob);
            LatexMindStore.forget(mob);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ChangedEntity mob && !mob.level().isClientSide()) {
            INSTALLED_MOBS.remove(mob);
            LatexMindStore.forget(mob);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (!(event.getObject() instanceof ChangedEntity)) return;

        event.addCapability(
                ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_inventory"),
                new com.katt.changedextras.common.inventory.LatexInventoryProvider()
        );
    }

    private static void ensureSmartAiInstalled(ChangedEntity mob) {
        if (!ChangedExtrasGameRules.isSmartLatexAiEnabled(mob.level().getGameRules())
                || INSTALLED_MOBS.contains(mob)
                || LatexAiUtil.isSmartAiExcluded(mob)) {
            return;
        }

        INSTALLED_MOBS.add(mob);
        removeConflictingLookGoals(mob);
        removeConflictingCombatGoals(mob);
        installTargetShareGoal(mob);
        mob.setCanPickUpLoot(true);

        AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getBaseValue() < 40.0D) {
            followRange.setBaseValue(40.0D);
        }

        AttributeInstance movementSpeed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null && movementSpeed.getBaseValue() < MIN_GLOBAL_LATEX_MOVE_SPEED) {
            movementSpeed.setBaseValue(MIN_GLOBAL_LATEX_MOVE_SPEED);
        }

    }

    private static void removeConflictingLookGoals(ChangedEntity mob) {
        GoalSelector goals = getSelector(mob, "goalSelector");
        if (goals != null) {
            goals.removeAllGoals(goal ->
                    goal instanceof RandomLookAroundGoal
                            || goal instanceof LookAtPlayerGoal
                            || goal instanceof WaterAvoidingRandomStrollGoal
            );
        }
    }

    private static void removeConflictingCombatGoals(ChangedEntity mob) {
        GoalSelector goals = getSelector(mob, "goalSelector");
        if (goals != null) {
            goals.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
        }

        GoalSelector targetSelector = getSelector(mob, "targetSelector");
        if (targetSelector != null) {
            targetSelector.removeAllGoals(goal ->
                    goal instanceof HurtByTargetGoal
                            || goal instanceof NearestAttackableTargetGoal<?>);
        }
    }

    private static void installTargetShareGoal(ChangedEntity mob) {
        GoalSelector targetSelector = getSelector(mob, "targetSelector");
        if (targetSelector != null) {
            targetSelector.addGoal(2, new ShareTargetGoal(mob, 12.0D, 10));
        }
    }

    @Nullable
    private static GoalSelector getSelector(ChangedEntity mob, String fieldName) {
        try {
            Field field = net.minecraft.world.entity.Mob.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (GoalSelector) field.get(mob);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static void installDefaultGoals(ChangedEntity mob) {
        GoalSelector goals = getSelector(mob, "goalSelector");
        if (goals != null) {
            goals.addGoal(5, new WaterAvoidingRandomStrollGoal(mob, 1.0D));
            goals.addGoal(6, new RandomLookAroundGoal(mob));
            goals.addGoal(6, new LookAtPlayerGoal(mob, Player.class, 8.0F));
            goals.addGoal(4, new MeleeAttackGoal(mob, 1.0D, false));
        }

        GoalSelector targetSelector = getSelector(mob, "targetSelector");
        if (targetSelector != null) {
            targetSelector.addGoal(1, new HurtByTargetGoal(mob));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, true));
        }

        // Reset movement speed to default
        AttributeInstance movementSpeed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null && movementSpeed.getBaseValue() > 0.25D) {
            movementSpeed.setBaseValue(0.23D);
        }
    }
}
