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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurnFeralAbility extends AbstractAbility<TurnFeralAbility.TurnFeralAbilityInstance> {

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

                FeralCatManager.setFeral(player, nextFeral);

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
