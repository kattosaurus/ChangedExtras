package com.katt.changedextras.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientParryTracker {
    private static final Map<UUID, Boolean> PARRYING = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> COUNTERATTACK_TIMERS = new ConcurrentHashMap<>();

    public static void setParryState(UUID uuid, boolean parrying, boolean counterattacking) {
        if (uuid == null) return;

        if (parrying) {
            PARRYING.put(uuid, true);
        } else {
            PARRYING.remove(uuid);
        }

        if (counterattacking) {
            COUNTERATTACK_TIMERS.put(uuid, 10);
            PARRYING.remove(uuid);
        }
    }

    public static boolean isParrying(UUID uuid) {
        return uuid != null && PARRYING.getOrDefault(uuid, false);
    }

    public static boolean isCounterattacking(UUID uuid) {
        return uuid != null && COUNTERATTACK_TIMERS.getOrDefault(uuid, 0) > 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || COUNTERATTACK_TIMERS.isEmpty()) return;

        for (UUID uuid : COUNTERATTACK_TIMERS.keySet()) {
            COUNTERATTACK_TIMERS.compute(uuid, (key, value) -> {
                if (value == null || value <= 1) {
                    return null;
                }
                return value - 1;
            });
        }
    }

    public static void clear() {
        PARRYING.clear();
        COUNTERATTACK_TIMERS.clear();
    }
}
