package net.shadew.nbt4j.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.shadew.nbt4j.NbtReader;
import net.shadew.nbt4j.TagBuilder;
import net.shadew.nbt4j.region.RegionFile;
import net.shadew.nbt4j.region.RegionFileCache;
import net.shadew.nbt4j.snbt.SnbtSerializer;
import net.shadew.nbt4j.tree.Tag;

public class NBTStuff {
    public static void main(String[] args) throws IOException {
        File regionDir = new File("testfiles/region");

        Tag tag;
        try (RegionFileCache region = new RegionFileCache(regionDir.toPath(), null, RegionFile.DSYNC | RegionFile.VERBOSE | RegionFile.BUFFERED, 16, true)) {
            try (InputStream in = region.openInputStream(0, 0)) {
                NbtReader reader = new NbtReader(new DataInputStream(in));
                TagBuilder builder = new TagBuilder();
                reader.accept(builder);
                reader.throwIoException();
                tag = builder.tag();
            }
        }

        SnbtSerializer snbt = new SnbtSerializer()
            .ansiHighlight(true)
            .indentString("    ")
            .quoteAllKeys(true);

        snbt.writeTag(tag);
        System.out.println(snbt);
    }
}
