package net.shadew.nbt4j;

import java.io.*;

import net.shadew.nbt4j.tree.CompoundTag;
import net.shadew.nbt4j.tree.EndTag;
import net.shadew.nbt4j.tree.Tag;
import net.shadew.nbt4j.util.NBTException;

public class NBTIO {
    public static Tag readTag(DataInput in) throws IOException {
        TagType type = TagType.readType(in);
        if (type == TagType.END) {
            // End tag has no name and does not deserialize, just return it's instance
            return EndTag.INSTANCE;
        }

        in.readUTF(); // Tag name, which we ignore
        return type.read(in, 0);
    }

    public static void writeTag(Tag tag, DataOutput out) throws IOException {
        TagType type = tag.type();
        if (!type.isValidImplementation(tag)) {
            throw new NBTException("Tag implementation is not one of the supported implementations");
        }

        type.writeType(out);
        out.writeUTF(""); // Tag name, which gets ignored
        type.write(tag, out);
    }

    public static CompoundTag readNBT(DataInput in) throws IOException {
        // Deliberately not delegating to readTag, so we can catch a non-compound without having to do the full
        // deserialization
        TagType type = TagType.readType(in);
        if (type != TagType.COMPOUND) {
            throw new NBTException("NBT data does not have TAG_Compound as root");
        }

        in.readUTF(); // Tag name, which we ignore
        return (CompoundTag) type.read(in, 0);
    }

    public static void writeNBT(CompoundTag tag, DataOutput out) throws IOException {
        writeTag(tag, out);
    }

    public static CompoundTag readNBT(InputStream in, Compression compression) throws IOException {
        return readNBT(compression.createIn(in));
    }

    public static void writeNBT(CompoundTag tag, OutputStream out, Compression compression) throws IOException {
        writeNBT(tag, compression.createOut(out));
    }

    public static CompoundTag readNBT(File in, Compression compression) throws IOException {
        return readNBT(new FileInputStream(in), compression);
    }

    public static void writeNBT(CompoundTag tag, File out, Compression compression) throws IOException {
        writeNBT(tag, new FileOutputStream(out), compression);
    }

    public static CompoundTag readNBT(byte[] in, Compression compression) throws IOException {
        return readNBT(new ByteArrayInputStream(in), compression);
    }

    public static byte[] writeNBT(CompoundTag tag, Compression compression) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeNBT(tag, baos, compression);
        return baos.toByteArray();
    }
}
