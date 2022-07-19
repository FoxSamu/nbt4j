package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public final class ByteTag extends NumericTag {
    // Let's just cache all possible byte tag instances to save performance when it comes to memory allocation and
    // garbage collection
    private static final ByteTag[] CACHE = new ByteTag[256];

    static {
        for (int b = Byte.MIN_VALUE; b <= Byte.MAX_VALUE; b++) {
            CACHE[b + 128] = new ByteTag((byte) b);
        }
    }

    public static final ByteTag TRUE = of(1);
    public static final ByteTag FALSE = of(0);

    private final byte value;

    private ByteTag(byte value) {
        this.value = value;
    }

    @Override
    public TagType type() {
        return TagType.BYTE;
    }

    @Override
    public ByteTag copy() {
        return this;
    }

    public static ByteTag of(byte v) {
        return CACHE[v + 128];
    }

    public static ByteTag of(short v) {
        return of((byte) v);
    }

    public static ByteTag of(int v) {
        return of((byte) v);
    }

    public static ByteTag of(long v) {
        return of((byte) v);
    }

    public static ByteTag of(float v) {
        return of((byte) v);
    }

    public static ByteTag of(double v) {
        return of((byte) v);
    }

    public static ByteTag of(boolean v) {
        return of(v ? 1 : 0);
    }

    public static ByteTag of(char v) {
        return of((byte) v);
    }

    public static ByteTag of(Number v) {
        return of(v.byteValue());
    }

    public static ByteTag of(Boolean v) {
        return of(v.booleanValue());
    }

    @Override
    public byte asByte() {
        return value;
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
        return "TAG_Byte:" + value;
    }
}
