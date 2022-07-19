package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public class ShortTag implements NumericTag {
    private final short value;

    private ShortTag(short value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.SHORT;
    }

    @Override
    public ShortTag copy() {
        return this;
    }

    public static ShortTag of(byte v) {
        return new ShortTag(v);
    }

    public static ShortTag of(short v) {
        return new ShortTag(v);
    }

    public static ShortTag of(int v) {
        return new ShortTag((short) v);
    }

    public static ShortTag of(long v) {
        return new ShortTag((short) v);
    }

    public static ShortTag of(float v) {
        return new ShortTag((short) v);
    }

    public static ShortTag of(double v) {
        return new ShortTag((short) v);
    }

    public static ShortTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static ShortTag of(char v) {
        return new ShortTag((short) v);
    }

    public static ShortTag of(Number v) {
        return new ShortTag(v.shortValue());
    }

    public static ShortTag of(Boolean v) {
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
        return value;
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
        return value + "s";
    }
}
