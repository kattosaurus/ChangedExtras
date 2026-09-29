package com.katt.changedextras.ability;

import com.katt.changedextras.common.FeralCatManager;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurnFeralAbility extends AbstractAbility<TurnFeralAbility.TurnFeralAbilityInstance> {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public TurnFeralAbility() {
        super(TurnFeralAbilityInstance::new);
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.turn_feral.name");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return ResourceLocation.tryParse("changedextras:textures/abilities/turn_feral.png");
    }

    @Nullable
    @Override
    public Component getSelectedDisplayText(IAbstractChangedEntity entity) {
        if (entity != null && entity.getEntity() instanceof Player player) {
            if (FeralCatManager.isFeral(player)) {
                return Component.translatable("ability.changedextras.turn_feral.can_humanoid");
            }
        }
        return Component.translatable("ability.changedextras.turn_feral.can");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.turn_feral.description"));
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 32;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 30;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (entity == null || entity.getEntity() == null) {
            return false;
        }
        LivingEntity living = entity.getEntity();
        return living.isAlive();
    }

    /**
     * Moves every equipped armor piece (and anything worn in an armor slot,
     * which includes Changed clothing that uses armor slots) back into the
     * inventory. If the inventory is full, the item is dropped at the player's feet.
     * Server side only.
     */
    public static void unequipAll(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack worn = player.getItemBySlot(slot);
            if (worn.isEmpty()) {
                continue;
            }
            ItemStack toMove = worn.copy();
            player.setItemSlot(slot, ItemStack.EMPTY);
            if (!player.getInventory().add(toMove)) {
                player.drop(toMove, false);
            }
        }
        player.inventoryMenu.broadcastChanges();
    }

    public static class TurnFeralAbilityInstance extends AbstractAbilityInstance {

        public TurnFeralAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
            super(ability, entity);
        }

        @Override
        public boolean canUse() {
            return this.entity != null && this.entity.getEntity() != null && this.entity.getEntity().isAlive();
        }

        @Override
        public boolean canKeepUsing() {
            return true;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            if (entity.getEntity() instanceof Player player) {
                Component text = ability.getSelectedDisplayText(this.entity);
                if (text != null) {
                    player.displayClientMessage(text, true);
                }
            }
        }

        @Override
        public void startUsing() {
            if (entity.getLevel().isClientSide()) {
                return;
            }

            if (entity.getEntity() instanceof Player player) {
                boolean currentlyFeral = FeralCatManager.isFeral(player);
                boolean nextFeral = !currentlyFeral;

                if (nextFeral) {
                    unequipAll(player);
                }

                FeralCatManager.setFeral(player, nextFeral);

                // Forces Minecraft to recompute hitbox + eye height (see EntityEvent.Size handler)
                player.refreshDimensions();

                Level level = player.level();
                if (nextFeral) {
                    if (ChangedSounds.TRANSFUR_BY_LATEX.isPresent()) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ChangedSounds.TRANSFUR_BY_LATEX.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.translatable("ability.changedextras.turn_feral.turned_feral"), true);
                } else {
                    if (ChangedSounds.TRANSFUR_BY_LATEX.isPresent()) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ChangedSounds.TRANSFUR_BY_LATEX.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.CAT_PURR, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.translatable("ability.changedextras.turn_feral.turned_humanoid"), true);
                }

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.POOF,
                            player.getX(), player.getY() + 0.3D, player.getZ(),
                            10, 0.2D, 0.2D, 0.2D, 0.05D);
                }
            }
        }

        @Override
        public void tick() {
        }

        @Override
        public void stopUsing() {
        }
    }
}