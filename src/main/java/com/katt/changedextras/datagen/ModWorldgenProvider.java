package com.katt.changedextras.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class ModWorldgenProvider extends ModJsonProvider {
    public ModWorldgenProvider(PackOutput output) {
        super(output, "Worldgen: changedextras");
    }

    @Override
    protected void generate() {
        write(PackOutput.Target.DATA_PACK, "worldgen/structure", new ResourceLocation("changedextras", "biological_studies_facility"), structure("changedextras:biological_studies_facility", "changedextras:has_structure/biological_studies_facility"));
        write(PackOutput.Target.DATA_PACK, "worldgen/structure", new ResourceLocation("changedextras", "bunker"), structure("changedextras:bunker", "changedextras:has_structure/bunker"));
        write(PackOutput.Target.DATA_PACK, "worldgen/structure_set", new ResourceLocation("changedextras", "biological_studies_facility"), structureSet("changedextras:biological_studies_facility", 48484848, 36, 12));
        write(PackOutput.Target.DATA_PACK, "worldgen/structure_set", new ResourceLocation("changedextras", "bunkers"), structureSet("changedextras:bunker", 88420137, 34, 10));

        write(PackOutput.Target.DATA_PACK, "worldgen/changed/facility/room/lab_cafeteria", new ResourceLocation("changedextras", "lab_cafeteria"), facilityRoom("changedextras:facility/room/lab_cafeteria/lab_cafeteria", "changedextras:chests/lab_cafeteria", 100, 0, 2, "changed:general"));
        // biome white_void is generated via ModBiomes bootstrap (Registries provider)
        write(PackOutput.Target.DATA_PACK, "worldgen/changed/facility/room/recreation_room", new ResourceLocation("changedextras", "recreation_room"), facilityRoom("changedextras:facility/room/recreation_room/recreation_room", "changedextras:chests/recreationstorage", 200, 0, 2, "changed:entrance"));

        write(PackOutput.Target.DATA_PACK, "structures/biological_studies_facility/rooms", new ResourceLocation("changedextras", "armory_room"), roomWeight(100, 0, 2));
        write(PackOutput.Target.DATA_PACK, "structures/biological_studies_facility/rooms", new ResourceLocation("changedextras", "exp12_room"), roomWeight(50, 0, 2));
        write(PackOutput.Target.DATA_PACK, "structures/biological_studies_facility/rooms", new ResourceLocation("changedextras", "scp009_room"), roomWeight(100, 0, 1));

        write(PackOutput.Target.DATA_PACK, "accessories/entities", new ResourceLocation("changed", "changedextras_humanoids"), accessoriesHumanoids());
        write(PackOutput.Target.DATA_PACK, "accessories/entities", new ResourceLocation("changed", "changedextras_taurs"), accessoriesTaurs());
        write(PackOutput.Target.DATA_PACK, "forge/biome_modifier", new ResourceLocation("changedextras", "overworld_latex_spawns"), biomeModifier());
    }

    private JsonObject structure(String type, String biomes) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        root.addProperty("biomes", "#" + biomes);
        root.add("spawn_overrides", new JsonObject());
        root.addProperty("step", "surface_structures");
        return root;
    }

    private JsonObject structureSet(String structure, int salt, int spacing, int separation) {
        JsonObject root = new JsonObject();
        JsonObject entry = new JsonObject();
        entry.addProperty("structure", structure);
        entry.addProperty("weight", 1);
        JsonArray structures = new JsonArray();
        structures.add(entry);
        root.add("structures", structures);

        JsonObject placement = new JsonObject();
        placement.addProperty("type", "minecraft:random_spread");
        placement.addProperty("salt", salt);
        placement.addProperty("spacing", spacing);
        placement.addProperty("separation", separation);
        placement.addProperty("spread_type", "linear");
        root.add("placement", placement);
        return root;
    }

    private JsonObject facilityRoom(String template, String lootTable, int spawnWeight, int min, int max, String neighbouringZone) {
        JsonObject root = new JsonObject();
        JsonObject piece = new JsonObject();
        piece.addProperty("type", "changed:room");
        piece.addProperty("template", template);
        piece.addProperty("loot_table", lootTable);
        root.add("piece", piece);
        root.addProperty("spawn_weight", spawnWeight);
        root.addProperty("minimum", min);
        root.addProperty("maximum", max);
        JsonArray zones = new JsonArray();
        zones.add(new JsonPrimitive(neighbouringZone));
        root.add("neighboring_zones", zones);
        return root;
    }

    private JsonObject roomWeight(int spawnWeight, int min, int max) {
        JsonObject root = new JsonObject();
        root.addProperty("spawn_weight", spawnWeight);
        root.addProperty("minimum", min);
        root.addProperty("maximum", max);
        return root;
    }

    private JsonObject accessories(String[] entities, String[] slots) {
        JsonObject root = new JsonObject();
        JsonArray entityArray = new JsonArray();
        for (String entity : entities) {
            entityArray.add(new JsonPrimitive(entity));
        }
        root.add("entities", entityArray);
        JsonArray slotArray = new JsonArray();
        for (String slot : slots) {
            slotArray.add(new JsonPrimitive(slot));
        }
        root.add("slots", slotArray);
        return root;
    }

    private JsonObject accessoriesHumanoids() {
        return accessories(new String[]{
                "changedextras:latex_artist", "changedextras:conekat/female", "changedextras:conekat/male",
                "changedextras:mint_conekat/female", "changedextras:mint_conekat/male",
                "changedextras:choco_conekat/female", "changedextras:choco_conekat/male",
                "changedextras:strawberry_conekat/female", "changedextras:strawberry_conekat/male",
                "changedextras:neapolitan_conekat/female", "changedextras:neapolitan_conekat/male",
                "changedextras:latex_jammer", "changedextras:latex_katto", "changedextras:latex_white_cat",
                "changedextras:latex_hazzy", "changedextras:scp_009", "changedextras:latex_catte",
                "changedextras:proto_bee", "changedextras:latex_thorniii"
        }, new String[]{
                "changed:body", "changed:hands", "changed:head", "changed:face",
                "changed:full_body", "changed:legs", "changed:neck"
        });
    }

    private JsonObject accessoriesTaurs() {
        return accessories(new String[]{
                "changedextras:latex_haku"
        }, new String[]{
                "changed:body", "changed:hands", "changed:head", "changed:face",
                "changed:lower_body", "changed:lower_body_side", "changed:neck"
        });
    }

    private JsonObject biomeModifier() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_spawns");
        root.addProperty("biomes", "#minecraft:is_overworld");
        JsonArray spawners = new JsonArray();
        addSpawner(spawners, "changedextras:conekat_male", 5);
        addSpawner(spawners, "changedextras:conekat_female", 5);
        addSpawner(spawners, "changedextras:mint_conekat_male", 2);
        addSpawner(spawners, "changedextras:mint_conekat_female", 2);
        addSpawner(spawners, "changedextras:choco_conekat_male", 2);
        addSpawner(spawners, "changedextras:choco_conekat_female", 2);
        addSpawner(spawners, "changedextras:strawberry_conekat_male", 2);
        addSpawner(spawners, "changedextras:strawberry_conekat_female", 2);
        addSpawner(spawners, "changedextras:neapolitan_conekat_male", 2);
        addSpawner(spawners, "changedextras:neapolitan_conekat_female", 2);
        addSpawner(spawners, "changedextras:white_cat", 5);
        addSpawner(spawners, "changedextras:proto_bee", 5);
        addSpawner(spawners, "changedextras:jammer", 3);
        addSpawner(spawners, "changedextras:hazzy", 3);
        addSpawner(spawners, "changedextras:catte", 3);
        addSpawner(spawners, "changedextras:haku", 3);
        addSpawner(spawners, "changedextras:thorniii", 3);
        root.add("spawners", spawners);
        return root;
    }

    private void addSpawner(JsonArray spawners, String type, int weight) {
        JsonObject spawner = new JsonObject();
        spawner.addProperty("type", type);
        spawner.addProperty("weight", weight);
        spawner.addProperty("minCount", 1);
        spawner.addProperty("maxCount", 2);
        spawners.add(spawner);
    }
}
