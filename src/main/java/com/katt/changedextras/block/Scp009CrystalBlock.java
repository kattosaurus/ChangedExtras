package com.katt.changedextras.block;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class Scp009CrystalBlock extends Block implements EntityBlock {
    private static final int CONVERSION_RADIUS = 4;
    private static final int CONVERSION_INTERVAL_TICKS = 20;
    private static final int GROWTH_CHANCE_DENOMINATOR = 20;

    public Scp009CrystalBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Scp009CrystalBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, CONVERSION_INTERVAL_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Scp009CrystalBlockEntity blockEntity = level.getBlockEntity(pos) instanceof Scp009CrystalBlockEntity crystal
                ? crystal
                : null;
        long gameTime = level.getGameTime();
        boolean canInfectIce = blockEntity == null || blockEntity.canInfectIce(gameTime);

        for (BlockPos target : BlockPos.betweenClosed(
                pos.offset(-CONVERSION_RADIUS, -CONVERSION_RADIUS, -CONVERSION_RADIUS),
                pos.offset(CONVERSION_RADIUS, CONVERSION_RADIUS, CONVERSION_RADIUS))) {
            BlockState targetState = level.getBlockState(target);
            if (targetState.getFluidState().is(Fluids.WATER) && targetState.getFluidState().isSource()) {
                level.setBlock(target.immutable(), ModFluids.SCP009_WATER_BLOCK.get().defaultBlockState(), 3);
            } else if (canInfectIce && targetState.is(BlockTags.ICE)) {
                if (blockEntity != null) {
                    blockEntity.markIceInfection(gameTime);
                }
                canInfectIce = false;
                level.setBlock(target.immutable(), this.defaultBlockState(), 3);
            }
        }
        level.scheduleTick(pos, this, CONVERSION_INTERVAL_TICKS);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
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
            BlockState smallCrystalState = ChangedExtras.SCP009_CRYSTAL_SMALL.get()
                    .defaultBlockState()
                    .setValue(Scp009CrystalSmallBlock.FACING, direction);

            if (smallCrystalState.canSurvive(level, growPos)) {
                level.setBlock(growPos, smallCrystalState, 3);
            }
        }
    }
}
