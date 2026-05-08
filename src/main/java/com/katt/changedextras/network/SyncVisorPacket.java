package com.katt.changedextras.network;

import com.katt.changedextras.common.ExoskeletonVisorStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncVisorPacket {
    private final UUID entityUUID;
    private final String patternId;
    private final int primaryColor;
    private final int secondaryColor;
    private final boolean customColors;

    public SyncVisorPacket(UUID entityUUID, String patternId, int primaryColor, int secondaryColor, boolean customColors) {
        this.entityUUID = entityUUID;
        this.patternId = patternId;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.customColors = customColors;
    }

    public UUID getEntityUUID() { return entityUUID; }
    public String getPatternId() { return patternId; }
    public int getPrimaryColor() { return primaryColor; }
    public int getSecondaryColor() { return secondaryColor; }
    public boolean isCustomColors() { return customColors; }

    public static void encode(SyncVisorPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityUUID);
        buf.writeUtf(msg.patternId);
        buf.writeInt(msg.primaryColor);
        buf.writeInt(msg.secondaryColor);
        buf.writeBoolean(msg.customColors);
    }

    public static SyncVisorPacket decode(FriendlyByteBuf buf) {
        return new SyncVisorPacket(
                buf.readUUID(),
                buf.readUtf(32767),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean()
        );
    }

    public static void broadcast(LivingEntity syncedEntity, ExoskeletonVisorStyle.Data data) {
        java.util.UUID entityUUID = syncedEntity.getUUID();
        SyncVisorPacket pkt = new SyncVisorPacket(entityUUID, data.pattern().id(), data.primaryColor(), data.secondaryColor(), data.customColors());
        ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> syncedEntity), pkt);
        if (syncedEntity instanceof ServerPlayer sp) {
            ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp), pkt);
        }
    }

    public static void handle(SyncVisorPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SyncVisorClientHandler.handlePacket(msg, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}
