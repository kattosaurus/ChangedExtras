package com.katt.changedextras.network;

import com.katt.changedextras.client.tracker.ClientPrepareTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PrepareStatePacket {
    private final int casterId;
    private final int highlightTicks;
    private final int fadeTicks;

    public PrepareStatePacket(int casterId, int highlightTicks, int fadeTicks) {
        this.casterId = casterId;
        this.highlightTicks = highlightTicks;
        this.fadeTicks = fadeTicks;
    }

    public static void encode(PrepareStatePacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.casterId);
        buf.writeVarInt(msg.highlightTicks);
        buf.writeVarInt(msg.fadeTicks);
    }

    public static PrepareStatePacket decode(FriendlyByteBuf buf) {
        return new PrepareStatePacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public static void broadcast(ServerLevel level, Entity caster, int highlightTicks, int fadeTicks) {
        PrepareStatePacket packet = new PrepareStatePacket(caster.getId(), highlightTicks, fadeTicks);
        ChangedExtrasNetwork.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> caster),
                packet
        );

        if (caster instanceof ServerPlayer serverPlayer) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    packet
            );
        }
    }

    public static void handle(PrepareStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ClientPrepareTracker.start(msg.casterId, msg.highlightTicks, msg.fadeTicks)));
        ctx.get().setPacketHandled(true);
    }
}
