package com.katt.changedextras.worldgen.feature;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.biome.ModBiomes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class BlueCaveCouchFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation STRUCTURE_LOCATION =
            new ResourceLocation(ChangedExtras.MODID, "blue_cave_couch");

    public BlueCaveCouchFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos floorPos = findCaveFloor(level, origin, random);
        if (floorPos == null) {
            return false;
        }

        ServerLevel serverLevel = level.getLevel();
        StructureTemplateManager manager = serverLevel.getStructureManager();
        Optional<StructureTemplate> templateOpt = manager.get(STRUCTURE_LOCATION);
        if (templateOpt.isEmpty()) {
            return false;
        }
        StructureTemplate template = templateOpt.get();

        Rotation rotation = Rotation.getRandom(random);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false)
                .setKeepLiquids(false);

        BlockPos placeOrigin = template.getZeroPositionWithTransform(floorPos, Mirror.NONE, rotation);
        return template.placeInWorld(level, placeOrigin, placeOrigin, settings, random, Block.UPDATE_CLIENTS);
    }

    private BlockPos findCaveFloor(WorldGenLevel level, BlockPos origin, RandomSource random) {
        // Search vertical column at origin
        BlockPos found = checkColumn(level, origin);
        if (found != null) {
            return found;
        }

        // Try random horizontal offsets in the chunk
        for (int i = 0; i < 8; i++) {
            BlockPos testPos = origin.offset(
                    random.nextInt(15) - 7,
                    random.nextInt(21) - 10,
                    random.nextInt(15) - 7
            );
            found = checkColumn(level, testPos);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private BlockPos checkColumn(WorldGenLevel level, BlockPos pos) {
        for (int dy = 0; dy <= 48; dy = dy <= 0 ? -dy + 1 : -dy) {
            BlockPos check = pos.above(dy);
            if (isValidPlacement(level, check)) {
                return check;
            }
        }
        return null;
    }

    private boolean isValidPlacement(WorldGenLevel level, BlockPos floorPos) {
        // Must be in blue_cave biome
        if (!level.getBiome(floorPos).is(ModBiomes.BLUE_CAVE)) {
            return false;
        }

        // The block at floorPos must be solid (the floor layer of the cave)
        if (!level.getBlockState(floorPos).isSolid()
                && !level.getBlockState(floorPos).is(ChangedExtras.BLUE_STONE_BLOCK.get())) {
            return false;
        }

        // Above the floor must be open cave air (not fluid, not solid block)
        for (int y = 1; y <= 3; y++) {
            BlockPos airCheck = floorPos.above(y);
            if (!level.getBlockState(airCheck).isAir()) {
                return false;
            }
        }

        // Check horizontal area (sample points in 8x8 footprint)
        // Center and corners at floor level or below should have solid ground
        int[][] samples = {{0, 0}, {7, 0}, {0, 7}, {7, 7}, {4, 4}};
        int solidCount = 0;
        for (int[] offset : samples) {
            BlockPos p = floorPos.offset(offset[0], 0, offset[1]);
            if (level.getBlockState(p).isSolid()
                    || level.getBlockState(p.below()).isSolid()
                    || level.getBlockState(p).is(ChangedExtras.BLUE_STONE_BLOCK.get())
                    || level.getBlockState(p.below()).is(ChangedExtras.BLUE_STONE_BLOCK.get())) {
                solidCount++;
            }
        }

        // At least 4 of 5 points should be solid ground
        if (solidCount < 4) {
            return false;
        }

        // Check headroom in the middle of the couch area
        BlockPos headCheck = floorPos.offset(4, 2, 4);
        return level.getBlockState(headCheck).isAir();
    }
}
