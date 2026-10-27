package com.katt.changedextras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlueStoneBlock extends Block {
    private static final int GROWTH_CHANCE_DENOMINATOR = 10;
    public BlueStoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROWTH_CHANCE_DENOMINATOR) != 0) {
            return;
        }

        Direction direction = Direction.getRandom(random);
        BlockPos growPos = pos.relative(direction);
        BlockState growTargetState = level.getBlockState(growPos);

        if (growTargetState.isAir() || growTargetState.canBeReplaced()) {
            BlockState crystalState = BlueStoneCrystalBlock.createState(level, growPos, direction);

            if (crystalState.canSurvive(level, growPos)) {
                level.setBlock(growPos, crystalState, 3);
            }
        }
    }
}
