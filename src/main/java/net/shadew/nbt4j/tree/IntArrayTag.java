package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class IntArrayTag implements Tag {
    private static final IntArrayTag EMPTY = of(0);

    private final int[] ints;

    private IntArrayTag(int len) {
        this.ints = new int[len];
    }

    private IntArrayTag(int[] ints, int off, int len) {
        this(len);
        System.arraycopy(ints, off, this.ints, 0, len);
    }

    @Override
    public TagType type() {
        return TagType.INT_ARRAY;
    }

    @Override
    public IntArrayTag copy() {
        return of(ints);
    }

    public int[] ints() {
        return ints;
    }

    public int length() {
        return ints.length;
    }

    public static IntArrayTag of(int length) {
        return new IntArrayTag(length);
    }

    public static IntArrayTag of(int[] ints) {
        return new IntArrayTag(ints, 0, ints.length);
    }

    public static IntArrayTag of(int[] ints, int off, int len) {
        return new IntArrayTag(ints, off, len);
    }

    public static IntArrayTag of(IntArrayTag copy) {
        return new IntArrayTag(copy.ints, 0, copy.length());
    }

    public static IntArrayTag of(IntArrayTag copy, int off, int len) {
        return new IntArrayTag(copy.ints, off, len);
    }

    public static IntArrayTag empty() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "TAG_IntArray[" + ints.length + "]";
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitIntArray(ints, name);
    }
}
