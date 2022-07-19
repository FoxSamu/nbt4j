package net.shadew.nbt4j.util;

import java.io.InputStream;

public class NullInputStream extends InputStream {

    public static final NullInputStream INSTANCE = new NullInputStream();

    private NullInputStream() {
    }

    @Override
    public int read() {
        return -1;
    }

    @Override
    public int read(byte[] b) {
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        return -1;
    }

    @Override
    public long skip(long n) {
        return 0L;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public void close() {
    }
}
