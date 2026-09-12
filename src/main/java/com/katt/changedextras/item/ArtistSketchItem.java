package com.katt.changedextras.item;

import com.katt.changedextras.entity.ModEntities;
import com.katt.changedextras.entity.beasts.ArtistEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ArtistSketchItem extends Item {
    public ArtistSketchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        ArtistEntity artist = ModEntities.ARTIST.get().create(serverLevel);
        if (artist == null) {
            return InteractionResult.FAIL;
        }

        artist.applyBossStats();
        artist.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                context.getRotation(),
                0.0F
        );
        serverLevel.addFreshEntity(artist);
        serverLevel.playSound(null, spawnPos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 0.8F, 1.15F);

        ItemStack stack = context.getItemInHand();
        if (!context.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}
