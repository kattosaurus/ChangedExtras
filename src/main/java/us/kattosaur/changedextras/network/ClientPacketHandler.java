package us.kattosaur.changedextras.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleJackpot(JackpotStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        // This is ONLY called on the client side
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            // Put your logic here (e.g., updating UI, playing sounds, etc.)
        }
    }
}