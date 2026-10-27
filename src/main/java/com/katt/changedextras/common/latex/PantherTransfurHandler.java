package com.katt.changedextras.common.latex;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.BlueStoneCrystalBlock;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
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
public final class PantherTransfurHandler {
    private static final String LAST_TICK_TAG = "changedextras_panther_last_tick";

    private PantherTransfurHandler() {
    }

    public static void onTouchedWhiteCrystal(Entity entity, BlockState state) {
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
        if (state.getValue(BlueStoneCrystalBlock.COLOR) != BlueStoneCrystalBlock.CrystalColor.WHITE) {
            return;
        }
        // Smallest ones shouldn't infect
        if (state.getValue(BlueStoneCrystalBlock.AGE) <= 0) {
            return;
        }

        CompoundTag data = livingEntity.getPersistentData();
        long currentTick = livingEntity.level().getGameTime();
        if (data.getLong(LAST_TICK_TAG) == currentTick) {
            return;
        }
        data.putLong(LAST_TICK_TAG, currentTick);

        float progress = state.getValue(BlueStoneCrystalBlock.AGE) == 1 ? 6.0f : 8.0f;
        ProcessTransfur.progressTransfur(livingEntity, LatexAssimilationDecision.fromBlockOrItem(
                ModTransfurVariants.LATEX_PANTHER.get(),
                TransfurContext.hazard(TransfurCause.CRYSTAL),
                progress
        ));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (level.isClientSide) {
            return;
        }
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
                    && state.getValue(BlueStoneCrystalBlock.COLOR) == BlueStoneCrystalBlock.CrystalColor.WHITE
                    && state.getValue(BlueStoneCrystalBlock.AGE) > 0) {
                onTouchedWhiteCrystal(livingEntity, state);
                break;
            }
        }
    }
}
