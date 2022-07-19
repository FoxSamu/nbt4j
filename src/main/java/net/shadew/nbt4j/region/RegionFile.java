package net.shadew.nbt4j.region;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.shadew.nbt4j.Compression;
import net.shadew.nbt4j.util.MalformedRegionFileException;
import net.shadew.nbt4j.util.NullInputStream;

public final class RegionFile implements AutoCloseable, Flushable {
    // Open flags

    /**
     * Flag to indicate a region file should be opened leniently. Instead of failing upon encountering an error, an
     * attempt is made to repair from malformed chunks. In the worst case it will have to recreate the file from
     * scratch. Even though this flag mutes all malformed region file errors, any other I/O error may still occur.
     * <p>
     * When using a custom {@link RegionFileFixer}, this option is automatically set.
     * </p>
     */
    public static final int LENIENT = 0b1;

    /**
     * Flag to indicate a region file must be created from scratch regardless of the existence of any region file. When
     * this flag is set when opening a region file, it will not read the header and assumes all chunks are empty.
     */
    public static final int CREATE = 0b10;

    /**
     * Flag to mark that the underlying file channel should be opened with {@link StandardOpenOption#DSYNC}.
     */
    public static final int DSYNC = 0b100;

    /**
     * Flag to mark that the region file will by default save chunks in {@linkplain Compression#GZIPPED GZIPped} format.
     * Must not be used with {@link #DEFLATE} or {@link #UNCOMPRESSED} (due to flag structure this will cause {@link
     * #UNCOMPRESSED} to be used). If no compression flag is set, {@code GZIP} is used as default.
     */
    public static final int GZIP = 0b01000;

    /**
     * Flag to mark that the region file will by default save chunks in {@linkplain Compression#DEFLATED DEFLATEd}
     * format. Must not be used with {@link #GZIP} or {@link #UNCOMPRESSED} (due to flag structure this will cause
     * {@link #UNCOMPRESSED} to be used).
     */
    public static final int DEFLATE = 0b10000;

    /**
     * Flag to mark that the region file will by default save chunks in {@linkplain Compression#UNCOMPRESSED
     * uncompressed} format. Must not be used with {@link #GZIP} or {@link #DEFLATE} (due to flag structure this will
     * cause {@code UNCOMPRESSED} to be used).
     */
    public static final int UNCOMPRESSED = 0b11000;

    /**
     * Flag to mark that streams opened from this region file should be buffered, i.e. use {@link BufferedInputStream}
     * for reading and {@link BufferedOutputStream} for writing. However, this does not mean that opened streams are
     * always an instance of {@link BufferedInputStream} or {@link BufferedOutputStream}, it may still be wrapped in
     * another stream, or in some cases in {@link #openInputStream}, a {@link NullInputStream} (which is, for
     * performance reasons, not buffered).
     */
    public static final int BUFFERED = 0b100000;

    /**
     * Flag to indicate a region file should be opened verbosely. This flag is the same as {@link #LENIENT}, except that
     * verbose opening will in addition print any encountered problem to {@link System#err}. This flag includes {@link
     * #LENIENT}, so {@link #LENIENT} does not need to be set when {@code VERBOSE} is set.
     * <p>
     * When using a custom {@link RegionFileFixer}, this option has no effect.
     * </p>
     */
    public static final int VERBOSE = 0b1000000 | LENIENT;
    private static final int VERBOSE_RAW = 0b1000000;

    // Sector metrics
    private static final int SECTOR_SIZE = 4096;
    private static final long SECTOR_SIZE_L = SECTOR_SIZE;
    private static final int SECTOR_INTS = SECTOR_SIZE / 4;
    private static final int HEADER_SIZE = SECTOR_SIZE * 2;

    // Chunk metrics
    private static final int CHUNK_HEADER_SIZE = 5; // Total chunk header size, including payload size integer
    private static final int EXTERNAL = 0b10000000;
    private static final int COMPRESSION_TYPE = 0b01111111;
    private static final int INTERNAL_SIZE_LIMIT = 256;

    private static final ByteBuffer ZERO_BYTE_BUF = ByteBuffer.wrap(new byte[0]);

