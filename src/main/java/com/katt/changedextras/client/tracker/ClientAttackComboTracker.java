package com.katt.changedextras.client.tracker;

import com.katt.changedextras.Config;
import com.katt.changedextras.common.combat.AttackComboTracker;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/**
 * Keeps the shared attack combo advancing every client tick. Rendering alone is
 * not enough: the local player is not rendered in first person, and entities can
 * be culled, which used to let the client fall behind the server's combo.
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientAttackComboTracker {
    private ClientAttackComboTracker() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        if (!Config.latexReworkedCombat) {
            AttackComboTracker.clearClient();
            return;
        }

        // Pure variants are real world entities.
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof ChangedEntity changed && changed.getUnderlyingPlayer() == null) {
                if (AttackComboTracker.isBareHanded(changed)) {
                    AttackComboTracker.tickClient(changed);
                } else {
                    AttackComboTracker.resetClient(changed);
                }
            }
        }

        // A transfurred player is rendered through this proxy entity, which is not
        // part of the level's entity list.
        for (Player player : mc.level.players()) {
            ProcessTransfur.ifPlayerTransfurred(player, instance -> {
                ChangedEntity changed = instance.getChangedEntity();
                if (changed != null) {
                    if (AttackComboTracker.isBareHanded(changed)) {
                        AttackComboTracker.tickClient(changed);
                    } else {
                        AttackComboTracker.resetClient(changed);
                    }
                }
            });
        }
    }

    /** Applies the server-confirmed final hit so the client plays Attack2. */
    public static void applyServerCombo(UUID entityUUID, int combo, long serverTick) {
        if (!Config.latexReworkedCombat) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        long now = mc.level != null ? mc.level.getGameTime() : 0L;
        AttackComboTracker.applyServerCombo(entityUUID, combo, serverTick, now);
    }

    public static void clear() {
        AttackComboTracker.clearClient();
    }
}
