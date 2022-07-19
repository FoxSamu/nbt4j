package net.shadew.nbt4j.region;

import java.io.InputStream;

import net.shadew.nbt4j.Compression;
import net.shadew.nbt4j.util.NullInputStream;

public class SilentRegionFixer implements RegionFileFixer {
    public static final SilentRegionFixer INSTANCE = new SilentRegionFixer();

    private SilentRegionFixer() {
    }

    @Override
    public void truncatedHeader(int foundBytes, int requiredBytes) {
        // N/A
    }

    @Override
    public void chunkOverlapsHeader(int index) {
        // N/A
    }

    @Override
    public void chunkOutOfFileSize(int index) {
        // N/A
    }

    @Override
    public void offsetZeroSectorChunk(int index) {
        // N/A
    }

    @Override
    public InputStream truncatedChunkHeader(int foundBytes, int requiredBytes, int x, int z) {
        // N/A
        return NullInputStream.INSTANCE;
    }

    @Override
    public void zeroChunkSize(int x, int z) {
        // N/A
    }

    @Override
    public InputStream negativeChunkSize(int x, int z, int unpaddedSize) {
        // N/A
        return NullInputStream.INSTANCE;
    }

    @Override
    public Compression unknownCompression(int x, int z, int compression, Compression defaultCompression) {
        // N/A
        return null;
    }
}
