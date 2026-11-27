package us.kattosaur.changedextras.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.kattosaur.changedextras.common.shadow.ShadowHideManager;

@Mixin(Entity.class)
public class EntityMixin {
    /** Hidden cats are not raytrace targets, so hitting their block hits them instead. */
    @Inject(method = "isPickable()Z", at = @At("HEAD"), cancellable = true)
    private void changedextras$hiddenPlayerIsNotPickable(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (ShadowHideManager.isHidden(entity)) {
            cir.setReturnValue(false);
        }
    }

    /** Hiding in a shadow never allows sprinting, even if sprint input is held. */
    @Inject(method = "isSprinting()Z", at = @At("HEAD"), cancellable = true)
    private void changedextras$hiddenPlayerCannotSprint(CallbackInfoReturnable<Boolean> cir) {
        if (ShadowHideManager.isHidden((Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
