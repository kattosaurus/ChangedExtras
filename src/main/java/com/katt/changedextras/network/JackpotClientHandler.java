package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientJackpotTracker;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JackpotClientHandler {

    public static void handlePacket(JackpotStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        // Always record the authoritative state, even when the target player entity is not
        // loaded/visible yet (for example when the packet arrives mid dimension change after
        // a domain collapse). The tick-driven aura/sound pick it up as soon as the player is
        // available, so a single dropped/early packet can no longer desync the effects.
        ClientJackpotTracker.setJackpotState(msg.getPlayerUUID(), msg.isActive());
    }
}
