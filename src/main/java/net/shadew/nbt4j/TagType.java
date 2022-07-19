package net.shadew.nbt4j;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.shadew.nbt4j.tree.*;
import net.shadew.nbt4j.util.NbtException;

/**
 * An enum with all NBT tag types. The {@link Tag} implementations supported by NBT4j are limited to the {@link
 * #getTypeClass()} values of the enum constants in this enum.
 */
public enum TagType {
    /**
     * The {@code TAG_End} type, which has binary ID {@code 0} and is resembled by {@link EndTag}. Note that this type
     * is special and must not be used right away as a tag.
     */
    END(0, "TAG_End", EndTag.class),

    /**
     * The {@code TAG_Byte} type, which has binary ID {@code 1} and is resembled by {@link ByteTag}.
     */
    BYTE(1, "TAG_Byte", ByteTag.class),

    /**
     * The {@code TAG_Short} type, which has binary ID {@code 2} and is resembled by {@link ShortTag}.
     */
    SHORT(2, "TAG_Short", ShortTag.class),

    /**
     * The {@code TAG_Int} type, which has binary ID {@code 3} and is resembled by {@link IntTag}.
     */
    INT(3, "TAG_Int", IntTag.class),

    /**
     * The {@code TAG_Long} type, which has binary ID {@code 4} and is resembled by {@link LongTag}.
     */
    LONG(4, "TAG_Long", LongTag.class),

    /**
     * The {@code TAG_Float} type, which has binary ID {@code 5} and is resembled by {@link FloatTag}.
     */
    FLOAT(5, "TAG_Float", FloatTag.class),

    /**
     * The {@code TAG_Double} type, which has binary ID {@code 6} and is resembled by {@link DoubleTag}.
     */
    DOUBLE(6, "TAG_Double", DoubleTag.class),

    /**
     * The {@code TAG_Byte_Array} type, which has binary ID {@code 7} and is resembled by {@link ByteArrayTag}.
     */
    BYTE_ARRAY(7, "TAG_Byte_Array", ByteArrayTag.class),

    /**
     * The {@code TAG_String} type, which has binary ID {@code 8} and is resembled by {@link StringTag}.
     */
    STRING(8, "TAG_String", StringTag.class),

    /**
     * The {@code TAG_List} type, which has binary ID {@code 9} and is resembled by {@link ListTag}.
     */
    LIST(9, "TAG_List", ListTag.class),

    /**
     * The {@code TAG_Compound} type, which has binary ID {@code 10} and is resembled by {@link CompoundTag}.
     */
    COMPOUND(10, "TAG_Compound", CompoundTag.class),

    /**
     * The {@code TAG_Int_Array} type, which has binary ID {@code 11} and is resembled by {@link IntArrayTag}.
     */
    INT_ARRAY(11, "TAG_Int_Array", IntArrayTag.class),

    /**
     * The {@code TAG_Int_Array} type, which has binary ID {@code 11} and is resembled by {@link IntArrayTag}.
     */
    LONG_ARRAY(12, "TAG_Long_Array", LongArrayTag.class);

    private final byte id;
    private final String name;
    private final Class<? extends Tag> type;

    <T extends Tag> TagType(int id, String name, Class<T> type) {
        this.id = (byte) id;
        this.name = name;
        this.type = type;
    }


    private static final Map<Class<? extends Tag>, TagType> BY_CLASS_NAME;
    private static final Map<Byte, TagType> BY_ID;

    static {
        // Dynamically fill BY_CLASS_NAME and BY_ID
        HashMap<Class<? extends Tag>, TagType> byClassName = new HashMap<>();
        HashMap<Byte, TagType> byId = new HashMap<>();

        for (TagType t : values()) {
            byClassName.put(t.type, t);
            byId.put(t.id, t);
        }

        BY_CLASS_NAME = Collections.unmodifiableMap(byClassName);
        BY_ID = Collections.unmodifiableMap(byId);
    }

    /**
     * Looks up the {@link TagType} for the given tag {@link Class}.
     *
     * @param type The tag class to get the tag type of
     * @return The {@link TagType} belonging to the given class
     *
     * @throws NullPointerException     When the given type class is null
     * @throws IllegalArgumentException When the given type class is not one of the supported implementations provided
     *                                  by NBT4j
     */
    public static TagType forClass(Class<? extends Tag> type) {
        if (type == null) {
            throw new NullPointerException("Tag class is null");
        }
        if (!BY_CLASS_NAME.containsKey(type)) {
            throw new IllegalArgumentException("Class " + type.getName() + " is not a supported Tag implementation");
        }
        return BY_CLASS_NAME.get(type);
    }

