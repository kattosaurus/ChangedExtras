package us.kattosaur.changedextras.init;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.effect.ArtistFearEffect;
import us.kattosaur.changedextras.effect.HypoxemiaEffect;
import us.kattosaur.changedextras.effect.MedicatedEffect;
import us.kattosaur.changedextras.effect.OxygenatedEffect;
import us.kattosaur.changedextras.effect.VascularFlowEffect;
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

    public static final RegistryObject<MobEffect> HYPOXIA =
            REGISTRY.register("hypoxia", HypoxemiaEffect::new);

    public static final RegistryObject<MobEffect> ARTIST_FEAR =
            REGISTRY.register("artist_fear", ArtistFearEffect::new);
}
