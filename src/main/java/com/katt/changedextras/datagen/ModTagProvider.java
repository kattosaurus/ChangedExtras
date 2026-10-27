package com.katt.changedextras.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class ModTagProvider extends ModJsonProvider {
    private static final String[] OVERWORLD_BIOMES = {
            "minecraft:plains", "minecraft:sunflower_plains", "minecraft:forest",
            "minecraft:flower_forest", "minecraft:birch_forest", "minecraft:old_growth_birch_forest",
            "minecraft:dark_forest", "minecraft:taiga", "minecraft:old_growth_pine_taiga",
            "minecraft:old_growth_spruce_taiga", "minecraft:snowy_taiga", "minecraft:snowy_plains",
            "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_hills",
            "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:meadow",
            "minecraft:grove", "minecraft:snowy_slopes", "minecraft:stony_peaks",
            "minecraft:desert", "minecraft:badlands", "minecraft:wooded_badlands"
    };

    private static final String[] CLAW_ENTITIES = {
            "changedextras:latex_artist", "changedextras:conekat/female", "changedextras:conekat/male",
            "changedextras:mint_conekat/female", "changedextras:mint_conekat/male",
            "changedextras:choco_conekat/female", "changedextras:choco_conekat/male",
            "changedextras:strawberry_conekat/female", "changedextras:strawberry_conekat/male",
            "changedextras:neapolitan_conekat/female", "changedextras:neapolitan_conekat/male",
            "changedextras:latex_jammer", "changedextras:latex_katto", "changedextras:latex_hazzy",
            "changedextras:proto_bee", "changedextras:latex_white_cat", "changedextras:latex_catte",
            "changedextras:latex_haku", "changedextras:latex_thorniii", "changedextras:latex_carneline",
            "changedextras:latex_panther"
    };

    public ModTagProvider(PackOutput output) {
        super(output, "Tags: changedextras");
    }

    @Override
    protected void generate() {
        // changedextras
        tag("changedextras", "tags/changed/latex_variant", "robot_like", "changedextras:proto_bee");
        tag("changedextras", "tags/items", "latex_armor",
                "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots",
                "minecraft:golden_helmet", "minecraft:golden_chestplate", "minecraft:golden_leggings", "minecraft:golden_boots",
                "minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots");
        tag("changedextras", "tags/items", "latex_tools",
                "minecraft:stone_sword", "minecraft:stone_pickaxe", "minecraft:stone_axe", "minecraft:stone_shovel", "minecraft:stone_hoe",
                "minecraft:iron_sword", "minecraft:iron_pickaxe", "minecraft:iron_axe", "minecraft:iron_shovel", "minecraft:iron_hoe",
                "minecraft:golden_sword", "minecraft:golden_pickaxe", "minecraft:golden_axe", "minecraft:golden_shovel", "minecraft:golden_hoe",
                "minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_axe", "minecraft:diamond_shovel", "minecraft:diamond_hoe");
        tag("changedextras", "tags/items", "pills",
                "changedextras:hydroxyurea", "changedextras:crizanlizumab", "changedextras:voxelotor");
        tag("changedextras", "tags/items", "used_syringes", "changed:latex_syringe");
        tag("changedextras", "tags/worldgen/biome/has_structure", "biological_studies_facility", OVERWORLD_BIOMES);
        tag("changedextras", "tags/worldgen/biome/has_structure", "bunker", OVERWORLD_BIOMES);

        // changed
        tag("changed", "tags/entity_types", "humanoids",
                "minecraft:player", "minecraft:drowned", "minecraft:evoker", "minecraft:skeleton",
                "minecraft:stray", "minecraft:wither_skeleton", "minecraft:husk", "minecraft:illusioner",
                "minecraft:piglin", "minecraft:piglin_brute", "minecraft:pillager", "minecraft:villager",
                "minecraft:vindicator", "minecraft:wandering_trader", "minecraft:witch", "minecraft:zombie",
                "minecraft:zombie_villager", "minecraft:zombified_piglin");
        tag("changed", "tags/entity_types", "latexes",
                "changedextras:latex_artist", "changedextras:latex_jammer", "changedextras:latex_katto",
                "changedextras:latex_hazzy", "changedextras:latex_white_cat", "changedextras:latex_catte",
                "changedextras:latex_haku", "changedextras:latex_thorniii", "changedextras:latex_carneline",
                "changedextras:latex_panther");
        tag("changed", "tags/entity_types", "organic_latex",
                "changedextras:conekat/male", "changedextras:conekat/female",
                "changedextras:mint_conekat/male", "changedextras:mint_conekat/female",
                "changedextras:choco_conekat/male", "changedextras:choco_conekat/female",
                "changedextras:strawberry_conekat/male", "changedextras:strawberry_conekat/female",
                "changedextras:neapolitan_conekat/male", "changedextras:neapolitan_conekat/female",
                "changedextras:proto_bee", "changedextras:scp_009");
        tag("changed", "tags/items", "body",
                "changed:sports_bra", "changed:black_tshirt", "changed:wetsuit",
                "changed:white_tshirt", "changedextras:long_sleeve_shirt");
        tag("changed", "tags/items", "face", "changedextras:jammer_headphones");

        // changed_addon
        tag("changed_addon", "tags/changed/latex_variant", "able_to_carry", "changedextras:latex_thorniii");
        tag("changed_addon", "tags/changed/latex_variant", "cat_like",
                "changedextras:latex_artist", "changedextras:conekat/female", "changedextras:conekat/male",
                "changedextras:mint_conekat/female", "changedextras:mint_conekat/male",
                "changedextras:choco_conekat/female", "changedextras:choco_conekat/male",
                "changedextras:strawberry_conekat/female", "changedextras:strawberry_conekat/male",
                "changedextras:neapolitan_conekat/female", "changedextras:neapolitan_conekat/male",
                "changedextras:latex_katto", "changedextras:latex_hazzy",
                "changedextras:latex_white_cat", "changedextras:latex_catte", "changedextras:latex_carneline",
                "changedextras:latex_panther");
        tag("changed_addon", "tags/changed/latex_variant", "has_claws", CLAW_ENTITIES);
        tag("changed_addon", "tags/changed/latex_variant", "wolf_like", "changedextras:latex_jammer");
        tag("changed_addon", "tags/entity_types", "has_claws", CLAW_ENTITIES);
        tag("changed_addon", "tags/transfur_variant", "able_to_carry", "changedextras:latex_thorniii");

        // minecraft
        tag("minecraft", "tags/blocks/mineable", "pickaxe",
                "changedextras:scp009_crystal", "changedextras:domain_barrier");
        tag("minecraft", "tags/blocks", "overworld_carver_replaceables", "changedextras:blue_stone");
        tag("minecraft", "tags/painting_variant", "placeable", "changedextras:the_eye");
        tag("minecraft", "tags/worldgen/biome", "is_overworld", "changedextras:blue_cave");

        // forge
        tag("forge", "tags/worldgen/biome", "is_cave", "changedextras:blue_cave");
    }

    private void tag(String namespace, String directory, String name, String... values) {
        JsonObject root = new JsonObject();
        root.addProperty("replace", false);
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(new JsonPrimitive(value));
        }
        root.add("values", array);
        write(PackOutput.Target.DATA_PACK, directory, new ResourceLocation(namespace, name), root);
    }
}
