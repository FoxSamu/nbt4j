package net.shadew.nbt4j;

public interface NbtVisitor {
    NbtVisitor NOOP = new NbtVisitor() {
    };

    default void visitByte(byte value, String name) {

    }

    default void visitShort(short value, String name) {

    }

    default void visitInt(int value, String name) {

    }

    default void visitLong(long value, String name) {

    }

    default void visitFloat(float value, String name) {

    }

    default void visitDouble(double value, String name) {

    }

    default void visitString(String value, String name) {

    }

    default void visitByteArray(byte[] value, String name) {

    }

    default void visitIntArray(int[] value, String name) {

    }

    default void visitLongArray(long[] value, String name) {

    }

    default NbtVisitor visitList(TagType type, int length, String name) {
        return this;
    }

    default NbtVisitor visitCompound(String name) {
        return this;
    }

    default void visitListEnd() {
    }

    default void visitEnd() {
    }
}