    private final Path directory;
    private final Path file;
    private final Compression compression;
    private final boolean buffered;
    private final boolean lenient;

    private final FileChannel io;
    private final SectorManager sectors = new SectorManager();

    private final ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
    private final IntBuffer locations;
    private final IntBuffer timestamps;

    private final RegionFileFixer fixer;





    // =====================================================
    // OPENING
    // =====================================================


    public RegionFile(Path directory, Path file, int openFlags) throws IOException {
        this(
            directory, file, openFlags,

            (openFlags & VERBOSE_RAW) != 0
            ? VerboseRegionFixer.INSTANCE
            : SilentRegionFixer.INSTANCE
        );
    }

    public RegionFile(Path directory, Path file, int openFlags, RegionFileFixer fixer) throws IOException {
        this(directory, file, openFlags | LENIENT, fixer, null);
    }

    @SuppressWarnings("unused") // We need the unused void parameter for the sake of method overloading
    private RegionFile(Path directory, Path file, int openFlags, RegionFileFixer fixer, Void unused) throws IOException {
        this.directory = directory;
        this.file = file;
        this.fixer = fixer;

        int compr = (openFlags & 0b11000) >> 3;
        if (compr >= 1 && compr <= 3) {
            compression = Compression.byRegionTypeId(compr);
        } else {
            compression = Compression.GZIPPED;
        }

        buffered = (openFlags & BUFFERED) != 0;
        lenient = (openFlags & LENIENT) != 0;

        io = open(file, (openFlags & DSYNC) != 0);

        // Allocate header sectors
        sectors.allocate(0, 2);

        // Create locations and timestamps buffer, which reflect the header buffer
        header.position(0);
        locations = header.asIntBuffer();
        locations.limit(SECTOR_INTS);

        header.position(SECTOR_SIZE);
        timestamps = header.asIntBuffer();
        timestamps.limit(SECTOR_INTS);

        if ((openFlags & CREATE) == 0)
            try {
                readHeader();
            } catch (IOException e) {
                // If readHeader fails we won't pass out of the constructor and our RegionFile gets lost. Hence, we have
                // to close 'io' here so that we don't cause a resource leak.
                io.close();
                throw e;
            }
    }

    private static FileChannel open(Path file, boolean dsync) throws IOException {
        Set<StandardOpenOption> options = new HashSet<>(Arrays.asList(
            StandardOpenOption.CREATE,
            StandardOpenOption.READ,
            StandardOpenOption.WRITE
        ));
        if (dsync) options.add(StandardOpenOption.DSYNC);
        return FileChannel.open(file, options);
    }


    /**
     * Reads the header of the region file into the {@link #header} buffer. When the channel has no bytes, it does
     * nothing.
     */
    private void readHeader() throws IOException {
        // Note that we don't synchronize on 'io' here: this method is only called from the constructor and as the
        // object is never available to any thread before the constructor has been called, no thread would ever be able
        // to write or read from the region file while the header is being read

        header.position(0);
        int bytesRead = io.read(header, 0);

        // New file was created so header is absent, we don't need to read anything
        if (bytesRead < 0)
            return;

        // We want our full header, if it's truncated we're malformed
        if (bytesRead < HEADER_SIZE) {
            if (lenient) {
                fixer.truncatedHeader(bytesRead, HEADER_SIZE);
                return;
            }

            throw new MalformedRegionFileException(
                "Truncated header, has only " + bytesRead + " of " + HEADER_SIZE + " bytes"
            );
        }

        // Mark necessary sectors as used
        long fileSize = io.size();

        for (int index = 0; index < SECTOR_INTS; index++) {
            int loc = locations.get(index);
            if (loc != 0) {
                int off = off(loc);
                int len = len(loc);

                if (off < 2) {
                    // Chunk overlaps header
                    if (!lenient)
                        throw new MalformedRegionFileException("Chunk offset overlaps header");

                    fixer.chunkOverlapsHeader(index);

                    // Repair: ignore chunk and make it empty
                    locations.put(index, 0);
                } else if (len == 0) {
                    // Chunk has offset but no sectors (not critical but warn fixer, by convention only when lenient)
                    if (lenient)
                        fixer.offsetZeroSectorChunk(index);

                    locations.put(index, 0);
                } else if (off * SECTOR_SIZE_L > fileSize) {
                    // Chunk is out of file space
                    if (!lenient)
                        throw new MalformedRegionFileException("Chunk payload is outside file space");

                    fixer.chunkOutOfFileSize(index);

                    // Repair: ignore chunk and make it empty
                    locations.put(index, 0);
                } else {
                    // Chunk exist, mark sectors as used
                    sectors.allocate(off, len);
                }
            }
        }
    }





