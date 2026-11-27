package us.kattosaur.changedextras.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.common.shadow.ShadowHideManager;
import us.kattosaur.changedextras.network.ChangedExtrasNetwork;
import us.kattosaur.changedextras.network.ShadowHideStatePacket;

/**
 * Server-side maintenance for Shadow Hide. The ability itself is INSTANT, so
 * all per-tick work and all detection/combat hooks live here.
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ShadowHideEvents {
    private ShadowHideEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        CompoundTag data = player.getPersistentData();

        int cooldown = data.getInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG);
        if (cooldown > 0) {
            data.putInt(ShadowHideManager.TOGGLE_COOLDOWN_TAG, cooldown - 1);
        }

        if (!ShadowHideManager.isHidden(player)) {
            return;
        }

        // Bright light, untransfurring or death reveals the cat automatically.
        if (!player.isAlive() || player.isSpectator() || !ShadowHideManager.isVoidCat(player)
                || ShadowHideManager.lightLevel(player) > ShadowHideManager.MAX_SHADOW_LIGHT) {
            ShadowHideManager.setHidden(player, false);
            return;
        }

        ShadowHideManager.tickHidden(player);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof Mob mob && ShadowHideManager.isHidden(mob.getTarget())) {
            mob.setTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (ShadowHideManager.isHidden(event.getNewTarget())) {
            event.setNewTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!ShadowHideManager.isHidden(event.getEntity())) {
            return;
        }
        DamageSource source = event.getSource();
        if (ShadowHideManager.isShadowDamage(source)) {
            return;
        }
        // Entity and projectile damage cannot find a hidden cat. Environmental
        // damage (drowning, lava, void, ...) is left intact.
        if (source.getEntity() != null || source.getDirectEntity() != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!ShadowHideManager.isHidden(event.getEntity())) {
            return;
        }
        DamageSource source = event.getSource();
        if (!ShadowHideManager.isShadowDamage(source)
                && (source.getEntity() != null || source.getDirectEntity() != null)) {
            event.setCanceled(true);
        }
    }

    /** Counterplay: hitting the block a cat hides in still deals damage. */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }

        Player attacker = event.getEntity();
        Player hidden = ShadowHideManager.getHiddenPlayerAt(attacker.level(), event.getPos());
        if (hidden == null || hidden == attacker) {
            return;
        }

        if (ShadowHideManager.strikeShadow(attacker, hidden, attacker.level())) {
            attacker.level().playSound(null, event.getPos(), SoundEvents.CAT_HURT,
                    SoundSource.PLAYERS, 1.0F, 0.8F);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            ShadowHideManager.setHidden(player, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ShadowHideManager.setHidden(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ShadowHideManager.setHidden(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ShadowHideManager.setHidden(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ShadowHideManager.setHidden(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        sendHiddenStateTo(event.getEntity());
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        sendHiddenStateTo(event.getTarget());
    }

    private static void sendHiddenStateTo(Entity target) {
        if (!(target instanceof Player player) || !ShadowHideManager.isHidden(player)) {
            return;
        }

        ChangedExtrasNetwork.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new ShadowHideStatePacket(player.getUUID(), true)
        );
    }
}
