package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasPaintings {
    public static final DeferredRegister<PaintingVariant> REGISTRY =
            DeferredRegister.create(Registries.PAINTING_VARIANT, ChangedExtras.MODID);

    public static final RegistryObject<PaintingVariant> THE_EYE =
            REGISTRY.register("the_eye", () -> new PaintingVariant(64, 64));

    private ChangedExtrasPaintings() {
    }
}
