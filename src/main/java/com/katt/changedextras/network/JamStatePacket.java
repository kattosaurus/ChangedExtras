package com.katt.changedextras.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Tells clients that a player started or stopped jamming, so they can run the looping
 * Jam animation. Mirrors {@link ParryStatePacket}.
 */
public class JamStatePacket {
    private final UUID playerUUID;
    private final boolean jamming;

    public JamStatePacket(UUID playerUUID, boolean jamming) {
        this.playerUUID = playerUUID;
        this.jamming = jamming;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isJamming() {
        return jamming;
    }

    public static void encode(JamStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.jamming);
    }

    public static JamStatePacket decode(FriendlyByteBuf buf) {
        return new JamStatePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void broadcast(ServerLevel level, UUID playerUUID, boolean jamming) {
        Entity entity = level.getPlayerByUUID(playerUUID);
        if (entity == null) {
            entity = level.getEntity(playerUUID);
        }

        if (entity != null) {
            final Entity targetEntity = entity;
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> targetEntity),
                    new JamStatePacket(playerUUID, jamming)
            );
        }

        if (entity instanceof ServerPlayer sp) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new JamStatePacket(playerUUID, jamming)
            );
        }
    }

    public static void handle(JamStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // This is the "firewall" from JackpotStatePacket: the server sees this call,
            // but never tries to load JamClientHandler (and its client-only classes).
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> JamClientHandler.handlePacket(msg, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}
