package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class ByteArrayTag implements Tag {
    private static final ByteArrayTag EMPTY = of(0);

    private final byte[] bytes;

    private ByteArrayTag(int len) {
        this.bytes = new byte[len];
    }

    private ByteArrayTag(byte[] bytes, int off, int len) {
        this(len);
        System.arraycopy(bytes, off, this.bytes, 0, len);
    }

    @Override
    public TagType type() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    public ByteArrayTag copy() {
        return of(bytes);
    }

    public byte[] bytes() {
        return bytes;
    }

    public int length() {
        return bytes.length;
    }

    public static ByteArrayTag of(int length) {
        return new ByteArrayTag(length);
    }

    public static ByteArrayTag of(byte[] bytes) {
        return new ByteArrayTag(bytes, 0, bytes.length);
    }

    public static ByteArrayTag of(byte[] bytes, int off, int len) {
        return new ByteArrayTag(bytes, off, len);
    }

    public static ByteArrayTag of(ByteArrayTag copy) {
        return new ByteArrayTag(copy.bytes, 0, copy.length());
    }

    public static ByteArrayTag of(ByteArrayTag copy, int off, int len) {
        return new ByteArrayTag(copy.bytes, off, len);
    }

    public static ByteArrayTag empty() {
        return EMPTY;
    }

    public static long countBytes(ByteArrayTag tag) {
        return 4L + tag.length(); // 4 for the length header
    }

    public static void serialize(ByteArrayTag tag, DataOutput out) throws IOException {
        out.writeInt(tag.length());
        out.write(tag.bytes);
    }

    public static ByteArrayTag deserialize(DataInput in, int nesting) throws IOException {
        int len = in.readInt();
        ByteArrayTag tag = new ByteArrayTag(len);
        in.readFully(tag.bytes);
        return tag;
    }

    @Override
    public String toString() {
        return "TAG_ByteArray[" + bytes.length + "]";
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitByteArray(bytes, name);
    }
}
