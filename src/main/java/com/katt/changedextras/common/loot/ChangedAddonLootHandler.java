package com.katt.changedextras.common.loot;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.compat.ChangedAddonCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Re-adds Changed Addon loot entries when the addon is installed. The generated loot table JSON
 * cannot reference addon items directly, otherwise it fails to parse without the addon.
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class ChangedAddonLootHandler {
    private ChangedAddonLootHandler() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!ChangedAddonCompat.isLoaded()) {
            return;
        }
        ResourceLocation name = event.getName();
        if (!ChangedExtras.MODID.equals(name.getNamespace())) {
            return;
        }
        LootTable table = event.getTable();
        switch (name.getPath()) {
            case "chests/armory" -> {
                addLoot(table, 0.0002F, "changed_addon:transfur_totem", 0, 0);
                addLoot(table, 0.01F, "changed_addon:electric_katana", 0, 0);
                addLoot(table, 0.1F, "changed_addon:crow_bar", 0, 0);
            }
            case "chests/blue_cave_couch" ->
                    addLoot(table, 0.1F, "changed_addon:orange_juice", 1, 4);
            case "chests/exp12_room" -> {
                addLoot(table, 1.0F, "changed_addon:orange_juice", 1, 16);
                addLoot(table, 1.0F, "changed_addon:ammonia_particle", 5, 10);
                addLoot(table, 1.0F, "changed_addon:laethin", 1, 5);
                addLoot(table, 1.0F, "changed_addon:pot_with_camonia", 2, 3);
                addLoot(table, 1.0F, "changed_addon:syringe_with_litix_cammonia", 1, 2);
            }
            case "chests/hiddenchest" -> {
                addLoot(table, 1.0F, "changed_addon:orange_juice", 1, 5);
                addLoot(table, 1.0F, "changed_addon:pot_with_camonia", 0, 1);
            }
            case "entities/latex_artist" -> {
                addLoot(table, 1.0F, "changed_addon:laethin", 1, 5);
                addLoot(table, 1.0F, "changed_addon:golden_orange", 10, 15);
                addLoot(table, 1.0F, "changed_addon:litix_camonia", 3, 10);
            }
            default -> {
            }
        }
    }

    private static void addLoot(LootTable table, float chance, String itemId, int min, int max) {
        ResourceLocation id = ResourceLocation.tryParse(itemId);
        if (id == null) {
            return;
        }
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null) {
            return;
        }

        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
        if (chance > 0.0F && chance < 1.0F) {
            pool.when(LootItemRandomChanceCondition.randomChance(chance));
        }

        LootPoolSingletonContainer.Builder<?> entry = LootItem.lootTableItem(item);
        if (min > 0 || max > 0) {
            entry.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
        }
        pool.add(entry);
        table.addPool(pool.build());
    }
}
