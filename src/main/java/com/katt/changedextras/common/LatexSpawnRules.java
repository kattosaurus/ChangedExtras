package com.katt.changedextras.common;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;

@SuppressWarnings("deprecation")
public final class LatexSpawnRules {
    private static volatile Map<EntityType<?>, ResourceLocation> entityTypeToVariant = Map.of();

    private LatexSpawnRules() {
    }

    public static boolean isVariantEnabled(MinecraftServer server, EntityType<?> entityType) {
        ResourceLocation entityTypeId = getEntityTypeId(entityType);
        return entityTypeId == null || LatexSpawnSelectionData.get(server).isEnabled(entityTypeId);
    }

    public static boolean shouldOverrideToDaySpawn(ServerLevelAccessor level, MobSpawnType spawnType) {
        return isNaturalSpawn(spawnType) && level.getLevel().getGameRules().getBoolean(ChangedExtrasGameRules.LATEX_SPAWN_IN_DAY);
    }

    public static <T extends ChangedEntity> boolean checkDaySpawnRules(EntityType<T> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return isSurfaceSpawn(level, pos)
                && checkSpawnBlock(level, spawnType, pos)
                && Monster.checkAnyLightMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    public static boolean isNaturalSpawn(MobSpawnType spawnType) {
        return spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION;
    }

    private static ResourceLocation getEntityTypeId(EntityType<?> entityType) {
        ResourceLocation entityTypeId = entityTypeToVariant.get(entityType);
        if (entityTypeId != null) {
            return entityTypeId;
        }

        entityTypeToVariant = LatexSpawnRegistry.buildEntityTypeMap();
        return entityTypeToVariant.get(entityType);
    }

    private static boolean checkSpawnBlock(ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos) {
        if (!isNaturalSpawn(spawnType)) {
            return true;
        }

        int checksRemaining = 3;
        BlockPos currentPos = pos;

        while (checksRemaining-- > 0) {
            BlockState state = level.getBlockState(currentPos);
            currentPos = currentPos.below();

            if (state.is(ChangedTags.Blocks.LATEX_SPAWNABLE_ON)) {
                return true;
            }

            if (!state.isAir() && state.isCollisionShapeFullBlock(level, currentPos.above())) {
                return false;
            }
        }

        return false;
    }

    public static boolean isSurfaceSpawn(ServerLevelAccessor level, BlockPos pos) {
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        return pos.getY() >= level.getSeaLevel() - 10
                && pos.getY() >= surfacePos.getY() - 1
                && level.canSeeSky(pos)
                && level.canSeeSky(pos.above());
    }
}