    /**
     * Looks up the {@link TagType} for the given tag ID.
     *
     * @param id The tag ID to get the tag type of
     * @return The {@link TagType} belonging to the given ID
     *
     * @throws IllegalArgumentException When the given type ID is not a supported type ID
     */
    public static TagType forId(int id) {
        byte byteId = (byte) id;
        if (!isValidId(id)) {
            throw new IllegalArgumentException("No such tag type with ID " + id);
        }
        return BY_ID.get(byteId);
    }

    /**
     * Reads a {@link TagType} from the given input stream. This reads exactly one byte which is then turned into a
     * {@link TagType} like {@link #forId}, but throwing an {@link NbtException} instead when the ID is unknown.
     *
     * @param in The input stream to read from
     * @return The {@link TagType} read
     *
     * @throws NullPointerException When the given stream is null
     * @throws NbtException         When the read type ID is an unknown ID
     * @throws EOFException         When no bytes are left in the input stream
     * @throws IOException          When an I/O error occurs
     */
    public static TagType readType(DataInput in) throws IOException {
        if (in == null) {
            throw new NullPointerException("Stream is null");
        }
        byte byteId = in.readByte();
        if (!isValidId(byteId)) {
            throw new NbtException("Found unknown tag ID " + byteId);
        }
        return BY_ID.get(byteId);
    }

    /**
     * Looks up whether the given tag ID can map to a valid {@link TagType}. When this method will return true for a
     * given ID, a call to {@link #forId} with that same ID will return a valid {@link TagType}. When this method
     * returns false for a given ID, a call to {@link #forId} with that same ID will result in an {@link
     * IllegalArgumentException} being thrown. In general, this method returns false when the given id is less than
     * {@link Byte#MIN_VALUE} or more than {@link Byte#MAX_VALUE}.
     *
     * @param id The tag ID to check
     * @return True when the given ID is a valid type ID, false otherwise.
     */
    public static boolean isValidId(int id) {
        if (id < Byte.MIN_VALUE || id > Byte.MAX_VALUE) return false;
        return BY_ID.containsKey((byte) id);
    }

    /**
     * Returns the type ID of this tag type. The returned type ID can be given to {@link #forId} in order to get this
     * tag type again: the condition {@code TagType.forId(type.getId()) == type} always holds.
     *
     * @return The type ID of this tag type.
     */
    public byte getId() {
        return id;
    }

    /**
     * Returns the name of this tag type, in the format of {@code TAG_[typename]}, where {@code [typename]} is replaced
     * with one of the supported NBT type names. For instance, {@link #BYTE_ARRAY} returns {@code TAG_Byte_Array}.
     *
     * @return The name of this tag type
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type class of this tag type. The returned type class can be given to {@link #forClass} in order to
     * get this tag type again: the condition {@code TagType.forClass(type.getTypeClass()) == type} always holds.
     *
     * @return The type class of this tag type.
     */
    public Class<? extends Tag> getTypeClass() {
        return type;
    }

    /**
     * Returns true when the given {@link Tag} instance is a valid implementation for this type. A {@link Tag} is a
     * valid implementation when the {@linkplain Object#getClass class} of that tag is exactly equal to the class
     * returned by {@link #getTypeClass}. When the given instance is null, false is returned.
     *
     * @param tag The tag to check
     * @return True when the given type is non-null and a valid implementation, false otherwise
     */
    public boolean isValidImplementation(Tag tag) {
        if (tag == null) return false;
        return tag.getClass().equals(type);
    }

    /**
     * Writes the type ID of this {@link TagType} as a byte to the given output stream. This type can later be read
     * through an input stream via {@link #readType}.
     *
     * @param out The output stream to write the type ID to.
     * @throws NullPointerException When the given output stream is null
     * @throws IOException          When an I/O error occurs
     */
    public void writeType(DataOutput out) throws IOException {
        if (out == null) {
            throw new NullPointerException("Output stream is null");
        }
        out.writeByte(id);
    }

    public boolean isNumeric() {
        return this == BYTE || this == SHORT || this == INT || this == LONG || this == FLOAT || this == DOUBLE;
    }

    @Override
    public String toString() {
        return name;
    }

    private interface Serializer<T extends Tag> {
        void write(T tag, DataOutput out) throws IOException;
    }

    private interface Deserializer<T extends Tag> {
        T read(DataInput in, int nesting) throws IOException;
    }
}
