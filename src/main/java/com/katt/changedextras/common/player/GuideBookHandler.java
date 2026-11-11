package com.katt.changedextras.common.player;
import com.katt.changedextras.common.ChangedExtrasGameRules;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GuideBookHandler {

    private static final String RECEIVED_GUIDE_BOOK_TAG = "changedextras.received_guide_book";

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            giveGuideBookIfNeeded(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Keep the flag across respawns so the book is only ever granted once.
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();
        if (oldData.contains(RECEIVED_GUIDE_BOOK_TAG)) {
            newData.putBoolean(RECEIVED_GUIDE_BOOK_TAG, oldData.getBoolean(RECEIVED_GUIDE_BOOK_TAG));
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            giveGuideBookIfNeeded(player);
        }
    }

    private static void giveGuideBookIfNeeded(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(RECEIVED_GUIDE_BOOK_TAG)) {
            return;
        }

        if (!player.level().getGameRules().getBoolean(ChangedExtrasGameRules.GIVE_GUIDE_BOOK)) {
            return;
        }

        ItemStack book = new ItemStack(ChangedExtras.CHANGED_EXTRAS_GUIDE.get());
        if (player.getInventory().contains(book)) {
            // Existing players already have a book from before the flag existed.
            data.putBoolean(RECEIVED_GUIDE_BOOK_TAG, true);
            return;
        }
        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
        data.putBoolean(RECEIVED_GUIDE_BOOK_TAG, true);
    }

    private GuideBookHandler() {
    }
}
