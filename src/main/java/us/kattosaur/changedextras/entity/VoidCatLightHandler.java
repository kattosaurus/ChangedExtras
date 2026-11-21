package us.kattosaur.changedextras.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.entity.beasts.VoidCatEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Maintains a temporary level-5 light block under void cats and void-cat transfurs. */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VoidCatLightHandler {
    private static final int LIGHT_LEVEL = 5;
    private static final BlockState LIGHT_STATE = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, LIGHT_LEVEL);
    private static final Map<UUID, Source> SOURCES = new HashMap<>();

    private VoidCatLightHandler() {}

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide)
            return;

        boolean isVoidCat = entity instanceof VoidCatEntity;
        if (!isVoidCat && entity instanceof ChangedEntity changedEntity)
            isVoidCat = changedEntity.getSelfVariant() == ModTransfurVariants.VOID_CAT.get();
        else if (!isVoidCat && entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))
            isVoidCat = IAbstractChangedEntity.forPlayer(player).getSelfVariant() == ModTransfurVariants.VOID_CAT.get();

        if (isVoidCat)
            updateSource(entity);
        else
            removeSource(entity.getUUID());
    }

    private static void updateSource(LivingEntity entity) {
        Level level = entity.level();
        Source previous = SOURCES.get(entity.getUUID());
        BlockPos target = findAirSource(level,
                BlockPos.containing(entity.getX(), entity.getY() + 0.6D, entity.getZ()), previous);
        if (target == null) {
            removeSource(entity.getUUID());
            return;
        }

        if (previous != null && previous.level == level && previous.pos.equals(target)
                && level.getBlockState(target).equals(LIGHT_STATE))
            return;

        removeSource(entity.getUUID());
        if (!level.getBlockState(target).isAir())
            return;

        level.setBlock(target, LIGHT_STATE, 3);
        SOURCES.put(entity.getUUID(), new Source(level, target));
    }

    private static BlockPos findAirSource(Level level, BlockPos base, Source previous) {
        BlockPos[] candidates = {
                base, base.above(), base.above(2), base.below(),
                base.north(), base.south(), base.east(), base.west()
        };
        for (BlockPos pos : candidates) {
            boolean isPreviousLight = previous != null && previous.level == level && previous.pos.equals(pos)
                    && level.getBlockState(pos).equals(LIGHT_STATE);
            if (pos.getY() >= level.getMinBuildHeight() && pos.getY() < level.getMaxBuildHeight()
                    && (level.getBlockState(pos).isAir() || isPreviousLight))
                return pos.immutable();
        }
        return null;
    }

    private static void removeSource(UUID id) {
        Source source = SOURCES.remove(id);
        if (source == null || source.level.isClientSide)
            return;

        BlockState current = source.level.getBlockState(source.pos);
        if (current.is(Blocks.LIGHT) && current.getValue(LightBlock.LEVEL) == LIGHT_LEVEL)
            source.level.setBlock(source.pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity)
            removeSource(livingEntity.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        removeSource(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        SOURCES.entrySet().removeIf(entry -> {
            Source source = entry.getValue();
            if (source.level instanceof ServerLevel serverLevel && serverLevel.getServer() == server) {
                BlockState current = source.level.getBlockState(source.pos);
                if (current.is(Blocks.LIGHT) && current.getValue(LightBlock.LEVEL) == LIGHT_LEVEL)
                    source.level.setBlock(source.pos, Blocks.AIR.defaultBlockState(), 3);
                return true;
            }
            return false;
        });
    }

    private record Source(Level level, BlockPos pos) {}
}
