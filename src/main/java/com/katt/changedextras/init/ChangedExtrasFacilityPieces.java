package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.worldgen.structure.facility.ForbiddenLibraryPiece;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.init.ChangedFacilityZones;
import net.ltxprogrammer.changed.world.features.structures.facility.ConfiguredFacilityPiece;
import net.ltxprogrammer.changed.world.features.structures.facility.GatherFacilityPiecesEvent;
import net.ltxprogrammer.changed.world.features.structures.facility.types.RoomType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * Registers this mod's custom facility pieces into the normal Changed facility structure.
 */
public final class ChangedExtrasFacilityPieces {
    public static final ResourceLocation FORBIDDEN_LIBRARY =
            new ResourceLocation(ChangedExtras.MODID, "forbidden_library");

    private ChangedExtrasFacilityPieces() {}

    public static void gatherFacilityPieces(GatherFacilityPiecesEvent event) {
        if (!(event.getPieceType() instanceof RoomType))
            return;

        event.getBuilder().register(new ConfiguredFacilityPiece(
                new ForbiddenLibraryPiece(),
                Weight.of(5),
                0,
                1,
                Set.of(ChangedFacilityZones.ENTRANCE_ZONE.get()),
                RegistryElementPredicate.forAll(ForgeRegistries.BIOMES)).setName(FORBIDDEN_LIBRARY));
    }
}
