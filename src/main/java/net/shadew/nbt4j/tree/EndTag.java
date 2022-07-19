package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.NbtException;

public final class EndTag extends Tag {
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {
    }

    @Override
    public TagType type() {
        return TagType.END;
    }

    @Override
    public EndTag copy() {
        return this;
    }

    public static EndTag doNotDeserialize(DataInput in, int nesting) throws IOException {
        throw new NbtException("TAG_End can't be deserialized as standard tag");
    }

    public static EndTag doNotSerialize(EndTag tag, DataOutput out) throws IOException {
        throw new NbtException("TAG_End can't be serialized as standard tag");
    }

    @Override
    public String toString() {
        return "TAG_End";
    }
}
