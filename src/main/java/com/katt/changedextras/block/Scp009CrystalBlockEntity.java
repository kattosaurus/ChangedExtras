package com.katt.changedextras.block;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Scp009CrystalBlockEntity extends BlockEntity {
    public static final int ICE_INFECTION_COOLDOWN_TICKS = 10 * 60 * 20;
    private static final String LAST_ICE_INFECTION_TICK_TAG = "LastIceInfectionTick";

    private long lastIceInfectionTick = Long.MIN_VALUE / 2L;

    public Scp009CrystalBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedExtras.SCP009_CRYSTAL_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean canInfectIce(long gameTime) {
        return gameTime - this.lastIceInfectionTick >= ICE_INFECTION_COOLDOWN_TICKS;
    }

    public void markIceInfection(long gameTime) {
        this.lastIceInfectionTick = gameTime;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong(LAST_ICE_INFECTION_TICK_TAG, this.lastIceInfectionTick);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(LAST_ICE_INFECTION_TICK_TAG)) {
            this.lastIceInfectionTick = tag.getLong(LAST_ICE_INFECTION_TICK_TAG);
        }
    }
}
