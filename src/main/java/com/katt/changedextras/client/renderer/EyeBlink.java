package com.katt.changedextras.client.renderer;

import com.katt.changedextras.Config;
import net.ltxprogrammer.changed.entity.ChangedEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Drives smooth per-entity blinking and gaze fixation.
 *
 * <p>The eyes hold their gaze in the direction they were looking at for a while,
 * and only swap to a new target direction during a blink (a blink saccade).
 * While open, the pupils remain fixed in place instead of constantly drifting
 * with micro-mouse movements.</p>
 */
public final class EyeBlink {
    /** Total duration of a blink from fully open, to closed, to fully open (seconds). */
    public static final float BLINK_SECONDS = 0.22F;

    /** Minimum natural blink interval between spontaneous blinks (seconds). */
    private static final float MIN_NATURAL_INTERVAL = 3.0F;
    /** Maximum natural blink interval between spontaneous blinks (seconds). */
    private static final float MAX_NATURAL_INTERVAL = 5.5F;

    /** Minimum time (seconds) to hold the current gaze before a new gaze shift can trigger a blink. */
    private static final float MIN_GAZE_HOLD_SECONDS = 1.2F;

    /** Minimum difference in pupil offset required to be considered a new gaze direction. */
    private static final float GAZE_CHANGE_THRESHOLD = 0.12F;

    /** Floor scale so the render matrix stays invertible. */
    private static final float MIN_SCALE = 0.01F;

    private static final Map<UUID, GazeState> STATES = new ConcurrentHashMap<>();
    private static long lastPruneTimeMs = 0L;

    public static final class EyeResult {
        public final float scaleY;
        public final float pupilX;

        public EyeResult(float scaleY, float pupilX) {
            this.scaleY = scaleY;
            this.pupilX = pupilX;
        }
    }

    private static final class GazeState {
        float displayedPupilX;
        float targetPupilX;
        float lastSwapTimeSeconds;
        float blinkStartTimeSeconds = -1.0F;
        float nextNaturalBlinkSeconds;
        boolean swappedThisBlink;
        long lastAccessMs;

        GazeState(float initialGaze, float timeSeconds, float nextBlink) {
            this.displayedPupilX = initialGaze;
            this.targetPupilX = initialGaze;
            this.lastSwapTimeSeconds = timeSeconds;
            this.nextNaturalBlinkSeconds = nextBlink;
            this.lastAccessMs = System.currentTimeMillis();
        }
    }

    private EyeBlink() {
    }

    /**
     * Updates the entity's blink state and gaze fixation.
     *
     * @param entity the entity being rendered
     * @param ageInTicks entity render age (tickCount + partialTick)
     * @param targetPupilX instantaneous target pupil offset derived from look yaw
     * @return EyeResult containing the current vertical scale and the held/swapped pupil offset
     */
    public static EyeResult update(ChangedEntity entity, float ageInTicks, float targetPupilX) {
        if (!Config.smoothBlinking) {
            return new EyeResult(1.0F, targetPupilX);
        }

        UUID uuid = entity.getUnderlyingPlayer() != null
                ? entity.getUnderlyingPlayer().getUUID()
                : entity.getUUID();

        float time = ageInTicks / 20.0F;
        long nowMs = System.currentTimeMillis();

        if (nowMs - lastPruneTimeMs > 10000L) {
            lastPruneTimeMs = nowMs;
            STATES.entrySet().removeIf(entry -> nowMs - entry.getValue().lastAccessMs > 15000L);
        }

        GazeState state = STATES.computeIfAbsent(uuid, id -> {
            float firstBlink = time + MIN_NATURAL_INTERVAL + (float) (Math.random() * (MAX_NATURAL_INTERVAL - MIN_NATURAL_INTERVAL));
            return new GazeState(targetPupilX, time, firstBlink);
        });

        state.lastAccessMs = nowMs;
        state.targetPupilX = targetPupilX;

        // If time jumped backwards (e.g. reload or respawn), reset timing
        if (time < state.lastSwapTimeSeconds || time < state.blinkStartTimeSeconds) {
            state.lastSwapTimeSeconds = time;
            state.blinkStartTimeSeconds = -1.0F;
            state.nextNaturalBlinkSeconds = time + MIN_NATURAL_INTERVAL;
            state.swappedThisBlink = false;
        }

        // 1. Check if we should initiate a blink:
        if (state.blinkStartTimeSeconds < 0.0F) {
            boolean timeForNaturalBlink = time >= state.nextNaturalBlinkSeconds;
            boolean gazeChanged = Math.abs(state.targetPupilX - state.displayedPupilX) > GAZE_CHANGE_THRESHOLD;
            boolean heldLongEnough = (time - state.lastSwapTimeSeconds) >= MIN_GAZE_HOLD_SECONDS;

            // Trigger blink if natural interval passed OR gaze changed after being held for a while
            if (timeForNaturalBlink || (gazeChanged && heldLongEnough)) {
                state.blinkStartTimeSeconds = time;
                state.swappedThisBlink = false;
                state.nextNaturalBlinkSeconds = time + MIN_NATURAL_INTERVAL
                        + (float) (Math.random() * (MAX_NATURAL_INTERVAL - MIN_NATURAL_INTERVAL));
            }
        }

        // 2. Compute current blink scale and handle in-blink gaze swap:
        float scaleY = 1.0F;
        if (state.blinkStartTimeSeconds >= 0.0F) {
            float elapsed = time - state.blinkStartTimeSeconds;
            if (elapsed >= BLINK_SECONDS) {
                // Blink completed
                state.blinkStartTimeSeconds = -1.0F;
                state.swappedThisBlink = false;
                scaleY = 1.0F;
            } else {
                float half = BLINK_SECONDS * 0.5F;

                // When eyelids reach apex (fully closed), swap to the new gaze direction!
                if (elapsed >= half && !state.swappedThisBlink) {
                    state.displayedPupilX = state.targetPupilX;
                    state.lastSwapTimeSeconds = time;
                    state.swappedThisBlink = true;
                }

                // Smooth cosine / eased curve for eyelid movement
                float progress = elapsed < half ? elapsed / half : (BLINK_SECONDS - elapsed) / half;
                float eased = progress * progress * (3.0F - 2.0F * progress);
                scaleY = Math.max(MIN_SCALE, 1.0F - eased);
            }
        }

        return new EyeResult(scaleY, state.displayedPupilX);
    }

    public static float scale(ChangedEntity entity, float ageInTicks) {
        return update(entity, ageInTicks, 0.0F).scaleY;
    }

    public static void clear() {
        STATES.clear();
    }
}
