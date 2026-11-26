package us.kattosaur.changedextras.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/**
 * Changed 0.16.0 renders fully transfurred players with its own
 * TransfurredPlayerRenderer, which never calls PlayerRenderer.render and
 * therefore never fires RenderPlayerEvent.Pre. Registering this through
 * Changed's RendererOverride is the supported way to replace the player model.
 */
public class FeralCatRenderOverride extends PlayerRenderer {

    public FeralCatRenderOverride(EntityRendererProvider.Context context) {
        super(context, false);
    }

    @Override
    public void render(AbstractClientPlayer player, float yRot, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        FeralCatClientRenderer.renderFeralCat(player, poseStack, buffer, packedLight, partialTick);
    }
}
