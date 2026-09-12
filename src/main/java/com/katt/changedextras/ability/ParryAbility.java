package com.katt.changedextras.ability;

import com.katt.changedextras.init.ChangedExtrasAbilities;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.network.ParryStatePacket;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ParryAbility extends AbstractAbility<ParryAbility.ParryAbilityInstance> {
    public static final String PARRY_ACTIVE_TAG = "changedextras.parry_active";
    public static final String PARRY_COUNT_TAG = "changedextras.parry_count";
    public static final String PARRY_READY_FOR_SPAM_TAG = "changedextras.parry_ready_for_spam";
    public static final String PARRY_SPAM_COUNT_TAG = "changedextras.parry_spam_count";
    public static final String PARRY_SPAM_DECAY_TAG = "changedextras.parry_spam_decay";
    public static final String HEARTBEAT_PLAYING_TAG = "changedextras.heartbeat_playing";

    public static final int REQUIRED_PARRIES = 5;
    public static final int REQUIRED_SPAM = 20;

    public ParryAbility() {
        super(ParryAbilityInstance::new);
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.parry.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.parry.description"));
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 0;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (entity.getPersistentData().getBoolean("JackpotActive")) {
            return false;
        }
        return entity.getEntity() != null;
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        if (entity.getPersistentData().getBoolean("JackpotActive")) {
            return false;
        }
        LivingEntity living = entity.getEntity();
        return living != null && living.isAlive();
    }

    public static class ParryAbilityInstance extends AbstractAbilityInstance {
        public ParryAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
            super(ability, entity);
        }

        @Override
        public boolean canUse() {
            if (this.entity.getPersistentData().getBoolean("JackpotActive")) {
                return false;
            }
            return this.entity.getEntity() != null;
        }

        @Override
        public boolean canKeepUsing() {
            if (this.entity.getPersistentData().getBoolean("JackpotActive")) {
                return false;
            }
            LivingEntity living = this.entity.getEntity();
            return living != null && living.isAlive();
        }

        @Override
        public void startUsing() {
            if (this.entity.getPersistentData().getBoolean("JackpotActive")) {
                return;
            }
            LivingEntity living = this.entity.getEntity();
            if (living == null) return;

            CompoundTag data = living.getPersistentData();

            // When 5 parries have been performed, the ability enters spam mode to unleash Jackpot
            if (data.getBoolean(PARRY_READY_FOR_SPAM_TAG)) {
                int currentSpam = data.getInt(PARRY_SPAM_COUNT_TAG) + 1;

                // Apply blindness while spamming
                living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 45, 0, false, false, false));

                // Play heartbeat audio on first spam
                if (currentSpam == 1 || !data.getBoolean(HEARTBEAT_PLAYING_TAG)) {
                    data.putBoolean(HEARTBEAT_PLAYING_TAG, true);
                    if (living.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(),
                                ChangedExtrasSounds.HEARTBEAT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }

                // Reset spam decay timer (2 seconds of inactivity will reset spam progress)
                data.putInt(PARRY_SPAM_DECAY_TAG, 40);

                if (currentSpam >= REQUIRED_SPAM) {
                    // JACKPOT UNLEASHED!
                    data.remove(PARRY_COUNT_TAG);
                    data.remove(PARRY_READY_FOR_SPAM_TAG);
                    data.remove(PARRY_SPAM_COUNT_TAG);
                    data.remove(PARRY_SPAM_DECAY_TAG);
                    data.remove(HEARTBEAT_PLAYING_TAG);
                    data.putBoolean(PARRY_ACTIVE_TAG, false);
                    living.removeEffect(MobEffects.BLINDNESS);
                    broadcastParry(false, false);

                    if (living.level() instanceof ServerLevel serverLevel) {
                        // Play Minecraft explosion sound and particles
                        serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(),
                                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                                living.getX(), living.getY() + 1.0D, living.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    if (living instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(
                                Component.literal("§6§l★ JACKPOT! ★"),
                                true
                        );
                    }
                    ChangedExtrasAbilities.activateJackpot(living);
                } else {
                    data.putInt(PARRY_SPAM_COUNT_TAG, currentSpam);
                    if (living instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(
                                Component.literal("§c§lSpam for Jackpot! §e" + currentSpam + " §7/ §e" + REQUIRED_SPAM),
                                true
                        );
                    }
                }
                return;
            }

            // Normal parrying stance
            data.putBoolean(PARRY_ACTIVE_TAG, true);
            broadcastParry(true, false);
        }

        @Override
        public void tick() {
            if (this.entity.getPersistentData().getBoolean("JackpotActive")) {
                stopUsing();
                return;
            }
            if (!this.entity.getPersistentData().getBoolean(PARRY_READY_FOR_SPAM_TAG)) {
                if (!this.entity.getPersistentData().getBoolean(PARRY_ACTIVE_TAG)) {
                    this.entity.getPersistentData().putBoolean(PARRY_ACTIVE_TAG, true);
                    broadcastParry(true, false);
                }
            }
        }

        @Override
        public void stopUsing() {
            if (!this.entity.getPersistentData().getBoolean(PARRY_READY_FOR_SPAM_TAG)) {
                this.entity.getPersistentData().putBoolean(PARRY_ACTIVE_TAG, false);
                broadcastParry(false, false);
            }
        }

        private void broadcastParry(boolean parrying, boolean counterattacking) {
            LivingEntity living = this.entity.getEntity();
            if (living != null && living.level() instanceof ServerLevel serverLevel) {
                ParryStatePacket.broadcast(serverLevel, living.getUUID(), parrying, counterattacking);
            }
        }

        @Override
        public void saveData(CompoundTag tag) {
            super.saveData(tag);
            tag.putBoolean("Parrying", this.entity.getPersistentData().getBoolean(PARRY_ACTIVE_TAG));
            tag.putInt("ParryCount", this.entity.getPersistentData().getInt(PARRY_COUNT_TAG));
            tag.putBoolean("ParryReadyForSpam", this.entity.getPersistentData().getBoolean(PARRY_READY_FOR_SPAM_TAG));
            tag.putInt("ParrySpamCount", this.entity.getPersistentData().getInt(PARRY_SPAM_COUNT_TAG));
        }

        @Override
        public void readData(CompoundTag tag) {
            super.readData(tag);
            this.entity.getPersistentData().putBoolean(PARRY_ACTIVE_TAG, tag.getBoolean("Parrying"));
            this.entity.getPersistentData().putInt(PARRY_COUNT_TAG, tag.getInt("ParryCount"));
            this.entity.getPersistentData().putBoolean(PARRY_READY_FOR_SPAM_TAG, tag.getBoolean("ParryReadyForSpam"));
            this.entity.getPersistentData().putInt(PARRY_SPAM_COUNT_TAG, tag.getInt("ParrySpamCount"));
        }
    }

    public static boolean isParrying(LivingEntity entity) {
        if (entity == null) return false;
        if (entity.getPersistentData().getBoolean("JackpotActive")) return false;
        if (entity.getPersistentData().getBoolean(PARRY_READY_FOR_SPAM_TAG)) return false;
        return entity.getPersistentData().getBoolean(PARRY_ACTIVE_TAG);
    }
}
