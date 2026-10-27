package com.katt.changedextras.client.sound;
import com.katt.changedextras.client.tracker.ClientJackpotTracker;

import com.katt.changedextras.init.ChangedExtrasSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Plays the looping jackpot theme for every player that currently has the jackpot active.
 * Driven every client tick from {@link ClientJackpotTracker} state (which is populated by
 * {@code JackpotStatePacket}). Being state-driven makes it self-healing: if a single packet
 * is missed or arrives while the player entity is not yet loaded, the sound starts/stops on
 * the next tick instead of getting stuck out of sync.
 */
@OnlyIn(Dist.CLIENT)
public final class JackpotSoundManager {

    private static final Map<UUID, SoundInstance> ACTIVE_SOUNDS = new HashMap<>();

    private JackpotSoundManager() {}

    public static void tick(Minecraft mc) {
        ClientLevel level = mc.level;
        if (level == null) {
            clear(mc);
            return;
        }

        Set<UUID> desired = new HashSet<>();

        for (Player player : level.players()) {
            UUID id = player.getUUID();
            if (!ClientJackpotTracker.isJackpotActive(id)) {
                continue;
            }
            desired.add(id);
            if (!ACTIVE_SOUNDS.containsKey(id)) {
                startSound(id);
            }
        }

        Iterator<Map.Entry<UUID, SoundInstance>> iterator = ACTIVE_SOUNDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, SoundInstance> entry = iterator.next();
            if (!desired.contains(entry.getKey())) {
                mc.getSoundManager().stop(entry.getValue());
                iterator.remove();
            }
        }
    }

    private static void startSound(UUID id) {
        SimpleSoundInstance sound = new SimpleSoundInstance(
                ChangedExtrasSounds.JACKPOT_LOOP.get().getLocation(),
                SoundSource.RECORDS,
                1.0f, 1.0f,
                RandomSource.create(),
                true,
                0,
                SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0,
                true
        );
        Minecraft.getInstance().getSoundManager().play(sound);
        ACTIVE_SOUNDS.put(id, sound);
    }

    public static void clear(Minecraft mc) {
        for (SoundInstance sound : ACTIVE_SOUNDS.values()) {
            mc.getSoundManager().stop(sound);
        }
        ACTIVE_SOUNDS.clear();
    }
}
