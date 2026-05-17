package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.structure.BunkerStructurePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, ChangedExtras.MODID);

    public static final RegistryObject<StructurePieceType> BUNKER =
            REGISTRY.register("bunker", () -> (StructurePieceType.StructureTemplateType) BunkerStructurePiece::new);

    private ChangedExtrasStructurePieceTypes() {
    }
}
