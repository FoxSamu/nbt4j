package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;

public final class EndTag implements Tag {
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

    @Override
    public String toString() {
        return "TAG_End";
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        visitor.visitEnd();
    }
}
