package com.katt.changedextras.client.tracker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side record of which players are currently jamming, populated by
 * {@code JamStatePacket}. Mirrors {@link ClientParryTracker}.
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientJamTracker {
    private static final Map<UUID, Long> JAM_START_TIMES = new ConcurrentHashMap<>();

    private ClientJamTracker() {}

    public static void setJamState(UUID uuid, boolean jamming) {
        if (uuid == null) return;

        if (jamming) {
            // putIfAbsent keeps re-broadcasts idempotent: the animation keeps its phase
            // instead of restarting.
            JAM_START_TIMES.putIfAbsent(uuid, System.currentTimeMillis());
        } else {
            JAM_START_TIMES.remove(uuid);
        }
    }

    public static boolean isJamming(UUID uuid) {
        return uuid != null && JAM_START_TIMES.containsKey(uuid);
    }

    public static long getJamElapsedMillis(UUID uuid) {
        if (uuid == null) return 0L;
        Long start = JAM_START_TIMES.get(uuid);
        return start != null ? (System.currentTimeMillis() - start) : 0L;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            if (!JAM_START_TIMES.isEmpty()) {
                clear();
            }
            return;
        }

        // Drop entries for players that left tracking range or the dimension, so a lost
        // "stop" packet cannot leave a jam pose stuck forever.
        JAM_START_TIMES.keySet().removeIf(uuid -> mc.level.getPlayerByUUID(uuid) == null);
    }

    public static void clear() {
        JAM_START_TIMES.clear();
    }
}
