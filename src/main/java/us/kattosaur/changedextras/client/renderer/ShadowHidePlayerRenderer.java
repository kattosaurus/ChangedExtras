package us.kattosaur.changedextras.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/**
 * Draws nothing for players hiding in shadows.
 *
 * Registered through Changed 0.16's RendererOverride at a lower priority than
 * both the feral cat renderer and Changed's transfurred renderer, so a hidden
 * void cat has no body, nameplate or outline on other clients.
 */
public class ShadowHidePlayerRenderer extends PlayerRenderer {
    public ShadowHidePlayerRenderer(EntityRendererProvider.Context context) {
        super(context, false);
    }

    @Override
    public void render(AbstractClientPlayer player, float yRot, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Intentionally empty: hidden players are completely invisible.
    }
}
