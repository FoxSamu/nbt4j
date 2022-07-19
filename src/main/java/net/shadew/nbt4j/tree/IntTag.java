package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class IntTag implements NumericTag {
    private final int value;

    private IntTag(int value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.INT;
    }

    @Override
    public IntTag copy() {
        return this;
    }

    public static IntTag of(byte v) {
        return new IntTag(v);
    }

    public static IntTag of(short v) {
        return new IntTag(v);
    }

    public static IntTag of(int v) {
        return new IntTag(v);
    }

    public static IntTag of(long v) {
        return new IntTag((int) v);
    }

    public static IntTag of(float v) {
        return new IntTag((int) v);
    }

    public static IntTag of(double v) {
        return new IntTag((int) v);
    }

    public static IntTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static IntTag of(char v) {
        return new IntTag(v);
    }

    public static IntTag of(Number v) {
        return new IntTag(v.intValue());
    }

    public static IntTag of(Boolean v) {
        return of(v.booleanValue());
    }

    @Override
    public byte asByte() {
        return (byte) value;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public short asShort() {
        return (short) value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public char asChar() {
        return (char) value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String toString() {
        return "TAG_Int:" + value;
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitInt(value, name);
    }
}
