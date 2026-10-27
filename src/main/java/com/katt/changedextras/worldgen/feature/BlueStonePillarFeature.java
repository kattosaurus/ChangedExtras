package com.katt.changedextras.worldgen.feature;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.BlueStoneCrystalBlock;
import com.katt.changedextras.worldgen.biome.ModBiomes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlueStonePillarFeature extends Feature<NoneFeatureConfiguration> {
    public BlueStonePillarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Find floor and ceiling in the cavern
        PillarBounds bounds = findCavernBounds(level, origin);
        if (bounds == null) {
            return false;
        }

        int floorY = bounds.floorY();
        int ceilingY = bounds.ceilingY();
        int cavernHeight = ceilingY - floorY;

        // Ensure reasonable height for cave pillars
        if (cavernHeight < 4 || cavernHeight > 70) {
            return false;
        }

        // Pillar dimensions
        int baseRadius = cavernHeight > 25 ? (random.nextBoolean() ? 2 : 1) : 1;
        boolean fullColumn = random.nextFloat() < 0.70F; // 70% chance to connect all the way, 30% stalactite/stalagmite pair

        int centerX = origin.getX();
        int centerZ = origin.getZ();
        int placedCount = 0;

        BlockState blueStone = ChangedExtras.BLUE_STONE_BLOCK.get().defaultBlockState();

        // Generate columns around center
        for (int dx = -baseRadius; dx <= baseRadius; dx++) {
            for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > baseRadius + 0.4D) {
                    continue;
                }

                BlockPos colPos = new BlockPos(centerX + dx, 0, centerZ + dz);
                PillarBounds colBounds = findCavernBounds(level, new BlockPos(colPos.getX(), (floorY + ceilingY) / 2, colPos.getZ()));
                if (colBounds == null) {
                    continue;
                }

                int cFloor = colBounds.floorY();
                int cCeil = colBounds.ceilingY();

                if (fullColumn) {
                    for (int y = cFloor; y <= cCeil; y++) {
                        // Thicken near floor and ceiling (hourglass / natural pillar taper)
                        double distToEnds = Math.min(y - cFloor, cCeil - y);
                        double maxDistForY = baseRadius - (distToEnds * 0.15D);
                        if (dist <= Math.max(0.5D, maxDistForY) || (dx == 0 && dz == 0)) {
                            BlockPos placePos = new BlockPos(colPos.getX(), y, colPos.getZ());
                            if (canReplaceForPillar(level, placePos)) {
                                level.setBlock(placePos, blueStone, 2);
                                placedCount++;
                            }
                        }
                    }
                } else {
                    // Stalagmite & stalactite meeting or leaving a small gap
                    int bottomHeight = (cCeil - cFloor) / 2 + random.nextInt(3) - 1;
                    int topHeight = (cCeil - cFloor) / 2 + random.nextInt(3) - 1;

                    for (int y = cFloor; y <= cFloor + bottomHeight; y++) {
                        BlockPos placePos = new BlockPos(colPos.getX(), y, colPos.getZ());
                        if (canReplaceForPillar(level, placePos)) {
                            level.setBlock(placePos, blueStone, 2);
                            placedCount++;
                        }
                    }
                    for (int y = cCeil; y >= cCeil - topHeight; y--) {
                        BlockPos placePos = new BlockPos(colPos.getX(), y, colPos.getZ());
                        if (canReplaceForPillar(level, placePos)) {
                            level.setBlock(placePos, blueStone, 2);
                            placedCount++;
                        }
                    }
                }
            }
        }

        // Decorate pillar surface with crystals
        if (placedCount > 0) {
            decorateWithCrystals(level, origin, baseRadius, floorY, ceilingY, random);
        }

        return placedCount > 0;
    }

    private boolean canReplaceForPillar(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir()
                || state.canBeReplaced()
                || state.is(Blocks.WATER)
                || state.is(Blocks.LAVA)
                || state.is(ChangedExtras.BLUE_STONE_BLOCK.get())
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.STONE);
    }

    private PillarBounds findCavernBounds(WorldGenLevel level, BlockPos origin) {
        int x = origin.getX();
        int z = origin.getZ();
        int startY = origin.getY();

        // Find floor (scan down from startY to find first solid ground with air above it)
        int floorY = Integer.MIN_VALUE;
        int ceilingY = Integer.MAX_VALUE;

        // Search down for solid floor
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, startY, z);
        for (int y = startY; y >= level.getMinBuildHeight() + 5; y--) {
            cursor.setY(y);
            BlockState state = level.getBlockState(cursor);
            if (!state.isAir() && state.isFaceSturdy(level, cursor, Direction.UP)) {
                // Check if space directly above is air
                BlockPos above = cursor.above();
                if (level.getBlockState(above).isAir() || level.getBlockState(above).canBeReplaced()) {
                    floorY = y;
                    break;
                }
            }
        }

        if (floorY == Integer.MIN_VALUE) {
            return null;
        }

        // Search up from floorY for ceiling
        for (int y = floorY + 1; y < level.getMaxBuildHeight() - 10; y++) {
            cursor.setY(y);
            BlockState state = level.getBlockState(cursor);
            if (!state.isAir() && state.isFaceSturdy(level, cursor, Direction.DOWN)) {
                ceilingY = y;
                break;
            }
        }

        if (ceilingY == Integer.MAX_VALUE || ceilingY <= floorY + 2) {
            return null;
        }

        return new PillarBounds(floorY, ceilingY);
    }

    private void decorateWithCrystals(WorldGenLevel level, BlockPos center, int radius, int floorY, int ceilingY, RandomSource random) {
        int crystalTries = 10 + random.nextInt(15);
        List<Direction> dirs = new ArrayList<>(List.of(Direction.values()));

        for (int i = 0; i < crystalTries; i++) {
            int cx = center.getX() + random.nextInt(radius * 2 + 3) - (radius + 1);
            int cz = center.getZ() + random.nextInt(radius * 2 + 3) - (radius + 1);
            int cy = floorY + 1 + random.nextInt(Math.max(1, ceilingY - floorY - 1));

            BlockPos targetPos = new BlockPos(cx, cy, cz);
            if (!level.getBlockState(targetPos).isAir()) {
                continue;
            }

            Collections.shuffle(dirs);
            for (Direction dir : dirs) {
                BlockPos support = targetPos.relative(dir.getOpposite());
                BlockState supportState = level.getBlockState(support);
                if (supportState.is(ChangedExtras.BLUE_STONE_BLOCK.get()) && supportState.isFaceSturdy(level, support, dir)) {
                    int age = 0;
                    if (BlueStoneCrystalBlock.canGrowToBig(level, targetPos) && random.nextFloat() < 0.20F) {
                        age = 2;
                    } else if (BlueStoneCrystalBlock.canGrowToMedium(level, targetPos) && random.nextFloat() < 0.40F) {
                        age = 1;
                    }

                    BlueStoneCrystalBlock.CrystalColor color = BlueStoneCrystalBlock.CrystalColor.BLUE;
                    if (random.nextFloat() < 0.15F) {
                        color = random.nextBoolean()
                                ? BlueStoneCrystalBlock.CrystalColor.WHITE
                                : BlueStoneCrystalBlock.CrystalColor.RED;
                    }

                    BlockState crystalState = ChangedExtras.BLUE_STONE_CRYSTAL.get().defaultBlockState()
                            .setValue(BlueStoneCrystalBlock.FACING, dir)
                            .setValue(BlueStoneCrystalBlock.AGE, age)
                            .setValue(BlueStoneCrystalBlock.COLOR, color);

                    if (crystalState.canSurvive(level, targetPos)) {
                        level.setBlock(targetPos, crystalState, 2);
                    }
                    break;
                }
            }
        }
    }

    private record PillarBounds(int floorY, int ceilingY) {}
}
