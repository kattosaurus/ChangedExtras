package com.katt.changedextras.events;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.Config;
import com.katt.changedextras.ability.ParryAbility;
import com.katt.changedextras.common.combat.AttackBoost;
import com.katt.changedextras.common.combat.AttackComboTracker;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.network.AttackComboPacket;
import com.katt.changedextras.network.JackpotStatePacket;
import com.katt.changedextras.network.ParryStatePacket;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangedExtrasEvents {

    private static final String NBT_TAG = "JackpotActive";
    public static final String JACKPOT_TICKS_TAG = "JackpotTicksRemaining";
    public static final int JACKPOT_DURATION_TICKS = (4 * 60 + 11) * 20;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();

        if (living.level().isClientSide()) return;

        tickJackpot(living);
        tickParryTimeline(living);

        if (living instanceof Player player) {
            tickPlayerAttackCombo(player);
        } else if (living instanceof ChangedEntity changed && changed.getUnderlyingPlayer() == null) {
            tickVariantAttackCombo(changed);
        }
    }

    /**
     * The third hit of the shared attack combo leans the player forward a little.
     * Only transfurred players use the shared humanoid animation layer, so humans
     * keep vanilla movement. Item swings stay vanilla, so the combo is dropped
     * whenever the main hand is not empty.
     */
    private static void tickPlayerAttackCombo(Player player) {
        if (!Config.latexReworkedCombat) {
            AttackComboTracker.resetServer(player);
            return;
        }

        if (!ProcessTransfur.isPlayerTransfurred(player)) {
            return;
        }

        if (!AttackComboTracker.isBareHanded(player)) {
            AttackComboTracker.resetServer(player);
            return;
        }

        AttackComboTracker.ServerSwing swing = AttackComboTracker.serverSwing(player);
        if (!swing.isThirdHit()) {
            return;
        }

        AttackBoost.applyForwardBoost(player);
        playAttack2Sound(player);
        AttackComboPacket.broadcast(player, swing.combo());
    }

    /**
     * Pure variants (not a player's transfur form) use the same shared layer, so
     * they also lunge forward when their combo reaches {@code Attack2}.
     */
    private static void tickVariantAttackCombo(ChangedEntity entity) {
        if (!Config.latexReworkedCombat) {
            AttackComboTracker.resetServer(entity);
            return;
        }

        if (!AttackComboTracker.isBareHanded(entity)) {
            AttackComboTracker.resetServer(entity);
            return;
        }

        AttackComboTracker.ServerSwing swing = AttackComboTracker.serverSwing(entity);
        if (!swing.isThirdHit()) {
            return;
        }

        AttackBoost.applyForwardBoost(entity);
        playAttack2Sound(entity);
        AttackComboPacket.broadcast(entity, swing.combo());
    }

    /**
     * The finisher deals twice the damage of a normal strike. Bare-handed melee
     * only, and the strike is counted here so the damage bonus can never fire
     * without the {@code Attack2} animation that follows it.
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide() || !Config.latexReworkedCombat) {
            return;
        }

        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof LivingEntity attacker) || direct != event.getSource().getEntity()) {
            // Projectiles and other indirect damage do not use the melee combo.
            return;
        }

        DamageSource source = event.getSource();
        boolean usesSharedCombo;
        boolean isMeleeAttack;
        if (attacker instanceof Player player) {
            usesSharedCombo = ProcessTransfur.isPlayerTransfurred(player);
            isMeleeAttack = source.is(DamageTypes.PLAYER_ATTACK);
        } else if (attacker instanceof ChangedEntity changed) {
            usesSharedCombo = changed.getUnderlyingPlayer() == null;
            isMeleeAttack = source.is(DamageTypes.MOB_ATTACK);
        } else {
            usesSharedCombo = false;
            isMeleeAttack = false;
        }

        if (!usesSharedCombo || !isMeleeAttack) {
            // Abilities and other non-melee sources must not feed the punch combo.
            return;
        }

        if (AttackComboTracker.serverHurtStrike(attacker)) {
            event.setAmount(event.getAmount() * AttackComboTracker.FINISHER_DAMAGE_MULTIPLIER);
        }
    }

    /** The finisher bites, so it reuses one of the phantom's bite sounds. */
    private static void playAttack2Sound(LivingEntity entity) {
        float pitch = 0.9F + entity.getRandom().nextFloat() * 0.2F;
        entity.level().playSound(null, entity, SoundEvents.PHANTOM_BITE, SoundSource.PLAYERS, 1.0F, pitch);
    }

    private static void tickParryTimeline(LivingEntity living) {
        CompoundTag data = living.getPersistentData();

        // Tick down parry cooldown
        int cooldown = data.getInt(ParryAbility.PARRY_COOLDOWN_TAG);
        if (cooldown > 0) {
            data.putInt(ParryAbility.PARRY_COOLDOWN_TAG, cooldown - 1);
        }

        // Tick parry sequence (3.0s = 60 ticks)
        if (data.getBoolean(ParryAbility.PARRY_SEQUENCE_ACTIVE_TAG)) {
            int ticks = data.getInt(ParryAbility.PARRY_TICKS_TAG) + 1;
            data.putInt(ParryAbility.PARRY_TICKS_TAG, ticks);

            // Recovery window ends at 3.0s (60 ticks)
            if (ticks >= ParryAbility.PARRY_TOTAL_TICKS) {
                data.remove(ParryAbility.PARRY_SEQUENCE_ACTIVE_TAG);
                data.remove(ParryAbility.PARRY_TICKS_TAG);

                // Failed parry: stay locked for an extra 0.5s (unless already released)
                PlayerFreezeManager.setRemaining(living.getUUID(), ParryAbility.FAILURE_LOCK_TICKS);

                if (living.level() instanceof ServerLevel serverLevel) {
                    ParryStatePacket.broadcast(serverLevel, living.getUUID(), false, false);
                }
            }
        }
    }

    private static void tickJackpot(LivingEntity living) {
        if (!living.getPersistentData().getBoolean(NBT_TAG)) return;

        int ticksRemaining = living.getPersistentData().getInt(JACKPOT_TICKS_TAG);
        if (ticksRemaining <= 0) {
            endJackpot(living);
            return;
        }

        living.getPersistentData().putInt(JACKPOT_TICKS_TAG, ticksRemaining - 1);

        // Periodically resync the client state so a single missed packet (most commonly the
        // one sent right before a domain-collapse dimension change) cannot leave the aura and
        // music stuck off for the whole jackpot. setJackpotState is idempotent client-side.
        if (living instanceof ServerPlayer serverPlayer && living.level() instanceof ServerLevel serverLevel
                && ticksRemaining % 20 == 0) {
            JackpotStatePacket.broadcast(serverLevel, serverPlayer.getUUID(), true);
        }

        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 9, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 2, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, 1, false, false));

        if (living instanceof Player player) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0f);
        }
    }

    private static void endJackpot(LivingEntity living) {
        living.getPersistentData().putBoolean(NBT_TAG, false);
        living.getPersistentData().remove(JACKPOT_TICKS_TAG);

        if (living instanceof KattEntity katt) {
            katt.setJackpot(false);
        }

        if (living instanceof ServerPlayer player && living.level() instanceof ServerLevel level) {
            JackpotStatePacket.broadcast(level, player.getUUID(), false);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) return;

        // The jackpot (aura, uniforms, looping song) stops the moment its holder dies.
        if (living.getPersistentData().getBoolean(NBT_TAG)) {
            endJackpot(living);
        }
    }
}
