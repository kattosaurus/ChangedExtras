package us.kattosaur.changedextras.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/**
 * A simple inventory for latex creatures.
 * Slots 0-8: general item storage (9 slots)
 * Slots 9-12: armor (feet, legs, chest, head)
 */
public class LatexCreatureInventory extends SimpleContainer {

    public static final int MAIN_SLOTS  = 9;
    public static final int ARMOR_SLOTS = 4;
    public static final int TOTAL_SLOTS = MAIN_SLOTS + ARMOR_SLOTS;

    // Armor slot indices within this inventory
    public static final int SLOT_FEET  = MAIN_SLOTS;
    public static final int SLOT_LEGS  = MAIN_SLOTS + 1;
    public static final int SLOT_CHEST = MAIN_SLOTS + 2;
    public static final int SLOT_HEAD  = MAIN_SLOTS + 3;

    private final Mob owner;

    public LatexCreatureInventory(Mob owner) {
        super(TOTAL_SLOTS);
        this.owner = owner;
    }

    // ── Armor convenience getters ─────────────────────────────────────────────

    public ItemStack getHelmet()     { return getItem(SLOT_HEAD); }
    public ItemStack getChestplate() { return getItem(SLOT_CHEST); }
    public ItemStack getLeggings()   { return getItem(SLOT_LEGS); }
    public ItemStack getBoots()      { return getItem(SLOT_FEET); }

    public void setHelmet(ItemStack stack)     { setItem(SLOT_HEAD,  stack); }
    public void setChestplate(ItemStack stack) { setItem(SLOT_CHEST, stack); }
    public void setLeggings(ItemStack stack)   { setItem(SLOT_LEGS,  stack); }
    public void setBoots(ItemStack stack)      { setItem(SLOT_FEET,  stack); }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putByte("Slot", (byte) i);
                stack.save(entry);
                list.add(entry);
            }
        }
        tag.put("LatexInventory", list);
    }

    public void load(CompoundTag tag) {
        clearContent();
        if (!tag.contains("LatexInventory")) return;
        ListTag list = tag.getList("LatexInventory", 10); // 10 = CompoundTag
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 0xFF;
            if (slot < TOTAL_SLOTS) {
                setItem(slot, ItemStack.of(entry));
            }
        }
    }

    // ── Drop everything on death ──────────────────────────────────────────────

    public void dropAllItems() {
        if (owner.level().isClientSide) return;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                owner.spawnAtLocation(stack);
                setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
