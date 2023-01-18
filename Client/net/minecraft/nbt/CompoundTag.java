/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.lang.Byte
 *  java.lang.ClassCastException
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Set
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.nbt.TagVisitor;

public class CompoundTag
implements Tag {
    public static final Codec<CompoundTag> CODEC = Codec.PASSTHROUGH.comapFlatMap($$0 -> {
        Tag $$1 = (Tag)$$0.convert((DynamicOps)NbtOps.INSTANCE).getValue();
        if ($$1 instanceof CompoundTag) {
            return DataResult.success((Object)((CompoundTag)$$1));
        }
        return DataResult.error((String)("Not a compound tag: " + $$1));
    }, $$0 -> new Dynamic((DynamicOps)NbtOps.INSTANCE, $$0));
    private static final int SELF_SIZE_IN_BYTES = 48;
    private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
    public static final TagType<CompoundTag> TYPE = new TagType.VariableSize<CompoundTag>(){

        @Override
        public CompoundTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            byte $$4;
            $$2.accountBytes(48L);
            if ($$1 > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            HashMap $$3 = Maps.newHashMap();
            while (($$4 = CompoundTag.readNamedTagType($$0, $$2)) != 0) {
                String $$5 = CompoundTag.readNamedTagName($$0, $$2);
                $$2.accountBytes(28 + 2 * $$5.length());
                Tag $$6 = CompoundTag.readNamedTagData(TagTypes.getType($$4), $$5, $$0, $$1 + 1, $$2);
                if ($$3.put((Object)$$5, (Object)$$6) != null) continue;
                $$2.accountBytes(36L);
            }
            return new CompoundTag((Map<String, Tag>)$$3);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            byte $$2;
            block13: while (($$2 = $$0.readByte()) != 0) {
                TagType<?> $$3 = TagTypes.getType($$2);
                switch ($$1.visitEntry($$3)) {
                    case HALT: {
                        return StreamTagVisitor.ValueResult.HALT;
                    }
                    case BREAK: {
                        StringTag.skipString($$0);
                        $$3.skip($$0);
                        break block13;
                    }
                    case SKIP: {
                        StringTag.skipString($$0);
                        $$3.skip($$0);
                        continue block13;
                    }
                    default: {
                        String $$4 = $$0.readUTF();
                        switch ($$1.visitEntry($$3, $$4)) {
                            case HALT: {
                                return StreamTagVisitor.ValueResult.HALT;
                            }
                            case BREAK: {
                                $$3.skip($$0);
                                break block13;
                            }
                            case SKIP: {
                                $$3.skip($$0);
                                continue block13;
                            }
                        }
                        switch ($$3.parse($$0, $$1)) {
                            case HALT: {
                                return StreamTagVisitor.ValueResult.HALT;
                            }
                        }
                        continue block13;
                    }
                }
            }
            if ($$2 != 0) {
                while (($$2 = $$0.readByte()) != 0) {
                    StringTag.skipString($$0);
                    TagTypes.getType($$2).skip($$0);
                }
            }
            return $$1.visitContainerEnd();
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            byte $$1;
            while (($$1 = $$0.readByte()) != 0) {
                StringTag.skipString($$0);
                TagTypes.getType($$1).skip($$0);
            }
        }

        @Override
        public String getName() {
            return "COMPOUND";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Compound";
        }
    };
    private final Map<String, Tag> tags;

    protected CompoundTag(Map<String, Tag> $$0) {
        this.tags = $$0;
    }

    public CompoundTag() {
        this((Map<String, Tag>)Maps.newHashMap());
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        for (String $$1 : this.tags.keySet()) {
            Tag $$2 = (Tag)this.tags.get((Object)$$1);
            CompoundTag.writeNamedTag($$1, $$2, $$0);
        }
        $$0.writeByte(0);
    }

    @Override
    public int sizeInBytes() {
        int $$0 = 48;
        for (Map.Entry $$1 : this.tags.entrySet()) {
            $$0 += 28 + 2 * ((String)$$1.getKey()).length();
            $$0 += 36;
            $$0 += ((Tag)$$1.getValue()).sizeInBytes();
        }
        return $$0;
    }

    public Set<String> getAllKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getId() {
        return 10;
    }

    public TagType<CompoundTag> getType() {
        return TYPE;
    }

    public int size() {
        return this.tags.size();
    }

    @Nullable
    public Tag put(String $$0, Tag $$1) {
        return (Tag)this.tags.put((Object)$$0, (Object)$$1);
    }

    public void putByte(String $$0, byte $$1) {
        this.tags.put((Object)$$0, (Object)ByteTag.valueOf($$1));
    }

    public void putShort(String $$0, short $$1) {
        this.tags.put((Object)$$0, (Object)ShortTag.valueOf($$1));
    }

    public void putInt(String $$0, int $$1) {
        this.tags.put((Object)$$0, (Object)IntTag.valueOf($$1));
    }

    public void putLong(String $$0, long $$1) {
        this.tags.put((Object)$$0, (Object)LongTag.valueOf($$1));
    }

    public void putUUID(String $$0, UUID $$1) {
        this.tags.put((Object)$$0, (Object)NbtUtils.createUUID($$1));
    }

    public UUID getUUID(String $$0) {
        return NbtUtils.loadUUID(this.get($$0));
    }

    public boolean hasUUID(String $$0) {
        Tag $$1 = this.get($$0);
        return $$1 != null && $$1.getType() == IntArrayTag.TYPE && ((IntArrayTag)$$1).getAsIntArray().length == 4;
    }

    public void putFloat(String $$0, float $$1) {
        this.tags.put((Object)$$0, (Object)FloatTag.valueOf($$1));
    }

    public void putDouble(String $$0, double $$1) {
        this.tags.put((Object)$$0, (Object)DoubleTag.valueOf($$1));
    }

    public void putString(String $$0, String $$1) {
        this.tags.put((Object)$$0, (Object)StringTag.valueOf($$1));
    }

    public void putByteArray(String $$0, byte[] $$1) {
        this.tags.put((Object)$$0, (Object)new ByteArrayTag($$1));
    }

    public void putByteArray(String $$0, List<Byte> $$1) {
        this.tags.put((Object)$$0, (Object)new ByteArrayTag($$1));
    }

    public void putIntArray(String $$0, int[] $$1) {
        this.tags.put((Object)$$0, (Object)new IntArrayTag($$1));
    }

    public void putIntArray(String $$0, List<Integer> $$1) {
        this.tags.put((Object)$$0, (Object)new IntArrayTag($$1));
    }

    public void putLongArray(String $$0, long[] $$1) {
        this.tags.put((Object)$$0, (Object)new LongArrayTag($$1));
    }

    public void putLongArray(String $$0, List<Long> $$1) {
        this.tags.put((Object)$$0, (Object)new LongArrayTag($$1));
    }

    public void putBoolean(String $$0, boolean $$1) {
        this.tags.put((Object)$$0, (Object)ByteTag.valueOf($$1));
    }

    @Nullable
    public Tag get(String $$0) {
        return (Tag)this.tags.get((Object)$$0);
    }

    public byte getTagType(String $$0) {
        Tag $$1 = (Tag)this.tags.get((Object)$$0);
        if ($$1 == null) {
            return 0;
        }
        return $$1.getId();
    }

    public boolean contains(String $$0) {
        return this.tags.containsKey((Object)$$0);
    }

    public boolean contains(String $$0, int $$1) {
        byte $$2 = this.getTagType($$0);
        if ($$2 == $$1) {
            return true;
        }
        if ($$1 == 99) {
            return $$2 == 1 || $$2 == 2 || $$2 == 3 || $$2 == 4 || $$2 == 5 || $$2 == 6;
        }
        return false;
    }

    public byte getByte(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsByte();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public short getShort(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsShort();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public int getInt(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsInt();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public long getLong(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsLong();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0L;
    }

    public float getFloat(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsFloat();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0f;
    }

    public double getDouble(String $$0) {
        try {
            if (this.contains($$0, 99)) {
                return ((NumericTag)this.tags.get((Object)$$0)).getAsDouble();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0;
    }

    public String getString(String $$0) {
        try {
            if (this.contains($$0, 8)) {
                return ((Tag)this.tags.get((Object)$$0)).getAsString();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return "";
    }

    public byte[] getByteArray(String $$0) {
        try {
            if (this.contains($$0, 7)) {
                return ((ByteArrayTag)this.tags.get((Object)$$0)).getAsByteArray();
            }
        }
        catch (ClassCastException $$1) {
            throw new ReportedException(this.createReport($$0, ByteArrayTag.TYPE, $$1));
        }
        return new byte[0];
    }

    public int[] getIntArray(String $$0) {
        try {
            if (this.contains($$0, 11)) {
                return ((IntArrayTag)this.tags.get((Object)$$0)).getAsIntArray();
            }
        }
        catch (ClassCastException $$1) {
            throw new ReportedException(this.createReport($$0, IntArrayTag.TYPE, $$1));
        }
        return new int[0];
    }

    public long[] getLongArray(String $$0) {
        try {
            if (this.contains($$0, 12)) {
                return ((LongArrayTag)this.tags.get((Object)$$0)).getAsLongArray();
            }
        }
        catch (ClassCastException $$1) {
            throw new ReportedException(this.createReport($$0, LongArrayTag.TYPE, $$1));
        }
        return new long[0];
    }

    public CompoundTag getCompound(String $$0) {
        try {
            if (this.contains($$0, 10)) {
                return (CompoundTag)this.tags.get((Object)$$0);
            }
        }
        catch (ClassCastException $$1) {
            throw new ReportedException(this.createReport($$0, TYPE, $$1));
        }
        return new CompoundTag();
    }

    public ListTag getList(String $$0, int $$1) {
        try {
            if (this.getTagType($$0) == 9) {
                ListTag $$2 = (ListTag)this.tags.get((Object)$$0);
                if ($$2.isEmpty() || $$2.getElementType() == $$1) {
                    return $$2;
                }
                return new ListTag();
            }
        }
        catch (ClassCastException $$3) {
            throw new ReportedException(this.createReport($$0, ListTag.TYPE, $$3));
        }
        return new ListTag();
    }

    public boolean getBoolean(String $$0) {
        return this.getByte($$0) != 0;
    }

    public void remove(String $$0) {
        this.tags.remove((Object)$$0);
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    private CrashReport createReport(String $$0, TagType<?> $$1, ClassCastException $$2) {
        CrashReport $$3 = CrashReport.forThrowable($$2, "Reading NBT data");
        CrashReportCategory $$4 = $$3.addCategory("Corrupt NBT tag", 1);
        $$4.setDetail("Tag type found", () -> ((Tag)this.tags.get((Object)$$0)).getType().getName());
        $$4.setDetail("Tag type expected", $$1::getName);
        $$4.setDetail("Tag name", $$0);
        return $$3;
    }

    @Override
    public CompoundTag copy() {
        HashMap $$0 = Maps.newHashMap((Map)Maps.transformValues(this.tags, Tag::copy));
        return new CompoundTag((Map<String, Tag>)$$0);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)$$0).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void writeNamedTag(String $$0, Tag $$1, DataOutput $$2) throws IOException {
        $$2.writeByte((int)$$1.getId());
        if ($$1.getId() == 0) {
            return;
        }
        $$2.writeUTF($$0);
        $$1.write($$2);
    }

    static byte readNamedTagType(DataInput $$0, NbtAccounter $$1) throws IOException {
        return $$0.readByte();
    }

    static String readNamedTagName(DataInput $$0, NbtAccounter $$1) throws IOException {
        return $$0.readUTF();
    }

    static Tag readNamedTagData(TagType<?> $$0, String $$1, DataInput $$2, int $$3, NbtAccounter $$4) {
        try {
            return $$0.load($$2, $$3, $$4);
        }
        catch (IOException $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Loading NBT data");
            CrashReportCategory $$7 = $$6.addCategory("NBT Tag");
            $$7.setDetail("Tag name", $$1);
            $$7.setDetail("Tag type", $$0.getName());
            throw new ReportedException($$6);
        }
    }

    public CompoundTag merge(CompoundTag $$0) {
        for (String $$1 : $$0.tags.keySet()) {
            Tag $$2 = (Tag)$$0.tags.get((Object)$$1);
            if ($$2.getId() == 10) {
                if (this.contains($$1, 10)) {
                    CompoundTag $$3 = this.getCompound($$1);
                    $$3.merge((CompoundTag)$$2);
                    continue;
                }
                this.put($$1, $$2.copy());
                continue;
            }
            this.put($$1, $$2.copy());
        }
        return this;
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitCompound(this);
    }

    protected Map<String, Tag> entries() {
        return Collections.unmodifiableMap(this.tags);
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        block14: for (Map.Entry $$1 : this.tags.entrySet()) {
            Tag $$2 = (Tag)$$1.getValue();
            TagType<?> $$3 = $$2.getType();
            StreamTagVisitor.EntryResult $$4 = $$0.visitEntry($$3);
            switch ($$4) {
                case HALT: {
                    return StreamTagVisitor.ValueResult.HALT;
                }
                case BREAK: {
                    return $$0.visitContainerEnd();
                }
                case SKIP: {
                    continue block14;
                }
            }
            $$4 = $$0.visitEntry($$3, (String)$$1.getKey());
            switch ($$4) {
                case HALT: {
                    return StreamTagVisitor.ValueResult.HALT;
                }
                case BREAK: {
                    return $$0.visitContainerEnd();
                }
                case SKIP: {
                    continue block14;
                }
            }
            StreamTagVisitor.ValueResult $$5 = $$2.accept($$0);
            switch ($$5) {
                case HALT: {
                    return StreamTagVisitor.ValueResult.HALT;
                }
                case BREAK: {
                    return $$0.visitContainerEnd();
                }
            }
        }
        return $$0.visitContainerEnd();
    }
}