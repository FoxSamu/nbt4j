package net.shadew.nbt4j.snbt;

import java.util.Iterator;
import java.util.Map;

import net.shadew.nbt4j.tree.*;

public class SnbtSerializer {
    private static final String ANSI_NUMBER = "\033[91m";
    private static final String ANSI_STRING = "\033[92m";
    private static final String ANSI_KEY = "\033[96m";
    private static final String ANSI_RESET = "\033[0m";

    private final StringBuilder builder = new StringBuilder();
    private boolean quoteAllStrings = true;
    private boolean quoteAllKeys;
    private int arrayPackThreshold = 10;
    private boolean packed;
    private boolean ansiHighlight;
    private String indentString = "  ";

    private int indent;

    public SnbtSerializer quoteAllStrings(boolean v) {
        quoteAllStrings = v;
        return this;
    }

    public SnbtSerializer quoteAllKeys(boolean v) {
        quoteAllKeys = v;
        return this;
    }

    public SnbtSerializer arrayPackThreshold(int v) {
        arrayPackThreshold = v;
        return this;
    }

    public SnbtSerializer packed(boolean v) {
        packed = v;
        return this;
    }

    public SnbtSerializer ansiHighlight(boolean v) {
        ansiHighlight = v;
        return this;
    }

    public SnbtSerializer indentString(String v) {
        indentString = v;
        return this;
    }

    private void addAnsi(String code) {
        if (ansiHighlight) builder.append(code);
    }

    private void indent(int add) {
        indent += add;
    }

    private void addIndent() {
        if (packed) return;
        for (int i = 0; i < indent; i++) {
            builder.append(indentString);
        }
    }

    private void addNewline(boolean p) {
        if (packed || p) return;
        builder.append(System.lineSeparator());
        addIndent();
    }

    private void addNewlineOrSpace(boolean p) {
        if (packed || p) {
            builder.append(" ");
            return;
        }
        builder.append(System.lineSeparator());
        addIndent();
    }

    private void writeNumeric(NumericTag num) {
        addAnsi(ANSI_NUMBER);
        builder.append(num.toString());
        addAnsi(ANSI_RESET);
    }

    private void writeString(StringTag str) {
        addAnsi(ANSI_STRING);
        if (quoteAllStrings) {
            builder.append(StringTag.makeQuotedSnbt(str.asString()));
        } else {
            builder.append(StringTag.makeSnbt(str.asString()));
        }
        addAnsi(ANSI_RESET);
    }

    private void writeKey(String key) {
        addAnsi(ANSI_KEY);
        if (quoteAllKeys) {
            builder.append(StringTag.makeQuotedSnbt(key));
        } else {
            builder.append(StringTag.makeSnbt(key));
        }
        addAnsi(ANSI_RESET);
    }

    private void writeByteArray(ByteArrayTag tag) {
        int iMax = tag.length() - 1;
        if (iMax == -1) {
            builder.append("[B;]");
            return;
        }

        boolean pack = tag.length() <= arrayPackThreshold;

        builder.append("[B;");
        indent(1);
        addNewline(pack);
        for (int i = 0; ; i++) {
            addAnsi(ANSI_NUMBER);
            builder.append(tag.get(i));
            addAnsi(ANSI_RESET);
            if (i == iMax) {
                indent(-1);
                addNewline(pack);
                builder.append(']');
                return;
            }
            builder.append(",");
            addNewlineOrSpace(pack);
        }
    }

    private void writeIntArray(IntArrayTag tag) {
        int iMax = tag.length() - 1;
        if (iMax == -1) {
            builder.append("[I;]");
            return;
        }

        boolean pack = tag.length() <= arrayPackThreshold;

        builder.append("[I;");
        indent(1);
        addNewline(pack);
        for (int i = 0; ; i++) {
            addAnsi(ANSI_NUMBER);
            builder.append(tag.get(i));
            addAnsi(ANSI_RESET);
            if (i == iMax) {
                indent(-1);
                addNewline(pack);
                builder.append(']');
                return;
            }
            builder.append(",");
            addNewlineOrSpace(pack);
        }
    }

    private void writeLongArray(LongArrayTag tag) {
        int iMax = tag.length() - 1;
        if (iMax == -1) {
            builder.append("[L;]");
            return;
        }

        boolean pack = tag.length() <= arrayPackThreshold;

        builder.append("[L;");
        indent(1);
        addNewline(pack);
        for (int i = 0; ; i++) {
            addAnsi(ANSI_NUMBER);
            builder.append(tag.get(i));
            addAnsi(ANSI_RESET);
            if (i == iMax) {
                indent(-1);
                addNewline(pack);
                builder.append(']');
                return;
            }
            builder.append(",");
            addNewlineOrSpace(pack);
        }
    }

    private void writeList(ListTag tag) {
        int iMax = tag.size() - 1;
        if (iMax == -1) {
            builder.append("[]");
            return;
        }

        builder.append("[");
        indent(1);
        addNewline(false);
        for (int i = 0; ; i++) {
            writeTag(tag.get(i));
            if (i == iMax) {
                indent(-1);
                addNewline(false);
                builder.append(']');
                return;
            }
            builder.append(",");
            addNewlineOrSpace(false);
        }
    }

    private void writeCompound(CompoundTag tag) {
        Iterator<Map.Entry<String, Tag>> i = tag.entrySet().iterator();
        if (!i.hasNext()) {
            builder.append("{}");
            return;
        }

        builder.append('{');
        indent(1);
        addNewline(false);
        while (true) {
            Map.Entry<String, Tag> entry = i.next();
            String name = entry.getKey();
            Tag element = entry.getValue();

            writeKey(name);
            builder.append(": ");
            writeTag(element);

            if (!i.hasNext()) {
                indent(-1);
                addNewline(false);
                builder.append('}');
                return;
            }
            builder.append(",");
            addNewlineOrSpace(false);
        }
    }

    public void writeTag(Tag tag) {
        if (tag instanceof NumericTag) {
            writeNumeric((NumericTag) tag);
        } else if (tag instanceof StringTag) {
            writeString((StringTag) tag);
        } else if (tag instanceof ByteArrayTag) {
            writeByteArray((ByteArrayTag) tag);
        } else if (tag instanceof IntArrayTag) {
            writeIntArray((IntArrayTag) tag);
        } else if (tag instanceof LongArrayTag) {
            writeLongArray((LongArrayTag) tag);
        } else if (tag instanceof ListTag) {
            writeList((ListTag) tag);
        } else if (tag instanceof CompoundTag) {
            writeCompound((CompoundTag) tag);
        } else if (tag instanceof EndTag) {
            builder.append("(END)");
        } else {
            builder.append("(UNKNOWN)");
        }
    }

    public String toString() {
        return builder.toString();
    }
}
