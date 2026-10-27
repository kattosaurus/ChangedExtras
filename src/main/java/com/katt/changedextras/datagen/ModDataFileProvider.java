package com.katt.changedextras.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.PackOutput;

public class ModDataFileProvider extends ModJsonProvider {
    public ModDataFileProvider(PackOutput output) {
        super(output, "Extra Data: changedextras");
    }

    @Override
    protected void generate() {
        advancements();
        infuserRecipes();
        clientAssets();
    }

    // ---------------------------------------------------------------- advancements

    private void advancements() {
        JsonObject root = new JsonObject();
        JsonObject display = new JsonObject();
        display.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/stone.png");
        display.add("icon", icon("changedextras:changed_extras_guide"));
        display.add("title", translate("advancements.changedextras.root_title"));
        display.add("description", translate("advancements.changedextras.root_desc"));
        display.addProperty("frame", "task");
        display.addProperty("show_toast", false);
        display.addProperty("announce_to_chat", false);
        display.addProperty("hidden", false);
        root.add("display", display);

        JsonObject criteria = new JsonObject();
        JsonObject tick = new JsonObject();
        tick.addProperty("trigger", "minecraft:tick");
        tick.add("conditions", new JsonObject());
        criteria.add("tick", tick);
        root.add("criteria", criteria);
        write(PackOutput.Target.DATA_PACK, "advancements", "advancements_root", root);

        write(PackOutput.Target.DATA_PACK, "advancements", "artist",
                killAdvancement("changedextras:flask_of_tears", "artist", "changedextras:latex_artist", 500));
        write(PackOutput.Target.DATA_PACK, "advancements", "katto",
                killAdvancement("changedextras:sketch", "katto", "changedextras:latex_katto", 200));
    }

    private JsonObject killAdvancement(String iconItem, String key, String entityType, int experience) {
        JsonObject root = new JsonObject();

        JsonObject display = new JsonObject();
        display.add("icon", icon(iconItem));
        display.add("title", translate("advancements.changedextras." + key + "_title"));
        display.add("description", translate("advancements.changedextras." + key + "_desc"));
        display.addProperty("frame", "challenge");
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", true);
        display.addProperty("hidden", false);
        root.add("display", display);
        root.addProperty("parent", "changedextras:advancements_root");

        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", entityType);
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:entity_properties");
        condition.addProperty("entity", "this");
        condition.add("predicate", predicate);
        JsonArray entities = new JsonArray();
        entities.add(condition);

        JsonObject conditions = new JsonObject();
        conditions.add("entity", entities);

        JsonObject kill = new JsonObject();
        kill.addProperty("trigger", "minecraft:player_killed_entity");
        kill.add("conditions", conditions);

        JsonObject criteria = new JsonObject();
        criteria.add("kill", kill);
        root.add("criteria", criteria);

        JsonObject rewards = new JsonObject();
        rewards.addProperty("experience", experience);
        root.add("rewards", rewards);
        return root;
    }

    // ---------------------------------------------------------------- infuser recipes

    private void infuserRecipes() {
        infuser("infuser/artist/artist", "changedextras:latex_artist",
                item("changedextras:artist_brush"),
                item("minecraft:totem_of_undying"),
                item("changedextras:the_palette"),
                item("changed:white_latex_goo"),
                item("changed_addon:transfur_totem"),
                item("changed:white_latex_goo"),
                nbt("changed_addon:laethin", "{type:\"white_latex\"}"),
                item("changed:black_tshirt"),
                nbt("changed_addon:laethin", "{type:\"white_latex\"}"));

        infuser("infuser/conekat/conekat_female", "changedextras:conekat/female",
                item("changed:latex_base"),
                item("changed:white_latex_goo"),
                item("minecraft:string"),
                item("minecraft:milk_bucket"),
                item("minecraft:sweet_berries"),
                item("minecraft:milk_bucket"));

        infuser("infuser/conekat/conekat_male", "changedextras:conekat/male",
                item("changed:latex_base"),
                item("changed:white_latex_goo"),
                item("minecraft:string"),
                item("minecraft:milk_bucket"),
                item("minecraft:sweet_berries"));

        infuser("infuser/white_cat/white_cat", "changedextras:latex_white_cat",
                item("changed:latex_base"),
                item("changed:white_latex_goo"),
                item("minecraft:string"),
                item("minecraft:cod"));
    }

