package com.katt.changedextras.worldgen.structure;

import com.katt.changedextras.init.ChangedExtrasStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class BunkerStructurePiece extends StructurePiece {
    private static final String TEMPLATE_TAG = "Template";
    private static final String ROTATION_TAG = "Rotation";
    private static final String ORIGIN_X_TAG = "OriginX";
    private static final String ORIGIN_Y_TAG = "OriginY";
    private static final String ORIGIN_Z_TAG = "OriginZ";

    private final ResourceLocation templateId;
    private final Rotation rotation;
    private final BlockPos origin;

    public BunkerStructurePiece(StructureTemplateManager templateManager, ResourceLocation templateId, BlockPos origin, Rotation rotation) {
        super(ChangedExtrasStructurePieceTypes.BUNKER.get(), 0, makeBoundingBox(templateManager, templateId, origin, rotation));
        this.templateId = templateId;
        this.rotation = rotation;
        this.origin = origin;
    }

    public BunkerStructurePiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(ChangedExtrasStructurePieceTypes.BUNKER.get(), tag);
        this.templateId = ResourceLocation.parse(tag.getString(TEMPLATE_TAG));
        this.rotation = Rotation.valueOf(tag.getString(ROTATION_TAG));
        this.origin = new BlockPos(tag.getInt(ORIGIN_X_TAG), tag.getInt(ORIGIN_Y_TAG), tag.getInt(ORIGIN_Z_TAG));
        this.boundingBox = makeBoundingBox(templateManager, this.templateId, this.origin, this.rotation);
    }

    private static BoundingBox makeBoundingBox(StructureTemplateManager templateManager, ResourceLocation templateId, BlockPos origin, Rotation rotation) {
        StructureTemplate template = templateManager.getOrCreate(templateId);
        return template.getBoundingBox(origin, rotation, BlockPos.ZERO, Mirror.NONE);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString(TEMPLATE_TAG, templateId.toString());
        tag.putString(ROTATION_TAG, rotation.name());
        tag.putInt(ORIGIN_X_TAG, origin.getX());
        tag.putInt(ORIGIN_Y_TAG, origin.getY());
        tag.putInt(ORIGIN_Z_TAG, origin.getZ());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        StructureTemplate template = level.getLevel().getStructureManager().getOrCreate(templateId);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setBoundingBox(box)
                .setRandom(random)
                .setIgnoreEntities(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        template.placeInWorld((ServerLevelAccessor) level, origin, origin, settings, random, 2);
    }
}
