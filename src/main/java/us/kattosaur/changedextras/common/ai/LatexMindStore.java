package us.kattosaur.changedextras.common.ai;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LatexMindStore {
    private static final Map<UUID, LatexMind> MINDS = new ConcurrentHashMap<>();

    private LatexMindStore() {}

    public static LatexMind get(LivingEntity entity) {
        return MINDS.computeIfAbsent(entity.getUUID(), id -> new LatexMind());
    }

    public static void forget(LivingEntity entity) {
        MINDS.remove(entity.getUUID());
    }

    public static void forget(UUID id) {
        MINDS.remove(id);
    }
}