package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class LongArrayTag implements Tag {
    private static final LongArrayTag EMPTY = of(0);

    private final long[] longs;

    private LongArrayTag(int len) {
        this.longs = new long[len];
    }

    private LongArrayTag(long[] longs, int off, int len) {
        this(len);
        System.arraycopy(longs, off, this.longs, 0, len);
    }

    @Override
    public TagType type() {
        return TagType.LONG_ARRAY;
    }

    @Override
    public LongArrayTag copy() {
        return of(longs);
    }

    public long[] longs() {
        return longs;
    }

    public int length() {
        return longs.length;
    }

    public static LongArrayTag of(int length) {
        return new LongArrayTag(length);
    }

    public static LongArrayTag of(long[] longs) {
        return new LongArrayTag(longs, 0, longs.length);
    }

    public static LongArrayTag of(long[] longs, int off, int len) {
        return new LongArrayTag(longs, off, len);
    }

    public static LongArrayTag of(LongArrayTag copy) {
        return new LongArrayTag(copy.longs, 0, copy.length());
    }

    public static LongArrayTag of(LongArrayTag copy, int off, int len) {
        return new LongArrayTag(copy.longs, off, len);
    }

    public static LongArrayTag empty() {
        return EMPTY;
    }

    public static long countBytes(LongArrayTag tag) {
        return 4L + tag.length() * 8L; // 4 for the length header
    }

    public static void serialize(LongArrayTag tag, DataOutput out) throws IOException {
        out.writeInt(tag.length());
        for (long l : tag.longs) {
            out.writeLong(l);
        }
    }

    public static LongArrayTag deserialize(DataInput in, int nesting) throws IOException {
        int len = in.readInt();
        LongArrayTag tag = new LongArrayTag(len);
        long[] longs = tag.longs;
        for (int i = 0; i < len; i++) {
            longs[i] = in.readLong();
        }
        return tag;
    }

    @Override
    public String toString() {
        return "TAG_LongArray[" + longs.length + "]";
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitLongArray(longs, name);
    }
}
