package us.kattosaur.changedextras.init;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.worldgen.structure.BiologicalStudiesFacilityStructure;
import us.kattosaur.changedextras.worldgen.structure.BunkerStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasStructureTypes {
    public static final DeferredRegister<StructureType<?>> REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, ChangedExtras.MODID);

    public static final RegistryObject<StructureType<BunkerStructure>> BUNKER =
            REGISTRY.register("bunker", () -> () -> BunkerStructure.CODEC);

    public static final RegistryObject<StructureType<BiologicalStudiesFacilityStructure>> BIOLOGICAL_STUDIES_FACILITY =
            REGISTRY.register("biological_studies_facility", () -> () -> BiologicalStudiesFacilityStructure.CODEC);

    private ChangedExtrasStructureTypes() {
    }
}