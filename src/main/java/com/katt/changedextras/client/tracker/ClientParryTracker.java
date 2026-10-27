package com.katt.changedextras.client.tracker;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientParryTracker {
    private static final Map<UUID, Long> PARRY_START_TIMES = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> PARRY_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> COUNTERATTACK_TIMERS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> COUNTERATTACK_START_TIMES = new ConcurrentHashMap<>();

    private ClientParryTracker() {}

    public static void setParryState(UUID uuid, boolean parrying, boolean counterattacking) {
        if (uuid == null) return;

        if (parrying) {
            PARRY_START_TIMES.put(uuid, System.currentTimeMillis());
            PARRY_TICKS.put(uuid, 0);
            COUNTERATTACK_TIMERS.remove(uuid);
            COUNTERATTACK_START_TIMES.remove(uuid);
        } else {
            PARRY_START_TIMES.remove(uuid);
            PARRY_TICKS.remove(uuid);
        }

        if (counterattacking) {
            // Immediately interrupt parry stance and doors
            PARRY_START_TIMES.remove(uuid);
            PARRY_TICKS.remove(uuid);
            COUNTERATTACK_TIMERS.put(uuid, 20); // 1.0s (20 ticks) for parry_successful
            COUNTERATTACK_START_TIMES.put(uuid, System.currentTimeMillis());
        }
    }

    public static boolean isParrying(UUID uuid) {
        return uuid != null && PARRY_START_TIMES.containsKey(uuid);
    }

    public static boolean isCounterattacking(UUID uuid) {
        return uuid != null && COUNTERATTACK_TIMERS.getOrDefault(uuid, 0) > 0;
    }

    public static int getParryTicks(UUID uuid) {
        return uuid != null ? PARRY_TICKS.getOrDefault(uuid, 0) : 0;
    }

    public static long getParryElapsedMillis(UUID uuid) {
        if (uuid == null) return 0L;
        Long start = PARRY_START_TIMES.get(uuid);
        return start != null ? (System.currentTimeMillis() - start) : 0L;
    }

    public static long getCounterattackElapsedMillis(UUID uuid) {
        if (uuid == null) return 0L;
        Long start = COUNTERATTACK_START_TIMES.get(uuid);
        return start != null ? (System.currentTimeMillis() - start) : 0L;
    }

    public static Set<UUID> getParryingEntities() {
        return Collections.unmodifiableSet(PARRY_START_TIMES.keySet());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // Advance parry ticks (total 3.0s = 60 ticks)
        for (UUID uuid : PARRY_TICKS.keySet()) {
            PARRY_TICKS.compute(uuid, (key, ticks) -> {
                if (ticks == null || ticks >= 60) {
                    PARRY_START_TIMES.remove(key);
                    return null;
                }
                return ticks + 1;
            });
        }

        // Decrement counterattack timers (total 1.0s = 20 ticks)
        for (UUID uuid : COUNTERATTACK_TIMERS.keySet()) {
            COUNTERATTACK_TIMERS.compute(uuid, (key, value) -> {
                if (value == null || value <= 1) {
                    COUNTERATTACK_START_TIMES.remove(key);
                    return null;
                }
                return value - 1;
            });
        }
    }

    public static void clear() {
        PARRY_START_TIMES.clear();
        PARRY_TICKS.clear();
        COUNTERATTACK_TIMERS.clear();
        COUNTERATTACK_START_TIMES.clear();
    }
}
