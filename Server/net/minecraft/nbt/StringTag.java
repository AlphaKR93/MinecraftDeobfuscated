/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.io.UTFDataFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Objects
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagVisitor;

public class StringTag
implements Tag {
    private static final int SELF_SIZE_IN_BYTES = 36;
    public static final TagType<StringTag> TYPE = new TagType.VariableSize<StringTag>(){

        @Override
        public StringTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            $$2.accountBytes(36L);
            String $$3 = $$0.readUTF();
            $$2.accountBytes(2 * $$3.length());
            return StringTag.valueOf($$3);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            return $$1.visit($$0.readUTF());
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            StringTag.skipString($$0);
        }

        @Override
        public String getName() {
            return "STRING";
        }

        @Override
        public String getPrettyName() {
            return "TAG_String";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private static final StringTag EMPTY = new StringTag("");
    private static final char DOUBLE_QUOTE = '\"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char ESCAPE = '\\';
    private static final char NOT_SET = '\u0000';
    private final String data;

    public static void skipString(DataInput $$0) throws IOException {
        $$0.skipBytes($$0.readUnsignedShort());
    }

    private StringTag(String $$0) {
        Objects.requireNonNull((Object)$$0, (String)"Null string not allowed");
        this.data = $$0;
    }

    public static StringTag valueOf(String $$0) {
        if ($$0.isEmpty()) {
            return EMPTY;
        }
        return new StringTag($$0);
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        try {
            $$0.writeUTF(this.data);
        }
        catch (UTFDataFormatException $$1) {
            Util.logAndPauseIfInIde("Failed to write NBT String", $$1);
            $$0.writeUTF("");
        }
    }

    @Override
    public int sizeInBytes() {
        return 36 + 2 * this.data.length();
    }

    @Override
    public byte getId() {
        return 8;
    }

    public TagType<StringTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return Tag.super.getAsString();
    }

    @Override
    public StringTag copy() {
        return this;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof StringTag && Objects.equals((Object)this.data, (Object)((StringTag)$$0).data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String getAsString() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitString(this);
    }

    public static String quoteAndEscape(String $$0) {
        StringBuilder $$1 = new StringBuilder(" ");
        int $$2 = 0;
        for (int $$3 = 0; $$3 < $$0.length(); ++$$3) {
            int $$4 = $$0.charAt($$3);
            if ($$4 == 92) {
                $$1.append('\\');
            } else if ($$4 == 34 || $$4 == 39) {
                if ($$2 == 0) {
                    int n = $$2 = $$4 == 34 ? 39 : 34;
                }
                if ($$2 == $$4) {
                    $$1.append('\\');
                }
            }
            $$1.append((char)$$4);
        }
        if ($$2 == 0) {
            $$2 = 34;
        }
        $$1.setCharAt(0, (char)$$2);
        $$1.append((char)$$2);
        return $$1.toString();
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        return $$0.visit(this.data);
    }
}