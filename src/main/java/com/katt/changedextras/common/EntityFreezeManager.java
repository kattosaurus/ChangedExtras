package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds non-player entities in place by snapping them back to a locked position every
 * server tick. Rotation is intentionally left untouched so mobs can still turn to look
 * around, but any attempted movement is cancelled (like an extreme slowness effect).
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class EntityFreezeManager {

    public static final int INDEFINITE = -1;

    private static final Map<UUID, FrozenEntity> FROZEN = new ConcurrentHashMap<>();

    private EntityFreezeManager() {}

    private static final class FrozenEntity {
        private final Entity entity;
        private final Vec3 position;
        private int ticksRemaining;

        private FrozenEntity(Entity entity, Vec3 position, int ticksRemaining) {
            this.entity = entity;
            this.position = position;
            this.ticksRemaining = ticksRemaining;
        }
    }

    public static void freeze(Entity entity, int ticks) {
        if (entity != null) {
            freezeAt(entity, entity.position(), ticks);
        }
    }

    public static void freezeAt(Entity entity, Vec3 position, int ticks) {
        if (entity == null || position == null) return;
        // Players are handled by PlayerFreezeManager
        if (entity instanceof ServerPlayer) return;
        FROZEN.put(entity.getUUID(), new FrozenEntity(entity, position, ticks));
        applyLock(entity, position);
    }

    public static void setRemaining(UUID id, int ticks) {
        FrozenEntity frozen = id == null ? null : FROZEN.get(id);
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

        FROZEN.entrySet().removeIf(entry -> {
            FrozenEntity frozen = entry.getValue();
            Entity entity = frozen.entity;
            if (entity == null || entity.isRemoved()) {
                return true;
            }

            applyLock(entity, frozen.position);

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

    private static void applyLock(Entity entity, Vec3 lock) {
        entity.setDeltaMovement(Vec3.ZERO);
        entity.resetFallDistance();
        entity.setPos(lock.x, lock.y, lock.z);
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
        }
    }
}
