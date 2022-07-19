package net.shadew.nbt4j.tree;

public sealed interface NumericTag extends Tag
    permits ByteTag, DoubleTag, FloatTag, IntTag, LongTag, ShortTag {

    byte asByte();
    boolean asBoolean();
    short asShort();
    int asInt();
    long asLong();
    char asChar();
    float asFloat();
    double asDouble();
}
