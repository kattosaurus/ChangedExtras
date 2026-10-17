package com.katt.changedextras.client;

import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.network.DomainStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientDomainTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDomainTracker.class);

    private static boolean domainActive = false;
    private static UUID casterUUID = null;
    private static String casterName = "";
    private static boolean showOverlay = false;
    private static int scenarioIndex = 1;
    private static String scenarioName = "";
    private static float luckMultiplier = 1.0f;
    private static SoundInstance domainMusicInstance = null;

    // White overlay: fades in over 0.5s, then stays pure white while shown, and fades out over
    // 0.5s once the server dismisses it (when the intro gate is passed).
    private static final long OVERLAY_FADE_IN_MS = 500L;
    private static final long OVERLAY_FADE_OUT_MS = 500L;
    private static long overlayFadeStart = -1L;
    private static long overlayHideStart = -1L;

    // Scenario swap white flash: 0.2s in, 0.5s out
    private static final long FLASH_FADE_IN_MS = 200L;
    private static final long FLASH_FADE_OUT_MS = 500L;
    private static long flashStart = -1L;

    // Rainbow rule text: 0.5s in, stays 20s after the domain opens, 0.5s out
    private static final long INFO_FADE_IN_MS = 500L;
    private static final long INFO_HOLD_MS = 19000L;
    private static final long INFO_FADE_OUT_MS = 500L;
    private static long infoTextStart = -1L;

    // Caster domain-open animation: plays once when the domain opens, then holds its last frame
    // until the intro (white overlay) ends and everyone is unfrozen.
    private static long domainAnimationStart = -1L;

    private ClientDomainTracker() {}

    public static void handlePacket(DomainStatePacket packet) {
        LOGGER.info("[ClientDomainTracker] Received packet: active={}, caster={}, overlay={}, scenario={}",
                packet.isActive(), packet.getCasterName(), packet.isShowOverlay(), packet.getScenarioName());

        boolean wasShowOverlay = showOverlay;
        boolean wasDomainActive = domainActive;

        domainActive = packet.isActive();
        casterUUID = packet.getCasterUUID();
        casterName = packet.getCasterName();
        showOverlay = packet.isShowOverlay();
        scenarioIndex = packet.getScenarioIndex();
        scenarioName = packet.getScenarioName();
        luckMultiplier = packet.getLuckMultiplier();

        // Restart the white fade whenever the intro overlay is (re)shown
        if (showOverlay && !wasShowOverlay) {
            overlayFadeStart = System.currentTimeMillis();
            overlayHideStart = -1L;
        } else if (!showOverlay && wasShowOverlay) {
            overlayHideStart = System.currentTimeMillis();
        }

        // Start the rainbow rule text for 20 seconds the moment the domain opens,
        // and drop it as soon as the domain is gone (independent of the white mask).
        if (domainActive && !wasDomainActive) {
            infoTextStart = System.currentTimeMillis();
            domainAnimationStart = System.currentTimeMillis();
        } else if (!domainActive) {
            infoTextStart = -1L;
            domainAnimationStart = -1L;
        }

        Minecraft mc = Minecraft.getInstance();

        // Only (re)start the domain music when the domain actually opens, never on the
        // scenario/break packets in between, so the looping track cannot stack up.
        if (domainActive && !wasDomainActive) {
            startDomainMusic(mc);
        } else if (!domainActive) {
            stopDomainMusic(mc);
        }
    }

    public static boolean isDomainActive() {
        return domainActive;
    }

    public static UUID getCasterUUID() {
        return casterUUID;
    }

    public static String getCasterName() {
        return casterName;
    }

    public static boolean isShowOverlay() {
        return showOverlay;
    }

    public static int getScenarioIndex() {
        return scenarioIndex;
    }

    public static String getScenarioName() {
        return scenarioName;
    }

    public static float getLuckMultiplier() {
        return luckMultiplier;
    }

    public static void setShowOverlay(boolean show) {
        if (show && !showOverlay) {
            overlayFadeStart = System.currentTimeMillis();
            overlayHideStart = -1L;
        } else if (!show && showOverlay) {
            overlayHideStart = System.currentTimeMillis();
        }
        showOverlay = show;
    }

    /**
     * Current opacity of the white intro overlay (0.0 = invisible, 1.0 = fully white).
     * Fades in over 0.5s, then stays fully white until it is dismissed, after which it fades
     * out over 0.5s.
     */
    public static float getOverlayAlpha() {
        long now = System.currentTimeMillis();

        // Dismissed: fade out from full white.
        if (overlayHideStart >= 0L) {
            long elapsed = now - overlayHideStart;
            if (elapsed >= OVERLAY_FADE_OUT_MS) {
                return 0.0f;
            }
            return 1.0f - (elapsed / (float) OVERLAY_FADE_OUT_MS);
        }

        if (!showOverlay || overlayFadeStart < 0L) {
            return 0.0f;
        }

        // Fade in, then hold at full white for as long as the overlay stays shown.
        long elapsed = now - overlayFadeStart;
        if (elapsed < OVERLAY_FADE_IN_MS) {
            return elapsed / (float) OVERLAY_FADE_IN_MS;
        }
        return 1.0f;
    }

    /**
     * Starts the short white flash used when the domain swaps to a new scenario.
     */
    public static void triggerFlash() {
        flashStart = System.currentTimeMillis();
    }

    /**
     * Current opacity of the scenario-swap flash (0.0 = invisible, 1.0 = fully white).
     * Fades in over 0.2s, then fades out over 0.5s.
     */
    public static float getFlashAlpha() {
        if (flashStart < 0L) {
            return 0.0f;
        }

        long elapsed = System.currentTimeMillis() - flashStart;
        if (elapsed < 0L) {
            return 0.0f;
        }
        if (elapsed < FLASH_FADE_IN_MS) {
            return elapsed / (float) FLASH_FADE_IN_MS;
        }

        elapsed -= FLASH_FADE_IN_MS;
        if (elapsed < FLASH_FADE_OUT_MS) {
            return 1.0f - (elapsed / (float) FLASH_FADE_OUT_MS);
        }

        flashStart = -1L;
        return 0.0f;
    }

    /**
     * Current opacity of the rainbow rule text (0.0 = invisible, 1.0 = fully visible).
     * Fades in over 0.5s, stays for 20s after the domain opens, then fades out over 0.5s.
     */
    public static float getInfoTextAlpha() {
        if (infoTextStart < 0L) {
            return 0.0f;
        }

        long elapsed = System.currentTimeMillis() - infoTextStart;
        if (elapsed < 0L) {
            return 0.0f;
        }
        if (elapsed < INFO_FADE_IN_MS) {
            return elapsed / (float) INFO_FADE_IN_MS;
        }

        elapsed -= INFO_FADE_IN_MS;
        if (elapsed < INFO_HOLD_MS) {
            return 1.0f;
        }

        elapsed -= INFO_HOLD_MS;
        if (elapsed < INFO_FADE_OUT_MS) {
            return 1.0f - (elapsed / (float) INFO_FADE_OUT_MS);
        }

        infoTextStart = -1L;
        return 0.0f;
    }

    /**
     * Milliseconds since the domain opened, used to animate the rainbow rule text.
     */
    public static long getInfoTextElapsedMs() {
        if (infoTextStart < 0L) {
            return 0L;
        }
        return Math.max(0L, System.currentTimeMillis() - infoTextStart);
    }

    /**
     * Milliseconds since the caster's domain-open animation started. The animation plays once
     * (1s) and then holds its final frame while the intro freeze is active.
     */
    public static long getDomainAnimationElapsedMs() {
        if (domainAnimationStart < 0L) {
            return 0L;
        }
        return Math.max(0L, System.currentTimeMillis() - domainAnimationStart);
    }

    private static void startDomainMusic(Minecraft mc) {
        if (domainMusicInstance != null) {
            mc.getSoundManager().stop(domainMusicInstance);
        }

        domainMusicInstance = new SimpleSoundInstance(
                ChangedExtrasSounds.JACKPOT_SONG.get().getLocation(),
                SoundSource.RECORDS,
                1.0f, 1.0f,
                RandomSource.create(),
                true,
                0,
                SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0,
                true
        );
        mc.getSoundManager().play(domainMusicInstance);
    }

    public static void stopDomainMusic(Minecraft mc) {
        if (domainMusicInstance != null) {
            mc.getSoundManager().stop(domainMusicInstance);
            domainMusicInstance = null;
        }
    }

    public static void clear() {
        domainActive = false;
        casterUUID = null;
        casterName = "";
        showOverlay = false;
        overlayFadeStart = -1L;
        overlayHideStart = -1L;
        flashStart = -1L;
        infoTextStart = -1L;
        domainAnimationStart = -1L;
        scenarioIndex = 1;
        scenarioName = "";
        luckMultiplier = 1.0f;
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            stopDomainMusic(mc);
        }
    }
}
