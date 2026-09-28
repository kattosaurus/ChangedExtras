package com.katt.changedextras.block;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.katt.changedextras.fluid.ModFluids;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class Scp009CrystalBlock extends Block {
    private static final int CONVERSION_RADIUS = 4;
    private static final int CONVERSION_INTERVAL_TICKS = 20;
    private static final int GROWTH_CHANCE_DENOMINATOR = 20; // ~1-in-20 random ticks attempts a grow
    private static final String LAST_PROCESSED_TICK_TAG = "changedextras_scp009_last_tick";

    public Scp009CrystalBlock(Properties properties) {
        super(properties);
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
            CompoundTag data = livingEntity.getPersistentData();
            long currentTick = level.getGameTime();
            if (data.getLong(LAST_PROCESSED_TICK_TAG) == currentTick) {
                return;
            }
            data.putLong(LAST_PROCESSED_TICK_TAG, currentTick);
            ProcessTransfur.progressTransfur(livingEntity, this.makeAssimilationDecision(livingEntity));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, CONVERSION_INTERVAL_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for (BlockPos target : BlockPos.betweenClosed(
                pos.offset(-CONVERSION_RADIUS, -CONVERSION_RADIUS, -CONVERSION_RADIUS),
                pos.offset(CONVERSION_RADIUS, CONVERSION_RADIUS, CONVERSION_RADIUS))) {
            BlockState targetState = level.getBlockState(target);
            if (targetState.getFluidState().is(Fluids.WATER) && targetState.getFluidState().isSource()) {
                level.setBlock(target.immutable(), ModFluids.SCP009_WATER_BLOCK.get().defaultBlockState(), 3);
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