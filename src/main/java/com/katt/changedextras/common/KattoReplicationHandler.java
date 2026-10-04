package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.process.TransfurEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
@SuppressWarnings("deprecation")
public final class KattoReplicationHandler {
    private KattoReplicationHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
        if (event.variant != ModTransfurVariants.KATT.get() || !isKattoPlayerReplication(event.context)) {
            return;
        }

        event.variant = ModTransfurVariants.WHITE_CAT.get();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLatexAssimilationDecision(TransfurEvents.LatexAssimilationDecisionEvent event) {
        if (shouldRedirectKattoReplication(event.getTransfurVariant(), event.getTransfurCause(), event.getSourceEntity())) {
            event.setTransfurVariant(ModTransfurVariants.WHITE_CAT.get());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onImmediateTransfurDecision(TransfurEvents.ImmediateTransfurDecisionEvent event) {
        if (shouldRedirectKattoReplication(event.getTransfurVariant(), event.getTransfurCause(), event.getSourceEntity())) {
            event.setTransfurVariant(ModTransfurVariants.WHITE_CAT.get());
        }
    }

    private static boolean isKattoPlayerReplication(TransfurContext context) {
        if (context == null || !context.isFromPlayer() || !isReplicationCause(context.cause()) || context.source() == null) {
            return false;
        }

        return context.source().left()
                .map(source -> source.isPlayer() && source.getTransfurVariant() == ModTransfurVariants.KATT.get())
                .orElse(false);
    }

    private static boolean shouldRedirectKattoReplication(Object variant, TransfurCause cause, LivingEntity sourceEntity) {
        if (variant != ModTransfurVariants.KATT.get() || !isReplicationCause(cause)) {
            return false;
        }

        return sourceEntity instanceof Player player
                && ProcessTransfur.getPlayerTransfurVariant(player) != null
                && ProcessTransfur.getPlayerTransfurVariant(player).getParent() == ModTransfurVariants.KATT.get();
    }

    private static boolean isReplicationCause(TransfurCause cause) {
        return cause == TransfurCause.ATTACK_REPLICATE_LEFT
                || cause == TransfurCause.ATTACK_REPLICATE_RIGHT
                || cause == TransfurCause.GRAB_REPLICATE;
    }
}
