package com.katt.changedextras.client.tracker;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientPrepareTracker {
    private static int casterId = -1;
    private static long startMillis;
    private static long highlightMillis;
    private static long fadeMillis;

    private ClientPrepareTracker() {
    }

    public static void start(int caster, int highlightTicks, int fadeTicks) {
        if (caster < 0 || highlightTicks <= 0) {
            clear();
            return;
        }

        casterId = caster;
        startMillis = System.currentTimeMillis();
        highlightMillis = highlightTicks * 50L;
        fadeMillis = Math.max(1, fadeTicks) * 50L;
    }

    public static int getCasterId() {
        return casterId;
    }

    public static boolean isActive() {
        return casterId >= 0 && getElapsedMillis() < highlightMillis + fadeMillis;
    }

    public static boolean isAnimationActive() {
        return casterId >= 0 && getElapsedMillis() < highlightMillis;
    }

    /**
     * Checks whether the supplied rendered entity belongs to the ability caster. This accounts for
     * both mob casters (same entity id) and transfurred players, whose latex body is a separate
     * {@link ChangedEntity} that carries the player as its underlying entity.
     */
    public static boolean isCaster(Entity entity) {
        if (entity == null || casterId < 0) {
            return false;
        }

        if (entity.getId() == casterId) {
            return true;
        }

        return entity instanceof ChangedEntity changed
                && changed.getUnderlyingPlayer() != null
                && changed.getUnderlyingPlayer().getId() == casterId;
    }

    public static float getAlpha() {
        if (casterId < 0) {
            return 0.0F;
        }

        long elapsed = getElapsedMillis();
        if (elapsed < highlightMillis) {
            return 1.0F;
        }

        long fadeElapsed = elapsed - highlightMillis;
        if (fadeElapsed >= fadeMillis) {
            return 0.0F;
        }

        return 1.0F - (float) fadeElapsed / (float) fadeMillis;
    }

    public static long getElapsedMillis() {
        return System.currentTimeMillis() - startMillis;
    }

    public static void clear() {
        casterId = -1;
        startMillis = 0L;
        highlightMillis = 0L;
        fadeMillis = 0L;
    }
}
