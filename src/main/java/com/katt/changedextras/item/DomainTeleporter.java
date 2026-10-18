package com.katt.changedextras.item;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DomainTeleporter implements ITeleporter {
    private static final Set<ResourceKey<Level>> PREPARED_ARENAS = ConcurrentHashMap.newKeySet();

    private final Vec3 targetPosition;
    private final boolean buildArena;

    public DomainTeleporter(Vec3 targetPosition, boolean buildArena) {
        this.targetPosition = targetPosition;
        this.buildArena = buildArena;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destinationLevel,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity teleportedEntity = repositionEntity.apply(false);
        teleportedEntity.teleportTo(targetPosition.x, targetPosition.y, targetPosition.z);

        if (buildArena) {
            buildArena(destinationLevel);
        }

        return teleportedEntity;
    }

    /**
     * Builds the white barrier arena that contains everyone trapped inside the domain.
     * Only built once per dimension.
     */
    private static void buildArena(ServerLevel level) {
        if (!PREPARED_ARENAS.add(level.dimension())) {
            return;
        }

        BlockState barrier = ChangedExtras.DOMAIN_BARRIER.get().defaultBlockState();
        int radius = 32;
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq > radiusSq) {
                    continue;
                }

                level.setBlockAndUpdate(new BlockPos(x, 0, z), barrier);
                level.setBlockAndUpdate(new BlockPos(x, 32, z), barrier);

                boolean isEdge = (x + 1) * (x + 1) + z * z > radiusSq
                        || (x - 1) * (x - 1) + z * z > radiusSq
                        || x * x + (z + 1) * (z + 1) > radiusSq
                        || x * x + (z - 1) * (z - 1) > radiusSq;

                if (isEdge) {
                    for (int y = 1; y < 32; y++) {
                        level.setBlockAndUpdate(new BlockPos(x, y, z), barrier);
                    }
                }
            }
        }
    }
}
