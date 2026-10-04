package com.katt.changedextras.block;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A small crystal that can be attached to any face of any block - floor, ceiling,
 * or any of the 4 walls - the same way an amethyst bud or a trapdoor attaches.
 * Transfurs on touch, same as the full-size crystal.
 */
@SuppressWarnings("deprecation")
public class Scp009CrystalSmallBlock extends DirectionalBlock {
    // Small bud-like hitboxes, one per facing direction (roughly a 6x6x6 nub offset from center).
    protected static final VoxelShape UP_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    protected static final VoxelShape DOWN_SHAPE = Block.box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape NORTH_SHAPE = Block.box(5.0D, 5.0D, 10.0D, 11.0D, 11.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 6.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(0.0D, 5.0D, 5.0D, 6.0D, 11.0D, 11.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(10.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);

    public Scp009CrystalSmallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(
                ModTransfurVariants.SCP_009.get(),
                TransfurContext.hazard(TransfurCause.LATEX_PUDDLE),
                6.0f
        );
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity
                && LevelUtil.isTouchingBlockCollision(level, blockPos, blockState, livingEntity)) {
            ProcessTransfur.progressTransfur(livingEntity, this.makeAssimilationDecision(livingEntity));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> UP_SHAPE;
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.is(ChangedExtras.SCP009_CRYSTAL.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == state.getValue(FACING) && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }
}
