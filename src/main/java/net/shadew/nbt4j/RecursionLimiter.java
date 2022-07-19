package net.shadew.nbt4j;

import net.shadew.nbt4j.util.NbtException;

public class RecursionLimiter extends FilterNbtVisitor {
    private final int maxDepth;
    private int depth = 0;

    private boolean exceeded;

    public RecursionLimiter(NbtVisitor visitor, int maxDepth) {
        super(visitor);
        this.maxDepth = maxDepth;
    }

    public RecursionLimiter(int maxDepth) {
        super();
        this.maxDepth = maxDepth;
    }

    public boolean exceeded() {
        return exceeded;
    }

    public void throwNbtException() throws NbtException {
        if (exceeded)
            throw new NbtException("Max recursion depth is " + maxDepth);
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        depth++;
        if (depth > maxDepth) {
            exceeded = true;
            return null;
        }
        return super.visitList(type, length, name);
    }

    @Override
    public void visitListEnd() {
        depth--;
        super.visitListEnd();
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        depth++;
        if (depth > maxDepth) {
            exceeded = true;
            return null;
        }
        return super.visitCompound(name);
    }

    @Override
    public void visitEnd() {
        depth--;
        super.visitEnd();
    }
}
