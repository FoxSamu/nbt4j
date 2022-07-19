package net.shadew.nbt4j.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.shadew.nbt4j.NbtVisitor;
import net.shadew.nbt4j.TagType;
import net.shadew.nbt4j.util.NbtException;

public final class ListTag implements List<Tag>, RandomAccess, Tag {
    private TagType elementType = TagType.END;
    private final ArrayList<Tag> elements = new ArrayList<>();

    public ListTag() {

    }

    public ListTag(TagType elementType) {
        this.elementType = elementType;
    }

    private ListTag(Collection<? extends Tag> copy) {
        super();
        addAll(copy);
    }

    public TagType getElementType() {
        return elementType;
    }

    @Override
    public TagType type() {
        return TagType.LIST;
    }

    @Override
    public ListTag copy() {
        return of(this);
    }

    private void validateNewElement(Tag t) {
        if (t == null) {
            throw new NullPointerException("Cannot add null tags");
        }
        if (t == this) {
            throw new NullPointerException("Cannot at self to self");
        }
        if (elementType == TagType.END) {
            elementType = t.type();
        }
        if (!elementType.isValidImplementation(t)) {
            if (t.type() == elementType) {
                throw new IllegalArgumentException("Tag implementation is not one of the supported implementations");
            } else {
                throw new IllegalStateException("Trying to add tag of incorrect type, expected " + elementType + " but got " + t.type());
            }
        }
    }

