package net.shadew.nbt4j.util;

import java.io.IOException;

public class MalformedRegionFileException extends IOException {
    public MalformedRegionFileException() {
    }

    public MalformedRegionFileException(String message) {
        super(message);
    }

    public MalformedRegionFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedRegionFileException(Throwable cause) {
        super(cause);
    }
}
