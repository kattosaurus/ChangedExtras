package com.katt.changedextras.network;

import com.katt.changedextras.client.tracker.ClientDomainTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DomainStatePacket {
    private final boolean active;
    private final UUID casterUUID;
    private final String casterName;
    private final boolean showOverlay;
    private final int scenarioIndex;
    private final String scenarioName;
    private final float luckMultiplier;
    private final boolean isJackpot;
    private final boolean isBroken;

    public DomainStatePacket(boolean active, UUID casterUUID, String casterName, boolean showOverlay,
                             int scenarioIndex, String scenarioName, float luckMultiplier,
                             boolean isJackpot, boolean isBroken) {
        this.active = active;
        this.casterUUID = casterUUID;
        this.casterName = casterName != null ? casterName : "Unknown";
        this.showOverlay = showOverlay;
        this.scenarioIndex = scenarioIndex;
        this.scenarioName = scenarioName != null ? scenarioName : "";
        this.luckMultiplier = luckMultiplier;
        this.isJackpot = isJackpot;
        this.isBroken = isBroken;
    }

    public static void encode(DomainStatePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.active);
        buf.writeUUID(msg.casterUUID);
        buf.writeUtf(msg.casterName);
        buf.writeBoolean(msg.showOverlay);
        buf.writeInt(msg.scenarioIndex);
        buf.writeUtf(msg.scenarioName);
        buf.writeFloat(msg.luckMultiplier);
        buf.writeBoolean(msg.isJackpot);
        buf.writeBoolean(msg.isBroken);
    }

    public static DomainStatePacket decode(FriendlyByteBuf buf) {
        return new DomainStatePacket(
                buf.readBoolean(),
                buf.readUUID(),
                buf.readUtf(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readUtf(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(DomainStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientDomainTracker.handlePacket(msg);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean isActive() { return active; }
    public UUID getCasterUUID() { return casterUUID; }
    public String getCasterName() { return casterName; }
    public boolean isShowOverlay() { return showOverlay; }
    public int getScenarioIndex() { return scenarioIndex; }
    public String getScenarioName() { return scenarioName; }
    public float getLuckMultiplier() { return luckMultiplier; }
    public boolean isJackpot() { return isJackpot; }
    public boolean isBroken() { return isBroken; }
}
