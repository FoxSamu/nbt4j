package net.shadew.nbt4j;

import java.io.DataInput;
import java.io.IOException;

import net.shadew.nbt4j.util.NbtException;

public class NbtReader implements NbtAcceptor {
    private final DataInput in;
    private boolean lenient;

    private IOException ioException;

    public NbtReader(DataInput in) {
        this.in = in;
    }

    public boolean lenient() {
        return lenient;
    }

    public NbtReader lenient(boolean lenient) {
        this.lenient = lenient;
        return this;
    }

    public IOException ioException() {
        return ioException;
    }

    public void throwIoException() throws IOException {
        if (ioException != null)
            throw ioException;
    }

    @Override
    public void accept(NbtVisitor visitor, String name) {
        try {
            accept0(visitor);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    private void accept0(NbtVisitor visitor) throws IOException {
        TagType type = TagType.readType(in);
        if (!lenient && type != TagType.COMPOUND)
            throw new NbtException("NBT data does not have TAG_Compound as root");

        String name = in.readUTF();
        acceptTag(type, visitor, name);
    }

    private void acceptTag(TagType type, NbtVisitor visitor, String name) throws IOException {
        switch (type) {
            case END -> throw new NbtException("TAG_End cannot be a independent tag");
            case BYTE -> visitor.visitByte(in.readByte(), name);
            case SHORT -> visitor.visitShort(in.readShort(), name);
            case INT -> visitor.visitInt(in.readInt(), name);
            case LONG -> visitor.visitLong(in.readLong(), name);
            case FLOAT -> visitor.visitFloat(in.readFloat(), name);
            case DOUBLE -> visitor.visitDouble(in.readDouble(), name);
            case STRING -> visitor.visitString(in.readUTF(), name);
            case BYTE_ARRAY -> {
                int len = in.readInt();
                byte[] value = new byte[len];
                in.readFully(value);
                visitor.visitByteArray(value, name);
            }
            case INT_ARRAY -> {
                int len = in.readInt();
                int[] value = new int[len];
                for (int i = 0; i < len; i++)
                    value[i] = in.readInt();
                visitor.visitIntArray(value, name);
            }
            case LONG_ARRAY -> {
                int len = in.readInt();
                long[] value = new long[len];
                for (int i = 0; i < len; i++)
                    value[i] = in.readLong();
                visitor.visitLongArray(value, name);
            }
            case LIST -> {
                TagType elemType = TagType.readType(in);
                int len = in.readInt();
                if (len < 0) {
                    throw new NbtException("Cannot deserialize TAG_List with negative length (" + len + ")");
                }
                if (elemType == TagType.END && len != 0) {
                    throw new NbtException("Cannot deserialize nonempty TAG_List with TAG_End element type");
                }

                NbtVisitor v = visitor.visitList(elemType, len, name);
                if (v == null) v = NbtVisitor.NOOP; // Use dummy so we won't skip reading

                try {
                    while (len > 0) {
                        acceptTag(elemType, v, null);
                        len--;
                    }
                } finally {
                    v.visitListEnd();
                }
            }
            case COMPOUND -> {
                NbtVisitor v = visitor.visitCompound(name);
                if (v == null) v = NbtVisitor.NOOP; // Use dummy so we won't skip reading

                try {
                    TagType elemType;
                    while ((elemType = TagType.readType(in)) != TagType.END) {
                        String elemName = in.readUTF();
                        acceptTag(elemType, v, elemName);
                    }
                } finally {
                    v.visitEnd();
                }
            }
        }
    }
}
