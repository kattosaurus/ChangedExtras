package us.kattosaur.changedextras.init;

import us.kattosaur.changedextras.ChangedExtras;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedExtrasParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ChangedExtras.MODID);

    public static final RegistryObject<SimpleParticleType> JACKPOT_AURA =
            REGISTRY.register("jackpot_aura", () -> new SimpleParticleType(true));
}
