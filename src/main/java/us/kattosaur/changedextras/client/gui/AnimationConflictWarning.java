package us.kattosaur.changedextras.client.gui;

import us.kattosaur.changedextras.ChangedExtras;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * Opens {@link AnimationConflictWarningScreen} on the first title screen of the
 * session when the standalone animations addon is also loaded. The warning is
 * informational only; the player can always continue.
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class AnimationConflictWarning {
    private static final String ANIMATION_MOD_ID = "refreshedanimations";

    private static boolean shown = false;

    private AnimationConflictWarning() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (shown || !(event.getNewScreen() instanceof TitleScreen titleScreen)) {
            return;
        }

        if (!ModList.get().isLoaded(ANIMATION_MOD_ID)) {
            return;
        }

        shown = true;
        event.setNewScreen(new AnimationConflictWarningScreen(titleScreen));
    }
}
