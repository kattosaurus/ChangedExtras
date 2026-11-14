package us.kattosaur.changedextras.network;

import us.kattosaur.changedextras.client.tracker.ClientJamTracker;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JamClientHandler {

    public static void handlePacket(JamStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ClientJamTracker.setJamState(msg.getPlayerUUID(), msg.isJamming());
    }
}
