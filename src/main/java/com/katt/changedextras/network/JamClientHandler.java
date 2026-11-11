package com.katt.changedextras.network;

import com.katt.changedextras.client.tracker.ClientJamTracker;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JamClientHandler {

    public static void handlePacket(JamStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ClientJamTracker.setJamState(msg.getPlayerUUID(), msg.isJamming());
    }
}
