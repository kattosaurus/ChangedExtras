package com.katt.changedextras.network;

import com.katt.changedextras.common.JammerVipManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncJammerVipPacket {
    private final UUID playerUuid;
    private final boolean vip;

    public SyncJammerVipPacket(UUID playerUuid, boolean vip) {
        this.playerUuid = playerUuid;
        this.vip = vip;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public boolean isVip() {
        return vip;
    }

    public static void encode(SyncJammerVipPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUuid);
        buf.writeBoolean(msg.vip);
    }

    public static SyncJammerVipPacket decode(FriendlyByteBuf buf) {
        return new SyncJammerVipPacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(SyncJammerVipPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            JammerVipManager.setClientVip(msg.getPlayerUuid(), msg.isVip());
        }));
        ctx.setPacketHandled(true);
    }
}