    // =====================================================
    // FLUSHING AND CLOSING
    // =====================================================


    private void writeHeader() throws IOException {
        header.position(0);
        synchronized (io) {
            io.write(header, 0);
        }
    }

    /**
     * Adds padding to the end of the file to fill the last sector to 4096 bytes. If we don't do this before closing,
     * the file might fail to open next time (this padding is checked on opening a region file).
     */
    private void addLastSectorPadding() throws IOException {
        long currentSize = io.size();
        long paddedSize = sectors.getSectorSpace() * SECTOR_SIZE_L;
        if (currentSize < paddedSize) {
            // We only need to write the last padding byte, and the file channel will automatically extend the file size
            // to the padded size
            ByteBuffer zero = ZERO_BYTE_BUF.duplicate();
            zero.position(0);
            synchronized (io) {
                io.write(zero, paddedSize - 1);
            }
        } else if (currentSize > paddedSize) {
            // Truncate file, so that we discard any unused sectors
            // Minecraft goes the lazy way of just keeping any discarded sectors at the end of a region file. For
            // optimization purposes, we remove them using this simple operation.
            synchronized (io) {
                io.truncate(paddedSize);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            flush();
        } finally {
            io.close();
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            writeHeader();
            addLastSectorPadding();
        } finally {
            io.force(true);
        }
    }





    // =====================================================
    // READING A CHUNK
    // =====================================================


    /**
     * Opens an input stream to read the data from a specific chunk in this region file. The chunk coordinates MUST be
     * absolute coordinates, since external chunk files have a name with their absolute coordinates. The region-local
     * coordinates are automatically computed where needed.
     * <p>
     * The stream will be opened leniently, suppressing all malformed chunk errors and trying to repair from them, when
     * the region file was opened using the {@link #LENIENT} flag. In most cases of a malformed chunk, the chunk is
     * assumed to be empty when opening leniently. Note that this only suppresses malformed chunk errors: any other I/O
     * exception is still thrown.
     * </p>
     *
     * @param x The absolute chunk X
     * @param z The absolute chunk Z
     * @return An input stream that reads the bytes of the requested chunk data.
     *
     * @throws MalformedRegionFileException When the chunk is malformed and the stream was not opened leniently
     * @throws IOException                  When an I/O error occurs
     */
    public InputStream openInputStream(int x, int z) throws IOException {
        int loc = getLocation(x, z);

        if (loc == 0)
            return NullInputStream.INSTANCE;

        int off = off(loc);
        int len = len(loc);
        int offBytes = off * SECTOR_SIZE;
        int lenBytes = len * SECTOR_SIZE;

        // Read all the chunk's sectors into a ByteBuffer
        ByteBuffer buf = ByteBuffer.allocate(lenBytes);
        synchronized (io) {
            io.read(buf, offBytes);
        }
        buf.flip();

        if (buf.remaining() < CHUNK_HEADER_SIZE) {
            if (lenient)
                return fixer.truncatedChunkHeader(buf.remaining(), CHUNK_HEADER_SIZE, x, z);

            throw new MalformedRegionFileException(
                "Chunk [" + x + ", " + z + "] header is truncated: " +
                    "has only " + buf.remaining() + " of " + CHUNK_HEADER_SIZE + " bytes"
            );
        }

        // Chunk data format summary:
        // Bytes 1-4:   Unpadded chunk size (including header, excluding these bytes)
        // Byte 5:      Chunk header (not present if 'size' is 0, in that case no payload is present either)
        // Bytes 6-x:   Chunk payload ('size - 1' bytes, not present if header is absent)
        // After byte x the buffer may still contain some padding bytes to fill the last sector

        int unpaddedSize = buf.getInt();
        if (unpaddedSize == 0) {
            // No header means chunk is empty. We don't handle this as an error in non-lenient cases since the header
            // won't contain any useful information for empty chunks - Minecraft neither handles this as a critical
            // region file error so why would we do (at last there is no header that can indicate what kind of payload
            // we can expect, nor is there payload)

            if (lenient) // If lenient, let the fixer know
                fixer.zeroChunkSize(x, z);

            return NullInputStream.INSTANCE;
        }

        if (unpaddedSize < 0) {
            if (lenient)
                return fixer.negativeChunkSize(x, z, unpaddedSize);

            throw new MalformedRegionFileException(
                "Chunk [" + x + ", " + z + "] has negative size: " + unpaddedSize
            );
        }

        int header = buf.get() & 0xFF;
        boolean external = (header & EXTERNAL) != 0; // When true, chunk payload is stored in external file
        int comprId = header & COMPRESSION_TYPE; // Compression level
        int payloadSize = unpaddedSize - 1;

        Compression compr;
        if (comprId < 1 || comprId > 3) {
            // 'byRegionTypeId' would throw IllegalArgumentException in this situation, but since the region file is in
            // this case responsible for providing the compression type ID, we must also throw this as a
            // MalformedRegionFileException, hence an extra precondition check here, and in addition we can handle
            // leniency

            if (lenient) {
                compr = fixer.unknownCompression(x, z, comprId, compression);

                if (compr == null)
                    // In case of leniency, we can't repair chunk data if we don't know the correct compression type.
                    // The fixer didn't provide us with any probable compression type, so the best way around is to just
                    // discard unknown chunk data and assume the chunk is empty.
                    return NullInputStream.INSTANCE;

                // If we got a compression type from the fixer, we pretend nothing happened and continue as if the
                // compression type was actually known
            } else {
                // Non-lenient, just fail now
                throw new MalformedRegionFileException(
                    "Unknown compression type " + comprId + " for chunk [" + x + ", " + z + "]"
                );
            }
        } else {
            compr = Compression.byRegionTypeId(comprId);
        }

        if (external) { // Payload is stored in external chunk file
            if (payloadSize > 0 && !lenient) { // Repair: ignore internal payload and read from external file
                throw new MalformedRegionFileException(
                    "Chunk [" + x + ", " + z + "] has both internal and external payload"
                );
            }

            InputStream in = openExternalIn(x, z, compr, lenient);
            if (in != null) {
                return in;
            }
            // If 'in' is actually null that would mean we have repaired from an external file being absent. In that
            // case two scenarios are possible:

            // The most likely scenario now is that no payload is available, in that case we have to return a null
            // stream
            if (payloadSize <= 0)
                return NullInputStream.INSTANCE;

            // A less likely scenario is that there is actually payload available (which, in case of non-leniency would
            // already have caused an error above). In this scenario we just leave this if block and pretend this chunk
            // wasn't stored externally.
        }

        if (payloadSize > buf.remaining()) {
            if (lenient)
                // Chunk data may be corrupted, we can't repair from that so instead we go the safe way and assume the
                // chunk is empty
                return NullInputStream.INSTANCE;

            throw new MalformedRegionFileException(
                "Chunk [" + x + ", " + z + "] payload is truncated: " +
                    "has only " + buf.remaining() + " of " + payloadSize + " bytes"
            );
        }


        return openInternalIn(compr, buf, payloadSize);
    }

    /**
     * Wraps the given input stream into an uncompressing stream, given the compresion ID. If the given compression ID
     * is an unknown ID this will either open a null stream or fail upon non-leniency. The
     */
    private InputStream wrapInStream(Compression compr, InputStream in) throws IOException {
        InputStream wrapped = compr.createInStream(in);
        if (buffered)
            wrapped = new BufferedInputStream(wrapped);
        return wrapped;
    }

    private InputStream openExternalIn(int x, int z, Compression compr, boolean lenient) throws IOException {
        Path path = externalPayloadPath(x, z);
        if (!Files.isRegularFile(path)) { // No such file, then fail
            if (lenient)
                // Repair: try read internal payload if we have
                // If no internal payload exists either, assume the chunk is empty
                // Our task here is to return null, the rest gets handled in 'openInputStream'
                return null;

            throw new MalformedRegionFileException(
                "External chunk file for [" + x + ", " + z + "] does not exist"
            );
        }

        return wrapInStream(compr, Files.newInputStream(path));
    }

    private InputStream openInternalIn(Compression compr, ByteBuffer buf, int payloadSize) throws IOException {
        return wrapInStream(compr, new ByteArrayInputStream(buf.array(), buf.position(), payloadSize));
    }





    // =====================================================
    // VALIDATING A CHUNK
    // =====================================================


    public boolean doesChunkExist(int x, int z) {
        int loc = getLocation(x, z);

        if (loc == 0)
            return false; // No chunk specified, no chunk exists

        int off = off(loc);
        int len = len(loc);
        int offBytes = off * SECTOR_SIZE;
        int lenBytes = len * SECTOR_SIZE;

        // Read chunk header
        ByteBuffer buf = ByteBuffer.allocate(CHUNK_HEADER_SIZE);
        try {
            synchronized (io) {
                io.read(buf, offBytes);
            }
        } catch (IOException e) {
            return false; // IO error, in that case no chunk
        }

        if (buf.remaining() < CHUNK_HEADER_SIZE)
            return false; // Truncated header, no chunk


        int unpaddedSize = buf.getInt();
        if (unpaddedSize <= 0)
            return false; // No bytes or negative size, no chunk

        int header = buf.get() & 0xFF;
        boolean external = (header & EXTERNAL) != 0; // When true, chunk payload is stored in external file
        int comprId = header & COMPRESSION_TYPE; // Compression level
        int payloadSize = unpaddedSize - 1;

        if (comprId < 1 || comprId > 3)
            return false; // Invalid compression type, no chunk

        if (external) {
            if (payloadSize > 0)
                return false; // Internal and external payload = no payload

            // If chunk payload is external we can check if a payload exists by checking if the payload file exists
            return Files.isRegularFile(externalPayloadPath(x, z));
        }

        return payloadSize >= 0 && payloadSize <= lenBytes;
    }





    // =====================================================
    // WRITING A CHUNK
    // =====================================================


    public OutputStream openOutputStream(int x, int z) throws IOException {
        OutputStream out = compression.createOutStream(new ChunkOutputStream(x, z));
        if (buffered)
            out = new BufferedOutputStream(out);
        return out;
    }

    private void flushChunkBuffer(int x, int z, ByteBuffer buf) throws IOException {
        int index = index(x, z);

        int loc = locations.get(index);
        int oldOff = off(loc);
        int oldLen = len(loc);

        int bufSize = buf.remaining();

        int newLen = sectors(bufSize);
        int newOff;

        // A write resource
        Closeable writeResource;

        if (newLen >= INTERNAL_SIZE_LIMIT) {
            // Chunk exceeds size limit, write it to an external file
            // Chunk's new length becomes 1 as its payload is not in the region file
            newOff = sectors.reallocate(oldOff, oldLen, 1);
            newLen = 1;

            writeResource = writeExternalFile(x, z, buf);

            try {
                synchronized (io) {
                    // In the header we specify that the chunk is written externally, so we still need to write the
                    // chunk header
                    io.write(makeExternalHeader(), newOff * SECTOR_SIZE_L);
                }
            } catch (IOException e) {
                // Make sure we close our write resource when an IOException is thrown
                writeResource.close();
                throw e;
            }
        } else {
            // Chunk can be saved internally
            newOff = sectors.reallocate(oldOff, oldLen, newLen);

            // Remove any old chunk file when completed
            writeResource = () -> Files.deleteIfExists(externalPayloadPath(x, z));

            try {
                synchronized (io) {
                    // Write chunk sectors
                    io.write(buf, newOff * SECTOR_SIZE_L);
                }
            } catch (IOException e) {
                // Make sure we close our write resource when an IOException is thrown
                writeResource.close();
                throw e;
            }
        }

        // Use try-with-resources now to close our resource when we are done, or when an error occurs
        try (Closeable ignored = writeResource) {
            locations.put(index, loc(newOff, newLen));
            timestamps.put(index, secondsSinceEpoch());
            writeHeader(); // Flush header to the FileChannel
        }
    }

    /**
     * Creates a ByteBuffer containing a header for a chunk file that is being stored in an external chunk file.
     */
    private ByteBuffer makeExternalHeader() {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.putInt(1); // Size = 1
        buf.put((byte) (compression.getRegionTypeId() | EXTERNAL));
        buf.flip();
        return buf;
    }

    /**
     * Writes the chunk payload to an external file.
     */
    private Closeable writeExternalFile(int x, int z, ByteBuffer buf) throws IOException {
        // For thread safety, write in a temporary file. We move this file into place after we have flushed the chunk
        // header and the new region header to the region file.
        Path path = externalPayloadPath(x, z);
        Path temp = Files.createTempFile(directory, "tmp", null);

        try (FileChannel tmpChannel = FileChannel.open(temp, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            buf.position(5); // No need to write chunk header
            tmpChannel.write(buf);
        } catch (IOException e) {
            // Delete our temp file upon error (instead of moving it), we don't need to keep this file because the error
            // cancels all chunk writing.
            Files.delete(temp);
            throw e;
        }

        return () -> Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * Output stream for chunks. Chunk data is buffered in this stream until it is closed, after which it flushes all
     * chunk data and the (possibly new) region file header to the underlying file.
     */
    private class ChunkOutputStream extends ByteArrayOutputStream {
        private final int x;
        private final int z;

        ChunkOutputStream(int x, int z) {
            super(2 * SECTOR_SIZE);
            this.x = x;
            this.z = z;

            count = 4; // Skip first 4 bytes, we overwrite these with the payload size later
            write(compression.getRegionTypeId());
        }

        @Override
        public void close() throws IOException {
            ByteBuffer nioBuf = ByteBuffer.wrap(buf, 0, count);
            nioBuf.putInt(0, count - 4); // Exclude 4 payload size bytes
            flushChunkBuffer(x, z, nioBuf);
        }
    }




    // =====================================================
    // REMOVING A CHUNK
    // =====================================================


    public void removeChunk(int x, int z) throws IOException {
        int idx = index(x, z);

        int loc = locations.get(idx);
        if (loc == 0) return;

        int off = off(loc);
        int len = len(loc);
        sectors.free(off, len);

        locations.put(idx, 0);
        timestamps.put(idx, 0);

        try {
            Files.deleteIfExists(externalPayloadPath(x, z));
        } finally {
            writeHeader();
        }
    }



    private Path externalPayloadPath(int x, int z) {
        String s = "c." + x + "." + z + ".mcc";
        return directory.resolve(s);
    }

    private int getLocation(int x, int z) {
        return locations.get(index(x, z));
    }

    public boolean hasChunk(int x, int z) {
        return getLocation(x, z) != 0;
    }

    public int getTimestamp(int x, int z) {
        return timestamps.get(index(x, z));
    }


    // In any case a region file should be closed manually, but if it really happens that this RegionFile gets discarded
    // before being closed, we close it upon finalization to make sure no resources are left open.
    @Override
    protected void finalize() throws Throwable {
        close();
    }

    private static int loc(int off, int len) {
        return (off & 0xFFFFFF) << 8 | len & 0xFF;
    }

    private static int len(int loc) {
        return loc & 0xFF;
    }

    private static int off(int loc) {
        return loc >> 8 & 0xFFFFFF;
    }

    private static int index(int x, int z) {
        int rx = x & 0x1F;
        int rz = z & 0x1F;
        return rx + rz * 32;
    }

    private static int sectors(int chunkBytes) {
        return (chunkBytes + SECTOR_SIZE - 1) / SECTOR_SIZE;
    }

    private static long sectorsL(long chunkBytes) {
        return (chunkBytes + SECTOR_SIZE_L - 1) / SECTOR_SIZE_L;
    }

    private static int secondsSinceEpoch() {
        return (int) (Instant.now().toEpochMilli() / 1000);
    }
}
