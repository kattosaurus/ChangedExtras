package us.kattosaur.changedextras.datagen;

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
            "changedextras:latex_panther", "changedextras:latex_kaiju", "changedextras:void_cat"
    };

    private static final String[] LATEX_VARIANTS = {
            "changedextras:latex_white_cat", "changedextras:latex_katto", "changedextras:latex_jammer",
            "changedextras:latex_artist", "changedextras:latex_hazzy"
    };

    private static final String[] NONLATEX_VARIANTS = {
            "changedextras:conekat/male", "changedextras:conekat/female",
            "changedextras:mint_conekat/male", "changedextras:mint_conekat/female",
            "changedextras:choco_conekat/male", "changedextras:choco_conekat/female",
            "changedextras:strawberry_conekat/male", "changedextras:strawberry_conekat/female",
            "changedextras:neapolitan_conekat/male", "changedextras:neapolitan_conekat/female",
            "changedextras:void_cat", "changedextras:proto_bee", "changedextras:scp_009", "changedextras:latex_catte",
            "changedextras:latex_haku", "changedextras:latex_thorniii", "changedextras:latex_carneline",
            "changedextras:latex_panther", "changedextras:latex_kaiju"
    };

    private static final String[] NIGHT_EYES_VARIANTS = {
            "changedextras:conekat/male", "changedextras:conekat/female",
            "changedextras:mint_conekat/male", "changedextras:mint_conekat/female",
            "changedextras:choco_conekat/male", "changedextras:choco_conekat/female",
            "changedextras:strawberry_conekat/male", "changedextras:strawberry_conekat/female",
            "changedextras:neapolitan_conekat/male", "changedextras:neapolitan_conekat/female",
            "changedextras:latex_white_cat", "changedextras:void_cat", "changedextras:latex_katto",
            "changedextras:latex_jammer", "changedextras:proto_bee", "changedextras:latex_artist",
            "changedextras:scp_009", "changedextras:latex_hazzy", "changedextras:latex_catte",
            "changedextras:latex_haku", "changedextras:latex_carneline", "changedextras:latex_panther",
            "changedextras:latex_kaiju"
    };

    private static final String[] DIGITIGRADE_VARIANTS = {
            "changedextras:conekat/male", "changedextras:conekat/female",
            "changedextras:mint_conekat/male", "changedextras:mint_conekat/female",
            "changedextras:choco_conekat/male", "changedextras:choco_conekat/female",
            "changedextras:strawberry_conekat/male", "changedextras:strawberry_conekat/female",
            "changedextras:neapolitan_conekat/male", "changedextras:neapolitan_conekat/female",
            "changedextras:latex_white_cat", "changedextras:void_cat", "changedextras:latex_katto",
            "changedextras:latex_jammer", "changedextras:proto_bee", "changedextras:latex_artist",
            "changedextras:scp_009", "changedextras:latex_hazzy", "changedextras:latex_catte",
            "changedextras:latex_thorniii", "changedextras:latex_carneline",
            "changedextras:latex_panther", "changedextras:latex_kaiju"
    };

    // Changed Addon Plus registers its forms in changed:latex_variant but does not add them to any
    // of the tags that Changed 0.16.0's ability trees select. Keep those assignments here so each
    // addon form receives the same progression trees as the equivalent Changed/Changed Extras form.
    private static final String[] ADDON_LATEX_VARIANTS = {
            "changed_addon:form_puro_kind/male", "changed_addon:form_puro_kind/female",
            "changed_addon:form_biosynth_snow_leopard/male", "changed_addon:form_biosynth_snow_leopard/female",
            "changed_addon:form_white_fox", "changed_addon:form_latex_snow_fox/male",
            "changed_addon:form_latex_snow_fox/female", "changed_addon:form_dazed_latex",
            "changed_addon:form_buff_dazed_latex", "changed_addon:form_mirror_white_tiger_female",
            "changed_addon:form_snepsi_leopard", "changed_addon:form_foxta_foxy",
            "changed_addon:form_latex_kitsune/male", "changed_addon:form_latex_kitsune/female",
            "changed_addon:form_latex_calico_cat", "changed_addon:form_latex_dragon_snow_leopard_shark",
            "changed_addon:form_latex_snep_feral", "changed_addon:form_latex_snep_feral_form",
            "changed_addon:form_luminarctic_leopard/male", "changed_addon:form_luminarctic_leopard/female",
            "changed_addon:form_latex_squid_tiger_shark", "changed_addon:form_luminara_flower_beast",
            "changed_addon:form_latex_white_snow_leopard/male", "changed_addon:form_latex_white_snow_leopard/female",
            "changed_addon:form_latex_cheetah/male", "changed_addon:form_latex_cheetah/female",
            "changed_addon:form_dark_latex_yufeng_queen", "changed_addon:form_latex_snow_leopard_partial",
            "changed_addon:form_void_fox", "changed_addon:form_avali",
            "changed_addon:form_avali_zergodmaster", "changed_addon:form_protogen",
            "changed_addon:form_prototype", "changed_addon:form_foxyas",
            "changed_addon:form_protogen_0senia0", "changed_addon:form_latex_kayla_shark",
            "changed_addon:form_wolfy", "changed_addon:form_reyn", "changed_addon:form_lynx",
            "changed_addon:form_fengqi_wolf", "changed_addon:form_latex_border_collie",
            "changed_addon:form_buny", "changed_addon:form_bagel", "changed_addon:form_hayden_fennec_fox",
            "changed_addon:form_himalayan_crystal_gas_cat/male",
            "changed_addon:form_himalayan_crystal_gas_cat/female", "changed_addon:form_blue_lizard",
            "changed_addon:form_latex_mongoose", "changed_addon:form_pink_cyan_skunk",
            "changed_addon:form_latex_wind_cat/male", "changed_addon:form_latex_wind_cat/female",
            "changed_addon:form_borealis/male", "changed_addon:form_borealis/female",
            "changed_addon:form_exp1/male", "changed_addon:form_exp1/female",
            "changed_addon:form_exp2/male", "changed_addon:form_exp2/female", "changed_addon:form_exp6",
            "changed_addon:form_experiment009", "changed_addon:form_experiment_10",
            "changed_addon:form_experiment009_boss", "changed_addon:form_experiment_10_boss"
    };

    private static final String[] ADDON_DIGITIGRADE_VARIANTS = {
            "changed_addon:form_biosynth_snow_leopard/male", "changed_addon:form_biosynth_snow_leopard/female",
            "changed_addon:form_latex_calico_cat", "changed_addon:form_himalayan_crystal_gas_cat/male",
            "changed_addon:form_himalayan_crystal_gas_cat/female", "changed_addon:form_latex_wind_cat/male",
            "changed_addon:form_latex_wind_cat/female", "changed_addon:form_latex_cheetah/male",
            "changed_addon:form_latex_cheetah/female", "changed_addon:form_luminarctic_leopard/male",
            "changed_addon:form_luminarctic_leopard/female", "changed_addon:form_latex_white_snow_leopard/male",
            "changed_addon:form_latex_white_snow_leopard/female", "changed_addon:form_latex_snow_leopard_partial",
            "changed_addon:form_snepsi_leopard", "changed_addon:form_mirror_white_tiger_female",
            "changed_addon:form_latex_snep_feral", "changed_addon:form_latex_snep_feral_form", "changed_addon:form_lynx",
            "changed_addon:form_borealis/male", "changed_addon:form_borealis/female",
            "changed_addon:form_white_fox", "changed_addon:form_latex_snow_fox/male",
            "changed_addon:form_latex_snow_fox/female", "changed_addon:form_foxta_foxy",
            "changed_addon:form_void_fox", "changed_addon:form_foxyas", "changed_addon:form_hayden_fennec_fox",
            "changed_addon:form_exp1/male", "changed_addon:form_exp1/female",
            "changed_addon:form_latex_kitsune/male", "changed_addon:form_latex_kitsune/female",
            "changed_addon:form_wolfy", "changed_addon:form_fengqi_wolf",
            "changed_addon:form_latex_border_collie", "changed_addon:form_puro_kind/male",
            "changed_addon:form_puro_kind/female", "changed_addon:form_protogen",
            "changed_addon:form_prototype", "changed_addon:form_protogen_0senia0",
            "changed_addon:form_avali", "changed_addon:form_avali_zergodmaster",
            "changed_addon:form_latex_dragon_snow_leopard_shark", "changed_addon:form_latex_squid_tiger_shark",
            "changed_addon:form_latex_kayla_shark", "changed_addon:form_latex_mongoose",
            "changed_addon:form_pink_cyan_skunk", "changed_addon:form_blue_lizard",
            "changed_addon:form_buny", "changed_addon:form_bagel", "changed_addon:form_exp2/male",
            "changed_addon:form_exp2/female", "changed_addon:form_exp6"
    };

    private static final String[] ADDON_NIGHT_EYES_VARIANTS = {
            "changed_addon:form_dazed_latex", "changed_addon:form_buff_dazed_latex",
            "changed_addon:form_latex_calico_cat", "changed_addon:form_himalayan_crystal_gas_cat/male",
            "changed_addon:form_himalayan_crystal_gas_cat/female", "changed_addon:form_latex_wind_cat/male",
            "changed_addon:form_latex_wind_cat/female", "changed_addon:form_latex_cheetah/male",
            "changed_addon:form_latex_cheetah/female", "changed_addon:form_luminarctic_leopard/male",
            "changed_addon:form_luminarctic_leopard/female", "changed_addon:form_latex_white_snow_leopard/male",
            "changed_addon:form_latex_white_snow_leopard/female", "changed_addon:form_latex_snow_leopard_partial",
            "changed_addon:form_snepsi_leopard", "changed_addon:form_mirror_white_tiger_female",
            "changed_addon:form_lynx", "changed_addon:form_borealis/male", "changed_addon:form_borealis/female",
            "changed_addon:form_latex_kitsune/male", "changed_addon:form_latex_kitsune/female",
            "changed_addon:form_avali", "changed_addon:form_avali_zergodmaster",
            "changed_addon:form_fengqi_wolf", "changed_addon:form_exp6",
            "changed_addon:form_experiment009", "changed_addon:form_experiment_10",
            "changed_addon:form_experiment009_boss", "changed_addon:form_experiment_10_boss"
    };

    private static final String[] ADDON_AQUATIC_VARIANTS = {
            "changed_addon:form_latex_dragon_snow_leopard_shark",
            "changed_addon:form_latex_squid_tiger_shark", "changed_addon:form_latex_kayla_shark"
    };

    private static final String[] ADDON_LAMNID_VARIANTS = {
            "changed_addon:form_latex_dragon_snow_leopard_shark",
            "changed_addon:form_latex_squid_tiger_shark", "changed_addon:form_latex_kayla_shark"
    };

    private static final String[] ADDON_COLEOID_VARIANTS = {
            "changed_addon:form_latex_squid_tiger_shark"
    };

    private static final String[] ADDON_MULTI_ARMED_VARIANTS = {
            "changed_addon:form_latex_squid_tiger_shark"
    };

    private static final String[] ADDON_WINGED_BACK_VARIANTS = {
            "changed_addon:form_avali", "changed_addon:form_avali_zergodmaster",
            "changed_addon:form_borealis/male", "changed_addon:form_borealis/female",
            "changed_addon:form_latex_dragon_snow_leopard_shark",
            "changed_addon:form_dark_latex_yufeng_queen"
    };

    private static final String[] ADDON_PLANT_LIKE_VARIANTS = {
            "changed_addon:form_luminara_flower_beast"
    };

    private static final String[] ADDON_DARK_LATEX_VARIANTS = {
            "changed_addon:form_dark_latex_yufeng_queen"
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
        // Changed 0.16.0's premade ability trees select transfurs through these variant tags.
        // Split the general Changed progression trees by latex type, then add trait trees.
        tag("changed", "tags/changed/latex_variant", "latex", combine(LATEX_VARIANTS, ADDON_LATEX_VARIANTS));
        tag("changed", "tags/changed/latex_variant", "nonlatex", NONLATEX_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "digitigrade",
                combine(DIGITIGRADE_VARIANTS, ADDON_DIGITIGRADE_VARIANTS));
        tag("changed", "tags/changed/latex_variant", "taur", "changedextras:latex_haku");
        tag("changed", "tags/changed/latex_variant", "night_eyes",
                combine(NIGHT_EYES_VARIANTS, ADDON_NIGHT_EYES_VARIANTS));
        tag("changed", "tags/changed/latex_variant", "aquatic",
                combine(new String[]{"changedextras:latex_thorniii"}, ADDON_AQUATIC_VARIANTS));
        tag("changed", "tags/changed/latex_variant", "apian", "changedextras:proto_bee");
        tag("changed", "tags/changed/latex_variant", "lamnid", ADDON_LAMNID_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "coleoid", ADDON_COLEOID_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "multi_armed", ADDON_MULTI_ARMED_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "winged_back", ADDON_WINGED_BACK_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "plant_like", ADDON_PLANT_LIKE_VARIANTS);
        tag("changed", "tags/changed/latex_variant", "dark_latex", ADDON_DARK_LATEX_VARIANTS);

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
                "changedextras:latex_panther", "changedextras:latex_kaiju");
        tag("changed", "tags/entity_types", "organic_latex",
                "changedextras:conekat/male", "changedextras:conekat/female",
                "changedextras:mint_conekat/male", "changedextras:mint_conekat/female",
                "changedextras:choco_conekat/male", "changedextras:choco_conekat/female",
                "changedextras:strawberry_conekat/male", "changedextras:strawberry_conekat/female",
                "changedextras:neapolitan_conekat/male", "changedextras:neapolitan_conekat/female",
                "changedextras:proto_bee", "changedextras:scp_009", "changedextras:void_cat");
        tag("changed", "tags/items", "body",
                "changed:sports_bra", "changed:black_tshirt", "changed:wetsuit",
                "changed:white_tshirt", "changedextras:long_sleeve_shirt", "changedextras:katto_jacket");
        tag("changed", "tags/items", "face", "changedextras:jammer_headphones");

        // changed_addon
        tag("changed_addon", "tags/changed/latex_variant", "able_to_carry", "changedextras:latex_thorniii");
        tag("changed_addon", "tags/changed/latex_variant", "cat_like",
                "changedextras:latex_artist", "changedextras:conekat/female", "changedextras:conekat/male",
                "changedextras:mint_conekat/female", "changedextras:mint_conekat/male",
                "changedextras:choco_conekat/female", "changedextras:choco_conekat/male",
                "changedextras:strawberry_conekat/female", "changedextras:strawberry_conekat/male",
                "changedextras:neapolitan_conekat/female", "changedextras:neapolitan_conekat/male",
                "changedextras:latex_katto",
                "changedextras:latex_hazzy", "changedextras:latex_kaiju",
                "changedextras:latex_white_cat", "changedextras:latex_catte", "changedextras:latex_carneline",
                "changedextras:latex_panther", "changedextras:void_cat");
        tag("changed_addon", "tags/changed/latex_variant", "has_claws", CLAW_ENTITIES);
        tag("changed_addon", "tags/changed/latex_variant", "wolf_like",
                "changedextras:latex_jammer", "changedextras:latex_hazzy");
        tag("changed_addon", "tags/changed/latex_variant", "no_tail_anim", "changedextras:latex_kaiju");
        tag("changed_addon", "tags/changed/latex_variant", "leopard_like",
                "changed:form_latex_snow_leopard/female", "changed:form_latex_snow_leopard/male",
                "changed_addon:form_biosynth_snow_leopard/male", "changed_addon:form_biosynth_snow_leopard/female",
                "changed_addon:form_snepsi_leopard", "changed_addon:form_latex_dragon_snow_leopard_shark",
                "changed_addon:form_luminarctic_leopard/male", "changed_addon:form_luminarctic_leopard/female",
                "changed_addon:form_latex_white_snow_leopard/male", "changed_addon:form_latex_white_snow_leopard/female",
                "changed_addon:form_latex_snow_leopard_partial", "changed:form_latex_red_panda",
                "changed:form_latex_raccoon", "changed_addon:form_exp2/female", "changed_addon:form_exp2/male",
                "changed_addon:form_exp6", "changed_addon:form_latex_snep_feral",
                "changed_addon:form_latex_snep_feral_form", "changed_addon:form_lynx",
                "changed_addon:form_latex_dragon_snow_leopard_shark", "changed_addon:form_borealis/male",
                "changed_addon:form_borealis/female");
        tag("changed_addon", "tags/entity_types", "has_claws", CLAW_ENTITIES);
        tag("changed_addon", "tags/transfur_variant", "able_to_carry", "changedextras:latex_thorniii");

        // minecraft
        tag("minecraft", "tags/blocks/mineable", "pickaxe",
                "changedextras:scp009_crystal", "changedextras:domain_barrier", "changedextras:blue_stone");
        tag("minecraft", "tags/blocks", "needs_iron_tool", "changedextras:blue_stone");
        tag("minecraft", "tags/blocks", "overworld_carver_replaceables", "changedextras:blue_stone");
        tag("minecraft", "tags/painting_variant", "placeable", "changedextras:the_eye");
        tag("minecraft", "tags/worldgen/biome", "is_overworld", "changedextras:blue_cave");

        // forge
        tag("forge", "tags/worldgen/biome", "is_cave", "changedextras:blue_cave");
    }

    private static String[] combine(String[]... arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int offset = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private void tag(String namespace, String directory, String name, String... values) {
        JsonObject root = new JsonObject();
        root.addProperty("replace", false);
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(new JsonPrimitive(value));
        }
        root.add("values", array);
        write(PackOutput.Target.DATA_PACK, directory, ResourceLocation.fromNamespaceAndPath(namespace, name), root);
    }
}
