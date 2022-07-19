package net.shadew.nbt4j;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public enum Compression {
    UNCOMPRESSED(
        3,
        in -> in,
        out -> out
    ),
    GZIPPED(
        1,
        GZIPInputStream::new,
        GZIPOutputStream::new
    ),
    DEFLATED(
        2,
        InflaterInputStream::new,
        DeflaterOutputStream::new
    );

    private final int regionTypeId;
    private final Factory<InputStream, InputStream> readFactory;
    private final Factory<OutputStream, OutputStream> writeFactory;

    Compression(int regionTypeId, Factory<InputStream, InputStream> readFactory, Factory<OutputStream, OutputStream> writeFactory) {
        this.regionTypeId = regionTypeId;
        this.readFactory = readFactory;
        this.writeFactory = writeFactory;
    }

    public static Compression byRegionTypeId(int id) {
        if (id < 1 || id > 3) {
            throw new IllegalArgumentException("Unknown ID " + id);
        }
        switch (id) {
            case 1: return GZIPPED;
            case 2: return DEFLATED;
            default: return UNCOMPRESSED;
        }
    }

    public int getRegionTypeId() {
        return regionTypeId;
    }

    public InputStream createInStream(InputStream in) throws IOException {
        return readFactory.create(in);
    }

    public OutputStream createOutStream(OutputStream out) throws IOException {
        return writeFactory.create(out);
    }

    public DataInput createIn(InputStream in) throws IOException {
        return new DataInputStream(readFactory.create(in));
    }

    public DataOutput createOut(OutputStream out) throws IOException {
        return new DataOutputStream(writeFactory.create(out));
    }

    private interface Factory<T, U> {
        U create(T t) throws IOException;
    }
}
