package com.katt.changedextras.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class PillItem extends Item {
    private final int dosageIntervalTicks;
    private final String dosageTag;
    private final Supplier<List<MobEffectInstance>> standardEffectsSupplier;
    private final Supplier<List<MobEffectInstance>> overdoseEffectsSupplier;
    private final String dosageDescription;
    private final List<String> standardEffectsDescriptions;
    private final List<String> overdoseEffectsDescriptions;

    public PillItem(
            Properties properties,
            int dosageIntervalTicks,
            String dosageTag,
            String dosageDescription,
            Supplier<List<MobEffectInstance>> standardEffectsSupplier,
            List<String> standardEffectsDescriptions,
            Supplier<List<MobEffectInstance>> overdoseEffectsSupplier,
            List<String> overdoseEffectsDescriptions
    ) {
        super(properties.stacksTo(16).food(new FoodProperties.Builder().alwaysEat().nutrition(0).saturationMod(0).build()));
        this.dosageIntervalTicks = dosageIntervalTicks;
        this.dosageTag = dosageTag;
        this.dosageDescription = dosageDescription;
        this.standardEffectsSupplier = standardEffectsSupplier;
        this.standardEffectsDescriptions = standardEffectsDescriptions;
        this.overdoseEffectsSupplier = overdoseEffectsSupplier;
        this.overdoseEffectsDescriptions = overdoseEffectsDescriptions;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16; // Quick consumption for pills
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof ServerPlayer player) {
            CompoundTag data = player.getPersistentData();
            long now = level.getGameTime();
            boolean isOverdose = data.contains(dosageTag) && (now - data.getLong(dosageTag) < dosageIntervalTicks);
            data.putLong(dosageTag, now);

            if (isOverdose) {
                for (MobEffectInstance effect : overdoseEffectsSupplier.get()) {
                    player.addEffect(new MobEffectInstance(effect));
                }
                player.displayClientMessage(
                        Component.translatable("message.changedextras.pill.overdose").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        true
                );
            } else {
                for (MobEffectInstance effect : standardEffectsSupplier.get()) {
                    player.addEffect(new MobEffectInstance(effect));
                }
                player.displayClientMessage(
                        Component.translatable("message.changedextras.pill.taken").withStyle(ChatFormatting.GREEN),
                        true
                );
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.changedextras.pill.recommended_dosage", dosageDescription).withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("tooltip.changedextras.pill.standard_effects").withStyle(ChatFormatting.AQUA));
        for (String line : standardEffectsDescriptions) {
            tooltipComponents.add(Component.literal(" \u2022 ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(line).withStyle(ChatFormatting.GRAY)));
        }
        tooltipComponents.add(Component.translatable("tooltip.changedextras.pill.overdose_effects").withStyle(ChatFormatting.RED));
        for (String line : overdoseEffectsDescriptions) {
            tooltipComponents.add(Component.literal(" \u2022 ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(line).withStyle(ChatFormatting.DARK_RED)));
        }
    }
}
