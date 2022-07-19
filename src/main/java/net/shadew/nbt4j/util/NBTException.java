package net.shadew.nbt4j.util;

import java.io.IOException;

public class NBTException extends IOException {
    public NBTException() {
    }

    public NBTException(String message) {
        super(message);
    }

    public NBTException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTException(Throwable cause) {
        super(cause);
    }
}
