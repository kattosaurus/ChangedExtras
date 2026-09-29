package com.katt.changedextras.client;

import com.katt.changedextras.common.FeralCatManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.RenderOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT)
public class FeralCatClientRenderer {

    /**
     * Extra scale applied on top of vanilla's own cat scale (CatRenderer already applies 0.8).
     * 1.0 = a normal vanilla adult cat. Raise it (e.g. 1.25) if you want it bigger.
     */
    private static final float FERAL_SCALE = 1.0F;

    // One dummy cat PER player (before, all feral players shared a single dummy)
    private static final Map<UUID, Cat> DUMMIES = new HashMap<>();

    public static void register() {
        RenderOverride.registerOverride(FeralCatRenderOverride::new);
    }

    public static boolean isFeral(Player player) {
        if (player == null) return false;
        return ClientFeralCatTracker.isFeral(player.getUUID()) || FeralCatManager.isFeral(player);
    }

    private static Cat getDummy(Player player, Level level) {
        Cat cat = DUMMIES.get(player.getUUID());
        if (cat == null || cat.level() != level) {
            cat = new Cat(EntityType.CAT, level);
            cat.setTame(true);
            CatVariant variant = BuiltInRegistries.CAT_VARIANT.get(CatVariant.WHITE);
            if (variant != null) {
                cat.setVariant(variant);
            }
            DUMMIES.put(player.getUUID(), cat);
        }
        return cat;
    }

    /**
     * Advances the dummy cat's walk animation exactly once per game tick, the same way
     * vanilla does for real entities (LivingEntity#updateWalkAnimation).
     *
     * The old code tried to copy the player's walk animation with
     * walkAnimation.position(player.walkAnimation.position()), but position(float) is a
     * getter, not a setter, so the dummy's leg position never advanced and the renderer's
     * partial-tick interpolation made the legs jitter at very high speed.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            DUMMIES.clear();
            return;
        }
        if (mc.isPaused()) return;

        Set<UUID> active = new HashSet<>();
        for (Player player : mc.level.players()) {
            if (!isFeral(player)) continue;

            active.add(player.getUUID());
            Cat cat = getDummy(player, mc.level);

            float dx = (float) (player.getX() - player.xo);
            float dz = (float) (player.getZ() - player.zo);
            float distance = Mth.sqrt(dx * dx + dz * dz);
            cat.walkAnimation.update(Math.min(distance * 4.0F, 1.0F), 0.4F);
        }
        DUMMIES.keySet().retainAll(active);
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player == null || !isFeral(player)) {
            return;
        }

        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        float partialTick = event.getPartialTick();

        renderFeralCat(player, poseStack, buffer, packedLight, partialTick);
    }

    @SuppressWarnings("unchecked")
    public static void renderFeralCat(Player player, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Cat dummyCat = getDummy(player, mc.level);

        // Sync dummy cat state from player
        // (walkAnimation is NOT copied here; it is advanced in onClientTick)
        dummyCat.setPos(player.getX(), player.getY(), player.getZ());
        dummyCat.xo = player.xo;
        dummyCat.yo = player.yo;
        dummyCat.zo = player.zo;
        dummyCat.xOld = player.xOld;
        dummyCat.yOld = player.yOld;
        dummyCat.zOld = player.zOld;
        dummyCat.yRotO = player.yRotO;
        dummyCat.setYRot(player.getYRot());
        dummyCat.xRotO = player.xRotO;
        dummyCat.setXRot(player.getXRot());
        dummyCat.yHeadRot = player.yHeadRot;
        dummyCat.yHeadRotO = player.yHeadRotO;
        dummyCat.yBodyRot = player.yBodyRot;
        dummyCat.yBodyRotO = player.yBodyRotO;
        dummyCat.tickCount = player.tickCount;
        dummyCat.setShiftKeyDown(player.isCrouching());
        dummyCat.setSprinting(player.isSprinting());
        dummyCat.setInSittingPose(player.isCrouching());
        dummyCat.setPose(player.getPose());
        dummyCat.setInvisible(player.isInvisible());

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super Cat> renderer = dispatcher.getRenderer(dummyCat);
        if (renderer != null) {
            poseStack.pushPose();
            poseStack.scale(FERAL_SCALE, FERAL_SCALE, FERAL_SCALE);
            renderer.render(dummyCat, player.getYRot(), partialTick, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isFeral(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isFeral(player)) {
            event.setCanceled(true);
        }
    }
}