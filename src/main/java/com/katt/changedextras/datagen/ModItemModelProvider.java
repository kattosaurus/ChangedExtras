package com.katt.changedextras.datagen;
import com.katt.changedextras.client.model.catte_bucket;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ChangedExtras.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerSpawnEggs();
        registerSyringes();
        registerBlockItemModels();
        registerGeneratedItems();
        registerAbilityModels();
    }

    private void registerSpawnEggs() {
        String[] spawnEggs = {
                "conekat_male_spawn_egg", "conekat_female_spawn_egg",
                "mint_conekat_male_spawn_egg", "mint_conekat_female_spawn_egg",
                "choco_conekat_male_spawn_egg", "choco_conekat_female_spawn_egg",
                "strawberry_conekat_male_spawn_egg", "strawberry_conekat_female_spawn_egg",
                "neapolitan_conekat_male_spawn_egg", "neapolitan_conekat_female_spawn_egg",
                "white_cat_spawn_egg", "latex_thorniii_spawn_egg",
                "katt_spawn_egg", "jammer_spawn_egg",
                "proto_bee_spawn_egg", "artist_spawn_egg",
                "scp_009_spawn_egg", "latex_hazzy_spawn_egg",
                "latex_catte_spawn_egg", "latex_haku_spawn_egg",
                "latex_carneline_spawn_egg", "latex_panther_spawn_egg"
        };
        for (String name : spawnEggs) {
            withExistingParent(name, mcLoc("item/template_spawn_egg"));
        }
    }

    private void registerSyringes() {
        String[] syringes = {
                "conekat_male_syringe", "conekat_female_syringe",
                "mint_conekat_male_syringe", "mint_conekat_female_syringe",
                "choco_conekat_male_syringe", "choco_conekat_female_syringe",
                "strawberry_conekat_male_syringe", "strawberry_conekat_female_syringe",
                "neapolitan_conekat_male_syringe", "neapolitan_conekat_female_syringe",
                "white_cat_syringe", "artist_syringe",
                "proto_bee_syringe", "scp_009_syringe",
                "latex_hazzy_syringe", "latex_catte_syringe",
                "latex_haku_syringe", "latex_thorniii_syringe",
                "latex_carneline_syringe", "latex_panther_syringe",
                "katt_syringe"
        };
        for (String name : syringes) {
            getBuilder(name).parent(new ModelFile.UncheckedModelFile(
                    new net.minecraft.resources.ResourceLocation("changed:item/latex_syringe")));
        }
    }

    private void registerBlockItemModels() {
        String[] blockItems = {
                "domain_barrier", "failed_route", "icecream_block",
                "jammer_headphones", "route", "scp009_crystal", "blue_stone"
        };
        for (String name : blockItems) {
            withExistingParent(name, modLoc("block/" + name));
        }

        withExistingParent("blue_stone_crystal", modLoc("block/blue_stone_crystal/medium_blue"));
    }

    private void registerGeneratedItems() {
        // name -> texture (null = default item/<name>)
        generated("voxelotor", "item/voxelotor");
        generated("vial", "item/vial");
        generated("used_vial", "item/vial");
        generated("processed_vial", "item/vial");
        generated("used_swab", "item/usedswab");
        generated("sterile_swab", "item/sterileswab");
        generated("the_palette", "item/pallette");
        generated("artist_brush", "item/brush");
        generated("sketch", "item/sketch");
        generated("flask_of_tears", "item/flask_of_tears");
        generated("changed_extras_guide", "item/changed_extras_guide");
        generated("hydroxyurea", "item/hydroxyurea");
        generated("crizanlizumab", "item/crizanlizumab");
        generated("pill_bottle", "item/pill_bottle");
        generated("catte_bucket", "item/catte_bucket");
        generated("long_sleeve_shirt", "item/long_sleeve_shirt");
        generated("scp009_water_bucket", "item/scp009_water_bucket");
        generated("icecream", "item/vanilla_icecream");
        generated("pink_icecream", "item/pink_icecream");
        generated("mint_icecream", "item/mint_icecream");
        generated("choco_icecream", "item/choco_icecream");
        generated("strawberry_icecream", "item/strawberry_icecream");
        generated("neapolitan_icecream", "item/neapolitan_icecream");
        generated("snep_spawn_egg", "item/sneppng");
        generated("pale_test_positive", "item/truepaletest");
        generated("pale_test_negative", "item/falsepaletest");
        existingFileHelper.trackGenerated(modLoc("item/strange_crystal_shard"), PackType.CLIENT_RESOURCES, ".png", "textures");
        generated("strange_crystal_shard", "item/strange_crystal_shard");

        // translucent item with block texture
        getBuilder("scp009_crystal_small")
                .parent(getExistingFile(mcLoc("item/generated")))
                .renderType("minecraft:translucent")
                .texture("layer0", modLoc("block/scp009_crystal_small"));
    }

    private void registerAbilityModels() {
        String[] abilities = {
                "turn_feral", "swing", "puncture",
                "parry", "paint_ball", "jackpot_aura", "prepare"
        };
        for (String name : abilities) {
            getBuilder("ability/" + name)
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", modLoc("abilities/" + name));
        }
    }

    private void generated(String name, String texture) {
        getBuilder(name)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc(texture));
    }
}
