package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientJackpotTracker;
import com.katt.changedextras.client.particle.JackpotSmokeParticle;
import com.katt.changedextras.init.ChangedExtrasParticles;
import com.katt.changedextras.init.ChangedExtrasSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class JackpotClientHandler {
    private static final Map<UUID, JackpotSmokeParticle> ACTIVE_PARTICLES = new HashMap<>();
    private static final Map<UUID, JackpotLoopSound> ACTIVE_SOUNDS = new HashMap<>();

    public static void handlePacket(JackpotStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        var level = Minecraft.getInstance().level;
        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && localPlayer.getUUID().equals(msg.getPlayerUUID())) {
            ClientJackpotTracker.setVignetteActive(msg.isActive());
        }
        ClientJackpotTracker.setJackpotState(msg.getPlayerUUID(), msg.isActive());

        if (level == null) {
            if (!msg.isActive()) {
                stopEffects(msg.getPlayerUUID());
            }
            return;
        }

        Player targetPlayer = level.getPlayerByUUID(msg.getPlayerUUID());
        if (targetPlayer == null) {
            if (!msg.isActive()) {
                stopEffects(msg.getPlayerUUID());
            }
            return;
        }

        if (msg.isActive()) {
            // Sound Logic
            if (!ACTIVE_SOUNDS.containsKey(msg.getPlayerUUID())) {
                JackpotLoopSound sound = new JackpotLoopSound(targetPlayer);
                Minecraft.getInstance().getSoundManager().play(sound);
                ACTIVE_SOUNDS.put(msg.getPlayerUUID(), sound);
            }

            // Particle Logic
            if (!ACTIVE_PARTICLES.containsKey(msg.getPlayerUUID()) || !ACTIVE_PARTICLES.get(msg.getPlayerUUID()).isAlive()) {
                var p = Minecraft.getInstance().particleEngine.createParticle(
                        ChangedExtrasParticles.JACKPOT_AURA.get(),
                        targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), 0, 0, 0);

                if (p instanceof JackpotSmokeParticle jsp) {
                    ACTIVE_PARTICLES.put(msg.getPlayerUUID(), jsp);
                }
            }
        } else {
            stopEffects(msg.getPlayerUUID());
        }
    }

    private static void stopEffects(UUID uuid) {
        if (ACTIVE_PARTICLES.containsKey(uuid)) {
            ACTIVE_PARTICLES.get(uuid).remove();
            ACTIVE_PARTICLES.remove(uuid);
        }
        if (ACTIVE_SOUNDS.containsKey(uuid)) {
            ACTIVE_SOUNDS.get(uuid).stopLoop();
            ACTIVE_SOUNDS.remove(uuid);
        }
        ClientJackpotTracker.setJackpotState(uuid, false);
    }

    public static void stopAllEffects() {
        for (UUID uuid : java.util.List.copyOf(ACTIVE_PARTICLES.keySet())) {
            stopEffects(uuid);
        }
        for (UUID uuid : java.util.List.copyOf(ACTIVE_SOUNDS.keySet())) {
            stopEffects(uuid);
        }
        ClientJackpotTracker.clear();
    }

    public static class JackpotLoopSound extends AbstractTickableSoundInstance {
        private final Player player;

        public JackpotLoopSound(Player player) {
            super(ChangedExtrasSounds.JACKPOT_SONG.get(), SoundSource.PLAYERS, player.getRandom());
            this.player = player;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0F;
            this.pitch = 1.0F;
            this.x = (float) player.getX();
            this.y = (float) player.getY();
            this.z = (float) player.getZ();
        }

        @Override
        public void tick() {
            if (this.player.isRemoved() || !this.player.isAlive()) {
                this.stopLoop();
            } else {
                this.x = (float) this.player.getX();
                this.y = (float) this.player.getY();
                this.z = (float) this.player.getZ();
            }
        }

        public void stopLoop() {
            this.stop();
        }
    }
}
