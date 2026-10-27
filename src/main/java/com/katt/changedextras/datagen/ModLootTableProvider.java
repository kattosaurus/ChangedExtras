package com.katt.changedextras.datagen;
import com.katt.changedextras.client.model.catte_bucket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;

public class ModLootTableProvider extends ModJsonProvider {
    public ModLootTableProvider(PackOutput output) {
        super(output, "Loot Tables: changedextras");
    }

    @Override
    protected void generate() {
        block("blue_stone_crystal", "changedextras:strange_crystal_shard", true);
        block("catte_bucket", "changedextras:catte_bucket", true);
        block("failed_route", "changedextras:failed_route", false);
        block("icecream_block", "changedextras:icecream", false);
        block("jammer_headphones", "changedextras:jammer_headphones", true);
        block("route", "changedextras:route", false);

        write("loot_tables/chests/armory", armory());
        write("loot_tables/chests/exp12_room", exp12Room());
        write("loot_tables/chests/hiddenchest", hiddenChest());
        write("loot_tables/chests/lab_cafeteria", labCafeteria());
        write("loot_tables/chests/recreationstorage", recreationStorage());
        write("loot_tables/entities/latex_artist", latexArtist());
    }

    private void write(String path, JsonObject json) {
        write(PackOutput.Target.DATA_PACK, "loot_tables", path.substring("loot_tables/".length()), json);
    }

