package com.katt.changedextras.inventory;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.init.ChangedExtrasMenus;
import com.katt.changedextras.item.PillBottleItem;
import com.katt.changedextras.item.PillItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PillBottleMenu extends AbstractContainerMenu {
    public static final int BOTTLE_SLOTS = 6;
    public static final TagKey<Item> PILLS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "pills"));

    private final ItemStack bottleStack;
    private final InteractionHand hand;
    private final int lockedSlotIndex;
    private final ItemStackHandler bottleInventory;

    public static PillBottleMenu createClientMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        InteractionHand hand = extraData.readEnum(InteractionHand.class);
        ItemStack bottleStack = extraData.readItem();
        return new PillBottleMenu(containerId, playerInventory, bottleStack, hand);
    }

    public PillBottleMenu(int containerId, Inventory playerInventory, ItemStack bottleStack, InteractionHand hand) {
        super(ChangedExtrasMenus.PILL_BOTTLE_MENU.get(), containerId);
        this.bottleStack = bottleStack;
        this.hand = hand;
        this.lockedSlotIndex = hand == InteractionHand.MAIN_HAND ? playerInventory.selected : 40;

        this.bottleInventory = new ItemStackHandler(BOTTLE_SLOTS) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return isPillItem(stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                saveBottleInventory();
            }
        };

        CompoundTag tag = bottleStack.getTag();
        if (tag != null && tag.contains(PillBottleItem.INVENTORY_TAG)) {
            bottleInventory.deserializeNBT(tag.getCompound(PillBottleItem.INVENTORY_TAG));
        }

        // Add 6 pill bottle slots (x=66, y=17)
        for (int i = 0; i < BOTTLE_SLOTS; i++) {
            int x = 66 + i * 18;
            int y = 18;
            this.addSlot(new SlotItemHandler(bottleInventory, i, x, y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return isPillItem(stack);
                }
            });
        }

        // Add 27 Player Inventory slots (centered in 182px wide GUI, x=11, y=45)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = 11 + col * 18;
                int y = 45 + row * 18;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y) {
                    @Override
                    public boolean mayPickup(Player playerIn) {
                        return slotIndex != lockedSlotIndex;
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return slotIndex != lockedSlotIndex;
                    }
                });
            }
        }

        // Add 9 Player Hotbar slots (centered in 182px wide GUI, x=11, y=103)
        for (int col = 0; col < 9; col++) {
            int slotIndex = col;
            int x = 11 + col * 18;
            int y = 103;
            this.addSlot(new Slot(playerInventory, slotIndex, x, y) {
                @Override
                public boolean mayPickup(Player playerIn) {
                    return slotIndex != lockedSlotIndex;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return slotIndex != lockedSlotIndex;
                }
            });
        }
    }

    public static boolean isPillItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof PillBottleItem) return false;
        if (stack.getItem() instanceof PillItem) return true;
        return stack.is(PILLS_TAG);
    }

    public void saveBottleInventory() {
        CompoundTag tag = bottleStack.getOrCreateTag();
        tag.put(PillBottleItem.INVENTORY_TAG, bottleInventory.serializeNBT());
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = slots.get(slotId);
            if (slot.container == player.getInventory()) {
                int rawIndex = slot.getContainerSlot();
                if (rawIndex == lockedSlotIndex) {
                    return;
                }
            }
        }
        super.clicked(slotId, button, clickType, player);
        saveBottleInventory();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < BOTTLE_SLOTS) {
                // Move from bottle into player inventory
                if (!this.moveItemStackTo(itemstack1, BOTTLE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory into bottle
                if (isPillItem(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, BOTTLE_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < BOTTLE_SLOTS + 27) {
                    if (!this.moveItemStackTo(itemstack1, BOTTLE_SLOTS + 27, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, BOTTLE_SLOTS, BOTTLE_SLOTS + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            saveBottleInventory();
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack currentStack = hand == InteractionHand.MAIN_HAND ? player.getMainHandItem() : player.getOffhandItem();
        return !currentStack.isEmpty() && currentStack.getItem() instanceof PillBottleItem;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveBottleInventory();
    }
}
