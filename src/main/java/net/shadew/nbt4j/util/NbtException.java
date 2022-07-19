package net.shadew.nbt4j.util;

import java.io.IOException;

public class NbtException extends IOException {
    public NbtException() {
    }

    public NbtException(String message) {
        super(message);
    }

    public NbtException(String message, Throwable cause) {
        super(message, cause);
    }

    public NbtException(Throwable cause) {
        super(cause);
    }
}
