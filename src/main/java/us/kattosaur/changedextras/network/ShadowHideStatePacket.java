package us.kattosaur.changedextras.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import us.kattosaur.changedextras.client.tracker.ClientShadowHideTracker;

import java.util.UUID;
import java.util.function.Supplier;

public class ShadowHideStatePacket {
    private final UUID playerUUID;
    private final boolean hidden;

    public ShadowHideStatePacket(UUID playerUUID, boolean hidden) {
        this.playerUUID = playerUUID;
        this.hidden = hidden;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isHidden() {
        return hidden;
    }

    public static void encode(ShadowHideStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.hidden);
    }

    public static ShadowHideStatePacket decode(FriendlyByteBuf buf) {
        return new ShadowHideStatePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void broadcast(ServerLevel level, UUID playerUUID, boolean hidden) {
        Player player = level.getPlayerByUUID(playerUUID);
        if (player != null) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new ShadowHideStatePacket(playerUUID, hidden)
            );
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new ShadowHideStatePacket(playerUUID, hidden)
            );
        }
    }

    public static void handle(ShadowHideStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> handleClient(msg)));
        ctx.get().setPacketHandled(true);
    }

    private static void handleClient(ShadowHideStatePacket msg) {
        ClientShadowHideTracker.setHidden(msg.getPlayerUUID(), msg.isHidden());
        if (Minecraft.getInstance().level != null) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(msg.getPlayerUUID());
            if (player != null) {
                player.refreshDimensions();
            }
        }
    }
}
