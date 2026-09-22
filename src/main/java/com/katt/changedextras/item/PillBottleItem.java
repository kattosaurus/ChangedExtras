package com.katt.changedextras.item;

import com.katt.changedextras.inventory.PillBottleMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PillBottleItem extends Item {
    public static final String INVENTORY_TAG = "Inventory";

    public PillBottleItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(
                    serverPlayer,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new PillBottleMenu(id, inv, p.getItemInHand(hand), hand),
                            Component.translatable("container.changedextras.pill_bottle")
                    ),
                    buf -> {
                        buf.writeEnum(hand);
                        buf.writeItem(stack);
                    }
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.changedextras.pill_bottle.capacity").withStyle(ChatFormatting.GOLD));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(INVENTORY_TAG)) {
            ItemStackHandler handler = new ItemStackHandler(PillBottleMenu.BOTTLE_SLOTS);
            handler.deserializeNBT(tag.getCompound(INVENTORY_TAG));
            boolean empty = true;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack item = handler.getStackInSlot(i);
                if (!item.isEmpty()) {
                    empty = false;
                    tooltipComponents.add(Component.literal(" \u2022 ").withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal(item.getCount() + "x ").withStyle(ChatFormatting.GRAY))
                            .append(item.getHoverName()));
                }
            }
            if (empty) {
                tooltipComponents.add(Component.translatable("tooltip.changedextras.pill_bottle.empty").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.changedextras.pill_bottle.empty").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }
}
