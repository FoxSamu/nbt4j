package net.shadew.nbt4j.test;

import java.io.*;
import java.util.zip.GZIPInputStream;

import net.shadew.nbt4j.NbtReader;
import net.shadew.nbt4j.NbtWriter;
import net.shadew.nbt4j.TagBuilder;
import net.shadew.nbt4j.snbt.SnbtSerializer;
import net.shadew.nbt4j.tree.Tag;

public class NBTStuff {
    public static void main(String[] args) throws IOException {
        File file = new File("testfiles/level.dat");
        File file1 = new File("testfiles/level2.dat");

        Tag tag;
        try (InputStream in = new GZIPInputStream(new FileInputStream(file))) {
            NbtReader reader = new NbtReader(new DataInputStream(in));
            TagBuilder builder = new TagBuilder();
            reader.accept(builder);
            reader.throwIoException();
            tag = builder.tag();
        }

        SnbtSerializer snbt = new SnbtSerializer()
            .ansiHighlight(true)
            .indentString("    ")
            .quoteAllKeys(true);

        snbt.writeTag(tag);
        System.out.println(snbt);

        NbtWriter writer = new NbtWriter(new DataOutputStream(new FileOutputStream(file1)));
        tag.accept(writer);
        writer.throwIoException();
    }
}
