package net.shadew.nbt4j.tree;

import net.shadew.nbt4j.NbtAcceptor;
import net.shadew.nbt4j.TagType;

public sealed interface Tag extends NbtAcceptor
    permits EndTag, ByteArrayTag, IntArrayTag, LongArrayTag, CompoundTag, ListTag, NumericTag, StringTag {

    TagType type();
    Tag copy();
}
