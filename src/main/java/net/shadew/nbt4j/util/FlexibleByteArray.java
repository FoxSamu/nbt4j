package net.shadew.nbt4j.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class FlexibleByteArray {
    private byte[] bytes;
    private int length;

    public FlexibleByteArray() {
        bytes = FlexibleArrays.make(byte[].class);
    }

    public FlexibleByteArray(byte[] b, int off, int len) {
        bytes = FlexibleArrays.make(byte[].class, len);
        System.arraycopy(b, off, bytes, 0, len);
        length = len;
    }

    public FlexibleByteArray(byte... b) {
        this(b, 0, b.length);
    }

    public FlexibleByteArray(FlexibleByteArray copy) {
        this(copy.bytes, 0, copy.length);
    }

    public byte get(int index) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return bytes[index];
    }

    public void getAll(int index, byte[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        System.arraycopy(bytes, index, b, off, len);
    }

    public void getAll(int index, byte[] b) {
        getAll(index, b, 0, b.length);
    }

    public void getAll(int index, FlexibleByteArray b, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        b.addAll(bytes, index, len);
    }

    public byte[] getAll(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        byte[] out = new byte[len];
        getAll(index, out, 0, len);
        return out;
    }

    public byte[] getAll() {
        return FlexibleArrays.copy(bytes, length);
    }

    public void set(int index, int v) {
        set(index, (byte) v);
    }

    public void set(int index, byte v) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        bytes[index] = v;
    }

    public void setAll(int index, byte[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        System.arraycopy(b, off, bytes, index, len);
    }

    public void setAll(int index, byte[] b) {
        setAll(index, b, 0, b.length);
    }

    public void setAll(int index, FlexibleByteArray b) {
        setAll(index, b.bytes, 0, b.length);
    }

    public void add(int index, byte b) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        bytes = FlexibleArrays.add(bytes, length, index, 1);
        length += 1;
        set(index, b);
    }

    public void add(int index, int b) {
        add(index, (byte) b);
    }

    public void addAll(int index, byte[] b, int off, int len) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        bytes = FlexibleArrays.add(bytes, length, index, len);
        length += len;
        setAll(index, b, off, len);
    }

    public void addAll(int index, byte[] b) {
        addAll(index, b, 0, b.length);
    }

    public void addAll(int index, FlexibleByteArray b) {
        addAll(index, b.bytes, 0, b.length);
    }

    public void add(byte b) {
        add(length, b);
    }

    public void add(int b) {
        add(length, b);
    }

    public void addAll(byte[] b, int off, int len) {
        addAll(length, b, off, len);
    }

    public void addAll(byte[] b) {
        addAll(length, b);
    }

    public void addAll(FlexibleByteArray b) {
        addAll(length, b);
    }

    public void remove(int index) {
        remove(index, 1);
    }

    public void remove(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        bytes = FlexibleArrays.remove(bytes, length, index, len);
        length -= len;
    }

    public void clear() {
        length = 0;
    }

    public void pack() {
        bytes = FlexibleArrays.pack(bytes, length);
    }

    public int length() {
        return length;
    }

    public FlexibleByteArray copy() {
        return new FlexibleByteArray(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlexibleByteArray)) return false;
        FlexibleByteArray that = (FlexibleByteArray) o;
        return length == that.length && areBuffersEqual(bytes, that.bytes, length);
    }

    private static boolean areBuffersEqual(byte[] a, byte[] b, int len) {
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(length);
        result = 31 * result + hash(bytes, length);
        return result;
    }

    private static int hash(byte[] bytes, int len) {
        int result = 1;
        for (int i = 0; i < len; i++)
            result = 31 * result + bytes[i];

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
            b.append(bytes[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static void writeBytes(FlexibleByteArray byteArr, DataOutput out) throws IOException {
        out.write(byteArr.bytes, 0, byteArr.length);
    }

    public static void readBytes(FlexibleByteArray byteArr, DataInput in, int amount) throws IOException {
        byteArr.bytes = FlexibleArrays.ensureSpace(byteArr.bytes, 0, amount);
        in.readFully(byteArr.bytes, 0, amount);
        byteArr.length = amount;
    }
}
