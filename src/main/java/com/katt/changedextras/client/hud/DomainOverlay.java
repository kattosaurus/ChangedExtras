package com.katt.changedextras.client.hud;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.tracker.ClientDomainTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DomainOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!ClientDomainTracker.isDomainActive()) {
            return;
        }

        // White mask: intro fade (0.5s in / 3s hold / 0.5s out) and/or scenario-swap flash
        float introAlpha = ClientDomainTracker.getOverlayAlpha();
        float flashAlpha = ClientDomainTracker.getFlashAlpha();
        // Rainbow rule text: independent 20s timer
        float infoAlpha = ClientDomainTracker.getInfoTextAlpha();

        float whiteAlpha = Math.max(introAlpha, flashAlpha);
        if (whiteAlpha <= 0.001f && infoAlpha <= 0.001f) {
            return;
        }

        // Render over the chat or final overlay to mask the full screen
        if (!event.getOverlay().id().getPath().equals("chat") && !event.getOverlay().id().getPath().equals("hotbar")) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();
        GuiGraphics gui = event.getGuiGraphics();
        Font font = mc.font;

        // 1. White Fullscreen Mask Overlay (intro fade and/or scenario flash)
        if (whiteAlpha > 0.001f) {
            int maskByte = Math.max(0, Math.min(255, (int) (whiteAlpha * 255.0f)));
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            gui.fill(0, 0, screenWidth, screenHeight, (maskByte << 24) | 0xFFFFFF);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }

        // 2. Rainbow Instructional Rule Text (decoupled from the white mask)
        if (infoAlpha <= 0.001f) {
            return;
        }

        int alphaByte = Math.max(0, Math.min(255, (int) (infoAlpha * 255.0f)));

        String caster = ClientDomainTracker.getCasterName();
        if (caster == null || caster.isEmpty()) {
            caster = "Caster";
        }

        List<String> rawParagraphs = getRuleParagraphs(caster);

        // Calculate text wrapping and layout
        int maxTextWidth = Math.max(260, screenWidth - 40);
        List<String> wrappedLines = new ArrayList<>();
        for (String para : rawParagraphs) {
            if (para.isEmpty()) {
                wrappedLines.add("");
            } else {
                for (FormattedText wrapped : font.getSplitter().splitLines(para, maxTextWidth, Style.EMPTY)) {
                    wrappedLines.add(wrapped.getString());
                }
            }
        }

        int lineHeight = 10;
        int totalTextHeight = wrappedLines.size() * lineHeight;

        PoseStack poseStack = gui.pose();
        poseStack.pushPose();

        float scale = 1.0f;
        if (totalTextHeight > screenHeight - 30) {
            scale = Math.max(0.6f, (float)(screenHeight - 30) / (float)totalTextHeight);
            poseStack.scale(scale, scale, 1.0f);
        }

        int startX = (int)((screenWidth / scale - maxTextWidth) / 2.0f);
        int startY = Math.max(10, (int)(((screenHeight / scale) - totalTextHeight) / 2.0f));

        // Alpha lives in the color param; RGB comes from the per-character rainbow styles.
        int color = (alphaByte << 24) | 0xFFFFFF;
        long elapsedMs = ClientDomainTracker.getInfoTextElapsedMs();
        int rainbowOffset = 0;

        for (int i = 0; i < wrappedLines.size(); i++) {
            String str = wrappedLines.get(i);
            MutableComponent line = buildRainbowLine(str, rainbowOffset, elapsedMs);
            gui.drawString(font, line, startX, startY + (i * lineHeight), color, true);
            rainbowOffset += str.length();
        }

        poseStack.popPose();
    }

    /**
     * Builds a component where every character gets its own animated rainbow color.
     */
    private static MutableComponent buildRainbowLine(String text, int charOffset, long elapsedMs) {
        MutableComponent line = Component.empty();
        if (text == null || text.isEmpty()) {
            return line;
        }

        float shift = elapsedMs / 40.0f;
        for (int i = 0; i < text.length(); i++) {
            float hue = ((charOffset + i) * 0.04f - shift) % 1.0f;
            if (hue < 0.0f) {
                hue += 1.0f;
            }
            int rgb = Mth.hsvToRgb(hue, 1.0f, 1.0f);
            line.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
        }
        return line;
    }

    private static List<String> getRuleParagraphs(String caster) {
        List<String> list = new ArrayList<>();
        list.add(caster + " opened a domain! Idle death gamble, as it's commonly known functions in the following way:");
        list.add("");
        list.add("- Visual abilities: Whenever \"" + caster + "\" does a parry, it count's as a visual ability");
        list.add("");
        list.add("- Scenarios: You will see a snow leopard png sliding or walking to a point in a metro station, if they successfully reach the other side or where they wanted to reach, " + caster + " gets their jackpot, there are 3 scenarios, each one with a different chance to win:");
        list.add("");
        list.add("> Bathroom: The snow leopards turns from a corner, and walks towards the bathroom, if they fail to reach it, they fall on their faces, 1/30 probability");
        list.add("");
        list.add("> Passage: Metro station passage, if they fail, they get launched backwards, 1/24 probability");
        list.add("");
        list.add("> Exit towards the surface: Title describes, if they fail, they fall backwards into the metro station, rolling down the stairs, 1/18 probability");
        list.add("");
        list.add("Between scenarios, theres a break where " + caster + " has to use 2 visual abilities to continue into the next scenario");
        list.add("");
        list.add("- If " + caster + " gets unlucky, each sceneario gives them a luck multiplier, the domain breaks at the 6th scenario.");
        list.add("");
        list.add("> 1st scenario has no luck boost");
        list.add("> 2nd scenario has a 1.5x luck boost");
        list.add("> 3rd scenario has a 2x luck boost");
        list.add("> 4th sceneario has a 3x luck boost");
        list.add("> 5th sceneario has a 10x luck boost");
        list.add("");
        list.add("Between each scenearios theres character cards, but I was too lazy to add them here.");
        return list;
    }
}
