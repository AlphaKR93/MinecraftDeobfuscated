/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.lang.Integer
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
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagVisitor;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag
extends CollectionTag<IntTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final TagType<IntArrayTag> TYPE = new TagType.VariableSize<IntArrayTag>(){

        @Override
        public IntArrayTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            $$2.accountBytes(24L);
            int $$3 = $$0.readInt();
            $$2.accountBytes(4L * (long)$$3);
            int[] $$4 = new int[$$3];
            for (int $$5 = 0; $$5 < $$3; ++$$5) {
                $$4[$$5] = $$0.readInt();
            }
            return new IntArrayTag($$4);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            int $$2 = $$0.readInt();
            int[] $$3 = new int[$$2];
            for (int $$4 = 0; $$4 < $$2; ++$$4) {
                $$3[$$4] = $$0.readInt();
            }
            return $$1.visit($$3);
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            $$0.skipBytes($$0.readInt() * 4);
        }

        @Override
        public String getName() {
            return "INT[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Int_Array";
        }
    };
    private int[] data;

    public IntArrayTag(int[] $$0) {
        this.data = $$0;
    }

    public IntArrayTag(List<Integer> $$0) {
        this(IntArrayTag.toArray($$0));
    }

    private static int[] toArray(List<Integer> $$0) {
        int[] $$1 = new int[$$0.size()];
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            Integer $$3 = (Integer)$$0.get($$2);
            $$1[$$2] = $$3 == null ? 0 : $$3;
        }
        return $$1;
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        $$0.writeInt(this.data.length);
        for (int $$1 : this.data) {
            $$0.writeInt($$1);
        }
    }

    @Override
    public int sizeInBytes() {
        return 24 + 4 * this.data.length;
    }

    @Override
    public byte getId() {
        return 11;
    }

    public TagType<IntArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public IntArrayTag copy() {
        int[] $$0 = new int[this.data.length];
        System.arraycopy((Object)this.data, (int)0, (Object)$$0, (int)0, (int)this.data.length);
        return new IntArrayTag($$0);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof IntArrayTag && Arrays.equals((int[])this.data, (int[])((IntArrayTag)$$0).data);
    }

    public int hashCode() {
        return Arrays.hashCode((int[])this.data);
    }

    public int[] getAsIntArray() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitIntArray(this);
    }

    public int size() {
        return this.data.length;
    }

    public IntTag get(int $$0) {
        return IntTag.valueOf(this.data[$$0]);
    }

    @Override
    public IntTag set(int $$0, IntTag $$1) {
        int $$2 = this.data[$$0];
        this.data[$$0] = $$1.getAsInt();
        return IntTag.valueOf($$2);
    }

    @Override
    public void add(int $$0, IntTag $$1) {
        this.data = ArrayUtils.add((int[])this.data, (int)$$0, (int)$$1.getAsInt());
    }

    @Override
    public boolean setTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data[$$0] = ((NumericTag)$$1).getAsInt();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data = ArrayUtils.add((int[])this.data, (int)$$0, (int)((NumericTag)$$1).getAsInt());
            return true;
        }
        return false;
    }

    @Override
    public IntTag remove(int $$0) {
        int $$1 = this.data[$$0];
        this.data = ArrayUtils.remove((int[])this.data, (int)$$0);
        return IntTag.valueOf($$1);
    }

    @Override
    public byte getElementType() {
        return 3;
    }

    public void clear() {
        this.data = new int[0];
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        return $$0.visit(this.data);
    }
}