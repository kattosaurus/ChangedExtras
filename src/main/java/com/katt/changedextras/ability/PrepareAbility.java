package com.katt.changedextras.ability;

import com.katt.changedextras.common.freeze.EntityFreezeManager;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.network.PrepareStatePacket;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class PrepareAbility extends SimpleAbility {
    /** The Prepare animation is 11 seconds long, so movement is locked for the same duration. */
    public static final int PREPARE_TICKS = 220;
    public static final int HIGHLIGHT_TICKS = PREPARE_TICKS;
    public static final int FADE_TICKS = 30;

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changedextras.prepare.name");
    }

    @Override
    public List<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(Component.translatable("ability.changedextras.prepare.description"));
    }

    @Override
    public AbstractAbility.UseType getUseType(IAbstractChangedEntity entity) {
        return AbstractAbility.UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 240;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getEntity() != null && entity.getEntity().isAlive();
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        Level level = entity.getLevel();
        if (level.isClientSide()) {
            return;
        }

        LivingEntity user = (LivingEntity) entity.getEntity();
        user.level().playSound(null, user.blockPosition(), SoundEvents.CAT_HISS, user.getSoundSource(), 1.0F, 0.75F);

        if (user instanceof ServerPlayer player) {
            PlayerFreezeManager.freeze(player, PREPARE_TICKS);
        }

        ChangedEntity changed = entity.getChangedEntity();
        if (changed != null && changed != user) {
            EntityFreezeManager.freeze(changed, PREPARE_TICKS);
        }

        if (level instanceof ServerLevel serverLevel) {
            PrepareStatePacket.broadcast(serverLevel, user, HIGHLIGHT_TICKS, FADE_TICKS);
        }
    }
}
