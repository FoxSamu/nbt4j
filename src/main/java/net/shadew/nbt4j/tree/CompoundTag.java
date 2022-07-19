package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.NbtException;

public final class CompoundTag extends Tag {
    private LinkedHashMap<String, Tag> tags = new LinkedHashMap<>();

    public CompoundTag() {
    }

    private CompoundTag(Map<String, ? extends Tag> elements) {
        elements.forEach(this::put);
    }

    private CompoundTag(CompoundTag elements) {
        tags.putAll(elements.tags);
    }

    @Override
    public TagType type() {
        return TagType.COMPOUND;
    }

    @Override
    public Tag copy() {
        return of(this);
    }

    private void validateNewElement(String name, Tag tag) {
        if (name == null) {
            throw new NullPointerException("Tag cannot have null name");
        }
        if (tag == null) {
            throw new NullPointerException("Tag cannot be null");
        }
        if (!tag.type().isValidImplementation(tag)) {
            throw new IllegalArgumentException("Tag implementation is not one of the supported implementations");
        }
        if (tag == this) {
            throw new IllegalArgumentException("Cannot add self to self");
        }
    }

    public void sort() {
        Map<String, Tag> old = tags;
        tags = new LinkedHashMap<>();
        old.keySet().stream().sorted().forEach(k -> tags.put(k, old.get(k)));
    }

    public void deepSort() {
        Map<String, Tag> old = tags;
        tags = new LinkedHashMap<>();
        old.keySet().stream().sorted().forEach(k -> {
            Tag t = old.get(k);
            if (t instanceof CompoundTag)
                ((CompoundTag) t).deepSort();
            tags.put(k, t);
        });
    }

    public int size() {
        return tags.size();
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(tags.keySet());
    }

    public Set<Map.Entry<String, Tag>> entrySet() {
        return Collections.unmodifiableSet(tags.entrySet());
    }

    public Collection<Tag> tags() {
        return Collections.unmodifiableCollection(tags.values());
    }

    public Tag get(String name) {
        return tags.get(name);
    }

    public Tag put(String name, Tag tag) {
        validateNewElement(name, tag);
        return tags.put(name, tag);
    }

    public Tag remove(String name) {
        return tags.remove(name);
    }

    public boolean contains(String name) {
        return tags.containsKey(name);
    }

    public boolean containsTyped(String name, TagType type) {
        return contains(name) && type.isValidImplementation(get(name));
    }

    public boolean containsNumeric(String name) {
        return contains(name) && get(name).type().isNumeric();
    }

    public boolean containsByte(String name) {
        return containsTyped(name, TagType.BYTE);
    }

    public boolean containsShort(String name) {
        return containsTyped(name, TagType.SHORT);
    }

    public boolean containsInt(String name) {
        return containsTyped(name, TagType.INT);
    }

    public boolean containsLong(String name) {
        return containsTyped(name, TagType.LONG);
    }

    public boolean containsFloat(String name) {
        return containsTyped(name, TagType.FLOAT);
    }

    public boolean containsDouble(String name) {
        return containsTyped(name, TagType.DOUBLE);
    }

    public boolean containsBoolean(String name) {
        return containsTyped(name, TagType.BYTE);
    }

    public boolean containsChar(String name) {
        return containsTyped(name, TagType.SHORT);
    }

    public boolean containsString(String name) {
        return containsTyped(name, TagType.STRING);
    }

    public boolean containsByteArray(String name) {
        return containsTyped(name, TagType.BYTE_ARRAY);
    }

    public boolean containsIntArray(String name) {
        return containsTyped(name, TagType.INT_ARRAY);
    }

    public boolean containsLongArray(String name) {
        return containsTyped(name, TagType.LONG_ARRAY);
    }

    public boolean containsListTag(String name) {
        return containsTyped(name, TagType.LIST);
    }

    public boolean containsCompoundTag(String name) {
        return containsTyped(name, TagType.COMPOUND);
    }

    public NumericTag getNumeric(String name) {
        return getNumericOrDefault(name, ByteTag.FALSE);
    }

    public NumericTag getNumericOrDefault(String name, NumericTag def) {
        if (containsNumeric(name))
            try {
                return (NumericTag) get(name);
            } catch (ClassCastException ignored) {
            }
        return def;
    }

    public byte getByte(String name) {
        return getNumeric(name).asByte();
    }

    public byte getByteOrDefault(String name, byte def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asByte();
    }

    public byte getByteOrDefault(String name, int def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? (byte) def : tag.asByte();
    }

    public short getShort(String name) {
        return getNumeric(name).asShort();
    }

