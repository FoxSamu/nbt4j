package net.shadew.nbt4j.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class FlexibleLongArray {
    private long[] longs;
    private int length;

    public FlexibleLongArray() {
        longs = FlexibleArrays.make(long[].class);
    }

    public FlexibleLongArray(long[] b, int off, int len) {
        longs = FlexibleArrays.make(long[].class, len);
        System.arraycopy(b, off, longs, 0, len);
        length = len;
    }

    public FlexibleLongArray(long... b) {
        this(b, 0, b.length);
    }

    public FlexibleLongArray(FlexibleLongArray copy) {
        this(copy.longs, 0, copy.length);
    }

    public long get(int index) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return longs[index];
    }

    public void getAll(int index, long[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        System.arraycopy(longs, index, b, off, len);
    }

    public void getAll(int index, long[] b) {
        getAll(index, b, 0, b.length);
    }

    public void getAll(int index, FlexibleLongArray b, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        b.addAll(longs, index, len);
    }

    public long[] getAll(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        long[] out = new long[len];
        getAll(index, out, 0, len);
        return out;
    }

    public long[] getAll() {
        return FlexibleArrays.copy(longs, length);
    }

    public void set(int index, long v) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        longs[index] = v;
    }

    public void setAll(int index, long[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        System.arraycopy(b, off, longs, index, len);
    }

    public void setAll(int index, long[] b) {
        setAll(index, b, 0, b.length);
    }

    public void setAll(int index, FlexibleLongArray b) {
        setAll(index, b.longs, 0, b.length);
    }

    public void add(int index, long b) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        longs = FlexibleArrays.add(longs, length, index, 1);
        length += 1;
        set(index, b);
    }

    public void addAll(int index, long[] b, int off, int len) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        longs = FlexibleArrays.add(longs, length, index, len);
        length += len;
        setAll(index, b, off, len);
    }

    public void addAll(int index, long[] b) {
        addAll(index, b, 0, b.length);
    }

    public void addAll(int index, FlexibleLongArray b) {
        addAll(index, b.longs, 0, b.length);
    }

    public void add(long b) {
        add(length, b);
    }

    public void addAll(long[] b, int off, int len) {
        addAll(length, b, off, len);
    }

    public void addAll(long[] b) {
        addAll(length, b);
    }

    public void addAll(FlexibleLongArray b) {
        addAll(length, b);
    }

    public void remove(int index) {
        remove(index, 1);
    }

    public void remove(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        longs = FlexibleArrays.remove(longs, length, index, len);
        length -= len;
    }

    public void clear() {
        length = 0;
    }

    public void pack() {
        longs = FlexibleArrays.pack(longs, length);
    }

    public int length() {
        return length;
    }

    public FlexibleLongArray copy() {
        return new FlexibleLongArray(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlexibleLongArray)) return false;
        FlexibleLongArray that = (FlexibleLongArray) o;
        return length == that.length && areBuffersEqual(longs, that.longs, length);
    }

    private static boolean areBuffersEqual(long[] a, long[] b, int len) {
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(length);
        result = 31 * result + hash(longs, length);
        return result;
    }

    private static int hash(long[] longs, int len) {
        int result = 1;
        for (int i = 0; i < len; i++)
            result = 31 * result + Long.hashCode(longs[i]);

        return result;
    }

    @Override
    public String toString() {
        int iMax = length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(longs[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static void writeLongs(FlexibleLongArray longArray, DataOutput out) throws IOException {
        long[] b = longArray.longs;
        for (int i = 0, l = longArray.length; i < l; i++) {
            out.writeLong(b[i]);
        }
    }

    public static void readLongs(FlexibleLongArray longArray, DataInput in, int amount) throws IOException {
        long[] b = longArray.longs = FlexibleArrays.ensureSpace(longArray.longs, 0, amount);
        for (int i = 0; i < amount; i++) {
            b[i] = in.readLong();
        }
        longArray.length = amount;
    }
}
