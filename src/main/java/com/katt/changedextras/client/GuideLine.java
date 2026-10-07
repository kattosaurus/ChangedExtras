package com.katt.changedextras.client;

import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record GuideLine(Kind kind, FormattedCharSequence text, float scale, int indent) {
    public static final int LIST_INDENT = 8;
    public static final int QUOTE_INDENT = 6;

    public GuideLine {
        if (kind == Kind.BLANK || kind == Kind.SEPARATOR) {
            text = null;
        }
        if (indent < 0) {
            indent = 0;
        }
    }

    public static GuideLine text(FormattedCharSequence text, int indent) {
        return new GuideLine(Kind.TEXT, text, 1.0F, indent);
    }

    public static GuideLine list(FormattedCharSequence text) {
        return new GuideLine(Kind.TEXT, text, 1.0F, LIST_INDENT);
    }

    public static GuideLine quote(FormattedCharSequence text) {
        return new GuideLine(Kind.QUOTE, text, 1.0F, QUOTE_INDENT);
    }

    public static GuideLine heading(FormattedCharSequence text, float scale) {
        return new GuideLine(Kind.HEADING, text, scale, 0);
    }

    public static GuideLine blank() {
        return new GuideLine(Kind.BLANK, null, 1.0F, 0);
    }

    public static GuideLine separator() {
        return new GuideLine(Kind.SEPARATOR, null, 1.0F, 0);
    }

    public enum Kind {
        TEXT,
        HEADING,
        QUOTE,
        SEPARATOR,
        BLANK
    }
}
