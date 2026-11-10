package com.katt.changedextras.ability;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Condenses the user's hunger into a strange crystal shard.
 */
public class GenerateStrangeCrystalAbility extends SimpleAbility {
    private static final int HUNGER_COST = 6;
    private static final int COOLDOWN_TICKS = 40;

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.generate_strange_crystal.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.generate_strange_crystal.description"));
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return ResourceLocation.tryParse("changedextras:textures/item/strange_crystal_shard.png");
    }

    @Override
    public AbstractAbility.UseType getUseType(IAbstractChangedEntity entity) {
        return AbstractAbility.UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return COOLDOWN_TICKS;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        LivingEntity living = entity.getEntity();
        if (living == null || !living.isAlive()) {
            return false;
        }
        if (living instanceof Player player) {
            return player.getFoodData().getFoodLevel() >= HUNGER_COST;
        }
        return true;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        LivingEntity living = entity.getEntity();
        if (living == null || living.level().isClientSide()) {
            return;
        }

        if (living instanceof Player player) {
            FoodData foodData = player.getFoodData();
            foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - HUNGER_COST));
            foodData.addExhaustion(1.0F);
        }

        ItemStack shard = new ItemStack(ChangedExtras.STRANGE_CRYSTAL_SHARD.get());
        if (!(living instanceof Player player) || !player.getInventory().add(shard)) {
            living.spawnAtLocation(shard);
        }

        if (living.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    living.getX(), living.getY() + living.getBbHeight() * 0.5D, living.getZ(),
                    12, 0.3D, 0.3D, 0.3D, 0.02D);
        }
        living.level().playSound(null, living.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK,
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
