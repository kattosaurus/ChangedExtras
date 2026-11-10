package com.katt.changedextras.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParryProgressPacket {
    private final int visualAbilitiesCounter;
    private final boolean triggerReadyScreenShake;

    public ParryProgressPacket(int visualAbilitiesCounter, boolean triggerReadyScreenShake) {
        this.visualAbilitiesCounter = visualAbilitiesCounter;
        this.triggerReadyScreenShake = triggerReadyScreenShake;
    }

    public static void encode(ParryProgressPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.visualAbilitiesCounter);
        buf.writeBoolean(msg.triggerReadyScreenShake);
    }

    public static ParryProgressPacket decode(FriendlyByteBuf buf) {
        return new ParryProgressPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(ParryProgressPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        ClientParryHandler.handleProgress(msg.triggerReadyScreenShake)));
        ctx.get().setPacketHandled(true);
    }
}