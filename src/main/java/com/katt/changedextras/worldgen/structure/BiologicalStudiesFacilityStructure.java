package com.katt.changedextras.worldgen.structure;

import com.katt.changedextras.Config;
import com.katt.changedextras.init.ChangedExtrasStructureTypes;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.block.GluBlock;
import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.*;

public class BiologicalStudiesFacilityStructure extends Structure {
    public static final Codec<BiologicalStudiesFacilityStructure> CODEC = simpleCodec(BiologicalStudiesFacilityStructure::new);

    public static final ResourceLocation ENTRANCE_TEMPLATE =
            ResourceLocation.fromNamespaceAndPath("changedextras", "biological_studies_facility/dark_facility");

    public static final BlockPos SURFACE_LOCAL_POS = new BlockPos(10, 17, 18);
    public static final int SURFACE_LOCAL_Y = 17;

    private static final int[] SURFACE_SAMPLE_X = {0, 10, 20};
    private static final int[] SURFACE_SAMPLE_Z = {9, 18, 27};
    private static final int MAX_SURFACE_HEIGHT_VARIATION = 4;

    /** Fewest rooms a facility aims for. The most is the biologicalFacilityMaxRooms server config value. */
    private static final int MIN_PIECES = Config.BIOLOGICAL_FACILITY_MIN_ROOMS;

    public BiologicalStudiesFacilityStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        ChunkPos chunkPos = context.chunkPos();

        int originX = chunkPos.getMinBlockX();
        int originZ = chunkPos.getMinBlockZ();

        // 1. Check surface center height and water clearance
        BlockPos surfaceOffset = StructureTemplate.transform(SURFACE_LOCAL_POS, Mirror.NONE, rotation, BlockPos.ZERO);
        int surfaceCenterX = originX + surfaceOffset.getX();
        int surfaceCenterZ = originZ + surfaceOffset.getZ();

        int centerSurfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                surfaceCenterX,
                surfaceCenterZ,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        if (centerSurfaceY < context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }

        int centerOceanFloorY = context.chunkGenerator().getFirstOccupiedHeight(
                surfaceCenterX,
                surfaceCenterZ,
                Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(),
                context.randomState()
        );

        if (centerSurfaceY != centerOceanFloorY) {
            return Optional.empty();
        }

        // 2. Check surface footprint for reasonable flatness
        int minSurfaceY = centerSurfaceY;
        int maxSurfaceY = centerSurfaceY;

        for (int lx : SURFACE_SAMPLE_X) {
            for (int lz : SURFACE_SAMPLE_Z) {
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

                if (surfaceY < minSurfaceY) minSurfaceY = surfaceY;
                if (surfaceY > maxSurfaceY) maxSurfaceY = surfaceY;

                if (maxSurfaceY - minSurfaceY > MAX_SURFACE_HEIGHT_VARIATION) {
                    return Optional.empty();
                }
            }
        }

        // Align origin Y so that the surface level sits at centerSurfaceY
        int originY = centerSurfaceY - SURFACE_LOCAL_Y;
        if (originY <= context.heightAccessor().getMinBuildHeight() + 15) {
            return Optional.empty();
        }

        BlockPos origin = new BlockPos(originX, originY, originZ);

