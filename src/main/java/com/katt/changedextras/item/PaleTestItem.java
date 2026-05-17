package com.katt.changedextras.item;

import net.foxyas.changedaddon.procedure.DoLatexInfectionTickHandle;
import net.ltxprogrammer.changed.process.Pale;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PaleTestItem extends Item {
    private static final String RESULT_READY_TAG = "changedextras.result_ready";
    private static final String RESULT_POSITIVE_TAG = "changedextras.positive_result";
    private static final String PALE_VALUE_TAG = "changedextras.pale_value";

    public PaleTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        boolean infected = DoLatexInfectionTickHandle.getInfected(player);
        int paleValue = infected ? Pale.getPaleExposure(player) : 0;

        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(RESULT_READY_TAG, true);
        tag.putBoolean(RESULT_POSITIVE_TAG, infected);
        tag.putInt(PALE_VALUE_TAG, paleValue);

        Component message = infected
                ? Component.translatable("message.changedextras.pale_test.positive", paleValue).withStyle(ChatFormatting.RED)
                : Component.translatable("message.changedextras.pale_test.negative", paleValue).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(message);

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    public static boolean isPositive(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(RESULT_READY_TAG) && tag.getBoolean(RESULT_POSITIVE_TAG);
    }

    public static boolean isNegative(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(RESULT_READY_TAG) && !tag.getBoolean(RESULT_POSITIVE_TAG);
    }
}
