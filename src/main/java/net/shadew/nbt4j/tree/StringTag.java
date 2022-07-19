package net.shadew.nbt4j.tree;

import java.util.regex.Pattern;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class StringTag implements Tag {
    public static final StringTag EMPTY = of("");

    private final String value;

    private StringTag(String value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.STRING;
    }

    @Override
    public StringTag copy() {
        return this;
    }

    public static StringTag of(String v) {
        if (v.isEmpty()) return EMPTY;
        return new StringTag(v);
    }

    public String asString() {
        return value;
    }

    public static long countUtfBytes(String data) {
        int len = data.length();
        long utfBytes = 2;

        for (int i = 0; i < len; i++) {
            int c = data.charAt(i);
            if (c >= 0x0001 && c <= 0x007F)
                utfBytes += 1;
            else if (c > 0x07FF)
                utfBytes += 3;
            else
                utfBytes += 2;
        }

        return utfBytes;
    }

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

    public static String makeSnbt(String tagValue) {
        return SIMPLE_VALUE.matcher(tagValue).matches()
               ? tagValue
               : makeQuotedSnbt(tagValue);
    }

    public static String makeQuotedSnbt(String str) {
        StringBuilder sb = new StringBuilder(" ");
        char delimiter = 0;

        for (int i = 0, l = str.length(); i < l; i++) {
            char c = str.charAt(i);

            if (c == '\\') {
                sb.append('\\');
            } else if (c == '"' || c == '\'') {
                // Determine delimiter based on contents
                if (delimiter == 0) delimiter = c == '"' ? '\'' : '"';
                // Escape if needed
                if (delimiter == c) sb.append('\\');
            }

            sb.append(c);
        }

        if (delimiter == 0) // Happens when no quotes (single nor double) are in the string, use double quote then
            delimiter = '"';

        sb.setCharAt(0, delimiter);
        sb.append(delimiter);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TAG_String:" + makeSnbt(value);
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitString(value, name);
    }
}