    private void infuser(String path, String form, JsonObject... ingredients) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "changed:infuser");
        JsonArray array = new JsonArray();
        for (JsonObject ingredient : ingredients) {
            array.add(ingredient);
        }
        root.add("ingredients", array);
        root.addProperty("form", form);
        int idx = path.lastIndexOf('/');
        write(PackOutput.Target.DATA_PACK, "recipes/" + path.substring(0, idx), path.substring(idx + 1), root);
    }

    // ---------------------------------------------------------------- client assets

    private void clientAssets() {
        JsonObject sounds = new JsonObject();
        addSound(sounds, "domain_expansion", "player", false);
        addSound(sounds, "domain_break", "player", false);
        addSound(sounds, "scenario_swap", "player", false);
        addSound(sounds, "jackpot_song", "record", true);
        addSound(sounds, "jackpot_loop", "record", true);
        addSound(sounds, "jackpot_stop", "record", false);
        addSound(sounds, "artist_theme", "record", true);
        addSound(sounds, "parry", "player", false);
        addSound(sounds, "heartbeat", "player", false);
        addSound(sounds, "scream", "player", false);
        write(PackOutput.Target.RESOURCE_PACK, "", "sounds", sounds);

        JsonObject atlas = new JsonObject();
        JsonArray sources = new JsonArray();
        JsonObject source = new JsonObject();
        source.addProperty("type", "directory");
        source.addProperty("source", "painting");
        source.addProperty("prefix", "");
        sources.add(source);
        atlas.add("sources", sources);
        write(PackOutput.Target.RESOURCE_PACK, "atlases", "paintings", atlas);

        JsonObject particle = new JsonObject();
        JsonArray textures = new JsonArray();
        for (int i = 0; i <= 10; i++) {
            textures.add(new JsonPrimitive("changedextras:jackpot_aura_" + i));
        }
        particle.add("textures", textures);
        write(PackOutput.Target.RESOURCE_PACK, "particles", "jackpot_aura", particle);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject modelTextures = new JsonObject();
        modelTextures.addProperty("layer0", "changedextras:item/emptypaletest");
        model.add("textures", modelTextures);
        JsonArray overrides = new JsonArray();
        overrides.add(override("changedextras:negative", "changedextras:item/pale_test_negative"));
        overrides.add(override("changedextras:positive", "changedextras:item/pale_test_positive"));
        model.add("overrides", overrides);
        write(PackOutput.Target.RESOURCE_PACK, "models/item", "pale_test", model);
    }

    // ---------------------------------------------------------------- helpers

    private static JsonObject item(String name) {
        JsonObject o = new JsonObject();
        o.addProperty("item", name);
        return o;
    }

    private static JsonObject nbt(String name, String nbt) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "forge:nbt");
        o.addProperty("item", name);
        o.addProperty("nbt", nbt);
        return o;
    }

    private static void addSound(JsonObject root, String key, String category, boolean stream) {
        JsonObject sound = new JsonObject();
        sound.addProperty("category", category);
        JsonArray sounds = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("name", "changedextras:" + key);
        entry.addProperty("stream", stream);
        sounds.add(entry);
        sound.add("sounds", sounds);
        root.add(key, sound);
    }

    private static JsonObject override(String predicate, String model) {
        JsonObject o = new JsonObject();
        JsonObject predicateObj = new JsonObject();
        predicateObj.addProperty(predicate, 1.0D);
        o.add("predicate", predicateObj);
        o.addProperty("model", model);
        return o;
    }

    private static JsonObject icon(String item) {
        JsonObject o = new JsonObject();
        o.addProperty("item", item);
        return o;
    }

    private static JsonObject translate(String key) {
        JsonObject o = new JsonObject();
        o.addProperty("translate", key);
        return o;
    }
}
