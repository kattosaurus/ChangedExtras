package com.katt.changedextras.block;

import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class Scp009WaterBlock extends LiquidBlock {
    private static final String LAST_PROCESSED_TICK_TAG = "changedextras_scp009_last_tick";

    public Scp009WaterBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
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
                return; // already processed this entity this tick - avoids duplicate spawns when a wide
                // entity's hitbox overlaps several fluid cells at once
            }
            data.putLong(LAST_PROCESSED_TICK_TAG, currentTick);
            ProcessTransfur.progressTransfur(livingEntity, this.makeAssimilationDecision(livingEntity));
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }
}