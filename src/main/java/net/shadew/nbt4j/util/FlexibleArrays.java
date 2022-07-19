package net.shadew.nbt4j.util;

import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
public class FlexibleArrays {
    private static final int CHUNK_SIZE = 16;

    public static <T> T add(T src, int curLen, int index, int amount) {
        Object dest = src;
        int len = Array.getLength(src);
        if (curLen + amount > len) {
            int newLen = ((len + amount) / CHUNK_SIZE + 1) * CHUNK_SIZE;
            if (newLen < 0) newLen = Integer.MAX_VALUE;
            dest = Array.newInstance(src.getClass().getComponentType(), newLen);
            System.arraycopy(src, 0, dest, 0, index);
        }
        System.arraycopy(src, index, dest, index + amount, curLen - index);
        return (T) dest;
    }

    public static <T> T remove(T src, int curLen, int index, int amount) {
        System.arraycopy(src, index + amount, src, index, curLen - (index + amount));
        return src;
    }

    public static <T> T make(Class<T> type) {
        return (T) Array.newInstance(type.getComponentType(), CHUNK_SIZE);
    }

    public static <T> T make(Class<T> type, int minLen) {
        int l = (minLen / CHUNK_SIZE + 1) * CHUNK_SIZE;
        return (T) Array.newInstance(type.getComponentType(), l);
    }

    public static <T> T copy(T src, int curLen) {
        Object dest = Array.newInstance(src.getClass().getComponentType(), curLen);
        System.arraycopy(src, 0, dest, 0, curLen);
        return (T) dest;
    }

    public static <T> T pack(T src, int curLen) {
        int len = Array.getLength(src);
        if (len == curLen) return src; // No need for copying, as we're already packed
        return copy(src, curLen);
    }

    public static <T> T ensureSpace(T src, int curLen, int newLen) {
        Object dest = src;
        int len = Array.getLength(src);
        if (newLen > len) {
            dest = make(src.getClass(), newLen);
            System.arraycopy(src, 0, dest, 0, curLen);
        }
        return (T) dest;
    }
}
