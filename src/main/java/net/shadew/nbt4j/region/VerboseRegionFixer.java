package net.shadew.nbt4j.region;

import java.io.InputStream;

import net.shadew.nbt4j.Compression;
import net.shadew.nbt4j.util.NullInputStream;

public class VerboseRegionFixer implements RegionFileFixer {
    public static final VerboseRegionFixer INSTANCE = new VerboseRegionFixer();

    private VerboseRegionFixer() {
    }

    @Override
    public void truncatedHeader(int foundBytes, int requiredBytes) {
        System.err.println(
            "[RegionFile - ERROR] Truncated header: has only " + foundBytes + " of " + requiredBytes + " bytes"
        );
    }

    @Override
    public void chunkOverlapsHeader(int index) {
        System.err.println(
            "[RegionFile - ERROR] Chunk offset overlaps header"
        );
    }

    @Override
    public void chunkOutOfFileSize(int index) {
        System.err.println(
            "[RegionFile - ERROR] Chunk payload is outside file space"
        );
    }

    @Override
    public void offsetZeroSectorChunk(int index) {
        System.err.println(
            "[RegionFile - WARN] Chunk has offset but spans zero sectors"
        );
    }

    @Override
    public InputStream truncatedChunkHeader(int foundBytes, int requiredBytes, int x, int z) {
        System.err.println(
            "[RegionFile - ERROR] Chunk [" + x + ", " + z + "] header is truncated: " +
                "has only " + foundBytes + " of " + requiredBytes + " bytes"
        );
        // N/A
        return NullInputStream.INSTANCE;
    }

    @Override
    public void zeroChunkSize(int x, int z) {
        System.err.println(
            "[RegionFile - WARN] Chunk [" + x + ", " + z + "] is present but has no payload"
        );
    }

    @Override
    public InputStream negativeChunkSize(int x, int z, int unpaddedSize) {
        System.err.println(
            "[RegionFile - ERROR] Chunk [" + x + ", " + z + "] has negative payload size: " + unpaddedSize
        );
        return NullInputStream.INSTANCE;
    }

    @Override
    public Compression unknownCompression(int x, int z, int compression, Compression defaultCompression) {
        System.err.println(
            "[RegionFile - ERROR] Chunk [" + x + ", " + z + "] has unknown payload format " + compression
        );
        // N/A
        return null;
    }
}
