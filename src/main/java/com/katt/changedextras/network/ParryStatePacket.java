package com.katt.changedextras.network;

import com.katt.changedextras.client.ClientParryTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class ParryStatePacket {
    private final UUID entityUUID;
    private final boolean parrying;
    private final boolean counterattacking;

    public ParryStatePacket(UUID entityUUID, boolean parrying, boolean counterattacking) {
        this.entityUUID = entityUUID;
        this.parrying = parrying;
        this.counterattacking = counterattacking;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public boolean isParrying() {
        return parrying;
    }

    public boolean isCounterattacking() {
        return counterattacking;
    }

    public static void encode(ParryStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityUUID);
        buf.writeBoolean(msg.parrying);
        buf.writeBoolean(msg.counterattacking);
    }

    public static ParryStatePacket decode(FriendlyByteBuf buf) {
        return new ParryStatePacket(buf.readUUID(), buf.readBoolean(), buf.readBoolean());
    }

    public static void broadcast(ServerLevel level, UUID entityUUID, boolean parrying, boolean counterattacking) {
        Entity entity = level.getPlayerByUUID(entityUUID);
        if (entity == null) {
            entity = level.getEntity(entityUUID);
        }

        if (entity != null) {
            final Entity targetEntity = entity;
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> targetEntity),
                    new ParryStatePacket(entityUUID, parrying, counterattacking)
            );
        }

        if (entity instanceof ServerPlayer sp) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new ParryStatePacket(entityUUID, parrying, counterattacking)
            );
        }
    }

    public static void handle(ParryStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientParryTracker.setParryState(msg.getEntityUUID(), msg.isParrying(), msg.isCounterattacking());
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
