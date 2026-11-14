package us.kattosaur.changedextras.item;

import net.ltxprogrammer.changed.item.ShirtItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class LongSleeveShirt extends ShirtItem implements DyeableLeatherItem {

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0xFFFFFF;
        CompoundTag display = tag.getCompound("display");
        return display.contains("color") ? display.getInt("color") : 0xFFFFFF;
    }
}