package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.FlexibleByteArray;

public class ByteArrayTag extends FlexibleByteArray implements Tag {
    public ByteArrayTag() {
    }

    // To be consistent with other tag constructors (except empty constructors), these are private and must be accessed
    // via one of the 'of' methods below
    private ByteArrayTag(byte[] b, int off, int len) {
        super(b, off, len);
    }

    private ByteArrayTag(byte... b) {
        super(b);
    }

    private ByteArrayTag(FlexibleByteArray copy) {
        super(copy);
    }

    @Override
    public TagType type() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    public ByteArrayTag copy() {
        return of(this);
    }

    public static ByteArrayTag of(byte... bytes) {
        return new ByteArrayTag(bytes);
    }

    public static ByteArrayTag of(byte[] bytes, int off, int len) {
        return new ByteArrayTag(bytes, off, len);
    }

    public static ByteArrayTag of(FlexibleByteArray copy) {
        return new ByteArrayTag(copy);
    }

    public static long countBytes(ByteArrayTag tag) {
        return 4L + tag.length(); // 4 for the length header
    }

    public static void serialize(ByteArrayTag tag, DataOutput out) throws IOException {
        out.writeInt(tag.length());
        FlexibleByteArray.writeBytes(tag, out);
    }

    public static ByteArrayTag deserialize(DataInput in, int nesting) throws IOException {
        ByteArrayTag tag = new ByteArrayTag();
        int len = in.readInt();
        FlexibleByteArray.readBytes(tag, in, len);
        return tag;
    }

    @Override
    public String toString() {
        int iMax = length() - 1;
        if (iMax == -1)
            return "[B;]";

        StringBuilder b = new StringBuilder();
        b.append("[B;");
        for (int i = 0; ; i++) {
            b.append(get(i));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}
