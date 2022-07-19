package net.shadew.nbt4j.region;

import java.util.BitSet;

class SectorManager {
    private final BitSet map = new BitSet();

    public synchronized void allocate(int off, int len) {
        map.set(off, off + len);
    }

    public synchronized void free(int off, int len) {
        map.clear(off, off + len);
    }

    public synchronized int findAndAllocate(int amount) {
        int cur = 0;

        while (true) {
            int from = map.nextClearBit(cur);
            int to = map.nextSetBit(from);

            if (to == -1 || to - from >= amount) {
                allocate(from, amount);
                return from;
            }

            cur = to;
        }
    }

    public synchronized int reallocate(int oldOff, int oldLen, int newLen) {
        if (newLen == oldLen)
            return oldOff; // No need to reallocate, because the size did not change

        free(oldOff, oldLen);
        if (newLen < oldLen) {
            allocate(oldOff, newLen);
            return oldOff;
        } else {
            return findAndAllocate(newLen);
        }
    }

    public synchronized int getSectorSpace() {
        return map.previousSetBit(map.size() - 1) + 1;
    }
}
