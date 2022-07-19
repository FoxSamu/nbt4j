package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public abstract class Tag {
    public abstract TagType type();
    public abstract Tag copy();
}
