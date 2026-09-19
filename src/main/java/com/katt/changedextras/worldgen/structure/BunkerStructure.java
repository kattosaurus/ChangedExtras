package com.katt.changedextras.worldgen.structure;

import com.katt.changedextras.init.ChangedExtrasStructureTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class BunkerStructure extends Structure {
    public static final Codec<BunkerStructure> CODEC = simpleCodec(BunkerStructure::new);
    public static final ResourceLocation TEMPLATE_ID = ResourceLocation.fromNamespaceAndPath("changedextras", "bunker");

    // Center of the entrance hatch surrounded by the ring of grass blocks (local coordinates in bunker.nbt)
    public static final BlockPos ENTRANCE_LOCAL_POS = new BlockPos(58, 18, 25);
    public static final int GRASS_RING_LOCAL_Y = 18;
    public static final int BUNKER_ROOF_LOCAL_Y = 14;

    // Grid points covering the entrance rim (X: 51..66, Z: 17..32)
    private static final int[] ENTRANCE_SAMPLE_X = {51, 55, 58, 62, 66};
    private static final int[] ENTRANCE_SAMPLE_Z = {17, 21, 25, 29, 32};

    // Grid points covering the underground bunker complex (X: 1..86, Z: 17..95)
    private static final int[] COMPLEX_SAMPLE_X = {1, 22, 43, 65, 86};
    private static final int[] COMPLEX_SAMPLE_Z = {17, 36, 55, 75, 95};

    // Maximum allowed elevation difference across the entrance area for flat terrain placement
    private static final int MAX_ENTRANCE_HEIGHT_VARIATION = 1;

    public BunkerStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        ChunkPos chunkPos = context.chunkPos();

        int originX = chunkPos.getMinBlockX();
        int originZ = chunkPos.getMinBlockZ();

        // 1. Calculate entrance world coordinates and check flatness around the entrance
        int minEntranceY = Integer.MAX_VALUE;
        int maxEntranceY = Integer.MIN_VALUE;

        for (int lx : ENTRANCE_SAMPLE_X) {
            for (int lz : ENTRANCE_SAMPLE_Z) {
                BlockPos sampleOffset = StructureTemplate.transform(new BlockPos(lx, 0, lz), Mirror.NONE, rotation, BlockPos.ZERO);
                int sampleX = originX + sampleOffset.getX();
                int sampleZ = originZ + sampleOffset.getZ();

                int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                        sampleX,
                        sampleZ,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),
                        context.randomState()
                );

                int oceanFloorY = context.chunkGenerator().getFirstOccupiedHeight(
                        sampleX,
                        sampleZ,
                        Heightmap.Types.OCEAN_FLOOR_WG,
                        context.heightAccessor(),
                        context.randomState()
                );

                // Reject if submerged in water
                if (surfaceY != oceanFloorY || surfaceY < context.chunkGenerator().getSeaLevel()) {
                    return Optional.empty();
                }

                if (surfaceY < minEntranceY) minEntranceY = surfaceY;
                if (surfaceY > maxEntranceY) maxEntranceY = surfaceY;
            }
        }

        // Reject if entrance terrain is too steep/sloped
        if (maxEntranceY - minEntranceY > MAX_ENTRANCE_HEIGHT_VARIATION) {
            return Optional.empty();
        }

        // Align origin Y so that the entrance grass ring at local Y=18 sits at the sampled surface Y
        int surfaceY = minEntranceY;
        int originY = surfaceY - GRASS_RING_LOCAL_Y;
        if (originY <= context.heightAccessor().getMinBuildHeight()) {
            return Optional.empty();
        }

        // 2. Check underground coverage across the bunker footprint (ensure terrain doesn't dip below the bunker ceiling)
        int minAllowedGroundY = originY + BUNKER_ROOF_LOCAL_Y + 1; // At least 1 solid block above bunker roof
        for (int lx : COMPLEX_SAMPLE_X) {
            for (int lz : COMPLEX_SAMPLE_Z) {
                BlockPos sampleOffset = StructureTemplate.transform(new BlockPos(lx, 0, lz), Mirror.NONE, rotation, BlockPos.ZERO);
                int sampleX = originX + sampleOffset.getX();
                int sampleZ = originZ + sampleOffset.getZ();

                int groundY = context.chunkGenerator().getFirstOccupiedHeight(
                        sampleX,
                        sampleZ,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),
                        context.randomState()
                );

                if (groundY < minAllowedGroundY) {
                    return Optional.empty(); // Valley, cliff, or ravine exposes bunker underground rooms
                }
            }
        }

        BlockPos origin = new BlockPos(originX, originY, originZ);

        return Optional.of(new GenerationStub(origin, builder ->
                builder.addPiece(new BunkerStructurePiece(context.structureTemplateManager(), TEMPLATE_ID, origin, rotation))));
    }

    @Override
    public StructureType<?> type() {
        return ChangedExtrasStructureTypes.BUNKER.get();
    }
}