    private void block(String id, String itemName, boolean survivesExplosion) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0D);
        pool.add("entries", entries(item(itemName)));
        if (survivesExplosion) {
            JsonObject condition = new JsonObject();
            condition.addProperty("condition", "minecraft:survives_explosion");
            pool.add("conditions", conditions(condition));
        }
        root.add("pools", pools(pool));
        write(PackOutput.Target.DATA_PACK, "loot_tables/blocks", id, root);
    }

    private JsonObject armory() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();

        JsonObject main = new JsonObject();
        main.add("rolls", uniform(5, 9));
        JsonArray entries = new JsonArray();
        for (String name : new String[]{
                "changed:tsc_baton", "changed:tsc_staff", "changed:tsc_shield",
                "minecraft:iron_sword", "minecraft:shield", "minecraft:bow",
                "minecraft:crossbow"}) {
            entries.add(weighted(name, 10));
        }
        entries.add(withFunction(weighted("minecraft:arrow", 10), setCountUniform(8, 24)));
        entries.add(weighted("changed:gas_mask", 10));
        main.add("entries", entries);
        pools.add(main);

        pools.add(single(0.02D, item("minecraft:totem_of_undying")));
        pools.add(single(0.05D, withFunction(item("minecraft:diamond_helmet"), setDamageUniform(0.4D, 0.6D))));
        pools.add(single(0.05D, withFunction(item("minecraft:diamond_chestplate"), setDamageUniform(0.4D, 0.6D))));
        pools.add(single(0.05D, withFunction(item("minecraft:diamond_leggings"), setDamageUniform(0.4D, 0.6D))));
        pools.add(single(0.05D, withFunction(item("minecraft:diamond_boots"), setDamageUniform(0.4D, 0.6D))));
        pools.add(single(0.0002D, item("changed_addon:transfur_totem")));
        pools.add(single(0.01D, item("changed_addon:electric_katana")));
        pools.add(single(0.01D, withFunction(item("changedextras:katt_syringe"), setCountConst(1))));
        pools.add(single(0.1D, item("changed_addon:crow_bar")));
        root.add("pools", pools);
        return root;
    }

    private JsonObject exp12Room() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();
        pools.add(singlePlain(withFunction(item("minecraft:netherite_scrap"), setCountRange(1, 2))));
        pools.add(singlePlain(withFunction(item("minecraft:diamond"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("minecraft:iron_ingot"), setCountRange(7, 15))));
        pools.add(singlePlain(withFunction(item("minecraft:gold_ingot"), setCountRange(1, 3))));
        pools.add(singlePlain(withFunction(item("changed_addon:orange_juice"), setCountRange(1, 16))));
        pools.add(singlePlain(withFunction(item("changed_addon:ammonia_particle"), setCountRange(5, 10))));
        pools.add(singlePlain(withFunction(item("changed_addon:laethin"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("changedextras:flask_of_tears"), setCountRange(1, 1))));
        pools.add(singlePlain(withFunction(item("changed_addon:pot_with_camonia"), setCountRange(2, 3))));
        pools.add(singlePlain(withFunction(item("changed_addon:syringe_with_litix_cammonia"), setCountRange(1, 2))));
        root.add("pools", pools);
        return root;
    }

    private JsonObject hiddenChest() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();
        pools.add(singlePlain(withFunction(item("minecraft:netherite_scrap"), setCountRange(1, 2))));
        pools.add(singlePlain(withFunction(item("minecraft:diamond"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("minecraft:iron_ingot"), setCountRange(7, 15))));
        pools.add(singlePlain(withFunction(item("minecraft:gold_ingot"), setCountRange(1, 3))));
        pools.add(singlePlain(withFunction(item("changed_addon:orange_juice"), setCountRange(1, 16))));
        pools.add(singlePlain(withFunction(item("changed_addon:ammonia_particle"), setCountRange(5, 10))));
        pools.add(singlePlain(withFunction(item("changed_addon:laethin"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("changed_addon:pot_with_camonia"), setCountRange(2, 3))));
        pools.add(singlePlain(withFunction(item("changed_addon:syringe_with_litix_cammonia"), setCountRange(1, 2))));
        root.add("pools", pools);
        return root;
    }

    private JsonObject labCafeteria() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 4);
        JsonArray entries = new JsonArray();
        entries.add(withFunction(weighted("changed:white_latex_goo", 1), setCountRange(2, 3)));
        entries.add(withFunction(weighted("minecraft:bread", 4), setCountRange(5, 15)));
        entries.add(withFunction(weighted("minecraft:apple", 4), setCountRange(2, 8)));
        entries.add(withFunction(weighted("changedextras:icecream", 3), setCountRange(3, 20)));
        entries.add(withFunction(weighted("changedextras:pink_icecream", 2), setCountRange(1, 4)));
        entries.add(withFunction(weighted("changedextras:mint_icecream", 2), setCountRange(1, 4)));
        entries.add(withFunction(weighted("changedextras:choco_icecream", 2), setCountRange(1, 4)));
        entries.add(withFunction(weighted("changedextras:strawberry_icecream", 2), setCountRange(1, 4)));
        entries.add(withFunction(weighted("changedextras:neapolitan_icecream", 2), setCountRange(1, 4)));
        pool.add("entries", entries);
        pools.add(pool);
        root.add("pools", pools);
        return root;
    }

    private JsonObject recreationStorage() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 4);
        JsonArray entries = new JsonArray();
        entries.add(withFunction(weighted("changed:white_latex_goo", 1), setCountRange(2, 3)));
        entries.add(withFunction(weighted("changed:syringe", 4), setCountRange(15, 35)));
        entries.add(withFunction(weighted("changed:dark_latex_goo", 4), setCountRange(15, 64)));
        entries.add(withFunction(weighted("changedextras:white_cat_syringe", 3), setCountRange(3, 5)));
        pool.add("entries", entries);
        pools.add(pool);
        root.add("pools", pools);
        return root;
    }

    private JsonObject latexArtist() {
        JsonObject root = chest();
        JsonArray pools = new JsonArray();
        pools.add(singlePlain(withFunction(item("minecraft:netherite_scrap"), setCountRange(2, 3))));
        pools.add(singlePlain(withFunction(item("changedextras:flask_of_tears"), setCountRange(1, 3))));
        pools.add(singlePlain(withFunction(item("changed_addon:laethin"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("changed_addon:golden_orange"), setCountRange(10, 15))));
        pools.add(singlePlain(withFunction(item("changedextras:the_palette"), setCountRange(1, 5))));
        pools.add(singlePlain(withFunction(item("changed_addon:litix_camonia"), setCountRange(3, 10))));
        root.add("pools", pools);
        return root;
    }

    private static JsonObject chest() {
        JsonObject o = new JsonObject();
        o.addProperty("type", "minecraft:chest");
        return o;
    }

    private static JsonObject item(String name) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "minecraft:item");
        o.addProperty("name", name);
        return o;
    }

    private static JsonObject weighted(String name, int weight) {
        JsonObject o = item(name);
        o.addProperty("weight", weight);
        return o;
    }

    private static JsonObject withFunction(JsonObject entry, JsonObject... functions) {
        JsonArray array = new JsonArray();
        for (JsonObject fn : functions) {
            array.add(fn);
        }
        entry.add("functions", array);
        return entry;
    }

    private static JsonArray entries(JsonObject... items) {
        JsonArray array = new JsonArray();
        for (JsonObject item : items) {
            array.add(item);
        }
        return array;
    }

    private static JsonArray pools(JsonObject... pool) {
        JsonArray array = new JsonArray();
        for (JsonObject p : pool) {
            array.add(p);
        }
        return array;
    }

    private static JsonArray conditions(JsonObject... condition) {
        JsonArray array = new JsonArray();
        for (JsonObject c : condition) {
            array.add(c);
        }
        return array;
    }

    private static JsonObject randomChance(double chance) {
        JsonObject o = new JsonObject();
        o.addProperty("condition", "minecraft:random_chance");
        o.addProperty("chance", chance);
        return o;
    }

    private static JsonObject single(double chance, JsonObject entry) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.add("conditions", conditions(randomChance(chance)));
        pool.add("entries", entries(entry));
        return pool;
    }

    private static JsonObject singlePlain(JsonObject entry) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.add("entries", entries(entry));
        return pool;
    }

    private static JsonObject uniform(int min, int max) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "minecraft:uniform");
        o.addProperty("min", min);
        o.addProperty("max", max);
        return o;
    }

    private static JsonObject setCountUniform(int min, int max) {
        JsonObject fn = new JsonObject();
        fn.addProperty("function", "minecraft:set_count");
        fn.add("count", uniform(min, max));
        return fn;
    }

    private static JsonObject setCountConst(int count) {
        JsonObject fn = new JsonObject();
        fn.addProperty("function", "minecraft:set_count");
        fn.addProperty("count", count);
        return fn;
    }

    private static JsonObject setCountRange(int min, int max) {
        JsonObject count = new JsonObject();
        count.addProperty("min", min);
        count.addProperty("max", max);
        JsonObject fn = new JsonObject();
        fn.addProperty("function", "minecraft:set_count");
        fn.add("count", count);
        return fn;
    }

    private static JsonObject setDamageUniform(double min, double max) {
        JsonObject damage = new JsonObject();
        damage.addProperty("type", "minecraft:uniform");
        damage.addProperty("min", min);
        damage.addProperty("max", max);
        JsonObject fn = new JsonObject();
        fn.addProperty("function", "minecraft:set_damage");
        fn.add("damage", damage);
        return fn;
    }
}
