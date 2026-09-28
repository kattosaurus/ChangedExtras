package com.katt.changedextras.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientFeralCatTracker {
    private static final Set<UUID> FERAL_PLAYERS = new HashSet<>();

    public static boolean isFeral(UUID playerUuid) {
        return FERAL_PLAYERS.contains(playerUuid);
    }

    public static void setFeral(UUID playerUuid, boolean feral) {
        if (feral) {
            FERAL_PLAYERS.add(playerUuid);
        } else {
            FERAL_PLAYERS.remove(playerUuid);
        }
    }

    public static void clear() {
        FERAL_PLAYERS.clear();
    }
}
