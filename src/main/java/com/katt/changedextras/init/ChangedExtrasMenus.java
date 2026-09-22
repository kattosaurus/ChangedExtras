package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.inventory.PillBottleMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedExtrasMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ChangedExtras.MODID);

    public static final RegistryObject<MenuType<PillBottleMenu>> PILL_BOTTLE_MENU =
            REGISTRY.register("pill_bottle", () -> IForgeMenuType.create(PillBottleMenu::createClientMenu));
}
