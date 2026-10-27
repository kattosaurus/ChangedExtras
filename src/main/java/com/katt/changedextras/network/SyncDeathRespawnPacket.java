package com.katt.changedextras.network;

import com.katt.changedextras.client.death.ClientDeathRespawnTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SyncDeathRespawnPacket {
    private final boolean configEnabled;
    private final boolean keepFormEnabled;
    @Nullable
    private final ResourceLocation variantId;

    public SyncDeathRespawnPacket(boolean configEnabled, boolean keepFormEnabled, @Nullable ResourceLocation variantId) {
        this.configEnabled = configEnabled;
        this.keepFormEnabled = keepFormEnabled;
        this.variantId = variantId;
    }

    public boolean isConfigEnabled() {
        return configEnabled;
    }

    public boolean isKeepFormEnabled() {
        return keepFormEnabled;
    }

    @Nullable
    public ResourceLocation getVariantId() {
        return variantId;
    }

    public static void encode(SyncDeathRespawnPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.configEnabled);
        buf.writeBoolean(msg.keepFormEnabled);
        buf.writeBoolean(msg.variantId != null);
        if (msg.variantId != null) {
            buf.writeResourceLocation(msg.variantId);
        }
    }

    public static SyncDeathRespawnPacket decode(FriendlyByteBuf buf) {
        boolean configEnabled = buf.readBoolean();
        boolean keepFormEnabled = buf.readBoolean();
        ResourceLocation variantId = buf.readBoolean() ? buf.readResourceLocation() : null;
        return new SyncDeathRespawnPacket(configEnabled, keepFormEnabled, variantId);
    }

    public static void handle(SyncDeathRespawnPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientDeathRespawnTracker.update(msg.isConfigEnabled(), msg.isKeepFormEnabled(), msg.getVariantId());
        }));
        ctx.setPacketHandled(true);
    }
}
