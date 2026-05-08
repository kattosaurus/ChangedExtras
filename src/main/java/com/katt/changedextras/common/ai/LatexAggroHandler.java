package com.katt.changedextras.common.ai;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.LatexCuddleHelper;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LatexAggroHandler {
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

        if (!isValidRetaliationTarget(attacker)) {
            return;
        }

        mob.setTarget(attacker);
        LatexMind mind = LatexMindStore.get(mob);
        mind.markRetaliationTarget(attacker, mob.tickCount);
        mind.remember(attacker, mob.tickCount, mob.hasLineOfSight(attacker));
    }

    private static boolean isValidRetaliationTarget(LivingEntity attacker) {
        if (!attacker.isAlive() || attacker instanceof ChangedEntity) {
            return false;
        }

        if (attacker instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }

        return true;
    }
}
