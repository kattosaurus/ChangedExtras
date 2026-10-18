package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds players perfectly still by snapping them back to a locked position every server tick.
 * Used both for the domain intro freeze and for the parry ability.
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class PlayerFreezeManager {

    public static final int INDEFINITE = -1;

    private static final Map<UUID, FrozenPlayer> FROZEN = new ConcurrentHashMap<>();

    private PlayerFreezeManager() {}

    private static final class FrozenPlayer {
        private final Vec3 position;
        private int ticksRemaining;

        private FrozenPlayer(Vec3 position, int ticksRemaining) {
            this.position = position;
            this.ticksRemaining = ticksRemaining;
        }
    }

    public static void freeze(ServerPlayer player, int ticks) {
        if (player == null) return;
        freezeAt(player, player.position(), ticks);
    }

    public static void freezeAt(ServerPlayer player, Vec3 position, int ticks) {
        if (player == null || position == null) return;
        FROZEN.put(player.getUUID(), new FrozenPlayer(position, ticks));
        applyLock(player, position);
    }

    /**
     * Overrides how many ticks are left for an already frozen player (used for the parry fail window).
     */
    public static void setRemaining(UUID id, int ticks) {
        if (id == null) return;
        FrozenPlayer frozen = FROZEN.get(id);
        if (frozen != null) {
            frozen.ticksRemaining = ticks;
        }
    }

    public static void unfreeze(UUID id) {
        if (id != null) {
            FROZEN.remove(id);
        }
    }

    public static boolean isFrozen(UUID id) {
        return id != null && FROZEN.containsKey(id);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (FROZEN.isEmpty()) return;

        MinecraftServer server = event.getServer();
        FROZEN.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null || player.isRemoved()) {
                return true;
            }

            FrozenPlayer frozen = entry.getValue();
            applyLock(player, frozen.position);

            if (frozen.ticksRemaining == INDEFINITE) {
                return false;
            }
            if (frozen.ticksRemaining <= 0) {
                return true;
            }
            frozen.ticksRemaining--;
            return false;
        });
    }

    private static void applyLock(ServerPlayer player, Vec3 lock) {
        player.setDeltaMovement(Vec3.ZERO);
        player.resetFallDistance();
        player.connection.teleport(lock.x, lock.y, lock.z, player.getYRot(), player.getXRot());
    }
}
