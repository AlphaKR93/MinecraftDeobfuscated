/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.lang.Byte
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.util.Arrays
 *  java.util.List
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagVisitor;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag
extends CollectionTag<ByteTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final TagType<ByteArrayTag> TYPE = new TagType.VariableSize<ByteArrayTag>(){

        @Override
        public ByteArrayTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            $$2.accountBytes(24L);
            int $$3 = $$0.readInt();
            $$2.accountBytes(1L * (long)$$3);
            byte[] $$4 = new byte[$$3];
            $$0.readFully($$4);
            return new ByteArrayTag($$4);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            int $$2 = $$0.readInt();
            byte[] $$3 = new byte[$$2];
            $$0.readFully($$3);
            return $$1.visit($$3);
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            $$0.skipBytes($$0.readInt() * 1);
        }

        @Override
        public String getName() {
            return "BYTE[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Byte_Array";
        }
    };
    private byte[] data;

    public ByteArrayTag(byte[] $$0) {
        this.data = $$0;
    }

    public ByteArrayTag(List<Byte> $$0) {
        this(ByteArrayTag.toArray($$0));
    }

    private static byte[] toArray(List<Byte> $$0) {
        byte[] $$1 = new byte[$$0.size()];
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            Byte $$3 = (Byte)$$0.get($$2);
            $$1[$$2] = $$3 == null ? (byte)0 : $$3;
        }
        return $$1;
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        $$0.writeInt(this.data.length);
        $$0.write(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 24 + 1 * this.data.length;
    }

    @Override
    public byte getId() {
        return 7;
    }

    public TagType<ByteArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public Tag copy() {
        byte[] $$0 = new byte[this.data.length];
        System.arraycopy((Object)this.data, (int)0, (Object)$$0, (int)0, (int)this.data.length);
        return new ByteArrayTag($$0);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof ByteArrayTag && Arrays.equals((byte[])this.data, (byte[])((ByteArrayTag)$$0).data);
    }

    public int hashCode() {
        return Arrays.hashCode((byte[])this.data);
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitByteArray(this);
    }

    public byte[] getAsByteArray() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public ByteTag get(int $$0) {
        return ByteTag.valueOf(this.data[$$0]);
    }

    @Override
    public ByteTag set(int $$0, ByteTag $$1) {
        byte $$2 = this.data[$$0];
        this.data[$$0] = $$1.getAsByte();
        return ByteTag.valueOf($$2);
    }

    @Override
    public void add(int $$0, ByteTag $$1) {
        this.data = ArrayUtils.add((byte[])this.data, (int)$$0, (byte)$$1.getAsByte());
    }

    @Override
    public boolean setTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data[$$0] = ((NumericTag)$$1).getAsByte();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data = ArrayUtils.add((byte[])this.data, (int)$$0, (byte)((NumericTag)$$1).getAsByte());
            return true;
        }
        return false;
    }

    @Override
    public ByteTag remove(int $$0) {
        byte $$1 = this.data[$$0];
        this.data = ArrayUtils.remove((byte[])this.data, (int)$$0);
        return ByteTag.valueOf($$1);
    }

    @Override
    public byte getElementType() {
        return 1;
    }

    public void clear() {
        this.data = new byte[0];
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        return $$0.visit(this.data);
    }
}