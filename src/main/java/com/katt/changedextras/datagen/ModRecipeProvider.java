package com.katt.changedextras.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "crizanlizumab"))
                .requires(item("changedextras", "used_vial"))
                .requires(item("changed_addon", "ammonia"))
                .unlockedBy("has_item", has(item("changedextras", "used_vial")))
                .save(writer, new ResourceLocation("changedextras", "crizanlizumab"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "hydroxyurea"))
                .requires(item("changed_addon", "biomass"))
                .requires(item("changed_addon", "ammonia"))
                .requires(Items.FERMENTED_SPIDER_EYE)
                .unlockedBy("has_item", has(item("changed_addon", "biomass")))
                .save(writer, new ResourceLocation("changedextras", "hydroxyurea"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("changedextras", "pale_test"))
                .pattern("###")
                .pattern("/_u")
                .pattern("=N=")
                .define('#', Items.GLASS_PANE)
                .define('/', tag("changedextras", "used_syringes"))
                .define('_', Items.PAPER)
                .define('u', item("changedextras", "processed_vial"))
                .define('=', Items.IRON_INGOT)
                .define('N', Items.NETHERITE_SCRAP)
                .unlockedBy("has_item", has(item("changedextras", "processed_vial")))
                .save(writer, new ResourceLocation("changedextras", "pale_test"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("changedextras", "pill_bottle"))
                .pattern(" T ")
                .pattern("G G")
                .pattern(" G ")
                .define('T', Items.ORANGE_TERRACOTTA)
                .define('G', Items.GLASS_PANE)
                .unlockedBy("has_item", has(Items.GLASS_PANE))
                .save(writer, new ResourceLocation("changedextras", "pill_bottle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "pink_icecream"))
                .requires(item("changedextras", "icecream"))
                .requires(item("changed_addon", "luminara_bloom_petals"))
                .unlockedBy("has_item", has(item("changedextras", "icecream")))
                .save(writer, new ResourceLocation("changedextras", "pink_icecream"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("changedextras", "sketch"))
                .pattern("NEQ")
                .pattern("IOR")
                .pattern("PPP")
                .define('Q', item("changed", "orange"))
                .define('N', item("changed_addon", "biomass"))
                .define('E', item("changed", "latex_base"))
                .define('I', Items.TOTEM_OF_UNDYING)
                .define('R', item("changedextras", "flask_of_tears"))
                .define('O', item("changedextras", "the_palette"))
                .define('P', Items.PAPER)
                .unlockedBy("has_item", has(item("changedextras", "flask_of_tears")))
                .save(writer, new ResourceLocation("changedextras", "sketch"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("changedextras", "sterile_swab"))
                .pattern("W")
                .pattern("S")
                .pattern("W")
                .define('W', Items.WHITE_WOOL)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Items.WHITE_WOOL))
                .save(writer, new ResourceLocation("changedextras", "sterile_swab"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "the_palette"))
                .requires(Items.BOWL)
                .requires(Items.RED_DYE)
                .requires(Items.BLUE_DYE)
                .requires(Items.YELLOW_DYE)
                .unlockedBy("has_item", has(Items.BOWL))
                .save(writer, new ResourceLocation("changedextras", "the_palette"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "used_vial"))
                .requires(item("changedextras", "used_swab"))
                .requires(item("changedextras", "vial"))
                .unlockedBy("has_item", has(item("changedextras", "vial")))
                .save(writer, new ResourceLocation("changedextras", "used_vial"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("changedextras", "vial"))
                .pattern("###")
                .pattern("GSG")
                .pattern("GuG")
                .define('#', Items.OAK_SLAB)
                .define('G', Items.GLASS_PANE)
                .define('S', Items.SUGAR)
                .define('u', Items.WATER_BUCKET)
                .unlockedBy("has_item", has(Items.GLASS_PANE))
                .save(writer, new ResourceLocation("changedextras", "vial"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("changedextras", "voxelotor"))
                .requires(item("changed_addon", "luminara_bloom_petals"))
                .requires(item("changed_addon", "biomass"))
                .requires(item("changed_addon", "ammonia"))
                .requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", has(item("changed_addon", "biomass")))
                .save(writer, new ResourceLocation("changedextras", "voxelotor"));
    }

    @Override
    @Nullable
    protected CompletableFuture<?> saveAdvancement(CachedOutput output, FinishedRecipe finishedRecipe, JsonObject advancementJson) {
        return null;
    }

    private static ItemLike item(String namespace, String path) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path));
        if (item == null) {
            throw new IllegalStateException("Unknown item: " + namespace + ":" + path);
        }
        return item;
    }

    private static TagKey<Item> tag(String namespace, String path) {
        return ItemTags.create(new ResourceLocation(namespace, path));
    }
}
