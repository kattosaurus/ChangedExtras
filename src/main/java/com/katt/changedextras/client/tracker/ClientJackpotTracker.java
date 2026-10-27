package com.katt.changedextras.client.tracker;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientJackpotTracker {
    private static boolean vignetteActive = false;
    private static int screenShakeTicks = 0;
    private static float screenShakeIntensity = 0.0f;

    private static final ResourceLocation VIGNETTE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/vignette.png");
    private static final Map<UUID, Long> JACKPOT_START_TIMES = new ConcurrentHashMap<>();

    // Delay before the jackpot aura, particles, vignette and music kick in, so the effects
    // do not fire the same instant the jackpot is won.
    private static final long JACKPOT_START_DELAY_MS = 1000L;

    public static void setVignetteActive(boolean active) {
        vignetteActive = active;
    }

    public static void triggerScreenShake(int ticks, float intensity) {
        screenShakeTicks = ticks;
        screenShakeIntensity = intensity;
    }

    public static void setJackpotState(UUID uuid, boolean active) {
        if (uuid == null) return;
        if (active) {
            // putIfAbsent keeps the original start time so repeated resyncs don't restart
            // the aura timeline (song-aligned scale changes). The start time is offset into
            // the future so the visual effects begin one second after the jackpot is won.
            JACKPOT_START_TIMES.putIfAbsent(uuid, System.currentTimeMillis() + JACKPOT_START_DELAY_MS);
        } else {
            JACKPOT_START_TIMES.remove(uuid);
        }
    }

    public static boolean isJackpotActive(UUID uuid) {
        if (uuid == null) return false;
        Long startTime = JACKPOT_START_TIMES.get(uuid);
        return startTime != null && System.currentTimeMillis() >= startTime;
    }

    public static float getAuraScale(UUID uuid) {
        if (uuid == null) return 1.0f;
        Long startTime = JACKPOT_START_TIMES.get(uuid);
        if (startTime == null) return 1.0f;
        float elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0f;
        if (elapsedSec < 0.0f) elapsedSec = 0.0f;
        return getAuraScaleForTime(elapsedSec);
    }

    public static float getAuraScaleForTime(float seconds) {
        if (seconds < 10.0f) return 0.5f;   // 0:00 Starts (0.5 of current size)
        if (seconds < 30.0f) return 0.7f;   // 0:10 Violin starts (0.7 of current size)
        if (seconds < 65.0f) return 0.6f;   // 0:30 Vocals start, tones down (0.6 of current size)
        if (seconds < 106.0f) return 1.2f;  // 1:05 Epic part starts (1.2 of current size)
        if (seconds < 115.0f) return 0.5f;  // 1:46 Epic part ends, tones down (0.5 of current size)
        if (seconds < 135.0f) return 0.7f;  // 1:55 Violin part again (0.7 of current size)
        if (seconds < 173.0f) return 0.6f;  // 2:15 Vocals start again, toned down (0.6 of current size)
        if (seconds < 191.0f) return 0.5f;  // 2:53 Tones down again (0.5 of current size)
        if (seconds < 231.0f) return 1.2f;  // 3:11 Epic part again (1.2 of current size)
        if (seconds < 251.0f) return 0.5f;  // 3:51 Epic part ends, tones down (0.5 of current size)
        return 0.5f;                        // 4:11 Song ends (0.5 of current size)
    }

    public static void clear() {
        JACKPOT_START_TIMES.clear();
        vignetteActive = false;
        screenShakeTicks = 0;
        screenShakeIntensity = 0.0f;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (screenShakeTicks > 0) {
            screenShakeTicks--;
        }

        // The jackpot vignette follows the (delayed) jackpot state of the local player.
        Minecraft minecraft = Minecraft.getInstance();
        vignetteActive = minecraft.player != null && minecraft.level != null
                && minecraft.player.isAlive() && !minecraft.player.isDeadOrDying()
                && isJackpotActive(minecraft.player.getUUID());
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // Render after the hotbar to ensure it overlays the world correctly
        if (!event.getOverlay().id().getPath().equals("hotbar")) return;

        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();

        if (vignetteActive) {
            drawPulse(event.getGuiGraphics(), w, h);
        }
    }

    private static void drawPulse(GuiGraphics gui, int w, int h) {
        float msPerBeat = 60000.0f / 126.0f;
        float phase = (System.currentTimeMillis() % (long)msPerBeat) / msPerBeat;

        // A sharper "pop" for the pulse (Sine squared)
        float alpha = (float) Math.pow(Math.sin(phase * Math.PI), 2) * 0.5f;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        // ADDITIVE BLENDING: This makes it glow/brighten instead of darken
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        // Bright Lime Green
        RenderSystem.setShaderColor(0.0f, 0.8f, 0.1f, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        gui.blit(VIGNETTE, 0, 0, 0, 0, w, h, w, h);

        // Reset to default blending and color
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        // Screen shake effect exclusively on the player screen
        if (screenShakeTicks > 0) {
            float progress = (float) screenShakeTicks / 35.0f;
            float intensity = screenShakeIntensity * progress;
            long currentTime = System.currentTimeMillis();
            float shakePitch = (float) Math.sin(currentTime * 0.08f) * intensity * 2.2f;
            float shakeYaw = (float) Math.cos(currentTime * 0.07f) * intensity * 2.2f;
            float shakeRoll = (float) Math.sin(currentTime * 0.09f) * intensity * 1.4f;

            event.setPitch(event.getPitch() + shakePitch);
            event.setYaw(event.getYaw() + shakeYaw);
            event.setRoll(event.getRoll() + shakeRoll);
        }

        if (vignetteActive) {
            float msPerBeat = 60000.0f / 124.0f;
            long currentTime = System.currentTimeMillis();
            float phase = (currentTime % (long)msPerBeat) / msPerBeat;
            float shakeEnvelope = (float) Math.pow(Math.sin(phase * Math.PI), 4);
            float pitchIntensity = 1.5f;
            float yawIntensity = 1.5f;

            float smoothSway = (float) Math.sin(currentTime * 0.005f) * yawIntensity;
            float smoothPitch = (float) Math.cos(currentTime * 0.003f) * pitchIntensity;

            event.setPitch(event.getPitch() + (smoothPitch * shakeEnvelope));
            event.setYaw(event.getYaw() + (smoothSway * shakeEnvelope));
        }
    }
}
