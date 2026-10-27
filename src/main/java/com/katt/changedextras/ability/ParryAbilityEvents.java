package com.katt.changedextras.ability;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.domain.DomainExpansionManager;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.ParryProgressPacket;
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
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class ParryAbilityEvents {

    private ParryAbilityEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim == null || victim.level().isClientSide()) {
            return;
        }

        // Timeline check: Only Active Block Window (0.5s - 2.75s) intercepts and negates damage.
        // During startup (0.0s - 0.5s) or recovery (2.75s - 3.0s), isParryActive returns false.
        if (!ParryAbility.isParryActive(victim)) {
            return;
        }

        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        if (!(attackerEntity instanceof LivingEntity attacker)) {
            return;
        }

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

        // 1. Cancel/Negate the damage event completely
        event.setCanceled(true);

        CompoundTag data = victim.getPersistentData();

        // 2. Immediately interrupt and stop the parry_start animation & parry stance
        data.remove(ParryAbility.PARRY_SEQUENCE_ACTIVE_TAG);
        data.remove(ParryAbility.PARRY_TICKS_TAG);

        // Successful parry: release the movement freeze immediately
        PlayerFreezeManager.unfreeze(victim.getUUID());

        if (victim.level() instanceof ServerLevel serverLevel) {
            // 3. Despawn/fade out the 2 Iron Doors instantly (handled on clients via parrying=false)
            // 4. Play the parry_successful animation (counterattacking=true)
            ParryStatePacket.broadcast(serverLevel, victim.getUUID(), false, true);

            // Counterattack visual and sound effects
            serverLevel.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    ChangedExtrasSounds.PARRY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            serverLevel.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.4F);

            Vec3 mid = victim.getEyePosition().add(attacker.getEyePosition()).scale(0.5D);
            serverLevel.sendParticles(ParticleTypes.CRIT, mid.x, mid.y, mid.z, 10, 0.2D, 0.2D, 0.2D, 0.05D);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, mid.x, mid.y, mid.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // Counterattack knockback and damage
        float incomingDamage = event.getAmount();
        float counterDamage = Math.max(1.0F, incomingDamage * 0.9F);
        DamageSource returnSource = (victim instanceof Player p)
                ? victim.damageSources().playerAttack(p)
                : victim.damageSources().mobAttack(victim);
        attacker.hurt(returnSource, counterDamage);
        Vec3 punchDir = attacker.position().subtract(victim.position()).normalize().scale(0.35D);
        attacker.push(punchDir.x, 0.1D, punchDir.z);
        attacker.hurtMarked = true;

        // 5. Increment the session variable visual_abilities_counter by 1
        int visualCounter = data.getInt(ParryAbility.VISUAL_ABILITIES_COUNTER_TAG) + 1;
        data.putInt(ParryAbility.VISUAL_ABILITIES_COUNTER_TAG, visualCounter);

        // Check if inside active Domain Expansion break phase
        if (DomainExpansionManager.getInstance().isDomainActive(victim.getUUID())) {
            DomainExpansionManager.getInstance().onParrySuccess(victim);
            return;
        }

        // 6. Before the domain is opened, keep building the parry counter toward readiness.
        boolean becameReady = (visualCounter >= ParryAbility.REQUIRED_PARRIES_FOR_DOMAIN)
                && !data.getBoolean(ParryAbility.DOMAIN_READY_TAG);

        if (becameReady) {
            data.putBoolean(ParryAbility.DOMAIN_READY_TAG, true);
        }

        if (victim instanceof ServerPlayer serverPlayer) {
            // Ready Notification (Screen Shake): The exact moment the player completes the required parries
            // trigger a screen shake effect exclusively on the casting player's screen.
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new ParryProgressPacket(visualCounter, becameReady)
            );

            if (becameReady) {
                serverPlayer.displayClientMessage(
                        Component.literal("§d§l★ DOMAIN EXPANSION READY! §ePress ability to open \"Idle Death Gamble\"! ★"),
                        true
                );
            } else {
                serverPlayer.displayClientMessage(
                        Component.literal("§aParry: §e" + visualCounter + " §7/ §e" + ParryAbility.REQUIRED_PARRIES_FOR_DOMAIN + " §a(Visual Ability)"),
                        true
                );
            }
        }
    }
}
