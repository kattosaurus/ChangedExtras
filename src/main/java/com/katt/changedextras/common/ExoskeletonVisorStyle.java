package com.katt.changedextras.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ExoskeletonVisorStyle {
    public static final String PATTERN_TAG = "visorStyle";
    private static final String LEGACY_PATTERN_TAG = "visorPattern";
    public static final String SECONDARY_COLOR_TAG = "visorSecondaryColor";
    public static final String CUSTOM_COLORS_TAG = "visorCustomColors";

    private ExoskeletonVisorStyle() {
    }

    public enum Pattern {
        PATTERN1("hypnosis"),
        PATTERN2("default");

        private final String id;

        Pattern(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static Pattern byId(String value) {
            if (value == null) return null;
            String v = value.trim().toLowerCase();
            // Accept new ids
            for (Pattern pattern : values()) {
                if (pattern.id.equalsIgnoreCase(v)) return pattern;
            }
            // Backwards compatibility for old ids
            return switch (v) {
                case "pattern1" -> PATTERN1;
                case "pattern2" -> PATTERN2;
                case "hex" -> PATTERN2;
                default -> null;
            };
        }
    }

    public record Data(Pattern pattern, int primaryColor, int secondaryColor, boolean customColors) {
        public boolean hasCustomPattern1Colors() {
            return customColors && pattern == Pattern.PATTERN1;
        }

        public boolean hasCustomHexColor() {
            return customColors && primaryColor != ExoskeletonVisorColor.DEFAULT_COLOR;
        }
    }

    public static Data read(CompoundTag tag) {
        if (tag == null) {
            return defaultData();
        }

        Pattern explicitPattern = Pattern.byId(tag.getString(PATTERN_TAG));
        if (explicitPattern == null && tag.contains(LEGACY_PATTERN_TAG)) {
            explicitPattern = Pattern.byId(tag.getString(LEGACY_PATTERN_TAG));
        }
        int primaryColor = ExoskeletonVisorColor.readColor(tag, ExoskeletonVisorColor.DEFAULT_COLOR);
        int secondaryColor = readSecondaryColor(tag, ExoskeletonVisorColor.DEFAULT_COLOR);
        boolean customColors = tag.contains(CUSTOM_COLORS_TAG) ? tag.getBoolean(CUSTOM_COLORS_TAG) : hasLegacyHexColor(tag, primaryColor);

        Pattern pattern = explicitPattern;
        if (pattern == null) {
            pattern = Pattern.PATTERN2;
        }

        return new Data(pattern, primaryColor, secondaryColor, customColors);
    }

    public static Data read(ItemStack stack) {
        return read(stack.getTag());
    }

    public static void write(CompoundTag tag, Data data) {
        tag.putString(PATTERN_TAG, data.pattern().id());
        ExoskeletonVisorColor.writeColor(tag, data.primaryColor());
        tag.putString(SECONDARY_COLOR_TAG, ExoskeletonVisorColor.formatHex(data.secondaryColor()));
        tag.putBoolean(CUSTOM_COLORS_TAG, data.customColors());
    }

    public static void write(ItemStack stack, Data data) {
        write(stack.getOrCreateTag(), data);
    }

    public static Data defaultData() {
        return new Data(Pattern.PATTERN2, ExoskeletonVisorColor.DEFAULT_COLOR, ExoskeletonVisorColor.DEFAULT_COLOR, false);
    }

    private static int readSecondaryColor(CompoundTag tag, int fallback) {
        if (tag == null || !tag.contains(SECONDARY_COLOR_TAG)) {
            return fallback;
        }

        if (tag.contains(SECONDARY_COLOR_TAG, CompoundTag.TAG_INT)) {
            return ExoskeletonVisorColor.sanitize(tag.getInt(SECONDARY_COLOR_TAG));
        }

        if (tag.contains(SECONDARY_COLOR_TAG, CompoundTag.TAG_STRING)) {
            return ExoskeletonVisorColor.parseHex(tag.getString(SECONDARY_COLOR_TAG), fallback);
        }

        return fallback;
    }

    private static boolean hasLegacyHexColor(CompoundTag tag, int primaryColor) {
        return tag.contains(ExoskeletonVisorColor.TAG_NAME) && primaryColor != ExoskeletonVisorColor.DEFAULT_COLOR;
    }
}
