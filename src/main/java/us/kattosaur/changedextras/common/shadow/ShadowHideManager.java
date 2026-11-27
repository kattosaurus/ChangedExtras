package us.kattosaur.changedextras.common.shadow;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import us.kattosaur.changedextras.entity.ModTransfurVariants;
import us.kattosaur.changedextras.network.ShadowHideStatePacket;

/**
 * Server-side state for the Void Cat's Shadow Hide ability.
 *
 * A hidden player is stored in persistent data so the state survives ability
 * instance reloads, but every mutating path goes through {@link #setHidden} so
 * clients, speed and safety cleanup stay in sync.
 */
public final class ShadowHideManager {
    public static final String HIDDEN_TAG = "changedextras:shadow_hidden";
    public static final String BASE_SPEED_TAG = "changedextras:shadow_base_speed";
    public static final String TOGGLE_COOLDOWN_TAG = "changedextras:shadow_hide_cooldown";
    public static final String BLOCK_HIT_COOLDOWN_TAG = "changedextras:shadow_block_hit_cooldown";

    /** Maximum light level that counts as a usable shadow. */
    public static final int MAX_SHADOW_LIGHT = 5;
    /** Vanilla walking speed; sprinting is suppressed separately. */
    public static final double WALK_SPEED = 0.1D;
    /** Damage dealt when another player strikes a hidden cat's shadow block. */
    public static final float BLOCK_HIT_DAMAGE = 4.0F;
    /** Minimum ticks between block-hit strikes from the same attacker. */
    public static final int BLOCK_HIT_COOLDOWN_TICKS = 10;
    private static final String DAMAGE_SOURCE_ID = "changedextras.shadow";
    private static final Holder<DamageType> SHADOW_DAMAGE_TYPE = Holder.direct(
            new DamageType(DAMAGE_SOURCE_ID, DamageScaling.ALWAYS, 0.1F));

    private ShadowHideManager() {}

    public static boolean isHidden(Entity entity) {
        return entity instanceof Player player
                && player.getPersistentData().getBoolean(HIDDEN_TAG);
    }

    public static boolean isVoidCat(LivingEntity entity) {
        if (!(entity instanceof Player player) || !ProcessTransfur.isPlayerTransfurred(player)) {
            return false;
        }
        IAbstractChangedEntity changed = IAbstractChangedEntity.forPlayer(player);
        return changed != null && changed.getSelfVariant() == ModTransfurVariants.VOID_CAT.get();
    }

    public static int lightLevel(Player player) {
        if (player == null) {
            return 15;
        }
        return player.level().getMaxLocalRawBrightness(player.blockPosition());
    }

    public static boolean canEnterShadow(Player player) {
        return player != null
                && player.isAlive()
                && !player.isSpectator()
                && isVoidCat(player)
                && lightLevel(player) <= MAX_SHADOW_LIGHT;
    }

    public static void setHidden(Player player, boolean hidden) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(HIDDEN_TAG) == hidden) {
            return;
        }

        if (hidden) {
            AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                data.putDouble(BASE_SPEED_TAG, speed.getBaseValue());
            }
            applyWalkCap(player);
        } else {
            restoreSpeed(player);
        }

        data.putBoolean(HIDDEN_TAG, hidden);

        if (player.level() instanceof ServerLevel serverLevel) {
            ShadowHideStatePacket.broadcast(serverLevel, player.getUUID(), hidden);
        }
    }

    /** Keeps a hidden cat at walking speed even if Changed or a potion raises it. */
    public static void tickHidden(Player player) {
        if (!isHidden(player)) {
            return;
        }

        player.setSprinting(false);
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        applyWalkCap(player);
    }

    private static void applyWalkCap(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getBaseValue() > WALK_SPEED) {
            speed.setBaseValue(WALK_SPEED);
        }
    }

    private static void restoreSpeed(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(BASE_SPEED_TAG)) {
            return;
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(data.getDouble(BASE_SPEED_TAG));
        }
        data.remove(BASE_SPEED_TAG);
    }

    /**
     * Finds a hidden player occupying or standing on the clicked block. This is
     * the counterplay to hiding: the model cannot be hit, but its shadow can.
     */
    public static Player getHiddenPlayerAt(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }

        for (Player player : level.players()) {
            if (!isHidden(player)) {
                continue;
            }

            BlockPos feet = player.blockPosition();
            if (feet.equals(pos) || feet.below().equals(pos) || feet.above().equals(pos)) {
                return player;
            }
        }
        return null;
    }

    public static DamageSource shadowDamage(Player attacker) {
        return new DamageSource(SHADOW_DAMAGE_TYPE, attacker, attacker);
    }

    public static boolean isShadowDamage(DamageSource source) {
        return source != null && DAMAGE_SOURCE_ID.equals(source.getMsgId());
    }

    /** Applies damage from a struck shadow block, with a short per-attacker cooldown. */
    public static boolean strikeShadow(Player attacker, Player hidden, Level level) {
        if (attacker == null || hidden == null || level == null || level.isClientSide()) {
            return false;
        }

        CompoundTag data = attacker.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(BLOCK_HIT_COOLDOWN_TAG)) {
            return false;
        }
        data.putLong(BLOCK_HIT_COOLDOWN_TAG, gameTime + BLOCK_HIT_COOLDOWN_TICKS);

        hidden.setLastHurtByPlayer(attacker);
        hidden.hurt(shadowDamage(attacker), BLOCK_HIT_DAMAGE);
        return true;
    }

    /** Clears hidden state without broadcasting, used on logout/respawn cleanup. */
    public static void clearWithoutSync(Player player) {
        if (player == null) {
            return;
        }
        restoreSpeed(player);
        player.getPersistentData().remove(HIDDEN_TAG);
    }
}
