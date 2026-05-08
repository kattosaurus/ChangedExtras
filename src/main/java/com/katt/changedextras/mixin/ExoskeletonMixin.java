package com.katt.changedextras.mixin;

import com.katt.changedextras.common.ExoskeletonVisorColor;
import com.katt.changedextras.common.ExoskeletonVisorColorHolder;
import com.katt.changedextras.common.ExoskeletonVisorStyle;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Exoskeleton.class)
public abstract class ExoskeletonMixin implements ExoskeletonVisorColorHolder {
    @Unique
    private static final EntityDataAccessor<Integer> CHANGEDEXTRAS$VISOR_COLOR =
            SynchedEntityData.defineId(Exoskeleton.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> CHANGEDEXTRAS$VISOR_SECONDARY_COLOR =
            SynchedEntityData.defineId(Exoskeleton.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> CHANGEDEXTRAS$VISOR_PATTERN =
            SynchedEntityData.defineId(Exoskeleton.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Boolean> CHANGEDEXTRAS$VISOR_CUSTOM_COLORS =
            SynchedEntityData.defineId(Exoskeleton.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final int CHANGEDEXTRAS$DEFAULT_VISOR_COLOR = ExoskeletonVisorColor.DEFAULT_COLOR;

    @Inject(method = "defineSynchedData", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$defineVisorColor(CallbackInfo ci) {
        ((Exoskeleton)(Object)this).getEntityData().define(CHANGEDEXTRAS$VISOR_COLOR, CHANGEDEXTRAS$DEFAULT_VISOR_COLOR);
        ((Exoskeleton)(Object)this).getEntityData().define(CHANGEDEXTRAS$VISOR_SECONDARY_COLOR, CHANGEDEXTRAS$DEFAULT_VISOR_COLOR);
        ((Exoskeleton)(Object)this).getEntityData().define(CHANGEDEXTRAS$VISOR_PATTERN, ExoskeletonVisorStyle.Pattern.PATTERN2.ordinal());
        ((Exoskeleton)(Object)this).getEntityData().define(CHANGEDEXTRAS$VISOR_CUSTOM_COLORS, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$saveVisorColor(CompoundTag tag, CallbackInfo ci) {
        ExoskeletonVisorStyle.write(tag, new ExoskeletonVisorStyle.Data(
                changedextras$getVisorPattern(),
                changedextras$getVisorColor(),
                changedextras$getVisorSecondaryColor(),
                changedextras$hasCustomVisorColors()
        ));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$readVisorColor(CompoundTag tag, CallbackInfo ci) {
        ExoskeletonVisorStyle.Data data = ExoskeletonVisorStyle.read(tag);
        changedextras$setVisorColor(data.primaryColor());
        changedextras$setVisorSecondaryColor(data.secondaryColor());
        changedextras$setVisorPattern(data.pattern());
        changedextras$setCustomVisorColors(data.customColors());
    }

    @Inject(method = "getDropItem", at = @At("RETURN"), remap = false, require = 0)
    private void changedextras$writeDropItemVisorColor(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty()) {
            ExoskeletonVisorStyle.write(stack, new ExoskeletonVisorStyle.Data(
                    changedextras$getVisorPattern(),
                    changedextras$getVisorColor(),
                    changedextras$getVisorSecondaryColor(),
                    changedextras$hasCustomVisorColors()
            ));
        }
    }

    @Inject(method = "loadFromItemStack", at = @At("TAIL"), remap = false, require = 0)
    private void changedextras$readItemVisorColor(ItemStack stack, CallbackInfo ci) {
        ExoskeletonVisorStyle.Data data = ExoskeletonVisorStyle.read(stack);
        changedextras$setVisorColor(data.primaryColor());
        changedextras$setVisorSecondaryColor(data.secondaryColor());
        changedextras$setVisorPattern(data.pattern());
        changedextras$setCustomVisorColors(data.customColors());
    }

    @Override
    public int changedextras$getVisorColor() {
        return ExoskeletonVisorColor.sanitize(((Exoskeleton)(Object)this).getEntityData().get(CHANGEDEXTRAS$VISOR_COLOR));
    }

    @Override
    public void changedextras$setVisorColor(int color) {
        ((Exoskeleton)(Object)this).getEntityData().set(CHANGEDEXTRAS$VISOR_COLOR, ExoskeletonVisorColor.sanitize(color));
    }

    @Override
    public int changedextras$getVisorSecondaryColor() {
        return ExoskeletonVisorColor.sanitize(((Exoskeleton)(Object)this).getEntityData().get(CHANGEDEXTRAS$VISOR_SECONDARY_COLOR));
    }

    @Override
    public void changedextras$setVisorSecondaryColor(int color) {
        ((Exoskeleton)(Object)this).getEntityData().set(CHANGEDEXTRAS$VISOR_SECONDARY_COLOR, ExoskeletonVisorColor.sanitize(color));
    }

    @Override
    public ExoskeletonVisorStyle.Pattern changedextras$getVisorPattern() {
        int ordinal = ((Exoskeleton)(Object)this).getEntityData().get(CHANGEDEXTRAS$VISOR_PATTERN);
        ExoskeletonVisorStyle.Pattern[] values = ExoskeletonVisorStyle.Pattern.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ExoskeletonVisorStyle.Pattern.PATTERN2;
    }

    @Override
    public void changedextras$setVisorPattern(ExoskeletonVisorStyle.Pattern pattern) {
        ((Exoskeleton)(Object)this).getEntityData().set(CHANGEDEXTRAS$VISOR_PATTERN, pattern.ordinal());
    }

    @Override
    public boolean changedextras$hasCustomVisorColors() {
        return ((Exoskeleton)(Object)this).getEntityData().get(CHANGEDEXTRAS$VISOR_CUSTOM_COLORS);
    }

    @Override
    public void changedextras$setCustomVisorColors(boolean enabled) {
        ((Exoskeleton)(Object)this).getEntityData().set(CHANGEDEXTRAS$VISOR_CUSTOM_COLORS, enabled);
    }
}
