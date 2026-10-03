package com.katt.changedextras.events;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.ability.ParryAbility;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.network.JackpotStatePacket;
import com.katt.changedextras.network.ParryStatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangedExtrasEvents {

    private static final String NBT_TAG = "JackpotActive";
    public static final String JACKPOT_TICKS_TAG = "JackpotTicksRemaining";
    private static final String JACKPOT_SLOWNESS_TICKS_TAG = "JackpotSlownessTicksRemaining";
    private static final String JACKPOT_NAUSEA_TICKS_TAG = "JackpotNauseaTicksRemaining";
    public static final int JACKPOT_DURATION_TICKS = (4 * 60 + 11) * 20;
    private static final int JACKPOT_SLOWNESS_DURATION_TICKS = 2 * 60 * 20;
    private static final int JACKPOT_NAUSEA_DURATION_TICKS = 60 * 20;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();

        if (living.level().isClientSide()) return;

        tickJackpot(living);
        tickJackpotAftermath(living);
        tickParrySpam(living);
    }

    private static void tickParrySpam(LivingEntity living) {
        CompoundTag data = living.getPersistentData();
        if (data.getBoolean(NBT_TAG)) {
            if (data.contains(ParryAbility.PARRY_SPAM_COUNT_TAG) || data.contains(ParryAbility.PARRY_READY_FOR_SPAM_TAG)) {
                data.remove(ParryAbility.PARRY_COUNT_TAG);
                data.remove(ParryAbility.PARRY_READY_FOR_SPAM_TAG);
                data.remove(ParryAbility.PARRY_READY_TIMEOUT_TAG);
                data.remove(ParryAbility.PARRY_SPAM_COUNT_TAG);
                data.remove(ParryAbility.PARRY_SPAM_DECAY_TAG);
                data.remove(ParryAbility.HEARTBEAT_PLAYING_TAG);
            }
            return;
        }

        if (data.getBoolean(ParryAbility.PARRY_READY_FOR_SPAM_TAG)) {
            // Check 15-second expiration timer if player does not spam ability key
            int readyTimer = data.contains(ParryAbility.PARRY_READY_TIMEOUT_TAG)
                    ? data.getInt(ParryAbility.PARRY_READY_TIMEOUT_TAG)
                    : ParryAbility.PARRY_READY_TIMEOUT_TICKS;

            readyTimer--;
            if (readyTimer <= 0) {
                // 15 seconds elapsed without completing spam - expire parries and reset state
                data.remove(ParryAbility.PARRY_COUNT_TAG);
                data.remove(ParryAbility.PARRY_READY_FOR_SPAM_TAG);
                data.remove(ParryAbility.PARRY_READY_TIMEOUT_TAG);
                data.remove(ParryAbility.PARRY_SPAM_COUNT_TAG);
                data.remove(ParryAbility.PARRY_SPAM_DECAY_TAG);
                data.remove(ParryAbility.HEARTBEAT_PLAYING_TAG);
                living.removeEffect(MobEffects.BLINDNESS);

                if (living.level() instanceof ServerLevel serverLevel) {
                    ParryStatePacket.broadcast(serverLevel, living.getUUID(), false, false);
                }
                if (living instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(
                            Component.literal("§cParry charge expired."),
                            true
                    );
                }
                return;
            } else {
                data.putInt(ParryAbility.PARRY_READY_TIMEOUT_TAG, readyTimer);
            }

            int decay = data.getInt(ParryAbility.PARRY_SPAM_DECAY_TAG);
            if (decay > 0) {
                decay--;
                if (decay <= 0) {
                    data.remove(ParryAbility.PARRY_SPAM_COUNT_TAG);
                    data.remove(ParryAbility.PARRY_SPAM_DECAY_TAG);
                    data.remove(ParryAbility.HEARTBEAT_PLAYING_TAG);
                    living.removeEffect(MobEffects.BLINDNESS);
                } else {
                    data.putInt(ParryAbility.PARRY_SPAM_DECAY_TAG, decay);
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, false));
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
        living.getPersistentData().putInt(JACKPOT_SLOWNESS_TICKS_TAG, JACKPOT_SLOWNESS_DURATION_TICKS);
        living.getPersistentData().putInt(JACKPOT_NAUSEA_TICKS_TAG, JACKPOT_NAUSEA_DURATION_TICKS);

        if (living instanceof KattEntity katt) {
            katt.setJackpot(false);
        }

        if (living instanceof ServerPlayer player && living.level() instanceof ServerLevel level) {
            JackpotStatePacket.broadcast(level, player.getUUID(), false);
        }
    }

    private static void tickJackpotAftermath(LivingEntity living) {
        int slownessTicks = living.getPersistentData().getInt(JACKPOT_SLOWNESS_TICKS_TAG);
        if (slownessTicks > 0) {
            living.getPersistentData().putInt(JACKPOT_SLOWNESS_TICKS_TAG, slownessTicks - 1);
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0, false, false));
        } else {
            living.getPersistentData().remove(JACKPOT_SLOWNESS_TICKS_TAG);
        }

        int nauseaTicks = living.getPersistentData().getInt(JACKPOT_NAUSEA_TICKS_TAG);
        if (nauseaTicks > 0) {
            living.getPersistentData().putInt(JACKPOT_NAUSEA_TICKS_TAG, nauseaTicks - 1);
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30, 1, false, false));
        } else {
            living.getPersistentData().remove(JACKPOT_NAUSEA_TICKS_TAG);
        }
    }
}
