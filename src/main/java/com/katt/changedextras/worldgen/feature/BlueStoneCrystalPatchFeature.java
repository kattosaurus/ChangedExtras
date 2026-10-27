package com.katt.changedextras.worldgen.feature;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.BlueStoneCrystalBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlueStoneCrystalPatchFeature extends Feature<NoneFeatureConfiguration> {
    public BlueStoneCrystalPatchFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos center = findCaveSurface(level, origin, random);
        if (center == null) {
            return false;
        }

        int attempts = 32 + random.nextInt(24);
        int placedCount = 0;
        int radiusXZ = 7;
        int radiusY = 5;

        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;
            int dy = random.nextInt(radiusY * 2 + 1) - radiusY;
            int dz = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;
            BlockPos targetPos = center.offset(dx, dy, dz);

            BlockState targetState = level.getBlockState(targetPos);
            if (!targetState.isAir() && !targetState.canBeReplaced()) {
                continue;
            }

            Collections.shuffle(directions);
            Direction validFacing = null;
            for (Direction dir : directions) {
                BlockPos supportPos = targetPos.relative(dir.getOpposite());
                BlockState supportState = level.getBlockState(supportPos);
                // Must be on blue stone and have a sturdy face
                if (supportState.is(ChangedExtras.BLUE_STONE_BLOCK.get()) && supportState.isFaceSturdy(level, supportPos, dir)) {
                    validFacing = dir;
                    break;
                }
            }

            if (validFacing == null) {
                continue;
            }

            // Determine crystal size following spacing rules:
            // Big crystal (Age 2) requires >= 6 blocks from any other crystal with age >= 1
            // Medium crystal (Age 1) requires >= 3 blocks from any other crystal with age >= 1
            int age = 0;
            if (BlueStoneCrystalBlock.canGrowToBig(level, targetPos) && random.nextFloat() < 0.25F) {
                age = 2;
            } else if (BlueStoneCrystalBlock.canGrowToMedium(level, targetPos) && random.nextFloat() < 0.50F) {
                age = 1;
            }

            BlueStoneCrystalBlock.CrystalColor color = BlueStoneCrystalBlock.CrystalColor.BLUE;
            if (random.nextFloat() < 0.15F) {
                color = random.nextBoolean()
                        ? BlueStoneCrystalBlock.CrystalColor.WHITE
                        : BlueStoneCrystalBlock.CrystalColor.RED;
            }

            BlockState crystalState = ChangedExtras.BLUE_STONE_CRYSTAL.get().defaultBlockState()
                    .setValue(BlueStoneCrystalBlock.FACING, validFacing)
                    .setValue(BlueStoneCrystalBlock.AGE, age)
                    .setValue(BlueStoneCrystalBlock.COLOR, color);

            if (crystalState.canSurvive(level, targetPos)) {
                level.setBlock(targetPos, crystalState, 2);
                placedCount++;
            }
        }

        return placedCount > 0;
    }

    private BlockPos findCaveSurface(WorldGenLevel level, BlockPos origin, RandomSource random) {
        // Search vertical column around origin (both above and below)
        for (int dy = 0; dy <= 24; dy = dy <= 0 ? -dy + 1 : -dy) {
            BlockPos checkPos = origin.above(dy);
            if (level.getBlockState(checkPos).isAir()) {
                for (Direction dir : Direction.values()) {
                    BlockPos support = checkPos.relative(dir.getOpposite());
                    BlockState supportState = level.getBlockState(support);
                    if (supportState.is(ChangedExtras.BLUE_STONE_BLOCK.get()) && supportState.isFaceSturdy(level, support, dir)) {
                        return checkPos;
                    }
                }
            }
        }

        // Try a few random horizontal offsets if exact vertical column didn't hit a surface
        for (int tries = 0; tries < 8; tries++) {
            BlockPos checkPos = origin.offset(
                    random.nextInt(11) - 5,
                    random.nextInt(17) - 8,
                    random.nextInt(11) - 5
            );
            if (level.getBlockState(checkPos).isAir()) {
                for (Direction dir : Direction.values()) {
                    BlockPos support = checkPos.relative(dir.getOpposite());
                    BlockState supportState = level.getBlockState(support);
                    if (supportState.is(ChangedExtras.BLUE_STONE_BLOCK.get()) && supportState.isFaceSturdy(level, support, dir)) {
                        return checkPos;
                    }
                }
            }
        }

        return null;
    }
}
