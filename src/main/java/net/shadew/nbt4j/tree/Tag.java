package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.TagType;

public interface Tag {
    TagType type();

    Tag copy();
}
