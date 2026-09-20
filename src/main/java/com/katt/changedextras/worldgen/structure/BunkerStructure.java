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

    // Corner sample points covering the entrance rim (X: 51..66, Z: 17..32)
    private static final int[] ENTRANCE_SAMPLE_X = {51, 66};
    private static final int[] ENTRANCE_SAMPLE_Z = {17, 32};

    // Strategic sample points covering the underground bunker complex (X: 1..86, Z: 17..95)
    private static final int[] COMPLEX_SAMPLE_X = {1, 43, 86};
    private static final int[] COMPLEX_SAMPLE_Z = {17, 56, 95};

    // Maximum allowed elevation difference across the entrance area for placement
    private static final int MAX_ENTRANCE_HEIGHT_VARIATION = 3;

    public BunkerStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        ChunkPos chunkPos = context.chunkPos();

        int originX = chunkPos.getMinBlockX();
        int originZ = chunkPos.getMinBlockZ();

        // 1. Fast check entrance center height and water clearance
        BlockPos entranceOffset = StructureTemplate.transform(ENTRANCE_LOCAL_POS, Mirror.NONE, rotation, BlockPos.ZERO);
        int entranceCenterX = originX + entranceOffset.getX();
        int entranceCenterZ = originZ + entranceOffset.getZ();

        int centerSurfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                entranceCenterX,
                entranceCenterZ,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        if (centerSurfaceY < context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }

        int centerOceanFloorY = context.chunkGenerator().getFirstOccupiedHeight(
                entranceCenterX,
                entranceCenterZ,
                Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(),
                context.randomState()
        );

        if (centerSurfaceY != centerOceanFloorY) {
            return Optional.empty();
        }

        // 2. Check entrance corners for reasonable flatness
        int minEntranceY = centerSurfaceY;
        int maxEntranceY = centerSurfaceY;

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

                if (surfaceY < minEntranceY) minEntranceY = surfaceY;
                if (surfaceY > maxEntranceY) maxEntranceY = surfaceY;

                if (maxEntranceY - minEntranceY > MAX_ENTRANCE_HEIGHT_VARIATION) {
                    return Optional.empty();
                }
            }
        }

        // Align origin Y so that the entrance grass ring at local Y=18 sits at the center surface Y
        int originY = centerSurfaceY - GRASS_RING_LOCAL_Y;
        if (originY <= context.heightAccessor().getMinBuildHeight()) {
            return Optional.empty();
        }

        // 3. Check underground coverage across key points (ensure terrain covers bunker roof)
        int minAllowedGroundY = originY + BUNKER_ROOF_LOCAL_Y;
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
                    return Optional.empty();
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
