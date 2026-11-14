package us.kattosaur.changedextras.init;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.worldgen.structure.BiologicalStudiesFacilityPiece;
import us.kattosaur.changedextras.worldgen.structure.BunkerStructurePiece;
import us.kattosaur.changedextras.worldgen.structure.facility.ForbiddenLibraryPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, ChangedExtras.MODID);

    public static final RegistryObject<StructurePieceType> BUNKER =
            REGISTRY.register("bunker", () -> (StructurePieceType.StructureTemplateType) BunkerStructurePiece::new);

    public static final RegistryObject<StructurePieceType> BIOLOGICAL_STUDIES_FACILITY =
            REGISTRY.register("biological_studies_facility", () -> (StructurePieceType.StructureTemplateType) BiologicalStudiesFacilityPiece::new);

    public static final RegistryObject<StructurePieceType> FORBIDDEN_LIBRARY =
            REGISTRY.register("forbidden_library", () -> (StructurePieceType.StructureTemplateType) (manager, tag) -> new ForbiddenLibraryPiece.Instance(manager, tag));

    private ChangedExtrasStructurePieceTypes() {
    }
}