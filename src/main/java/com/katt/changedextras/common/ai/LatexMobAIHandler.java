package com.katt.changedextras.common.ai;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LatexMobAIHandler {
    // Standard player-calibrated base movement speed for smart AI
    private static final double LATEX_PLAYER_BASE_SPEED = 0.38D;

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
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ChangedEntity mob)) return;
        if (mob instanceof ArtistEntity) return;

        boolean smartEnabled = ChangedExtrasGameRules.isSmartLatexAiEnabled(mob.level().getGameRules());
        if (!smartEnabled) {
            if (INSTALLED_MOBS.contains(mob)) {
                INSTALLED_MOBS.remove(mob);
                LatexMindStore.forget(mob);
                restoreNativeGoals(mob);
            }
            return;
        }

        if (!INSTALLED_MOBS.contains(mob)) {
            ensureSmartAiInstalled(mob);
        }

        LatexMind mind = LatexMindStore.get(mob);
        mind.tick(mob);
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ChangedEntity mob) {
            INSTALLED_MOBS.remove(mob);
            LatexMindStore.forget(mob);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ChangedEntity victim) {
            INSTALLED_MOBS.remove(victim);
            LatexMindStore.forget(victim);

            if (ChangedExtrasGameRules.isSmartLatexAiEnabled(victim.level().getGameRules())
                    && event.getSource().getEntity() instanceof LivingEntity killer) {
                alertNearbyAlliesOfMurder(victim, killer);
            }
        }
    }

    private static void alertNearbyAlliesOfMurder(ChangedEntity victim, LivingEntity killer) {
        double alertRadius = 18.0D;
        for (ChangedEntity ally : victim.level().getEntitiesOfClass(ChangedEntity.class, victim.getBoundingBox().inflate(alertRadius))) {
            if (ally != victim && LatexAiUtil.isSameLatexType(victim, ally)) {
                LatexMind allyMind = LatexMindStore.get(ally);
                allyMind.addGrudgeKiller(killer.getUUID(), ally.tickCount + 1200); // 60-second grudge
                allyMind.triggerEnrage(240);
                ally.setTarget(killer);
            }
        }
    }

    private static void ensureSmartAiInstalled(ChangedEntity mob) {
        if (INSTALLED_MOBS.contains(mob)) return;

        removeConflictingLookGoals(mob);
        removeConflictingCombatGoals(mob);
        installTargetShareGoal(mob);
        mob.setCanPickUpLoot(true);

        mob.getNavigation().setCanFloat(true);
        mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);

        AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getBaseValue() < 40.0D) {
            followRange.setBaseValue(40.0D);
        }

        AttributeInstance movementSpeed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(movementSpeed.getAttribute().getDefaultValue());
        }

        INSTALLED_MOBS.add(mob);
    }

    private static void removeConflictingLookGoals(ChangedEntity mob) {
        mob.goalSelector.removeAllGoals(goal ->
                goal instanceof RandomLookAroundGoal
                        || goal instanceof LookAtPlayerGoal
                        || goal instanceof WaterAvoidingRandomStrollGoal
        );
    }

    private static void removeConflictingCombatGoals(ChangedEntity mob) {
        mob.goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
        mob.targetSelector.removeAllGoals(goal ->
                goal instanceof HurtByTargetGoal
                        || goal instanceof NearestAttackableTargetGoal<?>);
    }

    private static void installTargetShareGoal(ChangedEntity mob) {
        mob.targetSelector.addGoal(2, new ShareTargetGoal(mob, 12.0D, 10));
    }

    private static void restoreNativeGoals(ChangedEntity mob) {
        mob.goalSelector.removeAllGoals(goal -> true);
        mob.targetSelector.removeAllGoals(goal -> true);

        try {
            Method registerGoalsMethod = ObfuscationReflectionHelper.findMethod(Mob.class, "m_8099_");
            registerGoalsMethod.setAccessible(true);
            registerGoalsMethod.invoke(mob);
        } catch (Exception e) {
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(2, new MeleeAttackGoal(mob, 0.4D, false));
            mob.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(mob, 0.3D));
            mob.goalSelector.addGoal(4, new LookAtPlayerGoal(mob, Player.class, 8.0F));
            mob.goalSelector.addGoal(5, new RandomLookAroundGoal(mob));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal(mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, true));
        }

        AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(16.0D);
        }

        AttributeInstance movementSpeed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance defaultAttribute = getDefaultAttribute(mob, Attributes.MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (defaultAttribute != null) {
                movementSpeed.setBaseValue(defaultAttribute.getBaseValue());
            } else {
                movementSpeed.setBaseValue(LATEX_PLAYER_BASE_SPEED);
            }
        }

        mob.setCanPickUpLoot(false);
    }

    @Nullable
    private static AttributeInstance getDefaultAttribute(ChangedEntity mob, Attribute attr) {
        TransfurVariant<?> variant = TransfurVariant.getEntityVariant(mob);
        if (variant != null) {
            ChangedEntity entity = ChangedEntities.getCachedEntity(mob.level(), variant.getEntityType());
            return entity.getAttribute(attr);
        } else {
            return null;
        }
    }
}
