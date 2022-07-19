package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.FlexibleIntArray;

public class IntArrayTag extends FlexibleIntArray implements Tag {
    public IntArrayTag() {
    }

    // To be consistent with other tag constructors (except empty constructors), these are private and must be accessed
    // via one of the 'of' methods below
    private IntArrayTag(int[] i, int off, int len) {
        super(i, off, len);
    }

    private IntArrayTag(int... i) {
        super(i);
    }

    private IntArrayTag(FlexibleIntArray copy) {
        super(copy);
    }

    @Override
    public TagType type() {
        return TagType.INT_ARRAY;
    }

    @Override
    public IntArrayTag copy() {
        return of(this);
    }

    public static IntArrayTag of(int... ints) {
        return new IntArrayTag(ints);
    }

    public static IntArrayTag of(int[] ints, int off, int len) {
        return new IntArrayTag(ints, off, len);
    }

    public static IntArrayTag of(FlexibleIntArray copy) {
        return new IntArrayTag(copy);
    }

    public static long countBytes(IntArrayTag tag) {
        return 4L + tag.length() * 4L; // 4 for the length header
    }

    public static void serialize(IntArrayTag tag, DataOutput out) throws IOException {
        out.writeInt(tag.length());
        FlexibleIntArray.writeInts(tag, out);
    }

    public static IntArrayTag deserialize(DataInput in, int nesting) throws IOException {
        IntArrayTag tag = new IntArrayTag();
        int len = in.readInt();
        FlexibleIntArray.readInts(tag, in, len);
        return tag;
    }

    @Override
    public String toString() {
        int iMax = length() - 1;
        if (iMax == -1)
            return "[I;]";

        StringBuilder b = new StringBuilder();
        b.append("[I;");
        for (int i = 0; ; i++) {
            b.append(get(i));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}
