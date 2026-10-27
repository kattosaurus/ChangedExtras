package com.katt.changedextras.common.scp;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.katt.changedextras.entity.beasts.AbstractScp009Entity;
import com.katt.changedextras.fluid.ModFluids;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public final class Scp009Helper {
    public static final String LAST_CONTACT_TICK_TAG = "changedextras_scp009_last_contact_tick";
    public static final String CONTACT_START_TICK_TAG = "changedextras_scp009_contact_start_tick";
    public static final String NEXT_CONTACT_DAMAGE_TICK_TAG = "changedextras_scp009_next_contact_damage_tick";

    public static final float CONTACT_DAMAGE = 1.0F;
    public static final int CONTACT_DAMAGE_INTERVAL_TICKS = 10;
    public static final int CONTACT_INFECTION_DELAY_TICKS = 60;

    private Scp009Helper() {
    }

    public static boolean isInfected(@Nullable LivingEntity entity) {
        if (entity instanceof AbstractScp009Entity) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        return ProcessTransfur.getEntityVariant(entity)
                .map(variant -> variant == ModTransfurVariants.SCP_009.get())
                .orElse(false);
    }

    public static boolean isProtectedTarget(LivingEntity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    public static void playCrystalBreak(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS,
                1.0F, 0.8F + level.random.nextFloat() * 0.2F);
    }

    public static boolean forceInfect(LivingEntity entity) {
        if (entity.level().isClientSide || isProtectedTarget(entity) || isInfected(entity)) {
            return false;
        }

        entity.setHealth(Math.max(entity.getHealth(), 1.0F));

        if (entity instanceof Player player) {
            ProcessTransfur.setPlayerTransfurVariant(player, ModTransfurVariants.SCP_009.get(),
                    TransfurContext.hazard(TransfurCause.LATEX_PUDDLE));
        } else {
            ProcessTransfur.changeTransfur(entity, ModTransfurVariants.SCP_009.get());
        }

        return true;
    }

    public static void tickContact(LivingEntity entity, ServerLevel level, long gameTime) {
        if (isProtectedTarget(entity) || isInfected(entity)) {
            return;
        }

        CompoundTag data = entity.getPersistentData();
        if (!isTouchingInfectedBlock(level, entity)) {
            if (gameTime - data.getLong(LAST_CONTACT_TICK_TAG) > 20L) {
                resetContact(data);
            }
            return;
        }

        data.putLong(LAST_CONTACT_TICK_TAG, gameTime);

        long contactStart = data.contains(CONTACT_START_TICK_TAG)
                ? data.getLong(CONTACT_START_TICK_TAG)
                : gameTime;
        data.putLong(CONTACT_START_TICK_TAG, contactStart);

        long nextDamageTick = data.getLong(NEXT_CONTACT_DAMAGE_TICK_TAG);
        if (nextDamageTick <= 0L || gameTime >= nextDamageTick) {
            data.putLong(NEXT_CONTACT_DAMAGE_TICK_TAG, gameTime + CONTACT_DAMAGE_INTERVAL_TICKS);

            if (entity.getHealth() <= CONTACT_DAMAGE) {
                playCrystalBreak(level, entity);
                resetContact(data);
                forceInfect(entity);
                return;
            }

            entity.hurt(level.damageSources().freeze(), CONTACT_DAMAGE);
        }

        if (gameTime - contactStart >= CONTACT_INFECTION_DELAY_TICKS) {
            playCrystalBreak(level, entity);
            resetContact(data);
            forceInfect(entity);
        }
    }

    public static void convertTouchedLiquids(LivingEntity entity, ServerLevel level) {
        AABB box = entity.getBoundingBox().inflate(0.1D);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof LiquidBlock
                    && state.getBlock() != ModFluids.SCP009_WATER_BLOCK.get()) {
                level.setBlock(pos.immutable(), ModFluids.SCP009_WATER_BLOCK.get().defaultBlockState(), 3);
            }
        }
    }

    private static boolean isTouchingInfectedBlock(ServerLevel level, LivingEntity entity) {
        AABB box = entity.getBoundingBox().inflate(0.1D);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() == ChangedExtras.SCP009_CRYSTAL.get()
                    || state.getBlock() == ChangedExtras.SCP009_CRYSTAL_SMALL.get()
                    || state.getBlock() == ModFluids.SCP009_WATER_BLOCK.get()) {
                return true;
            }
        }

        return false;
    }

    private static void resetContact(CompoundTag data) {
        data.remove(LAST_CONTACT_TICK_TAG);
        data.remove(CONTACT_START_TICK_TAG);
        data.remove(NEXT_CONTACT_DAMAGE_TICK_TAG);
    }
}
