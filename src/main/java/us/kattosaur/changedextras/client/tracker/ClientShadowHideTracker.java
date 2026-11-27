package us.kattosaur.changedextras.client.tracker;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ClientShadowHideTracker {
    private static final Set<UUID> HIDDEN_PLAYERS = new HashSet<>();

    private ClientShadowHideTracker() {}

    public static boolean isHidden(UUID playerUuid) {
        return HIDDEN_PLAYERS.contains(playerUuid);
    }

    public static void setHidden(UUID playerUuid, boolean hidden) {
        if (hidden) {
            HIDDEN_PLAYERS.add(playerUuid);
        } else {
            HIDDEN_PLAYERS.remove(playerUuid);
        }
    }

    public static void clear() {
        HIDDEN_PLAYERS.clear();
    }
}
