package com.katt.changedextras.network;

import com.katt.changedextras.common.death.DeathRespawnHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RespawnChoicePacket {
    private final boolean respawnAsTransfur;

    public RespawnChoicePacket(boolean respawnAsTransfur) {
        this.respawnAsTransfur = respawnAsTransfur;
    }

    public boolean isRespawnAsTransfur() {
        return respawnAsTransfur;
    }

    public static void encode(RespawnChoicePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.respawnAsTransfur);
    }

    public static RespawnChoicePacket decode(FriendlyByteBuf buf) {
        return new RespawnChoicePacket(buf.readBoolean());
    }

    public static void handle(RespawnChoicePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                DeathRespawnHandler.setRespawnChoice(player, msg.isRespawnAsTransfur());
            }
        });
        ctx.setPacketHandled(true);
    }
}
