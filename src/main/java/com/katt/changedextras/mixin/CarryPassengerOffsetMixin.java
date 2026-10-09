package com.katt.changedextras.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class CarryPassengerOffsetMixin {

    @Inject(method = "positionRider", at = @At("HEAD"), cancellable = true)
    private void changedextras$positionCarryPassenger(Entity passenger, CallbackInfo ci) {
        Entity carrier = (Entity) (Object) this;

        if (!(carrier instanceof Player player)) return;
        if (passenger == null) return;
        if (player.getFirstPassenger() != passenger) return;

        double x = player.getX() + player.getLookAngle().x * 0.6D;
        double y = player.getY() + player.getEyeHeight() + 0.25D;
        double z = player.getZ() + player.getLookAngle().z * 0.6D;

        passenger.setPos(x, y, z);
        ci.cancel();
    }
}