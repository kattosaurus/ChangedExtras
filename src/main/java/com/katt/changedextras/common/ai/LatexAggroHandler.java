package com.katt.changedextras.common.ai;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.LatexCuddleHelper;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import com.katt.changedextras.common.inventory.LatexInventory;
import com.katt.changedextras.common.inventory.LatexInventoryProvider;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("deprecation")
public final class LatexAggroHandler {
    private static final int GRUDGE_MEMORY_TICKS = 20 * 60 * 20; // 20 minutes of memory for killing an ally
    private static final double ALLY_ALERT_RADIUS = 24.0D;
    private static final double GRUDGE_ALERT_RADIUS = 32.0D;

    private LatexAggroHandler() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ChangedEntity mob) || mob.level().isClientSide()) {
            return;
        }

        if (!ChangedExtrasGameRules.isSmartLatexAiEnabled(mob.level().getGameRules())) {
            return;
        }

        if (LatexAiUtil.isSmartAiExcluded(mob)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker instanceof Player player && LatexCuddleHelper.isTamingOwner(mob, player)) {
            mob.setTarget(null);
            LatexMindStore.get(mob).clearTarget();
            return;
        }

        if (!isValidRetaliationTarget(mob, attacker)) {
            return;
        }

        mob.setTarget(attacker);
        LatexMind mind = LatexMindStore.get(mob);
        mind.markRetaliationTarget(attacker, mob.tickCount);
        mind.remember(attacker, mob.tickCount, mob.hasLineOfSight(attacker));

        // Alert nearby same-faction allies to assist
        alertNearbyAllies(mob, attacker);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) {
            return;
        }

if (event.getSource().getEntity() instanceof ChangedEntity latexMob) {
            // The Artist is a straight damage boss: her killing blows remain normal deaths and
            // are never converted into a transfur into her own latex form.
            if (!(latexMob instanceof ArtistEntity) && LatexAiUtil.isTransfurrable(victim)) {
                TransfurVariant<?> variant = latexMob.getSelfVariant();
                if (variant == null) {
                    variant = TransfurVariant.findEntityTransfurVariant(latexMob);
                }
                if (variant == null) {
                    return;
                }

                // If the damage is fatal, cancel normal death so the victim is transfurred instead of just killed
                if (event.getAmount() >= victim.getHealth()) {
                    if (event.isCancelable()) {
                        event.setCanceled(true);
                    }
                    event.setAmount(0.0F);
                    if (victim instanceof Player player) {
                        player.setHealth(Math.max(1.0F, player.getMaxHealth() * 0.5F));
                    }
                    ProcessTransfur.transfur(victim, victim.level(), variant, false, latexMob.getReplicateContext());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) {
            return;
        }

        // Drop items from capability inventory if victim was a latex mob
        if (victim instanceof ChangedEntity changedMob) {
            changedMob.getCapability(LatexInventoryProvider.LATEX_INVENTORY).ifPresent(inv -> {
                inv.dropAll(changedMob);
            });
        }

        if (!ChangedExtrasGameRules.isSmartLatexAiEnabled(victim.level().getGameRules())) {
            return;
        }

        // Grudge Memory: If victim was a latex creature, allies remember the killer and seek revenge!
        LatexAiUtil.LatexAlignment victimAlignment = LatexAiUtil.getAlignment(victim);
        if (victimAlignment == LatexAiUtil.LatexAlignment.WHITE || victimAlignment == LatexAiUtil.LatexAlignment.DARK) {
            LivingEntity killer = victim.getLastHurtByMob();
            if (killer == null && event.getSource().getEntity() instanceof LivingEntity sourceEntity) {
                killer = sourceEntity;
            }

            if (killer != null && killer.isAlive() && isValidRetaliationTarget(victim, killer)) {
                AABB alertBox = victim.getBoundingBox().inflate(GRUDGE_ALERT_RADIUS);
                List<ChangedEntity> allies = victim.level().getEntitiesOfClass(ChangedEntity.class, alertBox,
                        ally -> ally.isAlive() && ally != victim && LatexAiUtil.isSameLatexType(victim, ally));

                for (ChangedEntity ally : allies) {
                    if (LatexAiUtil.isSmartAiExcluded(ally)) continue;

                    LatexMind allyMind = LatexMindStore.get(ally);
                    allyMind.addGrudgeKiller(killer.getUUID(), ally.tickCount + GRUDGE_MEMORY_TICKS);
                    allyMind.triggerEnrage(300); // 15 seconds of immediate enrage sprint

                    if (ally.getTarget() == null || !ally.getTarget().isAlive()) {
                        ally.setTarget(killer);
                        allyMind.remember(killer, ally.tickCount, ally.hasLineOfSight(killer));
                    }
                }
            }
        }
    }

    private static void alertNearbyAllies(ChangedEntity victim, LivingEntity attacker) {
        LatexMind victimMind = LatexMindStore.get(victim);
        if (victimMind.allyAlertCooldown > 0) return;
        victimMind.allyAlertCooldown = 60; // once every 3 seconds

        AABB alertBox = victim.getBoundingBox().inflate(ALLY_ALERT_RADIUS);
        List<ChangedEntity> allies = victim.level().getEntitiesOfClass(ChangedEntity.class, alertBox,
                ally -> ally.isAlive() && ally != victim && LatexAiUtil.isSameLatexType(victim, ally));

        for (ChangedEntity ally : allies) {
            if (LatexAiUtil.isSmartAiExcluded(ally)) continue;
            if (attacker instanceof Player player && LatexCuddleHelper.isTamingOwner(ally, player)) continue;

            LatexMind allyMind = LatexMindStore.get(ally);
            if (ally.getTarget() == null || !ally.getTarget().isAlive()) {
                ally.setTarget(attacker);
                allyMind.markRetaliationTarget(attacker, ally.tickCount);
                allyMind.remember(attacker, ally.tickCount, ally.hasLineOfSight(attacker));
            }
        }
    }

    private static boolean isValidRetaliationTarget(LivingEntity victim, LivingEntity attacker) {
        if (!attacker.isAlive() || attacker == victim) {
            return false;
        }

        if (LatexAiUtil.isOrganicLatex(victim)) {
            if (attacker instanceof Player player) {
                return !player.isCreative() && !player.isSpectator();
            }
            return true;
        }

        // Same faction allies don't retaliate against each other unless hostile factions
        if (LatexAiUtil.isSameLatexType(victim, attacker)) {
            return false;
        }

        if (attacker instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }

        return true;
    }
}
