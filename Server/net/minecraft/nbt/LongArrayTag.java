/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.util.Arrays
 *  java.util.List
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagVisitor;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag
extends CollectionTag<LongTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final TagType<LongArrayTag> TYPE = new TagType.VariableSize<LongArrayTag>(){

        @Override
        public LongArrayTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            $$2.accountBytes(24L);
            int $$3 = $$0.readInt();
            $$2.accountBytes(8L * (long)$$3);
            long[] $$4 = new long[$$3];
            for (int $$5 = 0; $$5 < $$3; ++$$5) {
                $$4[$$5] = $$0.readLong();
            }
            return new LongArrayTag($$4);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            int $$2 = $$0.readInt();
            long[] $$3 = new long[$$2];
            for (int $$4 = 0; $$4 < $$2; ++$$4) {
                $$3[$$4] = $$0.readLong();
            }
            return $$1.visit($$3);
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            $$0.skipBytes($$0.readInt() * 8);
        }

        @Override
        public String getName() {
            return "LONG[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Long_Array";
        }
    };
    private long[] data;

    public LongArrayTag(long[] $$0) {
        this.data = $$0;
    }

    public LongArrayTag(LongSet $$0) {
        this.data = $$0.toLongArray();
    }

    public LongArrayTag(List<Long> $$0) {
        this(LongArrayTag.toArray($$0));
    }

    private static long[] toArray(List<Long> $$0) {
        long[] $$1 = new long[$$0.size()];
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            Long $$3 = (Long)$$0.get($$2);
            $$1[$$2] = $$3 == null ? 0L : $$3;
        }
        return $$1;
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        $$0.writeInt(this.data.length);
        for (long $$1 : this.data) {
            $$0.writeLong($$1);
        }
    }

    @Override
    public int sizeInBytes() {
        return 24 + 8 * this.data.length;
    }

    @Override
    public byte getId() {
        return 12;
    }

    public TagType<LongArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public LongArrayTag copy() {
        long[] $$0 = new long[this.data.length];
        System.arraycopy((Object)this.data, (int)0, (Object)$$0, (int)0, (int)this.data.length);
        return new LongArrayTag($$0);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof LongArrayTag && Arrays.equals((long[])this.data, (long[])((LongArrayTag)$$0).data);
    }

    public int hashCode() {
        return Arrays.hashCode((long[])this.data);
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitLongArray(this);
    }

    public long[] getAsLongArray() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public LongTag get(int $$0) {
        return LongTag.valueOf(this.data[$$0]);
    }

    @Override
    public LongTag set(int $$0, LongTag $$1) {
        long $$2 = this.data[$$0];
        this.data[$$0] = $$1.getAsLong();
        return LongTag.valueOf($$2);
    }

    @Override
    public void add(int $$0, LongTag $$1) {
        this.data = ArrayUtils.add((long[])this.data, (int)$$0, (long)$$1.getAsLong());
    }

    @Override
    public boolean setTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data[$$0] = ((NumericTag)$$1).getAsLong();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int $$0, Tag $$1) {
        if ($$1 instanceof NumericTag) {
            this.data = ArrayUtils.add((long[])this.data, (int)$$0, (long)((NumericTag)$$1).getAsLong());
            return true;
        }
        return false;
    }

    @Override
    public LongTag remove(int $$0) {
        long $$1 = this.data[$$0];
        this.data = ArrayUtils.remove((long[])this.data, (int)$$0);
        return LongTag.valueOf($$1);
    }

    @Override
    public byte getElementType() {
        return 4;
    }

    public void clear() {
        this.data = new long[0];
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        return $$0.visit(this.data);
    }
}