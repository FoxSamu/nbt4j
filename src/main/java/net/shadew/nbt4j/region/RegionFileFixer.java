package net.shadew.nbt4j.region;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import net.shadew.nbt4j.Compression;

/**
 * A fixer for a {@link RegionFile}. {@link RegionFile} handles two default strategies for handling corrupted region
 * files: lenient and non-lenient. Non-lenient region file reading causes a failure when a part of the region file is
 * corrupted, while lenient reading will silently ignore any malformed part and tries to repair as much as possible.
 * When it comes to advanced repairing of a region file, these two strategies are often not enough. That is where {@link
 * RegionFileFixer} comes to play a role. {@link RegionFileFixer} provides several methods that get called by {@link
 * RegionFile} when it encounters a malformed part in the region file.
 * <p>
 * A common use case for {@link RegionFileFixer} is when a {@link RegionFile} must be opened leniently but any
 * encountered error must be logged. Due to the wide amount of logging frameworks that exist, the default lenient
 * reading strategy silently repairs any error without logging them. NBT4j provides one extra {@link RegionFileFixer}
 * that prints any region file error to a {@link PrintStream}, by default {@link System#err}.
 * </p>
 */
public interface RegionFileFixer {
    // Header issues
    void truncatedHeader(int foundBytes, int requiredBytes) throws IOException;
    void chunkOverlapsHeader(int index) throws IOException;
    void chunkOutOfFileSize(int index) throws IOException;
    void offsetZeroSectorChunk(int index) throws IOException;

    // Chunk reading issues
    InputStream truncatedChunkHeader(int foundBytes, int requiredBytes, int x, int z) throws IOException;
    void zeroChunkSize(int x, int z) throws IOException;
    InputStream negativeChunkSize(int x, int z, int unpaddedSize) throws IOException;
    Compression unknownCompression(int x, int z, int compression, Compression defaultCompression) throws IOException;
}
