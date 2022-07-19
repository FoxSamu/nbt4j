package net.shadew.nbt4j;

import net.shadew.nbt4j.tree.StringTag;

public class ByteCounter extends FilterNbtVisitor {
    private final boolean includeName;
    private long bytes = 0;

    public ByteCounter(NbtVisitor visitor, boolean includeName) {
        super(visitor);
        this.includeName = includeName;
    }

    public ByteCounter(boolean includeName) {
        this.includeName = includeName;
    }

    public long bytes() {
        return bytes;
    }

    private void visitName(String name) {
        if (includeName) {
            bytes += 1L + StringTag.countUtfBytes(name == null ? "" : name);
        }
    }

    @Override
    public void visitByte(byte value, String name) {
        visitName(name);
        bytes++;
        super.visitByte(value, name);
    }

    @Override
    public void visitShort(short value, String name) {
        visitName(name);
        bytes += 2;
        super.visitShort(value, name);
    }

    @Override
    public void visitInt(int value, String name) {
        visitName(name);
        bytes += 4;
        super.visitInt(value, name);
    }

    @Override
    public void visitLong(long value, String name) {
        visitName(name);
        bytes += 8;
        super.visitLong(value, name);
    }

    @Override
    public void visitFloat(float value, String name) {
        visitName(name);
        bytes += 4;
        super.visitFloat(value, name);
    }

    @Override
    public void visitDouble(double value, String name) {
        visitName(name);
        bytes += 8;
        super.visitDouble(value, name);
    }

    @Override
    public void visitString(String value, String name) {
        visitName(name);
        bytes += StringTag.countUtfBytes(value);
        super.visitString(value, name);
    }

    @Override
    public void visitByteArray(byte[] value, String name) {
        visitName(name);
        bytes += value.length;
        super.visitByteArray(value, name);
    }

    @Override
    public void visitIntArray(int[] value, String name) {
        visitName(name);
        bytes += value.length * 4L;
        super.visitIntArray(value, name);
    }

    @Override
    public void visitLongArray(long[] value, String name) {
        visitName(name);
        bytes += value.length * 8L;
        super.visitLongArray(value, name);
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        visitName(name);
        return new ByteCounter(super.visitList(type, length, name), false) {
            @Override
            public void visitListEnd() {
                ByteCounter.this.bytes += bytes();
                super.visitListEnd();
            }
        };
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        visitName(name);
        return new ByteCounter(super.visitCompound(name), true) {
            @Override
            public void visitEnd() {
                ByteCounter.this.bytes += bytes();
                super.visitEnd();
            }
        };
    }

    @Override
    public void visitListEnd() {
        super.visitListEnd();
    }

    @Override
    public void visitEnd() {
        bytes++;
        super.visitEnd();
    }
}
