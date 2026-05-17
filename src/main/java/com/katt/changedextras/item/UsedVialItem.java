package com.katt.changedextras.item;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UsedVialItem extends Item {
    private static final String PROCESSING_START_TAG = "changedextras.processing_start";
    private static final int PROCESSING_TICKS = 20 * 60;

    public UsedVialItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(PROCESSING_START_TAG)) {
            tag.putLong(PROCESSING_START_TAG, level.getGameTime());
            return;
        }

        long elapsed = level.getGameTime() - tag.getLong(PROCESSING_START_TAG);
        if (elapsed < PROCESSING_TICKS) {
            return;
        }

        ItemStack processedVial = ChangedExtras.PROCESSED_VIAL.get().getDefaultInstance();
        if (stack.hasTag()) {
            processedVial.setTag(stack.getTag().copy());
            processedVial.getTag().remove(PROCESSING_START_TAG);
        }
        player.getInventory().setItem(slotId, processedVial);
    }
}
