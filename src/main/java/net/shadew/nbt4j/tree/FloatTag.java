package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public class FloatTag implements NumericTag {
    private final float value;

    private FloatTag(float value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.FLOAT;
    }

    @Override
    public FloatTag copy() {
        return this;
    }

    public static FloatTag of(byte v) {
        return new FloatTag(v);
    }

    public static FloatTag of(short v) {
        return new FloatTag(v);
    }

    public static FloatTag of(int v) {
        return new FloatTag(v);
    }

    public static FloatTag of(long v) {
        return new FloatTag(v);
    }

    public static FloatTag of(float v) {
        return new FloatTag(v);
    }

    public static FloatTag of(double v) {
        return new FloatTag((float) v);
    }

    public static FloatTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static FloatTag of(char v) {
        return new FloatTag(v);
    }

    public static FloatTag of(Number v) {
        return new FloatTag(v.floatValue());
    }

    public static FloatTag of(Boolean v) {
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
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String toString() {
        return value + "f";
    }
}
