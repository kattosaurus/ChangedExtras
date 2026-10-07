package com.katt.changedextras.client;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OnlyIn(Dist.CLIENT)
public final class ChangedExtrasGuideClientAccess {
    private static final String GUIDE_RESOURCE = "/assets/changedextras/guide.md";
    private static final Pattern MARKDOWN_IMAGE = Pattern.compile("!\\[[^\\]]*]\\([^)]*\\)");
    private static final Pattern HTML_IMAGE = Pattern.compile("(?i)<img\\b[^>]*>");
    private static final Pattern BLOCKQUOTE = Pattern.compile("^>\\s?(.*)$");
    private static final Pattern HEADING = Pattern.compile("^(#{1,6})\\s+(.+)$");
    private static final Pattern LIST_ITEM = Pattern.compile("^[-*]\\s+(.+)$");
    private static final Pattern SEPARATOR = Pattern.compile("^(\\*|-|_)(?:\\s*\\1){2,}$");
    private static final int MAX_PAGE_HEIGHT = ChangedExtrasGuideScreen.TEXT_HEIGHT;
    private static final String BULLET = "\u2022 ";

    private ChangedExtrasGuideClientAccess() {
    }

    public static void openGuide() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ChangedExtrasGuideScreen(createBookPages(minecraft.font)));
    }

    private static List<List<GuideLine>> createBookPages(Font font) {
        List<Block> blocks = parseBlocks(sanitizeMarkdown(readGuide()));
        if (blocks.stream().allMatch(block -> block.type() == BlockType.BLANK)) {
            blocks = List.of(
                    new Block(BlockType.HEADING, 1, "Changed Extras Guide"),
                    new Block(BlockType.BLANK, 0, ""),
                    new Block(BlockType.TEXT, 0, "No guide content has been written yet.")
            );
        }
        return paginate(blocks, font);
    }

    private static String readGuide() {
        try (InputStream stream = ChangedExtrasGuideClientAccess.class.getResourceAsStream(GUIDE_RESOURCE)) {
            if (stream == null) {
                return "# Changed Extras Guide\n\nGuide content is missing.";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
                return builder.toString();
            }
        } catch (IOException exception) {
            ChangedExtras.LOGGER.warn("Failed to load Changed Extras guide", exception);
            return "# Changed Extras Guide\n\nGuide content failed to load.";
        }
    }

    private static String sanitizeMarkdown(String markdown) {
        StringBuilder builder = new StringBuilder();
        for (String line : markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            String text = HTML_IMAGE.matcher(MARKDOWN_IMAGE.matcher(line).replaceAll("")).replaceAll("");
            builder.append(text).append('\n');
        }
        return builder.toString().trim();
    }

    private static List<Block> parseBlocks(String markdown) {
        List<Block> blocks = new ArrayList<>();
        for (String line : markdown.split("\n", -1)) {
            if (line.isBlank()) {
                blocks.add(new Block(BlockType.BLANK, 0, ""));
                continue;
            }

            String trimmed = line.trim();
            if (SEPARATOR.matcher(trimmed).matches()) {
                blocks.add(new Block(BlockType.SEPARATOR, 0, ""));
                continue;
            }

            Matcher heading = HEADING.matcher(trimmed);
            if (heading.matches()) {
                blocks.add(new Block(BlockType.HEADING, heading.group(1).length(), heading.group(2).trim()));
                continue;
            }

            Matcher quote = BLOCKQUOTE.matcher(trimmed);
            if (quote.matches()) {
                blocks.add(new Block(BlockType.QUOTE, 0, quote.group(1)));
                continue;
            }

            Matcher listItem = LIST_ITEM.matcher(trimmed);
            if (listItem.matches()) {
                blocks.add(new Block(BlockType.LIST_ITEM, 0, listItem.group(1)));
                continue;
            }

            blocks.add(new Block(BlockType.TEXT, 0, line));
        }
        return blocks;
    }

    static List<List<GuideLine>> paginate(List<Block> blocks, Font font) {
        List<List<GuideLine>> pages = new ArrayList<>();
        List<GuideLine> page = new ArrayList<>();
        int height = 0;
        int lineHeight = font.lineHeight;

        for (Block block : blocks) {
            switch (block.type()) {
                case BLANK -> {
                    if (!page.isEmpty() && height + lineHeight <= MAX_PAGE_HEIGHT) {
                        page.add(GuideLine.blank());
                        height += lineHeight;
                    }
                }
                case TEXT, LIST_ITEM -> {
                    boolean listItem = block.type() == BlockType.LIST_ITEM;
                    int indent = listItem ? GuideLine.LIST_INDENT : 0;
                    Component content = listItem
                            ? Component.literal(BULLET).append(parseInline(block.text()))
                            : parseInline(block.text());
                    for (FormattedCharSequence line : font.split(content, ChangedExtrasGuideScreen.TEXT_WIDTH - indent)) {
                        if (height + lineHeight > MAX_PAGE_HEIGHT) {
                            flushPage(pages, page);
                            height = 0;
                        }
                        page.add(GuideLine.text(line, indent));
                        height += lineHeight;
                    }
                }
                case QUOTE -> {
                    MutableComponent quote = Component.empty().append(parseInline(block.text()));
                    quote.withStyle(Style.EMPTY.withItalic(true));
                    for (FormattedCharSequence line : font.split(quote, ChangedExtrasGuideScreen.TEXT_WIDTH - GuideLine.QUOTE_INDENT)) {
                        if (height + lineHeight > MAX_PAGE_HEIGHT) {
                            flushPage(pages, page);
                            height = 0;
                        }
                        page.add(GuideLine.quote(line));
                        height += lineHeight;
                    }
                }
                case HEADING -> {
                    float scale = headingScale(block.level());
                    int advance = Math.max(lineHeight, Math.round(lineHeight * scale));
                    MutableComponent heading = Component.empty().append(parseInline(block.text()));
                    heading.withStyle(Style.EMPTY.withBold(true).withItalic(block.level() >= 4));

                    if (height + advance > MAX_PAGE_HEIGHT) {
                        flushPage(pages, page);
                        height = 0;
                    }

                    int wrapWidth = (int) (ChangedExtrasGuideScreen.TEXT_WIDTH / scale);
                    for (FormattedCharSequence line : font.split(heading, wrapWidth)) {
                        if (height + advance > MAX_PAGE_HEIGHT) {
                            flushPage(pages, page);
                            height = 0;
                        }
                        page.add(GuideLine.heading(line, scale));
                        height += advance;
                    }
                }
                case SEPARATOR -> {
                    if (height + lineHeight > MAX_PAGE_HEIGHT) {
                        flushPage(pages, page);
                        height = 0;
                    }
                    page.add(GuideLine.separator());
                    height += lineHeight;
                }
            }
        }

        flushPage(pages, page);
        return pages;
    }

    private static void flushPage(List<List<GuideLine>> pages, List<GuideLine> page) {
        while (!page.isEmpty() && page.get(page.size() - 1).kind() == GuideLine.Kind.BLANK) {
            page.remove(page.size() - 1);
        }
        if (!page.isEmpty()) {
            pages.add(List.copyOf(page));
            page.clear();
        }
    }

    private static float headingScale(int level) {
        if (level == 1) {
            return 1.5F;
        }
        if (level == 2) {
            return 1.25F;
        }
        if (level == 3) {
            return 1.1F;
        }
        return 1.0F;
    }

    static Component parseInline(String text) {
        MutableComponent component = Component.empty();
        StringBuilder segment = new StringBuilder();
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strikethrough = false;
        boolean code = false;

        for (int i = 0; i < text.length(); i++) {
            if (matches(text, i, "***")) {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                bold = !bold;
                italic = !italic;
                i += 2;
            } else if (matches(text, i, "**")) {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                bold = !bold;
                i++;
            } else if (matches(text, i, "__")) {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                underline = !underline;
                i++;
            } else if (matches(text, i, "~~")) {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                strikethrough = !strikethrough;
                i++;
            } else if (text.charAt(i) == '*') {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                italic = !italic;
            } else if (text.charAt(i) == '`') {
                appendSegment(component, segment, bold, italic, underline, strikethrough, code);
                code = !code;
            } else {
                segment.append(text.charAt(i));
            }
        }

        appendSegment(component, segment, bold, italic, underline, strikethrough, code);
        return component;
    }

    private static boolean matches(String text, int start, String marker) {
        return start + marker.length() <= text.length() && text.startsWith(marker, start);
    }

    private static void appendSegment(
            MutableComponent component,
            StringBuilder segment,
            boolean bold,
            boolean italic,
            boolean underline,
            boolean strikethrough,
            boolean code
    ) {
        if (segment.isEmpty()) {
            return;
        }

        Style style = Style.EMPTY;
        if (bold) {
            style = style.withBold(true);
        }
        if (italic) {
            style = style.withItalic(true);
        }
        if (underline) {
            style = style.withUnderlined(true);
        }
        if (strikethrough) {
            style = style.withStrikethrough(true);
        }
        if (code) {
            style = style.withColor(ChatFormatting.DARK_GRAY);
        }

        component.append(Component.literal(segment.toString()).withStyle(style));
        segment.setLength(0);
    }

    private enum BlockType {
        HEADING,
        TEXT,
        QUOTE,
        LIST_ITEM,
        SEPARATOR,
        BLANK
    }

    private record Block(BlockType type, int level, String text) {
    }
}
