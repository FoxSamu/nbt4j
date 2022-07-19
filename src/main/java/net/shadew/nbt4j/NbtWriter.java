package net.shadew.nbt4j;

import java.io.DataOutput;
import java.io.IOException;

import net.shadew.nbt4j.util.NbtException;

public class NbtWriter implements NbtVisitor {
    private final DataOutput out;
    private final NamedVisitor namedVisitor = new NamedVisitor();
    private final NamelessVisitor namelessVisitor = new NamelessVisitor();

    private IOException ioException;

    public NbtWriter(DataOutput out) {
        this.out = out;
    }

    public IOException ioException() {
        return ioException;
    }

    public void throwIoException() throws IOException {
        if (ioException != null)
            throw ioException;
    }

    @Override
    public void visitByte(byte value, String name) {
        try {
            out.writeByte(TagType.BYTE.getId());
            out.writeUTF(name);
            out.writeByte(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitShort(short value, String name) {
        try {
            out.writeByte(TagType.SHORT.getId());
            out.writeUTF(name);
            out.writeShort(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitInt(int value, String name) {
        try {
            out.writeByte(TagType.INT.getId());
            out.writeUTF(name);
            out.writeInt(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitLong(long value, String name) {
        try {
            out.writeByte(TagType.LONG.getId());
            out.writeUTF(name);
            out.writeLong(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitFloat(float value, String name) {
        try {
            out.writeByte(TagType.FLOAT.getId());
            out.writeUTF(name);
            out.writeFloat(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitDouble(double value, String name) {
        try {
            out.writeByte(TagType.DOUBLE.getId());
            out.writeUTF(name);
            out.writeDouble(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitString(String value, String name) {
        try {
            out.writeByte(TagType.STRING.getId());
            out.writeUTF(name);
            out.writeUTF(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitByteArray(byte[] value, String name) {
        try {
            out.writeByte(TagType.BYTE_ARRAY.getId());
            out.writeUTF(name);
            out.writeInt(value.length);
            out.write(value);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitIntArray(int[] value, String name) {
        try {
            out.writeByte(TagType.INT_ARRAY.getId());
            out.writeUTF(name);
            out.writeInt(value.length);
            for (int i : value) out.writeInt(i);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public void visitLongArray(long[] value, String name) {
        try {
            out.writeByte(TagType.LONG_ARRAY.getId());
            out.writeUTF(name);
            out.writeInt(value.length);
            for (long i : value) out.writeLong(i);
        } catch (IOException exc) {
            ioException = exc;
        }
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        if (type == TagType.END && length > 0) {
            ioException = new NbtException("Cannot serialize TAG_List that is not empty but has TAG_End element type");
            return NbtVisitor.NOOP;
        }
        try {
            out.writeByte(TagType.LIST.getId());
            out.writeUTF(name);
            out.writeByte(type.getId());
            out.writeInt(length);
            return namelessVisitor;
        } catch (IOException exc) {
            ioException = exc;
            return NbtVisitor.NOOP;
        }
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        try {
            out.writeByte(TagType.COMPOUND.getId());
            out.writeUTF(name);
            return namedVisitor;
        } catch (IOException exc) {
            ioException = exc;
            return NbtVisitor.NOOP;
        }
    }

    @Override
    public void visitEnd() {
        ioException = new NbtException("Cannot export independent TAG_End");
    }

    protected class NamedVisitor implements NbtVisitor {
        @Override
        public void visitByte(byte value, String name) {
            try {
                out.writeByte(TagType.BYTE.getId());
                out.writeUTF(name);
                out.writeByte(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitShort(short value, String name) {
            try {
                out.writeByte(TagType.SHORT.getId());
                out.writeUTF(name);
                out.writeShort(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitInt(int value, String name) {
            try {
                out.writeByte(TagType.INT.getId());
                out.writeUTF(name);
                out.writeInt(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitLong(long value, String name) {
            try {
                out.writeByte(TagType.LONG.getId());
                out.writeUTF(name);
                out.writeLong(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitFloat(float value, String name) {
            try {
                out.writeByte(TagType.FLOAT.getId());
                out.writeUTF(name);
                out.writeFloat(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitDouble(double value, String name) {
            try {
                out.writeByte(TagType.DOUBLE.getId());
                out.writeUTF(name);
                out.writeDouble(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitString(String value, String name) {
            try {
                out.writeByte(TagType.STRING.getId());
                out.writeUTF(name);
                out.writeUTF(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            try {
                out.writeByte(TagType.BYTE_ARRAY.getId());
                out.writeUTF(name);
                out.writeInt(value.length);
                out.write(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            try {
                out.writeByte(TagType.INT_ARRAY.getId());
                out.writeUTF(name);
                out.writeInt(value.length);
                for (int i : value) out.writeInt(i);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            try {
                out.writeByte(TagType.LONG_ARRAY.getId());
                out.writeUTF(name);
                out.writeInt(value.length);
                for (long i : value) out.writeLong(i);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            if (type == TagType.END && length > 0) {
                ioException = new NbtException("Cannot serialize TAG_List that is not empty but has TAG_End element type");
                return NbtVisitor.NOOP;
            }
            try {
                out.writeByte(TagType.LIST.getId());
                out.writeUTF(name);
                out.writeByte(type.getId());
                out.writeInt(length);
                return namelessVisitor;
            } catch (IOException exc) {
                ioException = exc;
                return NbtVisitor.NOOP;
            }
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            try {
                out.writeByte(TagType.COMPOUND.getId());
                out.writeUTF(name);
                return namedVisitor;
            } catch (IOException exc) {
                ioException = exc;
                return NbtVisitor.NOOP;
            }
        }

        @Override
        public void visitEnd() {
            try {
                out.writeByte(TagType.END.getId());
            } catch (IOException exc) {
                ioException = exc;
            }
        }
    }

    protected class NamelessVisitor implements NbtVisitor {
        @Override
        public void visitByte(byte value, String name) {
            try {
                out.writeByte(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitShort(short value, String name) {
            try {
                out.writeShort(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitInt(int value, String name) {
            try {
                out.writeInt(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitLong(long value, String name) {
            try {
                out.writeLong(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitFloat(float value, String name) {
            try {
                out.writeFloat(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitDouble(double value, String name) {
            try {
                out.writeDouble(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitString(String value, String name) {
            try {
                out.writeUTF(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            try {
                out.writeInt(value.length);
                out.write(value);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            try {
                out.writeInt(value.length);
                for (int i : value) out.writeInt(i);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            try {
                out.writeInt(value.length);
                for (long i : value) out.writeLong(i);
            } catch (IOException exc) {
                ioException = exc;
            }
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            if (type == TagType.END && length > 0) {
                ioException = new NbtException("Cannot serialize TAG_List that is not empty but has TAG_End element type");
                return NbtVisitor.NOOP;
            }
            try {
                out.writeByte(type.getId());
                out.writeInt(length);
                return namelessVisitor;
            } catch (IOException exc) {
                ioException = exc;
                return NbtVisitor.NOOP;
            }
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            return namedVisitor;
        }

        @Override
        public void visitEnd() {
            ioException = new NbtException("Cannot export TAG_End in TAG_List");
        }
    }
}
