package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.effect.HypoxemiaEffect;
import com.katt.changedextras.effect.MedicatedEffect;
import com.katt.changedextras.effect.OxygenatedEffect;
import com.katt.changedextras.effect.VascularFlowEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedExtrasEffects {
    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ChangedExtras.MODID);

    public static final RegistryObject<MobEffect> MEDICATED =
            REGISTRY.register("medicated", MedicatedEffect::new);

    public static final RegistryObject<MobEffect> VASCULAR_FLOW =
            REGISTRY.register("vascular_flow", VascularFlowEffect::new);

    public static final RegistryObject<MobEffect> OXYGENATED =
            REGISTRY.register("oxygenated", OxygenatedEffect::new);

    public static final RegistryObject<MobEffect> HYPOXEMIA =
            REGISTRY.register("hypoxemia", HypoxemiaEffect::new);
}
