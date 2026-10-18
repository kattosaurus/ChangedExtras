package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientDomainTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sent to players inside a domain whenever it swaps to a new scenario.
 * Triggers a short fullscreen white flash on the client.
 */
public class DomainFlashPacket {

    public DomainFlashPacket() {
    }

    public static void encode(DomainFlashPacket msg, FriendlyByteBuf buf) {
    }

    public static DomainFlashPacket decode(FriendlyByteBuf buf) {
        return new DomainFlashPacket();
    }

    public static void handle(DomainFlashPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientDomainTracker::triggerFlash);
        });
        ctx.get().setPacketHandled(true);
    }
}
