package com.katt.changedextras.common.scp;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class Scp009Handler {
    private Scp009Handler() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !entity.isAlive()) {
            return;
        }

        long gameTime = level.getGameTime();
        if ((gameTime + entity.getId()) % 5L != 0L) {
            return;
        }

        if (Scp009Helper.isInfected(entity)) {
            Scp009Helper.convertTouchedLiquids(entity, level);
        } else {
            Scp009Helper.tickContact(entity, level, gameTime);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }

        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || Scp009Helper.isProtectedTarget(victim)
                || Scp009Helper.isInfected(victim)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity killer)
                || killer == victim || !Scp009Helper.isInfected(killer)) {
            return;
        }

        event.setCanceled(true);
        Scp009Helper.playCrystalBreak(victim.level(), victim);
        victim.setHealth(Math.max(1.0F, victim.getMaxHealth() * 0.5F));
        Scp009Helper.forceInfect(victim);
    }
}
