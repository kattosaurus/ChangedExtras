package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientJackpotTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (msg.triggerReadyScreenShake) {
                    ClientJackpotTracker.triggerScreenShake(35, 1.8f);
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null && mc.level != null) {
                        mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