    public short getShortOrDefault(String name, short def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asShort();
    }

    public short getShortOrDefault(String name, int def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? (short) def : tag.asShort();
    }

    public int getInt(String name) {
        return getNumeric(name).asInt();
    }

    public int getIntOrDefault(String name, int def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asInt();
    }

    public long getLong(String name) {
        return getNumeric(name).asLong();
    }

    public long getLongOrDefault(String name, long def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asLong();
    }

    public float getFloat(String name) {
        return getNumeric(name).asFloat();
    }

    public float getFloatOrDefault(String name, float def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asFloat();
    }

    public double getDouble(String name) {
        return getNumeric(name).asDouble();
    }

    public double getDoubleOrDefault(String name, double def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asDouble();
    }

    public boolean getBoolean(String name) {
        return getNumeric(name).asBoolean();
    }

    public boolean getBooleanOrDefault(String name, boolean def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asBoolean();
    }

    public char getChar(String name) {
        return getNumeric(name).asChar();
    }

    public char getCharOrDefault(String name, char def) {
        NumericTag tag = getNumericOrDefault(name, null);
        return tag == null ? def : tag.asChar();
    }

    private <T extends Tag, V> V getTypedTag(String name, Class<T> type, Function<T, V> get, Supplier<V> def) {
        if (contains(name))
            try {
                return get.apply(type.cast(get(name)));
            } catch (ClassCastException ignored) { }
        return def.get();
    }

    public String getString(String name) {
        return getTypedTag(name, StringTag.class, StringTag::asString, () -> "");
    }

    public String getStringOrDefault(String name, String def) {
        return getTypedTag(name, StringTag.class, StringTag::asString, () -> def);
    }

    public String getStringOrDefault(String name, Supplier<String> def) {
        return getTypedTag(name, StringTag.class, StringTag::asString, def);
    }

    public byte[] getByteArray(String name) {
        return getTypedTag(name, ByteArrayTag.class, ByteArrayTag::bytes, () -> new byte[0]);
    }

    public byte[] getByteArrayOrDefault(String name, byte[] def) {
        return getTypedTag(name, ByteArrayTag.class, ByteArrayTag::bytes, () -> def);
    }

    public byte[] getByteArrayOrDefault(String name, Supplier<byte[]> def) {
        return getTypedTag(name, ByteArrayTag.class, ByteArrayTag::bytes, def);
    }

    public int[] getIntArray(String name) {
        return getTypedTag(name, IntArrayTag.class, IntArrayTag::ints, () -> new int[0]);
    }

    public int[] getIntArrayOrDefault(String name, int[] def) {
        return getTypedTag(name, IntArrayTag.class, IntArrayTag::ints, () -> def);
    }

    public int[] getIntArrayOrDefault(String name, Supplier<int[]> def) {
        return getTypedTag(name, IntArrayTag.class, IntArrayTag::ints, def);
    }

    public long[] getLongArray(String name) {
        return getTypedTag(name, LongArrayTag.class, LongArrayTag::longs, () -> new long[0]);
    }

    public long[] getLongArrayOrDefault(String name, long[] def) {
        return getTypedTag(name, LongArrayTag.class, LongArrayTag::longs, () -> def);
    }

    public long[] getLongArrayOrDefault(String name, Supplier<long[]> def) {
        return getTypedTag(name, LongArrayTag.class, LongArrayTag::longs, def);
    }

    public ByteArrayTag getByteArrayTag(String name) {
        return getTypedTag(name, ByteArrayTag.class, Function.identity(), ByteArrayTag::empty);
    }

    public ByteArrayTag getByteArrayTagOrDefault(String name, ByteArrayTag def) {
        return getTypedTag(name, ByteArrayTag.class, Function.identity(), () -> def);
    }

    public ByteArrayTag getByteArrayTagOrDefault(String name, Supplier<ByteArrayTag> def) {
        return getTypedTag(name, ByteArrayTag.class, Function.identity(), def);
    }

    public IntArrayTag getIntArrayTag(String name) {
        return getTypedTag(name, IntArrayTag.class, Function.identity(), IntArrayTag::empty);
    }

    public IntArrayTag getIntArrayTagOrDefault(String name, IntArrayTag def) {
        return getTypedTag(name, IntArrayTag.class, Function.identity(), () -> def);
    }

    public IntArrayTag getIntArrayTagOrDefault(String name, Supplier<IntArrayTag> def) {
        return getTypedTag(name, IntArrayTag.class, Function.identity(), def);
    }

