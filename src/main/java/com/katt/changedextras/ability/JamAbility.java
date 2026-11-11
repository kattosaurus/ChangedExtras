package com.katt.changedextras.ability;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.network.JamStatePacket;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

/**
 * Toggleable "Jam" ability of the Latex Jammer.
 *
 * While jamming the player is frozen in place (camera rotation and menus still work),
 * regenerates 1 heart every 2 seconds for free, plays the looping Jam animation and is
 * told that it sounds great. The jam ends when the ability is pressed again, when the
 * Jammer Headphones are removed, or when the player takes damage (which additionally
 * applies Slowness II for 3 seconds).
 */
public class JamAbility extends AbstractAbility<JamAbility.JamAbilityInstance> {
    public static final String JAM_ACTIVE_TAG = "changedextras.jam_active";
    public static final String JAM_TICKS_TAG = "changedextras.jam_ticks";
    public static final String JAM_COOLDOWN_TAG = "changedextras.jam_cooldown";

    /** Short cooldown between presses so the toggle cannot be spammed. */
    public static final int TOGGLE_COOLDOWN_TICKS = 10;
    /** One heart (2 HP) every 2 seconds (40 ticks). */
    public static final int HEAL_INTERVAL_TICKS = 40;
    public static final float HEAL_AMOUNT = 2.0F;

    private static final Predicate<ItemStack> HEADPHONES = stack ->
            stack.is(ChangedExtras.JAMMER_HEADPHONES.get());

    public JamAbility() {
        super(JamAbilityInstance::new);
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.jam.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.jam.description"));
    }

    // Input rule: non-holdable. Each discrete press toggles the jam on or off.
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return TOGGLE_COOLDOWN_TICKS;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        LivingEntity living = entity.getEntity();
        if (living == null || !living.isAlive()) return false;

        int cooldown = living.getPersistentData().getInt(JAM_COOLDOWN_TAG);
        if (cooldown > 0) return false;

        // While jamming, the next press toggles the jam off.
        if (isJamming(living)) return true;

        return isWearingHeadphones(living);
    }

    public static boolean isJamming(LivingEntity living) {
        return living != null && living.getPersistentData().getBoolean(JAM_ACTIVE_TAG);
    }

    public static boolean isWearingHeadphones(LivingEntity living) {
        if (living == null) return false;

        // Jammer Headphones live in the head accessory slot of the Changed clothing system.
        if (AccessorySlots.isWearing(living, HEADPHONES)) {
            return true;
        }

        // Fallback: vanilla head slot (covers armor stands and other addons).
        return living.getItemBySlot(EquipmentSlot.HEAD).is(ChangedExtras.JAMMER_HEADPHONES.get());
    }

    public static void startJamming(LivingEntity living) {
        if (living == null || living.level().isClientSide()) return;

        CompoundTag data = living.getPersistentData();
        data.putBoolean(JAM_ACTIVE_TAG, true);
        data.putInt(JAM_TICKS_TAG, 0);

        // Freeze the player in place. The lock uses connection.teleport with the player's own
        // yaw/pitch, so camera rotation and opening menus/inventories still work normally.
        if (living instanceof ServerPlayer serverPlayer) {
            PlayerFreezeManager.freeze(serverPlayer, PlayerFreezeManager.INDEFINITE);
            serverPlayer.displayClientMessage(
                    Component.translatable("message.changedextras.jam.sounds_great"), false);
        }

        if (living.level() instanceof ServerLevel serverLevel) {
            JamStatePacket.broadcast(serverLevel, living.getUUID(), true);
        }
    }

    /**
     * Ends the jam, unfreezes the player and syncs every client.
     *
     * @param musicGone true when the jam ended because the headphones were removed,
     *                 which sends the "The music is gone. Sad" chat message.
     */
    public static void stopJamming(LivingEntity living, boolean musicGone) {
        if (living == null || living.level().isClientSide()) return;

        CompoundTag data = living.getPersistentData();
        if (!data.getBoolean(JAM_ACTIVE_TAG)) return;

        data.remove(JAM_ACTIVE_TAG);
        data.remove(JAM_TICKS_TAG);

        PlayerFreezeManager.unfreeze(living.getUUID());

        if (living.level() instanceof ServerLevel serverLevel) {
            JamStatePacket.broadcast(serverLevel, living.getUUID(), false);
        }

        if (musicGone && living instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(
                    Component.translatable("message.changedextras.jam.music_gone"), false);
        }
    }

    public static class JamAbilityInstance extends AbstractAbilityInstance {
        public JamAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
            super(ability, entity);
        }

        @Override
        public boolean canUse() {
            LivingEntity living = this.entity.getEntity();
            if (living == null || !living.isAlive()) return false;

            int cooldown = living.getPersistentData().getInt(JAM_COOLDOWN_TAG);
            if (cooldown > 0) return false;

            if (isJamming(living)) return true;

            return isWearingHeadphones(living);
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

            data.putInt(JAM_COOLDOWN_TAG, TOGGLE_COOLDOWN_TICKS);
            getController().forceCooldown(TOGGLE_COOLDOWN_TICKS);

            // Toggle: pressing the ability while jamming stops the jam.
            if (data.getBoolean(JAM_ACTIVE_TAG)) {
                stopJamming(living, false);
                return;
            }

            if (!isWearingHeadphones(living)) return;

            startJamming(living);
        }

        @Override
        public void tick() {
            // INSTANT abilities are not ticked by the controller; maintenance lives in
            // JamAbilityEvents (headphones check, regeneration, cooldown).
        }

        @Override
        public void stopUsing() {}

        @Override
        public void saveData(CompoundTag tag) {
            super.saveData(tag);
            tag.putBoolean("JamActive", this.entity.getPersistentData().getBoolean(JAM_ACTIVE_TAG));
            tag.putInt("JamTicks", this.entity.getPersistentData().getInt(JAM_TICKS_TAG));
            tag.putInt("JamCooldown", this.entity.getPersistentData().getInt(JAM_COOLDOWN_TAG));
        }

        @Override
        public void readData(CompoundTag tag) {
            super.readData(tag);
            this.entity.getPersistentData().putBoolean(JAM_ACTIVE_TAG, tag.getBoolean("JamActive"));
            this.entity.getPersistentData().putInt(JAM_TICKS_TAG, tag.getInt("JamTicks"));
            this.entity.getPersistentData().putInt(JAM_COOLDOWN_TAG, tag.getInt("JamCooldown"));
        }
    }
}
