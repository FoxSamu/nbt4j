package net.shadew.nbt4j;

import net.shadew.json.JsonNode;

public class JsonBuilder implements NbtVisitor {
    private JsonNode tag;

    public JsonNode tag() {
        return tag;
    }

    @Override
    public void visitByte(byte value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitShort(short value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitInt(int value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitLong(long value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitFloat(float value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitDouble(double value, String name) {
        tag = JsonNode.number(value);
    }

    @Override
    public void visitString(String value, String name) {
        tag = JsonNode.string(value);
    }

    @Override
    public void visitByteArray(byte[] value, String name) {
        tag = JsonNode.numberArray(value);
    }

    @Override
    public void visitIntArray(int[] value, String name) {
        tag = JsonNode.numberArray(value);
    }

    @Override
    public void visitLongArray(long[] value, String name) {
        tag = JsonNode.numberArray(value);
    }

    private static class ListBuilder implements NbtVisitor {
        private final JsonNode tag;

        ListBuilder(JsonNode tag) {
            this.tag = tag;
        }

        @Override
        public void visitByte(byte value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitShort(short value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitInt(int value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitLong(long value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitFloat(float value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitDouble(double value, String name) {
            tag.add(JsonNode.number(value));
        }

        @Override
        public void visitString(String value, String name) {
            tag.add(JsonNode.string(value));
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            tag.add(JsonNode.numberArray(value));
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            tag.add(JsonNode.numberArray(value));
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            tag.add(JsonNode.numberArray(value));
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            JsonNode tag = JsonNode.array();
            this.tag.add(tag);
            return new ListBuilder(tag);
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            JsonNode tag = JsonNode.object();
            this.tag.add(tag);
            return new CompoundBuilder(tag);
        }
    }

    @Override
    public NbtVisitor visitList(TagType type, int length, String name) {
        JsonNode tag = JsonNode.object();
        this.tag = tag;
        return new ListBuilder(tag);
    }

    private static class CompoundBuilder implements NbtVisitor {
        private final JsonNode tag;

        CompoundBuilder(JsonNode tag) {
            this.tag = tag;
        }

        @Override
        public void visitByte(byte value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitShort(short value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitInt(int value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitLong(long value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitFloat(float value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitDouble(double value, String name) {
            tag.set(name, JsonNode.number(value));
        }

        @Override
        public void visitString(String value, String name) {
            tag.set(name, JsonNode.string(value));
        }

        @Override
        public void visitByteArray(byte[] value, String name) {
            tag.set(name, JsonNode.numberArray(value));
        }

        @Override
        public void visitIntArray(int[] value, String name) {
            tag.set(name, JsonNode.numberArray(value));
        }

        @Override
        public void visitLongArray(long[] value, String name) {
            tag.set(name, JsonNode.numberArray(value));
        }

        @Override
        public NbtVisitor visitList(TagType type, int length, String name) {
            JsonNode tag = JsonNode.array();
            this.tag.set(name, tag);
            return new ListBuilder(tag);
        }

        @Override
        public NbtVisitor visitCompound(String name) {
            JsonNode tag = JsonNode.object();
            this.tag.set(name, tag);
            return new CompoundBuilder(tag);
        }
    }

    @Override
    public NbtVisitor visitCompound(String name) {
        JsonNode tag = JsonNode.object();
        this.tag = tag;
        return new CompoundBuilder(tag);
    }
}
