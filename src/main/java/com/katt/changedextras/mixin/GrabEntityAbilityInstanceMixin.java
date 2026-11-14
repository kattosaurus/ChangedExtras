package com.katt.changedextras.mixin;

import com.katt.changedextras.Config;
import com.katt.changedextras.ability.GrabReachAccess;
import com.katt.changedextras.common.combat.AttackBoost;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Turns Changed's hold-to-grab into press-to-reach: a press plays the
 * {@code Attack2} strike as the reach, and the actual grab is attempted after the
 * reach finishes. If the player lets go early the reach still completes, and
 * holding the key after the grab keeps the original hold-to-release/unsuit
 * behavior. Landing the grab also applies the {@code Attack2} forward boost.
 */
@Mixin(GrabEntityAbilityInstance.class)
public abstract class GrabEntityAbilityInstanceMixin implements GrabReachAccess {
    @Unique
    private int changedextras$grabReachTicks = 0;

    @Unique
    private boolean changedextras$grabBoostPending = false;

    @Shadow(remap = false)
    int instructionTicks;

    @Override
    public int changedextras$getGrabReachTicks() {
        return this.changedextras$grabReachTicks;
    }

    @Override
    public void changedextras$setGrabReachTicks(int ticks) {
        this.changedextras$grabReachTicks = ticks;
    }

    @Inject(method = "startUsing", at = @At("HEAD"), remap = false, require = 0)
    private void changedextras$startGrabReach(CallbackInfo ci) {
        GrabEntityAbilityInstance self = (GrabEntityAbilityInstance) (Object) this;
        if (Config.latexReworkedCombat && self.grabbedEntity == null && self.entity.getLevel().isClientSide) {
            this.changedextras$grabReachTicks = GrabReachAccess.REACH_TICKS;
        }
    }

    /**
     * The original tick() grabs an entity immediately as soon as the key is held.
     * Suppress that path while nothing is grabbed; the reach timer in tickIdle()
     * decides when the grab actually happens.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void changedextras$suppressImmediateGrab(CallbackInfo ci) {
        GrabEntityAbilityInstance self = (GrabEntityAbilityInstance) (Object) this;
        if (Config.latexReworkedCombat && self.grabbedEntity == null) {
            ci.cancel();
        }
    }

    @Inject(method = "tickIdle", at = @At("HEAD"), remap = false, require = 0)
    private void changedextras$tickGrabReach(CallbackInfo ci) {
        GrabEntityAbilityInstance self = (GrabEntityAbilityInstance) (Object) this;

        if (!Config.latexReworkedCombat) {
            this.changedextras$grabReachTicks = 0;
            return;
        }

        if (self.grabbedEntity != null) {
            this.changedextras$grabReachTicks = 0;
            return;
        }

        if (this.changedextras$grabReachTicks <= 0 || !self.entity.getLevel().isClientSide) {
            return;
        }

        if (--this.changedextras$grabReachTicks == 0) {
            this.changedextras$finishGrab(self);
        }
    }

    @Unique
    private void changedextras$finishGrab(GrabEntityAbilityInstance self) {
        LivingEntity target = self.getHoveredEntity(self.entity);
        if (target == null) {
            return;
        }

        if (target instanceof Player && !(Boolean) Changed.config.server.isGrabEnabled.get()) {
            return;
        }

        if (!self.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(target.getBoundingBox())) {
            return;
        }

        if (!self.grabEntity(target)) {
            return;
        }

        if (self.entity.getEntity() instanceof Player player) {
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab(player, target));
        }

        this.instructionTicks = 180;
    }

    @Inject(method = "grabEntity", at = @At("HEAD"), remap = false, require = 0)
    private void changedextras$markGrabBoost(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        GrabEntityAbilityInstance self = (GrabEntityAbilityInstance) (Object) this;
        this.changedextras$grabBoostPending = Config.latexReworkedCombat && self.grabbedEntity != target;
    }

    /**
     * The grab uses the same {@code Attack2} strike as the combo finisher, so a
     * successful new grab also gets its forward boost. Targeting the same entity
     * again (hold ticks/suiting) does not boost twice.
     */
    @Inject(method = "grabEntity", at = @At("RETURN"), remap = false, require = 0)
    private void changedextras$applyGrabBoost(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean pending = this.changedextras$grabBoostPending;
        this.changedextras$grabBoostPending = false;

        if (!pending || !Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }

        GrabEntityAbilityInstance self = (GrabEntityAbilityInstance) (Object) this;
        AttackBoost.applyForwardBoost(self.entity.getEntity());
    }
}
