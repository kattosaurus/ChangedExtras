package com.katt.changedextras.common;

import com.katt.changedextras.entity.ModEntities;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ChangedExtrasSpawnController {
    private ChangedExtrasSpawnController() {
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(ModEntities.CONEKAT_MALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.CONEKAT_FEMALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.WHITE_CAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.PROTO_BEE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.FURRED_LATEX_TIGER_SHARK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.ARTIST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.SCP_009.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> LatexSpawnRules.shouldOverrideToDaySpawn(level, spawnType)
                        ? LatexSpawnRules.checkDaySpawnRules(entityType, level, spawnType, pos, random)
                        : ChangedEntity.checkEntitySpawnRules(entityType, level, spawnType, pos, random));
    }
}
