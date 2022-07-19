package net.shadew.nbt4j.region;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class RegionFileCache implements Closeable, Flushable {
    private final Path directory;
    private final RegionFileFixer fixer;
    private final int openFlags;
    private final Map<RegionPos, RegionFile> fileMap = new HashMap<>();
    private final List<RegionPos> recentlyUsed = new ArrayList<>();
    private final int maxCache;
    private final boolean anvil;

    public RegionFileCache(Path directory, RegionFileFixer fixer, int openFlags, int maxCache, boolean anvil) {
        this.directory = directory;
        this.fixer = fixer;
        this.openFlags = openFlags;
        this.maxCache = maxCache;
        this.anvil = anvil;
    }

    public InputStream openInputStream(int x, int z) throws IOException {
        int rx = x >> 5;
        int rz = z >> 5;

        RegionFile file = getRegionFile(new RegionPos(rx, rz));
        return file.openInputStream(x, z);
    }

    public OutputStream openOutputStream(int x, int z) throws IOException {
        int rx = x >> 5;
        int rz = z >> 5;

        RegionFile file = getRegionFile(new RegionPos(rx, rz));
        return file.openOutputStream(x, z);
    }

    public boolean doesChunkExist(int x, int z) {
        int rx = x >> 5;
        int rz = z >> 5;

        try {
            RegionFile file = getRegionFile(new RegionPos(rx, rz));
            return file.doesChunkExist(x, z);
        } catch (IOException exc) {
            return false;
        }
    }

    public void removeChunk(int x, int z) throws IOException {
        int rx = x >> 5;
        int rz = z >> 5;

        RegionFile file = getRegionFile(new RegionPos(rx, rz));
        file.removeChunk(x, z);
    }

    private void unloadRegionFile(RegionPos pos) throws IOException {
        recentlyUsed.remove(pos);
        RegionFile file = fileMap.remove(pos);
        if (file == null) return;
        file.close();
    }

    private RegionFile loadRegionFile(RegionPos pos) throws IOException {
        String pathName = "r." + pos.x + "." + pos.z + "." + (anvil ? "mca" : "mcr");
        Path path = directory.resolve(pathName);

        RegionFile file;
        if (fixer != null) {
            file = new RegionFile(directory, path, openFlags, fixer);
        } else {
            file = new RegionFile(directory, path, openFlags);
        }

        fileMap.put(pos, file);
        return file;
    }

    private RegionFile getRegionFile(RegionPos pos) throws IOException {
        RegionFile file = fileMap.get(pos);
        if (file == null) {
            file = loadRegionFile(pos);
        }

        // Move position to first
        recentlyUsed.remove(pos);
        recentlyUsed.add(0, pos);

        // Unload cache
        while (recentlyUsed.size() > maxCache) {
            unloadRegionFile(recentlyUsed.remove(maxCache));
        }
        return file;
    }

    @Override
    public void flush() throws IOException {
        for (RegionFile file : fileMap.values()) {
            file.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (RegionFile file : fileMap.values()) {
            file.close();
        }
        fileMap.clear();
        recentlyUsed.clear();
    }

    private static class RegionPos {
        final int x;
        final int z;

        RegionPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RegionPos)) return false;
            RegionPos regionPos = (RegionPos) o;
            return x == regionPos.x && z == regionPos.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }
}
