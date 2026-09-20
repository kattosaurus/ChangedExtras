package com.katt.changedextras.worldgen.structure;

import com.katt.changedextras.init.ChangedExtrasStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class BunkerStructurePiece extends TemplateStructurePiece {
    public BunkerStructurePiece(StructureTemplateManager templateManager, ResourceLocation templateLocation, BlockPos templatePosition, Rotation rotation) {
        super(ChangedExtrasStructurePieceTypes.BUNKER.get(), 0, templateManager, templateLocation, templateLocation.toString(), makeSettings(rotation), templatePosition);
    }

    public BunkerStructurePiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(ChangedExtrasStructurePieceTypes.BUNKER.get(), tag, templateManager, (location) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rot")));
        });
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings())
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setRotationPivot(BlockPos.ZERO)
                .setKnownShape(true)
                .setKeepLiquids(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
    }
}
