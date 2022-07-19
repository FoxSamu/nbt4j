package net.shadew.nbt4j.tree;

public interface NumericTag extends Tag {
    byte asByte();
    boolean asBoolean();
    short asShort();
    int asInt();
    long asLong();
    char asChar();
    float asFloat();
    double asDouble();

    NumericTag ZERO = ByteTag.FALSE;
}
