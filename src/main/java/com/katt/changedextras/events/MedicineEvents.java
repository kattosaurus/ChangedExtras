package com.katt.changedextras.events;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.init.ChangedExtrasEffects;
import net.ltxprogrammer.changed.process.Pale;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MedicineEvents {

    private static final ResourceKey<DamageType> PALE_DAMAGE_TYPE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("changed", "pale"));

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(ChangedExtrasEffects.VASCULAR_FLOW.get()) || player.hasEffect(ChangedExtrasEffects.OXYGENATED.get())) {
                DamageSource source = event.getSource();
                if (isPaleDamage(source)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.hasEffect(ChangedExtrasEffects.MEDICATED.get())) {
                // Slow down Pale exposure progression by 50% without curing it
                int currentExposure = Pale.getPaleExposure(player);
                if (currentExposure > 1 && player.tickCount % 2 == 1) {
                    Pale.setPaleExposure(player, currentExposure - 1);
                }
            }
        }
    }

    private static boolean isPaleDamage(DamageSource source) {
        if (source == null) return false;
        if (source.is(PALE_DAMAGE_TYPE)) return true;
        String msgId = source.getMsgId();
        return msgId != null && (msgId.equals("changed.pale") || msgId.equals("pale") || msgId.contains("pale"));
    }
}
