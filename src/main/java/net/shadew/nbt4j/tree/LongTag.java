package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class LongTag implements NumericTag {
    private final long value;

    private LongTag(long value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.LONG;
    }

    @Override
    public LongTag copy() {
        return this;
    }

    public static LongTag of(byte v) {
        return new LongTag(v);
    }

    public static LongTag of(short v) {
        return new LongTag(v);
    }

    public static LongTag of(int v) {
        return new LongTag(v);
    }

    public static LongTag of(long v) {
        return new LongTag(v);
    }

    public static LongTag of(float v) {
        return new LongTag((long) v);
    }

    public static LongTag of(double v) {
        return new LongTag((long) v);
    }

    public static LongTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static LongTag of(char v) {
        return new LongTag(v);
    }

    public static LongTag of(Number v) {
        return new LongTag(v.longValue());
    }

    public static LongTag of(Boolean v) {
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
        return "TAG_Long:" + value;
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitLong(value, name);
    }
}
