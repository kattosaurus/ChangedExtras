package com.katt.changedextras.common;

import com.katt.changedextras.client.FeralCatClientRenderer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "changedextras")
@SuppressWarnings("removal")
public class FeralCatDimensionsHandler {

    // Vanilla cat hitbox is 0.6 x 0.7
    private static final EntityDimensions STANDING = EntityDimensions.scalable(0.6F, 0.7F);
    private static final EntityDimensions CROUCHING = EntityDimensions.scalable(0.6F, 0.5F);

    private static boolean isFeral(Player player) {
        if (player.level().isClientSide) {
            // Client uses the synced tracker (via FeralCatClientRenderer.isFeral)
            Boolean result = DistExecutor.unsafeCallWhenOn(Dist.CLIENT,
                    () -> () -> FeralCatClientRenderer.isFeral(player));
            return Boolean.TRUE.equals(result);
        }
        return FeralCatManager.isFeral(player);
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof Player player) || !isFeral(player)) {
            return;
        }

        Pose pose = event.getPose();
        if (pose == Pose.SLEEPING) {
            return; // keep vanilla sleeping size
        }

        EntityDimensions dims = pose == Pose.CROUCHING ? CROUCHING : STANDING;
        event.setNewSize(dims);
        event.setNewEyeHeight(dims.height * 0.85F);
    }
}
