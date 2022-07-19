package net.shadew.nbt4j;

public interface NbtAcceptor {
    default void accept(NbtVisitor visitor) {
        accept(visitor, "");
    }
    void accept(NbtVisitor visitor, String name);
}
