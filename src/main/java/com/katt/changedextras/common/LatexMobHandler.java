package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.ai.LatexAiUtil;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ltxprogrammer.changed.entity.beast.LatexTaur;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public class LatexMobHandler {
    private static final TagKey<Item> LATEX_TOOLS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_tools"));
    private static final TagKey<Item> LATEX_ARMOR = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_armor"));

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private static final Item[] TOOL_POOL = {
            Items.STONE_SWORD,
            Items.STONE_PICKAXE,
            Items.STONE_AXE,
            Items.STONE_SHOVEL,
            Items.STONE_HOE,
            Items.IRON_SWORD,
            Items.IRON_PICKAXE,
            Items.IRON_AXE,
            Items.IRON_SHOVEL,
            Items.IRON_HOE,
            Items.GOLDEN_SWORD,
            Items.GOLDEN_PICKAXE,
            Items.GOLDEN_AXE,
            Items.GOLDEN_SHOVEL,
            Items.GOLDEN_HOE,
            Items.DIAMOND_SWORD,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_AXE,
            Items.DIAMOND_SHOVEL,
            Items.DIAMOND_HOE
    };

    private static final Item[][] ARMOR_POOLS = {
            {Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS},
            {Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS},
            {Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS}
    };

    private LatexMobHandler() {
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        Mob mob = event.getEntity();
        if (!isLatexCreature(mob)) {
            return;
        }

        if (event.getSpawnType() == MobSpawnType.CONVERSION) {
            return;
        }

        if (!mob.level().getGameRules().getBoolean(ChangedExtrasGameRules.LATEX_EQUIPMENT_ENABLED)) {
            return;
        }

        RandomSource random = mob.getRandom();
        float toolChance = ChangedExtrasGameRules.getToolChance(mob.level().getGameRules());
        if (random.nextFloat() < toolChance) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(getRandomTool(random)));
        }

        float armorChance = ChangedExtrasGameRules.getArmorChance(mob.level().getGameRules());
        if (random.nextFloat() < armorChance) {
            equipRandomArmor(mob, random);
        }
    }

    private static boolean isLatexCreature(Mob mob) {
        return mob instanceof ChangedEntity || LatexAiUtil.isInLatexesTag(mob);
    }

    private static Item getRandomTool(RandomSource random) {
        List<Item> taggedTools = tagItems(LATEX_TOOLS);
        if (!taggedTools.isEmpty()) {
            return taggedTools.get(random.nextInt(taggedTools.size()));
        }
        return TOOL_POOL[random.nextInt(TOOL_POOL.length)];
    }

    private static void equipRandomArmor(Mob mob, RandomSource random) {
        List<Item> taggedArmor = tagItems(LATEX_ARMOR);
        if (taggedArmor.isEmpty()) {
            equipRandomArmorSet(mob, random);
            return;
        }
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            List<Item> matches = new ArrayList<>();
            for (Item item : taggedArmor) {
                if (item instanceof ArmorItem armor && armor.getEquipmentSlot() == slot) {
                    matches.add(item);
                }
            }
            if (!matches.isEmpty()) {
                mob.setItemSlot(slot, new ItemStack(matches.get(random.nextInt(matches.size()))));
            }
        }
    }

    private static void equipRandomArmorSet(Mob mob, RandomSource random) {
        Item[] armorSet = ARMOR_POOLS[random.nextInt(ARMOR_POOLS.length)];
        mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(armorSet[0]));
        mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(armorSet[1]));
        mob.setItemSlot(EquipmentSlot.LEGS, new ItemStack(armorSet[2]));
        mob.setItemSlot(EquipmentSlot.FEET, new ItemStack(armorSet[3]));
    }

    private static List<Item> tagItems(TagKey<Item> tag) {
        List<Item> items = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            items.add(holder.value());
        }
        return items;
    }
}