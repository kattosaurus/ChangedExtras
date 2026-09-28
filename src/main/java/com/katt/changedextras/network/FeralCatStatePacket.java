package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientFeralCatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class FeralCatStatePacket {
    private final UUID playerUUID;
    private final boolean feral;

    public FeralCatStatePacket(UUID playerUUID, boolean feral) {
        this.playerUUID = playerUUID;
        this.feral = feral;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isFeral() {
        return feral;
    }

    public static void encode(FeralCatStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.feral);
    }

    public static FeralCatStatePacket decode(FriendlyByteBuf buf) {
        return new FeralCatStatePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void broadcast(ServerLevel level, UUID playerUUID, boolean feral) {
        Player player = level.getPlayerByUUID(playerUUID);
        if (player != null) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new FeralCatStatePacket(playerUUID, feral)
            );
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new FeralCatStatePacket(playerUUID, feral)
            );
        }
    }

    public static void handle(FeralCatStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(msg));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleClient(FeralCatStatePacket msg) {
        ClientFeralCatTracker.setFeral(msg.getPlayerUUID(), msg.isFeral());
        Player player = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getPlayerByUUID(msg.getPlayerUUID()) : null;
        if (player != null) {
            player.refreshDimensions();
        }
    }
}
