package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("deprecation")
public final class LatexDaySpawnManager {
    private static final int SPAWN_INTERVAL = 20;
    private static final int MAX_NEARBY_LATEX = 14;
    private static final int MAX_PLAYER_ATTEMPTS = 7;
    private static final int SPAWN_POSITION_ATTEMPTS = 24;
    private static final int MAX_SUCCESSFUL_SPAWNS_PER_CYCLE = 2;
    private static final double MIN_PLAYER_SPAWN_DISTANCE = 18.0D;
    private static int cachedSelectionRevision = Integer.MIN_VALUE;
    private static List<TransfurVariant<?>> cachedEnabledVariants = List.of();

    private LatexDaySpawnManager() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide() || !(event.level instanceof ServerLevel level)) {
            return;
        }

        if (!level.getGameRules().getBoolean(ChangedExtrasGameRules.LATEX_SPAWN_IN_DAY)
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || !level.isDay()
                || level.getDifficulty() == Difficulty.PEACEFUL
                || level.getGameTime() % SPAWN_INTERVAL != 0L) {
            return;
        }

        List<TransfurVariant<?>> enabledVariants = getEnabledVariants(level);
        if (enabledVariants.isEmpty()) {
            return;
        }

        List<ServerPlayer> players = level.players().stream().toList();
        if (players.isEmpty()) {
            return;
        }

        RandomSource random = level.getRandom();
        int attempts = Math.max(1, Math.min(players.size(), MAX_PLAYER_ATTEMPTS));
        int successfulSpawns = 0;
        for (int i = 0; i < attempts; i++) {
            ServerPlayer player = players.get(random.nextInt(players.size()));
            if (hasTooManyNearbyLatex(level, player.blockPosition())) {
                continue;
            }
            if (trySpawnNearPlayer(level, player, enabledVariants, random)) {
                successfulSpawns++;
                if (successfulSpawns >= MAX_SUCCESSFUL_SPAWNS_PER_CYCLE) {
                    break;
                }
            }
        }
    }

    private static boolean hasTooManyNearbyLatex(ServerLevel level, BlockPos center) {
        return level.getEntitiesOfClass(ChangedEntity.class, new AABB(center).inflate(48.0D)).size() >= MAX_NEARBY_LATEX;
    }

    private static List<TransfurVariant<?>> getEnabledVariants(ServerLevel level) {
        LatexSpawnSelectionData selectionData = LatexSpawnSelectionData.get(level.getServer());
        if (selectionData.getRevision() != cachedSelectionRevision) {
            cachedSelectionRevision = selectionData.getRevision();
            cachedEnabledVariants = LatexSpawnRegistry.getAllVariants().stream()
                    .filter(variant -> LatexSpawnRegistry.getEntityTypeId(variant) != null
                            && selectionData.isEnabled(LatexSpawnRegistry.getEntityTypeId(variant)))
                    .toList();
        }
        return cachedEnabledVariants;
    }

    private static boolean trySpawnNearPlayer(ServerLevel level, ServerPlayer player, List<TransfurVariant<?>> enabledVariants, RandomSource random) {
        for (int attempt = 0; attempt < SPAWN_POSITION_ATTEMPTS; attempt++) {
            TransfurVariant<?> variant = enabledVariants.get(random.nextInt(enabledVariants.size()));
            BlockPos spawnPos = getSpawnPos(level, player.blockPosition(), random);
            if (spawnPos == null || spawnPos.closerToCenterThan(player.position(), MIN_PLAYER_SPAWN_DISTANCE)) {
                continue;
            }

            Entity entity = variant.getEntityType().create(level);
            if (!(entity instanceof Mob mob)) {
                continue;
            }

            mob.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            if (!passesDaySpawnRules(variant, level, spawnPos, random)
                    || !level.noCollision(mob)
                    || !level.getWorldBorder().isWithinBounds(spawnPos)) {
                continue;
            }

            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, null, null);
            level.addFreshEntity(mob);
            return true;
        }

        return false;
    }

    private static BlockPos getSpawnPos(ServerLevel level, BlockPos playerPos, RandomSource random) {
        int dx = random.nextInt(49) - 24;
        int dz = random.nextInt(49) - 24;
        if (Math.abs(dx) < 12 && Math.abs(dz) < 12) {
            dx += dx < 0 ? -12 : 12;
            dz += dz < 0 ? -12 : 12;
        }

        BlockPos columnPos = playerPos.offset(dx, 0, dz);
        if (!level.hasChunkAt(columnPos)) {
            return null;
        }

        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, columnPos);
        return surfacePos;
    }

    @SuppressWarnings("unchecked")
    private static boolean passesDaySpawnRules(TransfurVariant<?> variant, ServerLevel level, BlockPos spawnPos, RandomSource random) {
        return LatexSpawnRules.checkDaySpawnRules(
                (net.minecraft.world.entity.EntityType<? extends ChangedEntity>) variant.getEntityType(),
                level,
                MobSpawnType.NATURAL,
                spawnPos,
                random
        );
    }
}
