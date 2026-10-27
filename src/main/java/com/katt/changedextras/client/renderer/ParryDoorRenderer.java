package com.katt.changedextras.client.renderer;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.client.tracker.ClientParryTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParryDoorRenderer {
    private static final ResourceLocation DOOR_BOTTOM =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/iron_door_bottom.png");
    private static final ResourceLocation DOOR_TOP =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/iron_door_top.png");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        float partialTick = event.getPartialTick();

        for (UUID uuid : ClientParryTracker.getParryingEntities()) {
            Entity entity = findEntity(level, uuid);
            if (entity == null || !entity.isAlive()) continue;

            double ex = Mth.lerp(partialTick, entity.xo, entity.getX());
            double ey = Mth.lerp(partialTick, entity.yo, entity.getY());
            double ez = Mth.lerp(partialTick, entity.zo, entity.getZ());
            float bodyYaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());

            float yawRad = bodyYaw * (float)(Math.PI / 180.0);
            double forwardX = -Math.sin(yawRad);
            double forwardZ = Math.cos(yawRad);
            double rightX = Math.cos(yawRad);
            double rightZ = Math.sin(yawRad);

            // Distance in front of caster
            double distInFront = 1.15;
            double baseX = ex + forwardX * distInFront;
            double baseY = ey;
            double baseZ = ez + forwardZ * distInFront;

            // Smooth alpha/opacity fade-in transition over the first few ticks (5 ticks = 250ms)
            int ticks = ClientParryTracker.getParryTicks(uuid);
            float alpha = Math.min(1.0f, (ticks + partialTick) / 5.0f);

            int packedLight = mc.getEntityRenderDispatcher().getPackedLightCoords(entity, partialTick);

            // Spawn 2 Iron Door models directly in front of the caster (side-by-side)
            double doorSpacing = 0.48;
            // Left door
            renderDoor(poseStack, bufferSource,
                    baseX - rightX * doorSpacing - camPos.x,
                    baseY - camPos.y,
                    baseZ - rightZ * doorSpacing - camPos.z,
                    bodyYaw, alpha, packedLight);

            // Right door
            renderDoor(poseStack, bufferSource,
                    baseX + rightX * doorSpacing - camPos.x,
                    baseY - camPos.y,
                    baseZ + rightZ * doorSpacing - camPos.z,
                    bodyYaw, alpha, packedLight);
        }

        bufferSource.endBatch();
    }

    private static void renderDoor(PoseStack poseStack, MultiBufferSource bufferSource,
                                   double rx, double ry, double rz,
                                   float yaw, float alpha, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw + 180.0f));

        // Scale transform: Modify their local transform properties. Scale X-axis and Y-axis by exactly 1.5.
        poseStack.scale(1.5f, 1.5f, 1.0f);

        // Center the 1.0 wide door horizontally
        poseStack.translate(-0.5, 0.0, 0.0);

        Matrix4f matrix = poseStack.last().pose();

        // Render bottom half
        VertexConsumer bottomConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(DOOR_BOTTOM));
        renderQuad(matrix, bottomConsumer, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, alpha, packedLight);
        renderQuad(matrix, bottomConsumer, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, alpha, packedLight); // Back face

        // Render top half
        VertexConsumer topConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(DOOR_TOP));
        renderQuad(matrix, topConsumer, 0.0f, 1.0f, 1.0f, 2.0f, 0.0f, alpha, packedLight);
        renderQuad(matrix, topConsumer, 1.0f, 1.0f, 0.0f, 2.0f, 0.0f, alpha, packedLight); // Back face

        poseStack.popPose();
    }

    private static void renderQuad(Matrix4f matrix, VertexConsumer consumer,
                                   float minX, float minY, float maxX, float maxY, float z,
                                   float alpha, int light) {
        consumer.vertex(matrix, minX, minY, z).color(1.0f, 1.0f, 1.0f, alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
        consumer.vertex(matrix, maxX, minY, z).color(1.0f, 1.0f, 1.0f, alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
        consumer.vertex(matrix, maxX, maxY, z).color(1.0f, 1.0f, 1.0f, alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
        consumer.vertex(matrix, minX, maxY, z).color(1.0f, 1.0f, 1.0f, alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
    }

    private static Entity findEntity(ClientLevel level, UUID uuid) {
        for (Entity e : level.entitiesForRendering()) {
            if (e.getUUID().equals(uuid)) return e;
        }
        return null;
    }
}
