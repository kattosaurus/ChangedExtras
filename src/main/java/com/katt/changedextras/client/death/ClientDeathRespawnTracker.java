package com.katt.changedextras.client.death;

import com.katt.changedextras.Config;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public final class ClientDeathRespawnTracker {
    private static boolean serverConfigEnabled = true;
    private static boolean keepFormRuleEnabled = false;
    private static boolean receivedServerSync = false;
    @Nullable
    private static ResourceLocation lastVariantId = null;

    private ClientDeathRespawnTracker() {
    }

    public static void update(boolean configEnabled, boolean keepForm, @Nullable ResourceLocation variantId) {
        serverConfigEnabled = configEnabled;
        keepFormRuleEnabled = keepForm;
        receivedServerSync = true;
        if (variantId != null) {
            lastVariantId = variantId;
        }
    }

    public static boolean isConfigEnabled() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && !receivedServerSync) {
            return Config.respawnAsTransfurInsteadOfKeepForm;
        }
        return serverConfigEnabled;
    }

    public static boolean isKeepFormEnabled() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.level != null && !receivedServerSync) {
            return mc.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM);
        }
        return keepFormRuleEnabled;
    }

    public static boolean shouldShowButton(@Nullable Player player) {
        if (!isConfigEnabled()) {
            return false;
        }
        if (!isKeepFormEnabled()) {
            return false;
        }
        return getActiveTransfurVariant(player) != null;
    }

    @Nullable
    public static TransfurVariant<?> getActiveTransfurVariant(@Nullable Player player) {
        if (player != null) {
            var instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null) {
                return instance.getParent();
            }
        }

        if (lastVariantId != null && ChangedRegistry.TRANSFUR_VARIANT.get() != null) {
            return ChangedRegistry.TRANSFUR_VARIANT.get().getValue(lastVariantId);
        }

        return null;
    }

    public static void reset() {
        lastVariantId = null;
    }
}
