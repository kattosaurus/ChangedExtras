package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.structure.BunkerStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasStructureTypes {
    public static final DeferredRegister<StructureType<?>> REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, ChangedExtras.MODID);

    public static final RegistryObject<StructureType<BunkerStructure>> BUNKER =
            REGISTRY.register("bunker", () -> () -> BunkerStructure.CODEC);

    private ChangedExtrasStructureTypes() {
    }
}
