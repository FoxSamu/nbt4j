package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.FlexibleLongArray;

public class LongArrayTag extends FlexibleLongArray implements Tag {
    public LongArrayTag() {
    }

    // To be consistent with other tag constructors (except empty constructors), these are private and must be accessed
    // via one of the 'of' methods below
    private LongArrayTag(long[] l, int off, int len) {
        super(l, off, len);
    }

    private LongArrayTag(long... l) {
        super(l);
    }

    private LongArrayTag(FlexibleLongArray copy) {
        super(copy);
    }

    @Override
    public TagType type() {
        return TagType.LONG_ARRAY;
    }

    @Override
    public LongArrayTag copy() {
        return of(this);
    }

    public static LongArrayTag of(long... longs) {
        return new LongArrayTag(longs);
    }

    public static LongArrayTag of(long[] longs, int off, int len) {
        return new LongArrayTag(longs, off, len);
    }

    public static LongArrayTag of(FlexibleLongArray copy) {
        return new LongArrayTag(copy);
    }

    public static long countBytes(LongArrayTag tag) {
        return 4L + tag.length() * 8L; // 4 for the length header
    }

    public static void serialize(LongArrayTag tag, DataOutput out) throws IOException {
        out.writeInt(tag.length());
        FlexibleLongArray.writeLongs(tag, out);
    }

    public static LongArrayTag deserialize(DataInput in, int nesting) throws IOException {
        LongArrayTag tag = new LongArrayTag();
        int len = in.readInt();
        FlexibleLongArray.readLongs(tag, in, len);
        return tag;
    }

    @Override
    public String toString() {
        int iMax = length() - 1;
        if (iMax == -1)
            return "[L;]";

        StringBuilder b = new StringBuilder();
        b.append("[L;");
        for (int i = 0; ; i++) {
            b.append(get(i));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}
