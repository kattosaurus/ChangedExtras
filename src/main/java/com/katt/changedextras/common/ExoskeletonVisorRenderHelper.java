package com.katt.changedextras.common;

import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ExoskeletonVisorRenderHelper {
    public static final ResourceLocation ACTIVE_COLORLESS = Changed.modResource("textures/models/exoskeleton_visor/active-colorless.png");
    public static final ResourceLocation HYPNO_LAYER1 = Changed.modResource("textures/models/exoskeleton_visor/hypno-colorless-layer1.png");
    public static final ResourceLocation HYPNO_LAYER2 = Changed.modResource("textures/models/exoskeleton_visor/hypno-colorless-layer2.png");

    private ExoskeletonVisorRenderHelper() {
    }

    /**
     * Read visor style data for a worn accessory. Prefer the wearer's persistent data first
     * (SyncVisorPacket / server tick write there — stack NBT can lag behind on clients), then
     * accessory-slot copies, then the ItemStack.
     */
    private static ExoskeletonVisorStyle.Data readWornData(LivingEntity wearer, ItemStack stack) {
        // Helper to check for any of the visor tags
        java.util.function.Predicate<net.minecraft.nbt.CompoundTag> hasTags = tag -> tag != null && tag.contains(ExoskeletonVisorStyle.PATTERN_TAG);

        if (wearer != null) {
            net.minecraft.nbt.CompoundTag persistent = wearer.getPersistentData();
            if (hasTags.test(persistent)) return ExoskeletonVisorStyle.read(persistent);

            if (persistent.contains("ForgeData", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                net.minecraft.nbt.CompoundTag forge = persistent.getCompound("ForgeData");
                if (hasTags.test(forge)) return ExoskeletonVisorStyle.read(forge);
            }

            if (persistent.contains("ChangedAccessorySlots", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                net.minecraft.nbt.CompoundTag slots = persistent.getCompound("ChangedAccessorySlots");
                for (String key : slots.getAllKeys()) {
                    net.minecraft.nbt.CompoundTag slotCompound = slots.getCompound(key);
                    if (slotCompound.contains("tag", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                        net.minecraft.nbt.CompoundTag slotTag = slotCompound.getCompound("tag");
                        if (hasTags.test(slotTag)) return ExoskeletonVisorStyle.read(slotTag);
                    }
                    // some systems store the NBT directly under the slot compound
                    if (hasTags.test(slotCompound)) return ExoskeletonVisorStyle.read(slotCompound);
                }
            }
        }

        if (stack != null && stack.getTag() != null) {
            net.minecraft.nbt.CompoundTag tag = stack.getTag();
            if (hasTags.test(tag)) return ExoskeletonVisorStyle.read(tag);
        }

        // fallback to stack read (may return default)
        return ExoskeletonVisorStyle.read(stack);
    }

    public static ResourceLocation resolveWornTexture(LivingEntity wearer, ItemStack stack, ResourceLocation originalTexture) {
        ExoskeletonVisorStyle.Data data = readWornData(wearer, stack);
        return switch (data.pattern()) {
            case PATTERN1 -> getAnimatedHypnoTexture(wearer != null ? wearer.tickCount : 0);
            case PATTERN2 -> originalTexture;
        };
    }

    public static ResourceLocation resolveEntityTexture(Exoskeleton exoskeleton, ResourceLocation originalTexture) {
        ExoskeletonVisorStyle.Data data = getEntityData(exoskeleton);
        return switch (data.pattern()) {
            case PATTERN1 -> getAnimatedHypnoTexture(exoskeleton.tickCount);
            case PATTERN2 -> originalTexture;
        };
    }

    public static int getPrimaryTint(ItemStack stack) {
        ExoskeletonVisorStyle.Data data = ExoskeletonVisorStyle.read(stack);
        if (data.customColors() && data.hasCustomHexColor()) {
            return data.primaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    public static int getPrimaryTint(Exoskeleton exoskeleton) {
        ExoskeletonVisorStyle.Data data = getEntityData(exoskeleton);
        if (data.customColors() && data.hasCustomHexColor()) {
            return data.primaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    /**
     * Get primary tint for a worn accessory, with fallback to wearer persistent data.
     */
    public static int getPrimaryTint(LivingEntity wearer, ItemStack stack) {
        ExoskeletonVisorStyle.Data data = readWornData(wearer, stack);
        if (data.customColors() && data.hasCustomHexColor()) {
            return data.primaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    public static boolean shouldRenderSecondaryLayer(ItemStack stack) {
        return ExoskeletonVisorStyle.read(stack).pattern() == ExoskeletonVisorStyle.Pattern.PATTERN1;
    }

    public static boolean shouldRenderSecondaryLayer(Exoskeleton exoskeleton) {
        return getEntityData(exoskeleton).pattern() == ExoskeletonVisorStyle.Pattern.PATTERN1;
    }

    /**
     * Should render secondary layer for worn accessory, with fallback to wearer persistent data.
     */
    public static boolean shouldRenderSecondaryLayer(LivingEntity wearer, ItemStack stack) {
        return readWornData(wearer, stack).pattern() == ExoskeletonVisorStyle.Pattern.PATTERN1;
    }

    public static int getSecondaryTint(ItemStack stack) {
        ExoskeletonVisorStyle.Data data = ExoskeletonVisorStyle.read(stack);
        if (data.customColors() && data.hasCustomPattern1Colors()) {
            return data.secondaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    public static int getSecondaryTint(Exoskeleton exoskeleton) {
        ExoskeletonVisorStyle.Data data = getEntityData(exoskeleton);
        if (data.customColors() && data.hasCustomPattern1Colors()) {
            return data.secondaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    /**
     * Get secondary tint for worn accessory, with fallback to wearer persistent data.
     */
    public static int getSecondaryTint(LivingEntity wearer, ItemStack stack) {
        ExoskeletonVisorStyle.Data data = readWornData(wearer, stack);
        if (data.customColors() && data.hasCustomPattern1Colors()) {
            return data.secondaryColor();
        }
        return ExoskeletonVisorColor.DEFAULT_COLOR;
    }

    private static ResourceLocation getAnimatedHypnoTexture(int tickCount) {
        return tickCount % 12 < 6 ? HYPNO_LAYER1 : HYPNO_LAYER2;
    }

    private static ExoskeletonVisorStyle.Data getEntityData(Exoskeleton exoskeleton) {
        ExoskeletonVisorColorHolder holder = (ExoskeletonVisorColorHolder) exoskeleton;
        return new ExoskeletonVisorStyle.Data(
                holder.changedextras$getVisorPattern(),
                holder.changedextras$getVisorColor(),
                holder.changedextras$getVisorSecondaryColor(),
                holder.changedextras$hasCustomVisorColors()
        );
    }
}