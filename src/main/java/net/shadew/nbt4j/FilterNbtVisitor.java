package net.shadew.nbt4j;

public class FilterNbtVisitor implements NbtVisitor {
    protected NbtVisitor visitor;

    public FilterNbtVisitor(NbtVisitor visitor) {
        this.visitor = visitor;
    }

    public FilterNbtVisitor() {
    }

    @Override
    public void visitByte(byte value, String name) {
        if (visitor != null)
            visitor.visitByte(value, name);
    }

    @Override
    public void visitShort(short value, String name) {
        if (visitor != null)
            visitor.visitShort(value, name);
    }

    @Override
    public void visitInt(int value, String name) {
        if (visitor != null)
            visitor.visitInt(value, name);
    }

    @Override
    public void visitLong(long value, String name) {
        if (visitor != null)
            visitor.visitLong(value, name);
    }

    @Override
    public void visitFloat(float value, String name) {
        if (visitor != null)
            visitor.visitFloat(value, name);
    }

    @Override
    public void visitDouble(double value, String name) {
        if (visitor != null)
            visitor.visitDouble(value, name);
    }

    @Override
    public void visitString(String value, String name) {
        if (visitor != null)
            visitor.visitString(value, name);
    }

    @Override
    public void visitByteArray(byte[] value, String name) {
        if (visitor != null)
            visitor.visitByteArray(value, name);
    }

    @Override
    public void visitIntArray(int[] value, String name) {
        if (visitor != null)
            visitor.visitIntArray(value, name);
    }

    @Override
    public void visitLongArray(long[] value, String name) {
        if (visitor != null)
            visitor.visitLongArray(value, name);
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        if (visitor != null)
            return visitor.visitList(type, length, name);
        else return null;
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        if (visitor != null)
            return visitor.visitCompound(name);
        else return null;
    }

    @Override
    public void visitListEnd() {
        if (visitor != null)
            visitor.visitListEnd();
    }

    @Override
    public void visitEnd() {
        if (visitor != null)
            visitor.visitEnd();
    }
}
