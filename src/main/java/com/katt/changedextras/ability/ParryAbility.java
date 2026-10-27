package com.katt.changedextras.ability;

import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.domain.DomainExpansionManager;
import com.katt.changedextras.network.ParryStatePacket;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ParryAbility extends AbstractAbility<ParryAbility.ParryAbilityInstance> {
    public static final String PARRY_SEQUENCE_ACTIVE_TAG = "changedextras.parry_seq_active";
    public static final String PARRY_TICKS_TAG = "changedextras.parry_ticks";
    public static final String PARRY_COOLDOWN_TAG = "changedextras.parry_cooldown";
    public static final String VISUAL_ABILITIES_COUNTER_TAG = "visual_abilities_counter";
    public static final String DOMAIN_READY_TAG = "changedextras.domain_ready";

    // Strict Timeline Constraints (Total 3.0 seconds = 60 ticks)
    public static final int PARRY_TOTAL_TICKS = 60;     // 3.0s
    public static final int STARTUP_TICKS = 10;         // 0.0s - 0.5s
    public static final int ACTIVE_BLOCK_TICKS = 55;    // 0.5s - 2.75s (active window)
    public static final int COOLDOWN_TICKS = 100;       // 5.0 seconds strict cooldown
    public static final int REQUIRED_PARRIES_FOR_DOMAIN = 5;
    public static final int FAILURE_LOCK_TICKS = 10;    // Extra 0.5s freeze if the parry fails

    public ParryAbility() {
        super(ParryAbilityInstance::new);
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        if (isDomainReady(entity != null ? entity.getEntity() : null)) {
            return Component.literal("§dDomain Expansion: Idle Death Gamble");
        }
        return Component.translatable("ability.changedextras.parry.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        if (isDomainReady(entity != null ? entity.getEntity() : null)) {
            return List.of(Component.literal("§dActivate to expand the domain barrier \"Idle Death Gamble\"!"));
        }
        return List.of(Component.translatable("ability.changedextras.parry.description"));
    }

    // Input Rule: Non-holdable. A single discrete press triggers the sequence.
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    // Cooldown: Strict 5-second cooldown immediately after the ability is used.
    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return COOLDOWN_TICKS;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (entity.getPersistentData().getBoolean("JackpotActive")) {
            return false;
        }
        LivingEntity living = entity.getEntity();
        if (living == null || !living.isAlive()) return false;

        // Domain Expansion ready check allows immediate casting
        if (isDomainReady(living)) {
            return true;
        }

        // Check if currently on cooldown or already executing parry sequence
        int cooldown = living.getPersistentData().getInt(PARRY_COOLDOWN_TAG);
        if (cooldown > 0) {
            return false;
        }
        return !living.getPersistentData().getBoolean(PARRY_SEQUENCE_ACTIVE_TAG);
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        return false;
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
            LivingEntity living = this.entity.getEntity();
            if (living == null || !living.isAlive()) return false;

            if (isDomainReady(living)) {
                return true;
            }

            int cooldown = living.getPersistentData().getInt(PARRY_COOLDOWN_TAG);
            return cooldown <= 0 && !living.getPersistentData().getBoolean(PARRY_SEQUENCE_ACTIVE_TAG);
        }

        @Override
        public boolean canKeepUsing() {
            return false;
        }

        @Override
        public void startUsing() {
            LivingEntity living = this.entity.getEntity();
            if (living == null || living.level().isClientSide()) return;

            CompoundTag data = living.getPersistentData();

            // The second activation of the ability (once ready) triggers the Domain Expansion state
            if (data.getBoolean(DOMAIN_READY_TAG)) {
                data.remove(DOMAIN_READY_TAG);
                if (living instanceof ServerPlayer serverPlayer) {
                    DomainExpansionManager.getInstance().openDomain(serverPlayer);
                }
                // Apply cooldown to controller
                getController().forceCooldown(COOLDOWN_TICKS);
                data.putInt(PARRY_COOLDOWN_TAG, COOLDOWN_TICKS);
                return;
            }

            // Normal Parry activation: Non-holdable single discrete press
            // Apply strict 5-second cooldown immediately after ability is used
            data.putInt(PARRY_COOLDOWN_TAG, COOLDOWN_TICKS);
            getController().forceCooldown(COOLDOWN_TICKS);

            // Initialize timeline tracker (0 ticks)
            data.putBoolean(PARRY_SEQUENCE_ACTIVE_TAG, true);
            data.putInt(PARRY_TICKS_TAG, 0);

            // Freeze the caster in place for the whole parry sequence
            if (living instanceof ServerPlayer serverPlayer) {
                PlayerFreezeManager.freeze(serverPlayer, PlayerFreezeManager.INDEFINITE);
            }

            // Spawn 2 Iron Door models and sync parry_start animation concurrently
            if (living.level() instanceof ServerLevel serverLevel) {
                ParryStatePacket.broadcast(serverLevel, living.getUUID(), true, false);
            }

            // Check if inside active Domain Expansion break/intermission phase
            if (DomainExpansionManager.getInstance().isDomainActive(living.getUUID())) {
                DomainExpansionManager.getInstance().onVisualAbilityUsed(living);
            }
        }

        @Override
        public void tick() {
            LivingEntity living = this.entity.getEntity();
            if (living == null || living.level().isClientSide()) return;

            CompoundTag data = living.getPersistentData();

            // Advance parry cooldown
            int cooldown = data.getInt(PARRY_COOLDOWN_TAG);
            if (cooldown > 0) {
                data.putInt(PARRY_COOLDOWN_TAG, cooldown - 1);
            }

            // Advance parry timeline
            if (data.getBoolean(PARRY_SEQUENCE_ACTIVE_TAG)) {
                int ticks = data.getInt(PARRY_TICKS_TAG) + 1;
                data.putInt(PARRY_TICKS_TAG, ticks);

                // Total 3.0 seconds (60 ticks): naturally finish recovery window
                if (ticks >= PARRY_TOTAL_TICKS) {
                    data.remove(PARRY_SEQUENCE_ACTIVE_TAG);
                    data.remove(PARRY_TICKS_TAG);

                    // Failed parry: stay locked for an extra 0.5s (unless already released)
                    PlayerFreezeManager.setRemaining(living.getUUID(), FAILURE_LOCK_TICKS);

                    if (living.level() instanceof ServerLevel serverLevel) {
                        ParryStatePacket.broadcast(serverLevel, living.getUUID(), false, false);
                    }
                }
            }
        }

        @Override
        public void stopUsing() {}

        @Override
        public void saveData(CompoundTag tag) {
            super.saveData(tag);
            tag.putBoolean("ParrySeqActive", this.entity.getPersistentData().getBoolean(PARRY_SEQUENCE_ACTIVE_TAG));
            tag.putInt("ParryTicks", this.entity.getPersistentData().getInt(PARRY_TICKS_TAG));
            tag.putInt("ParryCooldown", this.entity.getPersistentData().getInt(PARRY_COOLDOWN_TAG));
            tag.putInt("VisualAbilities", this.entity.getPersistentData().getInt(VISUAL_ABILITIES_COUNTER_TAG));
            tag.putBoolean("DomainReady", this.entity.getPersistentData().getBoolean(DOMAIN_READY_TAG));
        }

        @Override
        public void readData(CompoundTag tag) {
            super.readData(tag);
            this.entity.getPersistentData().putBoolean(PARRY_SEQUENCE_ACTIVE_TAG, tag.getBoolean("ParrySeqActive"));
            this.entity.getPersistentData().putInt(PARRY_TICKS_TAG, tag.getInt("ParryTicks"));
            this.entity.getPersistentData().putInt(PARRY_COOLDOWN_TAG, tag.getInt("ParryCooldown"));
            this.entity.getPersistentData().putInt(VISUAL_ABILITIES_COUNTER_TAG, tag.getInt("VisualAbilities"));
            this.entity.getPersistentData().putBoolean(DOMAIN_READY_TAG, tag.getBoolean("DomainReady"));
        }
    }

    /**
     * Checks if the entity is currently within the strict Active Block Window (0.5s - 2.75s / ticks 10 - 55).
     * During 0.0s - 0.5s (startup) or 2.75s - 3.0s (recovery), parry is NOT active and player takes full damage.
     */
    public static boolean isParryActive(LivingEntity entity) {
        if (entity == null) return false;
        CompoundTag data = entity.getPersistentData();
        if (!data.getBoolean(PARRY_SEQUENCE_ACTIVE_TAG)) return false;

        int ticks = data.getInt(PARRY_TICKS_TAG);
        return ticks >= STARTUP_TICKS && ticks <= ACTIVE_BLOCK_TICKS;
    }

    public static boolean isDomainReady(LivingEntity entity) {
        return entity != null && entity.getPersistentData().getBoolean(DOMAIN_READY_TAG);
    }
}
