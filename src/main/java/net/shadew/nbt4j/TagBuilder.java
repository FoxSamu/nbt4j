package net.shadew.nbt4j;

import net.shadew.nbt4j.tree.*;

public class TagBuilder implements NbtVisitor {
    private Tag tag;

    public Tag tag() {
        return tag;
    }

    @Override
    public void visitByte(byte value, String name) {
        tag = ByteTag.of(value);
    }

    @Override
    public void visitShort(short value, String name) {
        tag = ShortTag.of(value);
    }

    @Override
    public void visitInt(int value, String name) {
        tag = IntTag.of(value);
    }

    @Override
    public void visitLong(long value, String name) {
        tag = LongTag.of(value);
    }

    @Override
    public void visitFloat(float value, String name) {
        tag = FloatTag.of(value);
    }

    @Override
    public void visitDouble(double value, String name) {
        tag = DoubleTag.of(value);
    }

    @Override
    public void visitString(String value, String name) {
        tag = StringTag.of(value);
    }

    @Override
    public void visitByteArray(byte[] value, String name) {
        tag = ByteArrayTag.of(value);
    }

    @Override
    public void visitIntArray(int[] value, String name) {
        tag = IntArrayTag.of(value);
    }

    @Override
    public void visitLongArray(long[] value, String name) {
        tag = LongArrayTag.of(value);
    }

    private static class ListBuilder implements NbtVisitor {
        private final ListTag tag;

        ListBuilder(ListTag tag) {
            this.tag = tag;
        }

        @Override
        public void visitByte(byte value, String name) {
            tag.add(ByteTag.of(value));
        }

        @Override
        public void visitShort(short value, String name) {
            tag.add(ShortTag.of(value));
        }

        @Override
        public void visitInt(int value, String name) {
            tag.add(IntTag.of(value));
        }

        @Override
        public void visitLong(long value, String name) {
            tag.add(LongTag.of(value));
        }

        @Override
        public void visitFloat(float value, String name) {
            tag.add(FloatTag.of(value));
        }

        @Override
        public void visitDouble(double value, String name) {
            tag.add(DoubleTag.of(value));
        }

        @Override
        public void visitString(String value, String name) {
            tag.add(StringTag.of(value));
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            tag.add(ByteArrayTag.of(value));
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            tag.add(IntArrayTag.of(value));
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            tag.add(LongArrayTag.of(value));
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            ListTag tag = new ListTag(type);
            this.tag.add(tag);
            return new ListBuilder(tag);
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            CompoundTag tag = new CompoundTag();
            this.tag.add(tag);
            return new CompoundBuilder(tag);
        }
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        ListTag tag = new ListTag(type);
        this.tag = tag;
        return new ListBuilder(tag);
    }

    private static class CompoundBuilder implements NbtVisitor {
        private final CompoundTag tag;

        CompoundBuilder(CompoundTag tag) {
            this.tag = tag;
        }

        @Override
        public void visitByte(byte value, String name) {
            tag.put(name, ByteTag.of(value));
        }

        @Override
        public void visitShort(short value, String name) {
            tag.put(name, ShortTag.of(value));
        }

        @Override
        public void visitInt(int value, String name) {
            tag.put(name, IntTag.of(value));
        }

        @Override
        public void visitLong(long value, String name) {
            tag.put(name, LongTag.of(value));
        }

        @Override
        public void visitFloat(float value, String name) {
            tag.put(name, FloatTag.of(value));
        }

        @Override
        public void visitDouble(double value, String name) {
            tag.put(name, DoubleTag.of(value));
        }

        @Override
        public void visitString(String value, String name) {
            tag.put(name, StringTag.of(value));
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            tag.put(name, ByteArrayTag.of(value));
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            tag.put(name, IntArrayTag.of(value));
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            tag.put(name, LongArrayTag.of(value));
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            ListTag tag = new ListTag(type);
            this.tag.put(name, tag);
            return new ListBuilder(tag);
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            CompoundTag tag = new CompoundTag();
            this.tag.put(name, tag);
            return new CompoundBuilder(tag);
        }
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        CompoundTag tag = new CompoundTag();
        this.tag = tag;
        return new CompoundBuilder(tag);
    }
}
