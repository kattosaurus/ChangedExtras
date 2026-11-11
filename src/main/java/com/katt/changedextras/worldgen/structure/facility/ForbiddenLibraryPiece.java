package com.katt.changedextras.worldgen.structure.facility;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.init.ChangedExtrasStructurePieceTypes;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityPieceInstance;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityRoomPiece;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

/**
 * Facility room piece for the forbidden library. Beyond placing the template, the generated
 * structure instance fills every chiseled bookshelf in the room, see
 * {@link ForbiddenLibraryBooks}.
 */
public class ForbiddenLibraryPiece extends FacilityRoomPiece {
    public static final ResourceLocation TEMPLATE =
            new ResourceLocation(ChangedExtras.MODID, "facility/room/forbidden_library/forbidden_library");

    public ForbiddenLibraryPiece() {
        super(TEMPLATE, Optional.empty());
    }

    @Override
    public FacilityPieceInstance createStructurePiece(StructureTemplateManager manager, int genDepth) {
        return new Instance(manager, genDepth, templateName, lootTable, null);
    }

    public static class Instance extends FacilitySinglePiece.StructureInstance {
        public Instance(StructureTemplateManager manager, int genDepth, ResourceLocation templateName,
                        Optional<ResourceLocation> lootTable, BoundingBox box) {
            super(manager, genDepth, templateName, lootTable, box);
        }

        public Instance(StructureTemplateManager manager, CompoundTag tag) {
            super(manager, tag);
        }

        /**
         * Serializes under this mod's own structure piece type, so that chunks of a library that
         * generate after a game restart still run the bookshelf filling logic.
         */
        @Override
        public StructurePieceType getType() {
            return ChangedExtrasStructurePieceTypes.FORBIDDEN_LIBRARY.get();
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
            super.postProcess(level, structureManager, generator, random, box, chunkPos, pivot);
            ForbiddenLibraryBooks.fillBookshelves(level, getBoundingBox(), box, random);
        }
    }
}
