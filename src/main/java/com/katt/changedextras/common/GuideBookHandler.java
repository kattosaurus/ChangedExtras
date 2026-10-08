package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GuideBookHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            giveGuideBookIfNeeded(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            giveGuideBookIfNeeded(player);
        }
    }

    private static void giveGuideBookIfNeeded(ServerPlayer player) {
        if (!player.level().getGameRules().getBoolean(ChangedExtrasGameRules.GIVE_GUIDE_BOOK)) {
            return;
        }

        ItemStack book = new ItemStack(ChangedExtras.CHANGED_EXTRAS_GUIDE.get());
        if (player.getInventory().contains(book)) {
            return;
        }
        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
    }

    private GuideBookHandler() {
    }
}
