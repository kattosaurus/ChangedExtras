package com.katt.changedextras.worldgen.structure;

import com.katt.changedextras.init.ChangedExtrasStructureTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class BunkerStructure extends Structure {
    public static final Codec<BunkerStructure> CODEC = simpleCodec(BunkerStructure::new);
    public static final ResourceLocation TEMPLATE_ID = ResourceLocation.fromNamespaceAndPath("changedextras", "bunker");

    public BunkerStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos origin = getLowestYIn5by5BoxOffset7Blocks(context, rotation);
        if (origin.getY() <= context.heightAccessor().getMinBuildHeight()) {
            return Optional.empty();
        }

        return Optional.of(new GenerationStub(origin, builder ->
                builder.addPiece(new BunkerStructurePiece(context.structureTemplateManager(), TEMPLATE_ID, origin, rotation))));
    }

    @Override
    public StructureType<?> type() {
        return ChangedExtrasStructureTypes.BUNKER.get();
    }
}
