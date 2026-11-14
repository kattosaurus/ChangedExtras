package us.kattosaur.changedextras.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class JackpotStatePacket {
    private final UUID playerUUID;
    private final boolean active;

    public JackpotStatePacket(UUID playerUUID, boolean active) {
        this.playerUUID = playerUUID;
        this.active = active;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public boolean isActive() { return active; }

    public static void encode(JackpotStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.active);
    }

    public static JackpotStatePacket decode(FriendlyByteBuf buf) {
        return new JackpotStatePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void broadcast(ServerLevel level, UUID playerUUID, boolean active) {
        ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(
                        () -> level.getPlayerByUUID(playerUUID)),
                new JackpotStatePacket(playerUUID, active)
        );

        Player self = level.getPlayerByUUID(playerUUID);
        if (self instanceof net.minecraft.server.level.ServerPlayer sp) {
            ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp),
                    new JackpotStatePacket(playerUUID, active));
        }
    }

    public static void handle(JackpotStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // This is the magic "firewall".
            // The server will see this call, but it won't try to load JackpotClientHandler.
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> JackpotClientHandler.handlePacket(msg, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}