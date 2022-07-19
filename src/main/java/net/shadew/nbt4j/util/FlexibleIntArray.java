package net.shadew.nbt4j.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class FlexibleIntArray {
    private int[] ints;
    private int length;

    public FlexibleIntArray() {
        ints = FlexibleArrays.make(int[].class);
    }

    public FlexibleIntArray(int[] b, int off, int len) {
        ints = FlexibleArrays.make(int[].class, len);
        System.arraycopy(b, off, ints, 0, len);
        length = len;
    }

    public FlexibleIntArray(int... b) {
        this(b, 0, b.length);
    }

    public FlexibleIntArray(FlexibleIntArray copy) {
        this(copy.ints, 0, copy.length);
    }

    public int get(int index) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return ints[index];
    }

    public void getAll(int index, int[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        System.arraycopy(ints, index, b, off, len);
    }

    public void getAll(int index, int[] b) {
        getAll(index, b, 0, b.length);
    }

    public void getAll(int index, FlexibleIntArray b, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        b.addAll(ints, index, len);
    }

    public int[] getAll(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int[] out = new int[len];
        getAll(index, out, 0, len);
        return out;
    }

    public int[] getAll() {
        return FlexibleArrays.copy(ints, length);
    }

    public void set(int index, int v) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        ints[index] = v;
    }

    public void setAll(int index, int[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: off=" + off + ", len=" + len);
        }
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        System.arraycopy(b, off, ints, index, len);
    }

    public void setAll(int index, int[] b) {
        setAll(index, b, 0, b.length);
    }

    public void setAll(int index, FlexibleIntArray b) {
        setAll(index, b.ints, 0, b.length);
    }

    public void add(int index, int b) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        ints = FlexibleArrays.add(ints, length, index, 1);
        length += 1;
        set(index, b);
    }

    public void addAll(int index, int[] b, int off, int len) {
        if (index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        ints = FlexibleArrays.add(ints, length, index, len);
        length += len;
        setAll(index, b, off, len);
    }

    public void addAll(int index, int[] b) {
        addAll(index, b, 0, b.length);
    }

    public void addAll(int index, FlexibleIntArray b) {
        addAll(index, b.ints, 0, b.length);
    }

    public void add(int b) {
        add(length, b);
    }

    public void addAll(int[] b, int off, int len) {
        addAll(length, b, off, len);
    }

    public void addAll(int[] b) {
        addAll(length, b);
    }

    public void addAll(FlexibleIntArray b) {
        addAll(length, b);
    }

    public void remove(int index) {
        remove(index, 1);
    }

    public void remove(int index, int len) {
        if (index < 0 || index + len > length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        ints = FlexibleArrays.remove(ints, length, index, len);
        length -= len;
    }

    public void clear() {
        length = 0;
    }

    public void pack() {
        ints = FlexibleArrays.pack(ints, length);
    }

    public int length() {
        return length;
    }

    public FlexibleIntArray copy() {
        return new FlexibleIntArray(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlexibleIntArray)) return false;
        FlexibleIntArray that = (FlexibleIntArray) o;
        return length == that.length && areBuffersEqual(ints, that.ints, length);
    }

    private static boolean areBuffersEqual(int[] a, int[] b, int len) {
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(length);
        result = 31 * result + hash(ints, length);
        return result;
    }

    private static int hash(int[] ints, int len) {
        int result = 1;
        for (int i = 0; i < len; i++)
            result = 31 * result + ints[i];

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
            b.append(ints[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static void writeInts(FlexibleIntArray intArray, DataOutput out) throws IOException {
        int[] b = intArray.ints;
        for (int i = 0, l = intArray.length; i < l; i++) {
            out.writeInt(b[i]);
        }
    }

    public static void readInts(FlexibleIntArray intArray, DataInput in, int amount) throws IOException {
        int[] b = intArray.ints = FlexibleArrays.ensureSpace(intArray.ints, 0, amount);
        for (int i = 0; i < amount; i++) {
            b[i] = in.readInt();
        }
        intArray.length = amount;
    }
}
