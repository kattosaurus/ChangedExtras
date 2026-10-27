package com.katt.changedextras.common.latex;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.BlueStoneCrystalBlock;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class CarnelineTransfurHandler {
    public static final String CARNELINE_TIMER_TAG = "changedextras_carneline_infection_tick";
    public static final int DELAY_TICKS = 80; // 4 seconds of "nothing happens"

    private CarnelineTransfurHandler() {
    }

    public static void onTouchedRedCrystal(Entity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return;
        }
        if (ProcessTransfur.hasVariant(livingEntity)) {
            return;
        }
        if (livingEntity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player)) {
            return;
        }

        CompoundTag data = livingEntity.getPersistentData();
        if (data.contains(CARNELINE_TIMER_TAG)) {
            return; // Already touched once, waiting for delay to expire
        }

        // Set delay timer: "nothing happens for some seconds then you get transfurred"
        data.putLong(CARNELINE_TIMER_TAG, livingEntity.level().getGameTime() + DELAY_TICKS);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (level.isClientSide) {
            return;
        }

        CompoundTag data = livingEntity.getPersistentData();

        // 1. Process pending transfur timer
        if (data.contains(CARNELINE_TIMER_TAG)) {
            if (!livingEntity.isAlive() || ProcessTransfur.hasVariant(livingEntity)
                    || (livingEntity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))) {
                data.remove(CARNELINE_TIMER_TAG);
                return;
            }

            long targetTick = data.getLong(CARNELINE_TIMER_TAG);
            if (level.getGameTime() >= targetTick) {
                data.remove(CARNELINE_TIMER_TAG);
                if (livingEntity instanceof Player player) {
                    ProcessTransfur.setPlayerTransfurVariant(player, ModTransfurVariants.LATEX_CARNELINE.get(),
                            TransfurContext.hazard(TransfurCause.LATEX_PUDDLE));
                } else {
                    ProcessTransfur.transfur(livingEntity, level, ModTransfurVariants.LATEX_CARNELINE.get(), true);
                }
            }
            return;
        }

        // 2. Proximity check for touching red crystals (in case entity collides with side of block)
        if (ProcessTransfur.hasVariant(livingEntity)) {
            return;
        }
        if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerTransfurred(player))) {
            return;
        }

        AABB box = livingEntity.getBoundingBox().inflate(0.05D);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ChangedExtras.BLUE_STONE_CRYSTAL.get())
                    && state.getValue(BlueStoneCrystalBlock.COLOR) == BlueStoneCrystalBlock.CrystalColor.RED) {
                onTouchedRedCrystal(livingEntity);
                break;
            }
        }
    }
}
