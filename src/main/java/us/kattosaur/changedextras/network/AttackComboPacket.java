package us.kattosaur.changedextras.network;

import us.kattosaur.changedextras.client.tracker.ClientAttackComboTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Tells clients which combo strike the server confirmed for an entity.
 */
public class AttackComboPacket {
    private final UUID entityUUID;
    private final int combo;
    private final long serverTick;

    public AttackComboPacket(UUID entityUUID, int combo, long serverTick) {
        this.entityUUID = entityUUID;
        this.combo = combo;
        this.serverTick = serverTick;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public int getCombo() {
        return combo;
    }

    public long getServerTick() {
        return serverTick;
    }

    public static void encode(AttackComboPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityUUID);
        buf.writeByte(msg.combo);
        buf.writeVarLong(msg.serverTick);
    }

    public static AttackComboPacket decode(FriendlyByteBuf buf) {
        return new AttackComboPacket(buf.readUUID(), buf.readByte(), buf.readVarLong());
    }

    public static void broadcast(Entity entity, int combo) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }

        AttackComboPacket packet = new AttackComboPacket(entity.getUUID(), combo, entity.level().getGameTime());
        ChangedExtrasNetwork.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                packet
        );

        if (entity instanceof ServerPlayer serverPlayer) {
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    packet
            );
        }
    }

    public static void handle(AttackComboPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientAttackComboTracker.applyServerCombo(msg.entityUUID, msg.combo, msg.serverTick)));
        ctx.get().setPacketHandled(true);
    }
}
