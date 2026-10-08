package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;


// Credit to KJ for helping
public class InitCrTabs {
    public static final DeferredRegister<CreativeModeTab> CT_TAB_REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChangedExtras.MODID);
    public static final RegistryObject<CreativeModeTab> CHANGEDEXTRAS = register(
            "changedextras",
            (builder) -> builder.icon(() -> new ItemStack(ChangedExtras.CHANGED_EXTRAS_GUIDE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ChangedExtras.CHANGED_EXTRAS_GUIDE.get());
                        output.accept(ChangedExtras.FLASK_OF_TEARS.get());
                        output.accept(ChangedExtras.THE_PALETTE.get());
                        output.accept(ChangedExtras.ICECREAM_ITEM.get());
                        output.accept(ChangedExtras.ICECREAM_BLOCK_ITEM.get());
                        output.accept(ChangedExtras.SCP009_CRYSTAL_ITEM.get());
                        output.accept(ChangedExtras.SCP009_CRYSTAL_SMALL_ITEM.get());
                        output.accept(ChangedExtras.JAMMER_HEADPHONES.get());
                        output.accept(ChangedExtras.CATTE_BUCKET.get());
                        output.accept(ChangedExtras.ARTIST_SPAWN_EGG.get());
                        output.accept(ChangedExtras.STERILE_SWAB.get());
                        output.accept(ChangedExtras.USED_SWAB.get());
                        output.accept(ChangedExtras.VIAL.get());
                        output.accept(ChangedExtras.USED_VIAL.get());
                        output.accept(ChangedExtras.PROCESSED_VIAL.get());
                        output.accept(ChangedExtras.PALE_TEST.get());
                        output.accept(ChangedExtras.PILL_BOTTLE.get());
                        output.accept(ChangedExtras.HYDROXYUREA.get());
                        output.accept(ChangedExtras.CRIZANLIZUMAB.get());
                        output.accept(ChangedExtras.VOXELOTOR.get());
                        output.accept(ChangedExtras.ARTIST_BRUSH.get());
                        ChangedExtras.SPAWN_EGGS.values().stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept);
                        ModTransfurVariants.REGISTRY.getEntries().forEach((variant) -> {
                            ItemStack stack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
                            Syringe.setOwner(stack, UniversalDist.getLocalPlayer());
                            Syringe.setPureVariant(stack, variant.getId());
                            output.accept(stack);
                        });
                    })
                    .build()
    );

    private static RegistryObject<CreativeModeTab> register(String id, Function<CreativeModeTab.Builder, CreativeModeTab> finalizer) {
        return CT_TAB_REGISTRY.register(id, () -> finalizer.apply(CreativeModeTab.builder().title(Component.translatable("itemGroup.changedextras." + id))));
    }

}