        return Optional.of(new GenerationStub(origin, builder ->
                generatePieces(builder, context.structureTemplateManager(), origin, rotation, context.random(), context.heightAccessor().getMinBuildHeight())));
    }

    private void generatePieces(StructurePiecesBuilder builder, StructureTemplateManager templateManager,
                                BlockPos entranceOrigin, Rotation entranceRotation, RandomSource random, int minBuildHeight) {
        List<BoundingBox> placedBoxes = new ArrayList<>();
        StructureTemplate entranceTemplate = templateManager.getOrCreate(ENTRANCE_TEMPLATE);

        // Add Entrance piece
        builder.addPiece(new BiologicalStudiesFacilityPiece(templateManager, ENTRANCE_TEMPLATE, entranceOrigin, entranceRotation));

        // Entrance sub-boxes for accurate collision detection
        placedBoxes.add(calculateBoundingBox(entranceOrigin, new Vec3i(16, 7, 13), entranceRotation));
        BlockPos surfaceOrigin = entranceOrigin.offset(StructureTemplate.transform(new BlockPos(0, 7, 0), Mirror.NONE, entranceRotation, BlockPos.ZERO));
        placedBoxes.add(calculateBoundingBox(surfaceOrigin, new Vec3i(21, 19, 28), entranceRotation));

        // Dynamically extract glu blocks from entrance template
        List<GluData> entranceGluBlocks = extractGluBlocks(entranceTemplate);
        List<OpenGluConnection> openGluConnections = new ArrayList<>();

        for (GluData glu : entranceGluBlocks) {
            if (glu.jointType == GluBlockEntity.JointType.ENTRANCE) {
                Direction worldFront = entranceRotation.rotate(glu.front);
                BlockPos worldPos = entranceOrigin.offset(StructureTemplate.transform(glu.localPos, Mirror.NONE, entranceRotation, BlockPos.ZERO));
                openGluConnections.add(new OpenGluConnection(worldPos, worldFront, glu.jointType, glu.size, glu.doorId, 0));
            }
        }

        List<WeightedTemplate> corridorPool = buildCorridorPool(templateManager);
        List<WeightedTemplate> roomPool = buildRoomPool(templateManager);

        // Read the configured maximum, never letting it drop below the minimum
        int maxPieces = Math.max(MIN_PIECES, Config.biologicalFacilityMaxRooms);
        int targetPieceCount = random.nextIntBetweenInclusive(MIN_PIECES, maxPieces);
        int pieceCount = 1;

        List<OpenGluConnection> terminalDeadEnds = new ArrayList<>();

        // Phase 1: Hallway and junction network expansion.
        // Keep going until enough pieces are placed. Every open end will later be capped with a room (Phase 2),
        // so also stop once the pieces placed plus those pending caps would reach the configured maximum.
        while (!openGluConnections.isEmpty()
                && pieceCount < targetPieceCount
                && pieceCount + openGluConnections.size() + terminalDeadEnds.size() < maxPieces) {
            OpenGluConnection currentGlu = openGluConnections.remove(0);

            // Chance to branch into a room directly if depth > 1.
            // Rooms have no exits, so only do this while other open ends remain; otherwise a room placed on the
            // last open end would finish the whole facility early.
            boolean tryRoomFirst = currentGlu.depth > 1 && !openGluConnections.isEmpty() && random.nextFloat() < 0.25F;
            boolean placed = false;

            if (tryRoomFirst) {
                placed = tryPlacePiece(builder, templateManager, currentGlu, roomPool, placedBoxes, openGluConnections, minBuildHeight, random, false);
                if (placed) {
                    pieceCount++;
                    continue;
                }
            }

            // Try placing corridor / intersection
            placed = tryPlacePiece(builder, templateManager, currentGlu, corridorPool, placedBoxes, openGluConnections, minBuildHeight, random, true);
            if (placed) {
                pieceCount++;
            } else {
                // If a corridor cannot fit, mark this open connection as a terminal dead-end to cap with a room
                terminalDeadEnds.add(currentGlu);
            }
        }

        // Phase 2: Cap ALL remaining hallway/intersection ends with a room
        List<OpenGluConnection> allRemainingEnds = new ArrayList<>();
        allRemainingEnds.addAll(terminalDeadEnds);
        allRemainingEnds.addAll(openGluConnections);
        openGluConnections.clear();

        for (OpenGluConnection deadEnd : allRemainingEnds) {
            boolean placedRoom = tryPlacePiece(builder, templateManager, deadEnd, roomPool, placedBoxes, openGluConnections, minBuildHeight, random, false);
            if (placedRoom) {
                pieceCount++;
            }
        }
    }

    private static boolean tryPlacePiece(StructurePiecesBuilder builder, StructureTemplateManager templateManager,
                                         OpenGluConnection currentGlu, List<WeightedTemplate> pool,
                                         List<BoundingBox> placedBoxes, List<OpenGluConnection> openGluConnections,
                                         int minBuildHeight, RandomSource random, boolean addOpenConnectors) {
        Direction targetFacing = currentGlu.worldFront.getOpposite();
        BlockPos targetConnectPos = currentGlu.worldPos.relative(currentGlu.worldFront);

        List<WeightedTemplate> candidates = new ArrayList<>(pool);
        Collections.shuffle(candidates, new java.util.Random(random.nextLong()));

        for (WeightedTemplate candidate : candidates) {
            List<GluData> candGluList = new ArrayList<>(candidate.gluBlocks);
            Collections.shuffle(candGluList, new java.util.Random(random.nextLong()));

            for (GluData candGlu : candGluList) {
                if (!candGlu.jointType.canConnectTo(currentGlu.jointType) || candGlu.size != currentGlu.size) {
                    continue;
                }

                Rotation neededRotation = getRotationToMatch(candGlu.front, targetFacing);
                BlockPos rotatedLocalPos = StructureTemplate.transform(candGlu.localPos, Mirror.NONE, neededRotation, BlockPos.ZERO);
                BlockPos candidateOrigin = targetConnectPos.subtract(rotatedLocalPos);
                BoundingBox candidateBox = calculateBoundingBox(candidateOrigin, candidate.size, neededRotation);

                if (candidateBox.minY() < minBuildHeight + 5) {
                    continue;
                }

                boolean collides = false;
                for (BoundingBox placedBox : placedBoxes) {
                    if (candidateBox.intersects(placedBox)) {
                        collides = true;
                        break;
                    }
                }

                if (!collides) {
                    builder.addPiece(new BiologicalStudiesFacilityPiece(templateManager, candidate.templateId, candidateOrigin, neededRotation));
                    placedBoxes.add(candidateBox);

                    if (addOpenConnectors) {
                        for (GluData otherGlu : candidate.gluBlocks) {
                            // Open every other end of the piece. Ends are told apart by the wall they face, not by door id:
                            // the facility templates give every glu block the same door id, which used to leave hallways
                            // with no open far end and stopped generation after a single hallway.
                            if (otherGlu.front != candGlu.front && otherGlu.jointType == GluBlockEntity.JointType.ENTRANCE) {
                                Direction otherWorldFront = neededRotation.rotate(otherGlu.front);
                                BlockPos otherWorldPos = candidateOrigin.offset(
                                        StructureTemplate.transform(otherGlu.localPos, Mirror.NONE, neededRotation, BlockPos.ZERO)
                                );
                                openGluConnections.add(new OpenGluConnection(otherWorldPos, otherWorldFront, otherGlu.jointType, otherGlu.size, otherGlu.doorId, currentGlu.depth + 1));
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private static List<GluData> extractGluBlocks(StructureTemplate template) {
        List<GluData> gluList = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> blockInfos = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), ChangedBlocks.GLU_BLOCK.get(), true);

        for (StructureTemplate.StructureBlockInfo info : blockInfos) {
            FrontAndTop orientation = info.state().hasProperty(GluBlock.ORIENTATION) ?
                    info.state().getValue(GluBlock.ORIENTATION) : FrontAndTop.NORTH_UP;
            Direction front = orientation.front();

            CompoundTag nbt = info.nbt();
            GluBlockEntity.JointType joint = GluBlockEntity.JointType.ENTRANCE;
            int size = 3;
            int doorId = 0;

            if (nbt != null) {
                if (nbt.contains(GluBlockEntity.JOINT)) {
                    joint = GluBlockEntity.JointType.byName(nbt.getString(GluBlockEntity.JOINT)).orElse(GluBlockEntity.JointType.ENTRANCE);
                }
                if (nbt.contains(GluBlockEntity.SIZE)) {
                    size = nbt.getInt(GluBlockEntity.SIZE);
                }
                if (nbt.contains("door")) {
                    doorId = nbt.getInt("door");
                }
            }

            gluList.add(new GluData(info.pos(), front, joint, size, doorId));
        }

        return gluList;
    }

    private static List<WeightedTemplate> buildCorridorPool(StructureTemplateManager templateManager) {
        List<WeightedTemplate> pool = new ArrayList<>();

        addWeighted(pool, templateManager, "biological_studies_facility/long_hallways/longhallway_1", 6);
        addWeighted(pool, templateManager, "biological_studies_facility/long_hallways/longhallway_2", 6);
        addWeighted(pool, templateManager, "biological_studies_facility/long_hallways/longhallway_3", 6);
        addWeighted(pool, templateManager, "biological_studies_facility/long_hallways/longhallway_4", 6);
        addWeighted(pool, templateManager, "biological_studies_facility/corridors/corridor_turn", 8);
        addWeighted(pool, templateManager, "biological_studies_facility/corridors/corridor_circle", 4);

        return pool;
    }

    private static List<WeightedTemplate> buildRoomPool(StructureTemplateManager templateManager) {
        List<WeightedTemplate> pool = new ArrayList<>();

        addWeighted(pool, templateManager, "biological_studies_facility/scp009_room", 1);

        return pool;
    }

    private static void addWeighted(List<WeightedTemplate> pool, StructureTemplateManager templateManager, String path, int weight) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("changedextras", path);
        StructureTemplate template = templateManager.getOrCreate(id);
        Vec3i size = template.getSize();
        List<GluData> gluBlocks = extractGluBlocks(template);
        WeightedTemplate wt = new WeightedTemplate(id, size, gluBlocks);
        for (int i = 0; i < weight; i++) {
            pool.add(wt);
        }
    }

    private static Rotation getRotationToMatch(Direction localFacing, Direction targetFacing) {
        for (Rotation rot : Rotation.values()) {
            if (rot.rotate(localFacing) == targetFacing) {
                return rot;
            }
        }
        return Rotation.NONE;
    }

    private static BoundingBox calculateBoundingBox(BlockPos origin, Vec3i size, Rotation rotation) {
        BlockPos c1 = StructureTemplate.transform(BlockPos.ZERO, Mirror.NONE, rotation, BlockPos.ZERO);
        BlockPos c2 = StructureTemplate.transform(new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1), Mirror.NONE, rotation, BlockPos.ZERO);
        return BoundingBox.fromCorners(origin.offset(c1), origin.offset(c2));
    }

    @Override
    public StructureType<?> type() {
        return ChangedExtrasStructureTypes.BIOLOGICAL_STUDIES_FACILITY.get();
    }

    private record GluData(BlockPos localPos, Direction front, GluBlockEntity.JointType jointType, int size, int doorId) {}

    private record WeightedTemplate(ResourceLocation templateId, Vec3i size, List<GluData> gluBlocks) {}

    private record OpenGluConnection(BlockPos worldPos, Direction worldFront, GluBlockEntity.JointType jointType, int size, int doorId, int depth) {}
}