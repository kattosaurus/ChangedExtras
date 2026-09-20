package com.katt.changedextras.ability;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.network.ParryStatePacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class ParryAbilityEvents {

    private ParryAbilityEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim == null || victim.level().isClientSide()) {
            return;
        }

        if (!ParryAbility.isParrying(victim)) {
            return;
        }

        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        if (!(attackerEntity instanceof LivingEntity attacker)) {
            return;
        }

        // Only melee attacks: direct attacker must match entity (no arrows/bullets/projectiles)
        if (directEntity != attackerEntity) {
            return;
        }

        if (source.is(DamageTypeTags.IS_PROJECTILE) || source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        if (victim.distanceToSqr(attacker) > 36.0D) {
            return;
        }

        // Prevent self-parry
        if (attacker.getUUID().equals(victim.getUUID())) {
            return;
        }

        // Block incoming attack
        event.setCanceled(true);

        float incomingDamage = event.getAmount();
        float counterDamage = Math.max(1.0F, incomingDamage * 0.9F);

        // Counterattack attacker with left fist returning ~0.9x damage
        DamageSource returnSource = (victim instanceof Player p)
                ? victim.damageSources().playerAttack(p)
                : victim.damageSources().mobAttack(victim);
        attacker.hurt(returnSource, counterDamage);

        Vec3 punchDir = attacker.position().subtract(victim.position()).normalize().scale(0.35D);
        attacker.push(punchDir.x, 0.1D, punchDir.z);
        attacker.hurtMarked = true;

        if (victim.level() instanceof ServerLevel serverLevel) {
            // Play custom parry sound
            serverLevel.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    ChangedExtrasSounds.PARRY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            serverLevel.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.4F);

            Vec3 mid = victim.getEyePosition().add(attacker.getEyePosition()).scale(0.5D);
            serverLevel.sendParticles(ParticleTypes.CRIT, mid.x, mid.y, mid.z, 10, 0.2D, 0.2D, 0.2D, 0.05D);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, mid.x, mid.y, mid.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            // Trigger counterattack animation
            ParryStatePacket.broadcast(serverLevel, victim.getUUID(), false, true);
        }

        // Increment parry count towards 5
        CompoundTag data = victim.getPersistentData();
        int currentCount = data.getInt(ParryAbility.PARRY_COUNT_TAG) + 1;

        if (currentCount >= ParryAbility.REQUIRED_PARRIES) {
            data.putInt(ParryAbility.PARRY_COUNT_TAG, ParryAbility.REQUIRED_PARRIES);
            data.putBoolean(ParryAbility.PARRY_READY_FOR_SPAM_TAG, true);
            data.putInt(ParryAbility.PARRY_READY_TIMEOUT_TAG, ParryAbility.PARRY_READY_TIMEOUT_TICKS);
            data.putBoolean(ParryAbility.PARRY_ACTIVE_TAG, false);
            if (victim.level() instanceof ServerLevel serverLevel) {
                ParryStatePacket.broadcast(serverLevel, victim.getUUID(), false, false);
                serverLevel.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            if (victim instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(
                        Component.literal("§e§l5/5 Parries! §c§lSPAM PARRY TO UNLEASH JACKPOT!"),
                        true
                );
            }
        } else {
            data.putInt(ParryAbility.PARRY_COUNT_TAG, currentCount);
            if (victim instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(
                        Component.literal("§6Parry: §e" + currentCount + " §7/ §e" + ParryAbility.REQUIRED_PARRIES),
                        true
                );
            }
        }
    }
}