    @Override
    public Tag set(int index, Tag element) {
        validateNewElement(element);
        return elements.set(index, element);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public Iterator<Tag> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean add(Tag tag) {
        validateNewElement(tag);
        return elements.add(tag);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public void add(int index, Tag element) {
        validateNewElement(element);
        elements.add(index, element);
    }

    @Override
    public Tag remove(int index) {
        return elements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @Override
    public ListIterator<Tag> listIterator() {
        return elements.listIterator();
    }

    @Override
    public ListIterator<Tag> listIterator(int index) {
        return elements.listIterator(index);
    }

    @Override
    public List<Tag> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }

    @Override
    public boolean addAll(Collection<? extends Tag> c) {
        c.forEach(this::validateNewElement);
        return elements.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Tag> c) {
        c.forEach(this::validateNewElement);
        return elements.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public Tag get(int index) {
        return elements.get(index);
    }

    public NumericTag getNumeric(int index) {
        try {
            return (NumericTag) get(index);
        } catch (ClassCastException ignored) { }
        return ByteTag.FALSE;
    }

    public byte getByte(int index) {
        return getNumeric(index).asByte();
    }

    public short getShort(int index) {
        return getNumeric(index).asShort();
    }

    public int getInt(int index) {
        return getNumeric(index).asInt();
    }

    public long getLong(int index) {
        return getNumeric(index).asLong();
    }

    public float getFloat(int index) {
        return getNumeric(index).asFloat();
    }

    public double getDouble(int index) {
        return getNumeric(index).asDouble();
    }

    public boolean getBoolean(int index) {
        return getNumeric(index).asBoolean();
    }

    public char getChar(int index) {
        return getNumeric(index).asChar();
    }

    private <T extends Tag, V> V getTypedTag(int index, Class<T> type, Function<T, V> get, Supplier<V> def) {
        try {
            return get.apply(type.cast(get(index)));
        } catch (ClassCastException ignored) { }
        return def.get();
    }

    public String getString(int index) {
        return getTypedTag(index, StringTag.class, StringTag::asString, () -> "");
    }

    public byte[] getByteArray(int index) {
        return getTypedTag(index, ByteArrayTag.class, ByteArrayTag::bytes, () -> new byte[0]);
    }

    public int[] getIntArray(int index) {
        return getTypedTag(index, IntArrayTag.class, IntArrayTag::ints, () -> new int[0]);
    }

    public long[] getLongArray(int index) {
        return getTypedTag(index, LongArrayTag.class, LongArrayTag::longs, () -> new long[0]);
    }

    public ByteArrayTag getByteArrayTag(int index) {
        return getTypedTag(index, ByteArrayTag.class, Function.identity(), ByteArrayTag::empty);
    }

    public IntArrayTag getIntArrayTag(int index) {
        return getTypedTag(index, IntArrayTag.class, Function.identity(), IntArrayTag::empty);
    }

    public LongArrayTag getLongArrayTag(int index) {
        return getTypedTag(index, LongArrayTag.class, Function.identity(), LongArrayTag::empty);
    }

    public ListTag getListTag(int index) {
        return getTypedTag(index, ListTag.class, Function.identity(), ListTag::new);
    }

    public CompoundTag getCompoundTag(int index) {
        return getTypedTag(index, CompoundTag.class, Function.identity(), CompoundTag::new);
    }

    public Tag setByte(int index, int value) {
        return set(index, ByteTag.of(value));
    }

    public Tag setByte(int index, byte value) {
        return set(index, ByteTag.of(value));
    }

    public Tag setShort(int index, int value) {
        return set(index, ShortTag.of(value));
    }

    public Tag setShort(int index, short value) {
        return set(index, ShortTag.of(value));
    }

    public Tag setInt(int index, int value) {
        return set(index, IntTag.of(value));
    }

    public Tag setLong(int index, long value) {
        return set(index, LongTag.of(value));
    }

    public Tag setFloat(int index, float value) {
        return set(index, FloatTag.of(value));
    }

    public Tag setDouble(int index, double value) {
        return set(index, DoubleTag.of(value));
    }

    public Tag setBoolean(int index, boolean value) {
        return set(index, ByteTag.of(value));
    }

    public Tag setChar(int index, char value) {
        return set(index, ShortTag.of(value));
    }

    public Tag setString(int index, String value) {
        return set(index, StringTag.of(value));
    }

    public Tag setByteArray(int index, byte[] value) {
        return set(index, ByteArrayTag.of(value));
    }

    public Tag setIntArray(int index, int[] value) {
        return set(index, IntArrayTag.of(value));
    }

    public Tag setLongArray(int index, long[] value) {
        return set(index, LongArrayTag.of(value));
    }

    public void addByte(int index, int value) {
        add(index, ByteTag.of(value));
    }

    public void addByte(int index, byte value) {
        add(index, ByteTag.of(value));
    }

    public void addShort(int index, int value) {
        add(index, ShortTag.of(value));
    }

    public void addShort(int index, short value) {
        add(index, ShortTag.of(value));
    }

    public void addInt(int index, int value) {
        add(index, IntTag.of(value));
    }

    public void addLong(int index, long value) {
        add(index, LongTag.of(value));
    }

    public void addFloat(int index, float value) {
        add(index, FloatTag.of(value));
    }

    public void addDouble(int index, double value) {
        add(index, DoubleTag.of(value));
    }

    public void addBoolean(int index, boolean value) {
        add(index, ByteTag.of(value));
    }

    public void addChar(int index, char value) {
        add(index, ShortTag.of(value));
    }

    public void addString(int index, String value) {
        add(index, StringTag.of(value));
    }

    public void addByteArray(int index, byte[] value) {
        add(index, ByteArrayTag.of(value));
    }

    public void addIntArray(int index, int[] value) {
        add(index, IntArrayTag.of(value));
    }

    public void addLongArray(int index, long[] value) {
        add(index, LongArrayTag.of(value));
    }

    public boolean addByte(int value) {
        return add(ByteTag.of(value));
    }

    public boolean addByte(byte value) {
        return add(ByteTag.of(value));
    }

    public boolean addShort(int value) {
        return add(ShortTag.of(value));
    }

    public boolean addShort(short value) {
        return add(ShortTag.of(value));
    }

    public boolean addInt(int value) {
        return add(IntTag.of(value));
    }

    public boolean addLong(long value) {
        return add(LongTag.of(value));
    }

    public boolean addFloat(float value) {
        return add(FloatTag.of(value));
    }

    public boolean addDouble(double value) {
        return add(DoubleTag.of(value));
    }

    public boolean addBoolean(boolean value) {
        return add(ByteTag.of(value));
    }

    public boolean addChar(char value) {
        return add(ShortTag.of(value));
    }

    public boolean addString(String value) {
        return add(StringTag.of(value));
    }

    public boolean addByteArray(byte[] value) {
        return add(ByteArrayTag.of(value));
    }

    public boolean addIntArray(int[] value) {
        return add(IntArrayTag.of(value));
    }

    public boolean addLongArray(long[] value) {
        return add(LongArrayTag.of(value));
    }

    public static ListTag of(Collection<? extends Tag> c) {
        return new ListTag(c);
    }

    public static ListTag of(byte... v) {
        ListTag tag = new ListTag(TagType.BYTE);
        for (byte b : v) tag.addByte(b);
        return tag;
    }

    public static ListTag of(short... v) {
        ListTag tag = new ListTag(TagType.SHORT);
        for (short s : v) tag.addShort(s);
        return tag;
    }

    public static ListTag of(int... v) {
        ListTag tag = new ListTag(TagType.INT);
        for (int i : v) tag.addInt(i);
        return tag;
    }

    public static ListTag of(long... v) {
        ListTag tag = new ListTag(TagType.LONG);
        for (long l : v) tag.addLong(l);
        return tag;
    }

    public static ListTag of(float... v) {
        ListTag tag = new ListTag(TagType.FLOAT);
        for (float f : v) tag.addFloat(f);
        return tag;
    }

    public static ListTag of(double... v) {
        ListTag tag = new ListTag(TagType.DOUBLE);
        for (double d : v) tag.addDouble(d);
        return tag;
    }

    public static ListTag of(boolean... v) {
        ListTag tag = new ListTag(TagType.BYTE);
        for (boolean b : v) tag.addBoolean(b);
        return tag;
    }

    public static ListTag of(char... v) {
        ListTag tag = new ListTag(TagType.SHORT);
        for (char c : v) tag.addChar(c);
        return tag;
    }

    public static ListTag of(String... v) {
        ListTag tag = new ListTag(TagType.STRING);
        for (String s : v) tag.addString(s);
        return tag;
    }

    public static ListTag of(ByteArrayTag... v) {
        ListTag tag = new ListTag(TagType.BYTE_ARRAY);
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static ListTag of(IntArrayTag... v) {
        ListTag tag = new ListTag(TagType.INT_ARRAY);
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static ListTag of(LongArrayTag... v) {
        ListTag tag = new ListTag(TagType.LONG_ARRAY);
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static ListTag of(ListTag... v) {
        ListTag tag = new ListTag(TagType.LIST);
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static ListTag of(CompoundTag... v) {
        ListTag tag = new ListTag(TagType.COMPOUND);
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static ListTag of(Tag... v) {
        ListTag tag = new ListTag();
        tag.addAll(Arrays.asList(v));
        return tag;
    }

    public static long countBytes(ListTag tag) {
        long bytes = 5; // Element type (1) and length (4)
        for (Tag element : tag) {
            bytes += element.type().countBytes(element);
        }
        return bytes;
    }

    public static void serialize(ListTag tag, DataOutput out) throws IOException {
        TagType type = tag.elementType;
        if (type == TagType.END && !tag.isEmpty()) {
            throw new NbtException("Cannot serialize TAG_List that is not empty but has TAG_End element type");
        }

        type.writeType(out);
        out.writeInt(tag.size());
        for (Tag element : tag) {
            if (!type.isValidImplementation(element)) {
                throw new NbtException("Cannot serialize tag of incorrect type in TAG_List");
            }
            type.write(element, out);
        }
    }

    public static ListTag deserialize(DataInput in, int nesting) throws IOException {
        TagType type = TagType.readType(in);
        int length = in.readInt();
        if (length < 0) {
            throw new NbtException("Cannot deserialize TAG_List with negative length (" + length + ")");
        }
        if (type == TagType.END && length != 0) {
            throw new NbtException("Cannot deserialize nonempty TAG_List with TAG_End element type");
        }

        ListTag tag = new ListTag(type);
        while (length > 0) {
            tag.add(type.read(in, nesting + 1));
            length--;
        }
        return tag;
    }

    @Override
    public String toString() {
        return "TAG_List[" + size() + "]";
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        NbtVisitor v = visitor.visitList(elementType, size(), name);
        if (v == null) return;

        for (Tag t : this)
            t.accept(v);
        v.visitListEnd();
    }
}
