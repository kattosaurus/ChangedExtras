package us.kattosaur.changedextras.common.artist;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.entity.beasts.ArtistEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

/** Lets a player provoke the fleeing beast with the "you killed her" line. */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class ArtistFleeChatHandler {
    /** How close a player must be to the hiding beast to be heard. */
    private static final double HEARING_RANGE = 48.0D;

    private ArtistFleeChatHandler() {
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String message = normalize(event.getRawText());
        if (!message.contains("youkilledher")
                || (!message.contains("yourfault") && !message.contains("bringherback"))) {
            return;
        }

        ServerPlayer speaker = event.getPlayer();
        ArtistEntity beast = null;
        double bestDistance = HEARING_RANGE * HEARING_RANGE;
        for (ArtistEntity artist : speaker.serverLevel().getEntitiesOfClass(ArtistEntity.class,
                speaker.getBoundingBox().inflate(HEARING_RANGE))) {
            if (artist.getFightPhase() != ArtistEntity.FightPhase.FLEEING) {
                continue;
            }
            double distance = artist.distanceToSqr(speaker);
            if (distance < bestDistance) {
                bestDistance = distance;
                beast = artist;
            }
        }

        if (beast != null) {
            beast.onFleeTaunt();
        }
    }

    /** Ignore case, spaces and punctuation so natural phrasing still matches. */
    private static String normalize(String text) {
        StringBuilder builder = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                builder.append(c);
            }
        }
        return builder.toString().toLowerCase(Locale.ROOT);
    }
}
