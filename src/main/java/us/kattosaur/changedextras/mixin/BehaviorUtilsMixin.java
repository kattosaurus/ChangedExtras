package us.kattosaur.changedextras.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.kattosaur.changedextras.common.shadow.ShadowHideManager;

@Mixin(BehaviorUtils.class)
public class BehaviorUtilsMixin {
    /** The second argument is the entity being checked for visibility. */
    @Inject(method = "canSee(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void changedextras$hiddenPlayersAreNeverVisible(LivingEntity observer,
                                                                   LivingEntity target,
                                                                   CallbackInfoReturnable<Boolean> cir) {
        if (ShadowHideManager.isHidden(target)) {
            cir.setReturnValue(false);
        }
    }
}