    public LongArrayTag getLongArrayTag(String name) {
        return getTypedTag(name, LongArrayTag.class, Function.identity(), LongArrayTag::empty);
    }

    public LongArrayTag getLongArrayTagOrDefault(String name, LongArrayTag def) {
        return getTypedTag(name, LongArrayTag.class, Function.identity(), () -> def);
    }

    public LongArrayTag getLongArrayTagOrDefault(String name, Supplier<LongArrayTag> def) {
        return getTypedTag(name, LongArrayTag.class, Function.identity(), def);
    }

    public ListTag getListTag(String name) {
        return getTypedTag(name, ListTag.class, Function.identity(), ListTag::new);
    }

    public ListTag getListTagOrDefault(String name, ListTag def) {
        return getTypedTag(name, ListTag.class, Function.identity(), () -> def);
    }

    public ListTag getListTagOrDefault(String name, Supplier<ListTag> def) {
        return getTypedTag(name, ListTag.class, Function.identity(), def);
    }

    public CompoundTag getCompoundTag(String name) {
        return getTypedTag(name, CompoundTag.class, Function.identity(), CompoundTag::new);
    }

    public CompoundTag getCompoundTagOrDefault(String name, CompoundTag def) {
        return getTypedTag(name, CompoundTag.class, Function.identity(), () -> def);
    }

    public CompoundTag getCompoundTagOrDefault(String name, Supplier<CompoundTag> def) {
        return getTypedTag(name, CompoundTag.class, Function.identity(), def);
    }

    public Tag putByte(String name, byte v) {
        return put(name, ByteTag.of(v));
    }

    public Tag putByte(String name, int v) {
        return put(name, ByteTag.of(v));
    }

    public Tag putShort(String name, short v) {
        return put(name, ShortTag.of(v));
    }

    public Tag putShort(String name, int v) {
        return put(name, ShortTag.of(v));
    }

    public Tag putInt(String name, int v) {
        return put(name, IntTag.of(v));
    }

    public Tag putLong(String name, long v) {
        return put(name, LongTag.of(v));
    }

    public Tag putFloat(String name, float v) {
        return put(name, FloatTag.of(v));
    }

    public Tag putDouble(String name, double v) {
        return put(name, DoubleTag.of(v));
    }

    public Tag putBoolean(String name, boolean v) {
        return put(name, ByteTag.of(v));
    }

    public Tag putChar(String name, char v) {
        return put(name, ShortTag.of(v));
    }

    public Tag putString(String name, String v) {
        return put(name, StringTag.of(v));
    }

    public Tag putByteArray(String name, byte[] v) {
        return put(name, ByteArrayTag.of(v));
    }

    public Tag putIntArray(String name, int[] v) {
        return put(name, IntArrayTag.of(v));
    }

    public Tag putLongArray(String name, long[] v) {
        return put(name, LongArrayTag.of(v));
    }

    public static CompoundTag of(Map<String, ? extends Tag> elements) {
        return new CompoundTag(elements);
    }

    public static CompoundTag of(CompoundTag o) {
        return new CompoundTag(o);
    }

    public static long countBytes(CompoundTag tag) {
        long bytes = 1; // 1 for TAG_End ID

        for (Map.Entry<String, Tag> entry : tag.tags.entrySet()) {
            bytes += 1; // Type ID
            bytes += StringTag.countBytes(entry.getKey()); // Name

            Tag element = entry.getValue();
            bytes += element.type().countBytes(element); // Payload
        }
        return bytes;
    }

    public static void serialize(CompoundTag tag, DataOutput out) throws IOException {
        for (Map.Entry<String, Tag> entry : tag.tags.entrySet()) {
            String name = entry.getKey();
            Tag element = entry.getValue();
            TagType type = element.type();

            if (!type.isValidImplementation(element)) {
                throw new NbtException("Cannot serialize tag of unsupported implementation in TAG_Compound");
            }

            type.writeType(out);
            out.writeUTF(name);
            type.write(element, out);
        }
        // Terminator
        TagType.END.writeType(out);
    }

    public static CompoundTag deserialize(DataInput in, int nesting) throws IOException {
        CompoundTag tag = new CompoundTag();
        TagType type;
        while ((type = TagType.readType(in)) != TagType.END) {
            String name = in.readUTF();
            if (tag.contains(name)) {
                throw new NbtException("Tag with name '" + name + "' exists twice in TAG_Compound");
            }
            Tag element = type.read(in, nesting + 1);
            tag.put(name, element);
        }
        return tag;
    }

    @Override
    public String toString() {
        return "TAG_Compound[" + tags.size() + "]";
    }
}
