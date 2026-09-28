package com.katt.changedextras.client;

import com.katt.changedextras.common.FeralCatManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.RenderOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "changedextras", value = Dist.CLIENT)
public class FeralCatClientRenderer {
    private static Cat dummyCat = null;

    public static void register() {
        RenderOverride.registerOverride(FeralCatRenderOverride::new);
    }

    public static boolean isFeral(Player player) {
        if (player == null) return false;
        return ClientFeralCatTracker.isFeral(player.getUUID()) || FeralCatManager.isFeral(player);
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

        if (dummyCat == null || dummyCat.level() != mc.level) {
            dummyCat = new Cat(EntityType.CAT, mc.level);
            dummyCat.setTame(true);
            CatVariant variant = BuiltInRegistries.CAT_VARIANT.get(CatVariant.WHITE);
            if (variant != null) {
                dummyCat.setVariant(variant);
            }
        }

        // Sync dummy cat state from player
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
        dummyCat.walkAnimation.setSpeed(player.walkAnimation.speed());
        dummyCat.walkAnimation.position(player.walkAnimation.position());
        dummyCat.setShiftKeyDown(player.isCrouching());
        dummyCat.setSprinting(player.isSprinting());
        dummyCat.setInSittingPose(player.isCrouching());
        dummyCat.setPose(player.getPose());
        dummyCat.setInvisible(player.isInvisible());

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super Cat> renderer = dispatcher.getRenderer(dummyCat);
        if (renderer != null) {
            renderer.render(dummyCat, player.getYRot(), partialTick, poseStack, buffer, packedLight);
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
