package com.katt.changedextras.client.hud;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Renders a full-screen white glare overlay (max 50% opacity) whenever the
 * local player is on the surface during daytime. This makes the Latex Panther
 * difficult to see in daylight, emphasising that its glowing eyes are best
 * viewed in the dark.
 *
 * <p>The overlay is active in the Overworld only. It fades in/out smoothly
 * over ~25 seconds at dawn and dusk so the transition is not jarring.
 * It also fades out gradually when the player goes underground (loses sky
 * access), so entering a cave feels natural.</p>
 *
 * <ul>
 *   <li>Day window : ticks 1000–13000 within a 24 000-tick day cycle</li>
 *   <li>Fade width : ±500 ticks (~25 s) at the edges of the window</li>
 *   <li>Max alpha  : 0.50 (50 %)</li>
 * </ul>
 *
 * <p>Additionally, the first time a Latex Panther player walks into full
 * sunlight (per session), a one-time flavour tooltip — "The light blinds
 * you." — fades in, lingers, then fades out above the hotbar.</p>
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SunGlareOverlay {

    // ── Glare constants ───────────────────────────────────────────────────────

    /** Maximum glare opacity (50 %). */
    private static final float MAX_ALPHA = 0.50f;

    /** Tick within the 24 000-tick day cycle at which full daytime begins. */
    private static final float DAY_START = 1000f;

    /** Tick within the 24 000-tick day cycle at which daytime ends. */
    private static final float DAY_END = 13000f;

    /**
     * Half-width of the fade zone at each edge of the day window (in ticks).
     * The overlay ramps in over this many ticks at dawn and out at dusk.
     */
    private static final float FADE_TICKS = 500f;

    /**
     * Smoothed sky-exposure value [0, 1]. Lerped toward the binary
     * {@code canSeeSky} result each frame to avoid snapping on/off under
     * overhangs.
     */
    private static float smoothSkyFactor = 0f;

    /** How quickly {@link #smoothSkyFactor} tracks its target (per frame). */
    private static final float SKY_LERP_SPEED = 0.05f;

    // ── Tooltip constants ─────────────────────────────────────────────────────

    /** The flavour text shown once to a Panther player who steps into sunlight. */
    private static final String TOOLTIP_TEXT = "The light blinds you.";

    /**
     * Duration breakdown for the tooltip animation (driven by
     * {@link #tooltipFrames}, which increments each rendered frame).
     *
     * <pre>
     *   0  …  FADE_IN_FRAMES           — fade in
     *   FADE_IN_FRAMES … HOLD_END      — hold at full opacity
     *   HOLD_END … TOTAL_FRAMES        — fade out
     * </pre>
     */
    private static final int TOOLTIP_FADE_IN_FRAMES  = 20;   // ~1 s at 20 fps
    private static final int TOOLTIP_HOLD_FRAMES     = 60;   // ~3 s
    private static final int TOOLTIP_FADE_OUT_FRAMES = 30;   // ~1.5 s
    private static final int TOOLTIP_TOTAL_FRAMES    =
            TOOLTIP_FADE_IN_FRAMES + TOOLTIP_HOLD_FRAMES + TOOLTIP_FADE_OUT_FRAMES;

    /** Y offset from the bottom of the screen where the tooltip is centred. */
    private static final int TOOLTIP_BOTTOM_OFFSET = 50;

    /**
     * Players (by UUID) who have already seen the tooltip this session.
     * Reset on each game launch — seeing it once per play-session is fine.
     */
    private static final Set<UUID> SAW_TOOLTIP = new HashSet<>();

    /**
     * Frame counter driving the tooltip animation. -1 means inactive.
     * Increments each rendered frame once triggered.
     */
    private static int tooltipFrames = -1;

    // ── Event handler ─────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // Render once, after the hotbar is drawn.
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Overworld only (dimension has a normal day/night cycle).
        if (!mc.level.dimensionType().hasSkyLight()) return;

        // The glare only affects the Latex Panther variant.
        boolean isPanther = isPantherVariant(mc);

        // ── Sky exposure ──────────────────────────────────────────────────────
        if (isPanther) {
            BlockPos eyePos = BlockPos.containing(
                    mc.player.getX(),
                    mc.player.getEyeY(),
                    mc.player.getZ());
            boolean canSeeSky = mc.level.canSeeSky(eyePos);
            float skyTarget = canSeeSky ? 1f : 0f;
            smoothSkyFactor = Mth.lerp(SKY_LERP_SPEED, smoothSkyFactor, skyTarget);
        } else {
            // Not the Panther: drop the glare immediately so switching forms
            // does not leave a lingering bright screen.
            smoothSkyFactor = 0f;
        }

        // ── Daytime factor ────────────────────────────────────────────────────
        float dayTime   = mc.level.getDayTime() % 24000L;
        float dayFactor = computeDayFactor(dayTime);

        // Combined "is the glare meaningful?" flag (Panther AND sky visible AND daytime).
        boolean glareActive = isPanther && smoothSkyFactor > 0.001f && dayFactor > 0.001f;

        // ── White glare overlay ───────────────────────────────────────────────
        if (glareActive) {
            float rainFactor = 1f - mc.level.getRainLevel(mc.getFrameTime()) * 0.6f;
            float alpha = MAX_ALPHA * dayFactor * smoothSkyFactor * rainFactor;

            if (alpha >= 0.002f) {
                int alphaByte = Mth.clamp(Math.round(alpha * 255f), 0, 255);
                int color = (alphaByte << 24) | 0x00FFFFFF; // ARGB white

                GuiGraphics gui = event.getGuiGraphics();
                int w = event.getWindow().getGuiScaledWidth();
                int h = event.getWindow().getGuiScaledHeight();

                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                gui.fill(0, 0, w, h, color);
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            }
        }

        // ── One-time "The light blinds you." tooltip ──────────────────────────
        tickTooltip(mc, glareActive, isPanther, event.getGuiGraphics(), event.getWindow());
    }

    // ── Tooltip logic ─────────────────────────────────────────────────────────

    private static void tickTooltip(Minecraft mc,
                                    boolean glareActive,
                                    boolean isPanther,
                                    GuiGraphics gui,
                                    com.mojang.blaze3d.platform.Window window) {

        UUID playerId = mc.player.getUUID();

        // Trigger: glare is fully visible, player is the Panther variant, and
        // they haven't seen the tooltip yet this session.
        if (tooltipFrames < 0 && glareActive
                && smoothSkyFactor > 0.9f
                && isPanther
                && !SAW_TOOLTIP.contains(playerId)) {

            SAW_TOOLTIP.add(playerId);
            tooltipFrames = 0;
        }

        if (tooltipFrames < 0) return;

        tooltipFrames++;
        float tooltipAlpha = computeTooltipAlpha(tooltipFrames);

        if (tooltipAlpha > 0.001f) {
            int alphaByte = Mth.clamp(Math.round(tooltipAlpha * 255f), 0, 255);
            int color = (alphaByte << 24) | 0xE0E0E0; // soft white-grey

            Font font   = mc.font;
            int screenW = window.getGuiScaledWidth();
            int screenH = window.getGuiScaledHeight();
            int textY   = screenH - TOOLTIP_BOTTOM_OFFSET;

            gui.drawCenteredString(font, TOOLTIP_TEXT, screenW / 2, textY, color);
        }

        if (tooltipFrames >= TOOLTIP_TOTAL_FRAMES) {
            tooltipFrames = -1;
        }
    }

    private static float computeTooltipAlpha(int frame) {
        if (frame <= TOOLTIP_FADE_IN_FRAMES) {
            return frame / (float) TOOLTIP_FADE_IN_FRAMES;
        }
        int holdEnd = TOOLTIP_FADE_IN_FRAMES + TOOLTIP_HOLD_FRAMES;
        if (frame <= holdEnd) {
            return 1f;
        }
        int fadeOutFrame = frame - holdEnd;
        if (fadeOutFrame <= TOOLTIP_FADE_OUT_FRAMES) {
            return 1f - fadeOutFrame / (float) TOOLTIP_FADE_OUT_FRAMES;
        }
        return 0f;
    }

    /**
     * Returns {@code true} if the local player is currently transformed as
     * the Latex Panther variant.
     *
     * Uses {@link TransfurVariantInstance#getParent()} — the correct public
     * API — instead of accessing any field directly.
     */
    private static boolean isPantherVariant(Minecraft mc) {
        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(mc.player);
        if (instance == null) return false;
        TransfurVariant<?> variant = instance.getParent();
        TransfurVariant<?> pantherVariant = ModTransfurVariants.LATEX_PANTHER.get();
        return variant == pantherVariant;
    }

    // ── Day-cycle helper ──────────────────────────────────────────────────────

    private static float computeDayFactor(float dayTime) {
        if (dayTime < DAY_START - FADE_TICKS || dayTime > DAY_END + FADE_TICKS) {
            return 0f;
        }
        if (dayTime < DAY_START) {
            return (dayTime - (DAY_START - FADE_TICKS)) / FADE_TICKS;
        }
        if (dayTime > DAY_END) {
            return 1f - (dayTime - DAY_END) / FADE_TICKS;
        }
        return 1f;
    }
}
