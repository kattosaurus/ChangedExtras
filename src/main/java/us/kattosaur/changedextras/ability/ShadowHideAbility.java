package us.kattosaur.changedextras.ability;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import us.kattosaur.changedextras.common.shadow.ShadowHideManager;

import java.util.List;

/**
 * Void Cat toggle: slip into a shadow (light level 5 or less) and become
 * completely undetectable. Press again to emerge. While hidden the player
 * cannot sprint and can only be struck by attacking the block they occupy.
 */
public class ShadowHideAbility extends AbstractAbility<ShadowHideAbility.ShadowHideAbilityInstance> {
    public static final int TOGGLE_COOLDOWN_TICKS = 10;

    public ShadowHideAbility() {
        super(ShadowHideAbilityInstance::new);
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.shadow_hide.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.shadow_hide.description"));
    }

    @Nullable
    @Override
    public Component getSelectedDisplayText(IAbstractChangedEntity entity) {
        LivingEntity living = entity == null ? null : entity.getEntity();
        if (!(living instanceof Player player)) {
            return null;
        }
        if (ShadowHideManager.isHidden(player)) {
            return Component.translatable("ability.changedextras.shadow_hide.can_emerge");
        }
        if (ShadowHideManager.canEnterShadow(player)) {
            return Component.translatable("ability.changedextras.shadow_hide.can_hide");
        }
        return null;
    }

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
        LivingEntity living = entity == null ? null : entity.getEntity();
        if (!(living instanceof Player player) || !player.isAlive()) {
            return false;
        }
        if (player.getPersistentData().getInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG) > 0) {
            return false;
        }
        return ShadowHideManager.isHidden(player) || ShadowHideManager.canEnterShadow(player);
    }

    public static class ShadowHideAbilityInstance extends AbstractAbilityInstance {
        public ShadowHideAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
            super(ability, entity);
        }

        @Override
        public boolean canUse() {
            return ability.canUse(entity);
        }

        @Override
        public boolean canKeepUsing() {
            return false;
        }

        @Override
        public void startUsing() {
            LivingEntity living = entity == null ? null : entity.getEntity();
            if (!(living instanceof Player player) || player.level().isClientSide()) {
                return;
            }

            CompoundTag data = player.getPersistentData();
            data.putInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG, TOGGLE_COOLDOWN_TICKS);
            getController().forceCooldown(TOGGLE_COOLDOWN_TICKS);

            if (ShadowHideManager.isHidden(player)) {
                ShadowHideManager.setHidden(player, false);
                player.displayClientMessage(
                        Component.translatable("ability.changedextras.shadow_hide.revealed"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 0.7F, 1.2F);
                return;
            }

            if (!ShadowHideManager.canEnterShadow(player)) {
                player.displayClientMessage(
                        Component.translatable("ability.changedextras.shadow_hide.too_bright"), true);
                return;
            }

            ShadowHideManager.setHidden(player, true);
            player.displayClientMessage(
                    Component.translatable("ability.changedextras.shadow_hide.hidden"), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CAT_PURR, SoundSource.PLAYERS, 0.7F, 0.8F);
        }

        @Override
        public void tick() {
            // INSTANT abilities do not tick; maintenance lives in ShadowHideEvents.
        }

        @Override
        public void stopUsing() {}

        @Override
        public void saveData(CompoundTag tag) {
            super.saveData(tag);
            tag.putBoolean("ShadowHidden", this.entity.getPersistentData().getBoolean(ShadowHideManager.HIDDEN_TAG));
            tag.putInt("ShadowCooldown", this.entity.getPersistentData().getInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG));
        }

        @Override
        public void readData(CompoundTag tag) {
            super.readData(tag);
            this.entity.getPersistentData().putBoolean(ShadowHideManager.HIDDEN_TAG, tag.getBoolean("ShadowHidden"));
            this.entity.getPersistentData().putInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG, tag.getInt("ShadowCooldown"));
        }
    }
}
