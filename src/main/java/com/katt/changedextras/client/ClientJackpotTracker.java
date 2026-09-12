package com.katt.changedextras.client;

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
    private static final ResourceLocation VIGNETTE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/vignette.png");
    private static final Map<UUID, Long> JACKPOT_START_TIMES = new ConcurrentHashMap<>();

    public static void setVignetteActive(boolean active) {
        vignetteActive = active;
    }

    public static void setJackpotState(UUID uuid, boolean active) {
        if (uuid == null) return;
        if (active) {
            JACKPOT_START_TIMES.put(uuid, System.currentTimeMillis());
        } else {
            JACKPOT_START_TIMES.remove(uuid);
        }
    }

    public static boolean isJackpotActive(UUID uuid) {
        return uuid != null && JACKPOT_START_TIMES.containsKey(uuid);
    }

    public static float getAuraScale(UUID uuid) {
        if (uuid == null) return 1.0f;
        Long startTime = JACKPOT_START_TIMES.get(uuid);
        if (startTime == null) return 1.0f;
        float elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0f;
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
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !vignetteActive) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || !minecraft.player.isAlive() || minecraft.player.isDeadOrDying()) {
            vignetteActive = false;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!vignetteActive) return;

        // Render after the hotbar to ensure it overlays the world correctly
        if (event.getOverlay().id().getPath().equals("hotbar")) {
            drawPulse(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
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
        if (!vignetteActive) return;

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
