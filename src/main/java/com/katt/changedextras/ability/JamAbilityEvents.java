package com.katt.changedextras.ability;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.network.JamStatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side maintenance for the Jammer's Jam ability. The ability itself is INSTANT
 * (fire-and-forget per press), so all per-tick logic runs here from LivingTickEvent,
 * exactly like the parry/jacket timelines in {@link com.katt.changedextras.events.ChangedExtrasEvents}.
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JamAbilityEvents {

    private JamAbilityEvents() {}

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) return;

        CompoundTag data = living.getPersistentData();

        // Tick down the jam toggle cooldown
        int cooldown = data.getInt(JamAbility.JAM_COOLDOWN_TAG);
        if (cooldown > 0) {
            data.putInt(JamAbility.JAM_COOLDOWN_TAG, cooldown - 1);
        }

        if (!data.getBoolean(JamAbility.JAM_ACTIVE_TAG)) return;

        // The headphones must stay on; removing them instantly cancels the ability.
        if (!JamAbility.isWearingHeadphones(living)) {
            JamAbility.stopJamming(living, true);
            return;
        }

        // Self-heal: if the jam survived a logout or a lost freeze (e.g. login with a stale
        // tag), re-establish the freeze and re-sync clients instead of lingering half-jamming.
        if (living instanceof ServerPlayer serverPlayer && !PlayerFreezeManager.isFrozen(living.getUUID())) {
            PlayerFreezeManager.freeze(serverPlayer, PlayerFreezeManager.INDEFINITE);

            if (living.level() instanceof ServerLevel serverLevel) {
                JamStatePacket.broadcast(serverLevel, living.getUUID(), true);
            }
        }

        // Periodically resync the client state so a single missed packet cannot leave the
        // animation stuck for the rest of the jam. setJamState is idempotent.
        int ticks = data.getInt(JamAbility.JAM_TICKS_TAG) + 1;
        data.putInt(JamAbility.JAM_TICKS_TAG, ticks);
        if (ticks % JamAbility.HEAL_INTERVAL_TICKS == 0
                && living.level() instanceof ServerLevel serverLevel) {
            JamStatePacket.broadcast(serverLevel, living.getUUID(), true);
        }

        // Regeneration: 1 heart (2 HP) every 40 ticks, capped at max health, completely free.
        if (ticks % JamAbility.HEAL_INTERVAL_TICKS == 0 && living.getHealth() < living.getMaxHealth()) {
            living.heal(JamAbility.HEAL_AMOUNT);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if (living == null || living.level().isClientSide()) return;
        if (!JamAbility.isJamming(living)) return;

        // The damage itself is NOT cancelled; it simply interrupts the jam...
        JamAbility.stopJamming(living, false);

        // ...and interrupts hurt: Slowness II for 3 seconds (60 ticks).
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, true));
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) return;

        // Safety net: never leave a dead player frozen or flagged as jamming.
        if (JamAbility.isJamming(living)) {
            JamAbility.stopJamming(living, false);
        }
    }
}
