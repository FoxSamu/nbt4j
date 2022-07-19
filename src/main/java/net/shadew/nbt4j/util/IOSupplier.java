package net.shadew.nbt4j.util;

import java.io.IOException;

public interface IOSupplier<V> {
    V get() throws IOException;
}
