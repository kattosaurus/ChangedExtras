package com.katt.changedextras.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class ExoskeletonVisorColor {
    public static final String TAG_NAME = "visorColor";
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    private ExoskeletonVisorColor() {
    }

    public static int sanitize(int color) {
        return Mth.clamp(color, 0x000000, 0xFFFFFF);
    }

    public static int readColor(CompoundTag tag, int fallback) {
        if (tag == null || !tag.contains(TAG_NAME)) {
            return fallback;
        }

        if (tag.contains(TAG_NAME, CompoundTag.TAG_INT)) {
            return sanitize(tag.getInt(TAG_NAME));
        }

        if (tag.contains(TAG_NAME, CompoundTag.TAG_STRING)) {
            return parseHex(tag.getString(TAG_NAME), fallback);
        }

        return fallback;
    }

    public static int readColor(ItemStack stack, int fallback) {
        return readColor(stack.getTag(), fallback);
    }

    public static void writeColor(CompoundTag tag, int color) {
        tag.putString(TAG_NAME, formatHex(color));
    }

    public static void writeColor(ItemStack stack, int color) {
        writeColor(stack.getOrCreateTag(), color);
    }

    public static String formatHex(int color) {
        return String.format("#%06X", sanitize(color));
    }

    public static int parseHex(String value, int fallback) {
        if (value == null) {
            return fallback;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return fallback;
        }

        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        } else if (normalized.startsWith("0x") || normalized.startsWith("0X")) {
            normalized = normalized.substring(2);
        }

        if (normalized.length() != 6) {
            return fallback;
        }

        try {
            return sanitize(Integer.parseInt(normalized, 16));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
