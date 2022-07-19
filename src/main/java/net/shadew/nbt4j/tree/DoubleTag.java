package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public final class DoubleTag extends NumericTag {
    private final double value;

    private DoubleTag(double value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.DOUBLE;
    }

    @Override
    public DoubleTag copy() {
        return this;
    }

    public static DoubleTag of(byte v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(short v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(int v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(long v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(float v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(double v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static DoubleTag of(char v) {
        return new DoubleTag(v);
    }

    public static DoubleTag of(Number v) {
        return new DoubleTag(v.floatValue());
    }

    public static DoubleTag of(Boolean v) {
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
        return (int) value;
    }

    @Override
    public long asLong() {
        return (long) value;
    }

    @Override
    public char asChar() {
        return (char) value;
    }

    @Override
    public float asFloat() {
        return (float) value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String toString() {
        return "TAG_Double:" + value;
    }
}
