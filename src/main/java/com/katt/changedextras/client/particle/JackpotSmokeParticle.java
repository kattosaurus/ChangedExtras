package com.katt.changedextras.client.particle;

import com.katt.changedextras.client.ClientJackpotTracker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class JackpotSmokeParticle extends TextureSheetParticle {
    private static final float BASE_SIZE = 2.8f;
    private final Entity target;
    private final SpriteSet sprites;
    private final double xOffset;
    private final double zOffset;

    public JackpotSmokeParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed,
                                SpriteSet sprites, Entity target) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.target = target;
        this.sprites = sprites;

        // Random jitter for a chaotic aura
        this.xOffset = (level.random.nextFloat() - 0.5F) * 0.4F;
        this.zOffset = (level.random.nextFloat() - 0.5F) * 0.4F;

        this.lifetime = 22;
        float auraScale = target != null ? ClientJackpotTracker.getAuraScale(target.getUUID()) : 1.0f;
        this.quadSize = BASE_SIZE * auraScale;
        this.setSpriteFromAge(sprites);

        // Pure high-intensity Green/Cyan
        this.rCol = 0.6f;
        this.gCol = 1.0f;
        this.bCol = 0.8f;
        this.alpha = 1.0f;
        this.hasPhysics = false;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880; // Absolute Fullbright
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float originalSize = this.quadSize;
        float originalAlpha = this.alpha;

        // Layer 1: The Core (Very Bright, Small)
        this.alpha = 1.0f;
        this.quadSize = originalSize * 0.8f;
        super.render(buffer, camera, partialTicks);

        // Layer 2: The Inner Glow (Slightly larger)
        this.alpha = 0.6f;
        this.quadSize = originalSize * 1.1f;
        super.render(buffer, camera, partialTicks);

        // Layer 3: The Outer Bloom (Large and soft)
        this.alpha = 0.3f;
        this.quadSize = originalSize * 1.5f;
        super.render(buffer, camera, partialTicks);

        // Layer 4: The Massive Aura (Huge, very faint)
        this.alpha = 0.15f;
        this.quadSize = originalSize * 2.0f;
        super.render(buffer, camera, partialTicks);

        // Reset for next tick
        this.quadSize = originalSize;
        this.alpha = originalAlpha;
    }

    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive()) {
            this.remove();
            return;
        }

        this.xo = this.x; this.yo = this.y; this.zo = this.z;

        // Position the particle slightly behind the player's center
        // We use a small negative offset on the Z axis relative to the player
        double lookAngle = Math.toRadians(target.getViewYRot(1.0F));
        double backX = Math.sin(lookAngle) * 0.2D;
        double backZ = -Math.cos(lookAngle) * 0.2D;

        this.setPos(target.getX() + xOffset + backX,
                target.getY() + 1.1D,
                target.getZ() + zOffset + backZ);

        float auraScale = ClientJackpotTracker.getAuraScale(target.getUUID());
        this.quadSize = (BASE_SIZE * auraScale) + (0.15f * auraScale * (float)Math.sin(this.age * 0.5f));

        if (this.age++ >= this.lifetime) { this.age = 0; }
        this.setSpriteFromAge(this.sprites);
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return new ParticleRenderType() {
            @Override
            public void begin(@NotNull BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();

                // 1. Keep Additive Blending for that "Super Bright" look
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

                // 2. CRITICAL: Enable Depth Mask so the player's model blocks the particle
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(515); // 515 is GL_LEQUAL (Standard Minecraft Depth)

                ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT.begin(bufferBuilder, textureManager);
            }

            @Override
            public void end(@NotNull Tesselator tesselator) {
                ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT.end(tesselator);
                // Reset to defaults to avoid breaking other things in the world
                RenderSystem.disableBlend();
            }

            public String toString() {
                return "JACKPOT_BEHIND_GLOW";
            }
        };
    }
}
