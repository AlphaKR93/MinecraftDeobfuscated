/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 */
package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.nbt.TagVisitor;

public class ListTag
extends CollectionTag<Tag> {
    private static final int SELF_SIZE_IN_BYTES = 37;
    public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>(){

        @Override
        public ListTag load(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
            $$2.accountBytes(37L);
            if ($$1 > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            byte $$3 = $$0.readByte();
            int $$4 = $$0.readInt();
            if ($$3 == 0 && $$4 > 0) {
                throw new RuntimeException("Missing type on ListTag");
            }
            $$2.accountBytes(4L * (long)$$4);
            TagType<?> $$5 = TagTypes.getType($$3);
            ArrayList $$6 = Lists.newArrayListWithCapacity((int)$$4);
            for (int $$7 = 0; $$7 < $$4; ++$$7) {
                $$6.add($$5.load($$0, $$1 + 1, $$2));
            }
            return new ListTag((List<Tag>)$$6, $$3);
        }

        /*
         * Exception decompiling
         */
        @Override
        public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[SWITCH], 8[CASE]], but top level block is 9[SWITCH]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
             *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
             *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
             *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
             *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
             *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
             *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
             *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
             *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
             *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
             *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
             *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
             *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
             *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
             */
            throw new IllegalStateException("Decompilation failed");
        }

        @Override
        public void skip(DataInput $$0) throws IOException {
            TagType<?> $$1 = TagTypes.getType($$0.readByte());
            int $$2 = $$0.readInt();
            $$1.skip($$0, $$2);
        }

        @Override
        public String getName() {
            return "LIST";
        }

        @Override
        public String getPrettyName() {
            return "TAG_List";
        }
    };
    private final List<Tag> list;
    private byte type;

    ListTag(List<Tag> $$0, byte $$1) {
        this.list = $$0;
        this.type = $$1;
    }

    public ListTag() {
        this((List<Tag>)Lists.newArrayList(), 0);
    }

    @Override
    public void write(DataOutput $$0) throws IOException {
        this.type = this.list.isEmpty() ? (byte)0 : ((Tag)this.list.get(0)).getId();
        $$0.writeByte((int)this.type);
        $$0.writeInt(this.list.size());
        for (Tag $$1 : this.list) {
            $$1.write($$0);
        }
    }

    @Override
    public int sizeInBytes() {
        int $$0 = 37;
        $$0 += 4 * this.list.size();
        for (Tag $$1 : this.list) {
            $$0 += $$1.sizeInBytes();
        }
        return $$0;
    }

    @Override
    public byte getId() {
        return 9;
    }

    public TagType<ListTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    private void updateTypeAfterRemove() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }
    }

    @Override
    public Tag remove(int $$0) {
        Tag $$1 = (Tag)this.list.remove($$0);
        this.updateTypeAfterRemove();
        return $$1;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public CompoundTag getCompound(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 10) {
            return (CompoundTag)$$1;
        }
        return new CompoundTag();
    }

    public ListTag getList(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 9) {
            return (ListTag)$$1;
        }
        return new ListTag();
    }

    public short getShort(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 2) {
            return ((ShortTag)$$1).getAsShort();
        }
        return 0;
    }

    public int getInt(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 3) {
            return ((IntTag)$$1).getAsInt();
        }
        return 0;
    }

    public int[] getIntArray(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 11) {
            return ((IntArrayTag)$$1).getAsIntArray();
        }
        return new int[0];
    }

    public long[] getLongArray(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 11) {
            return ((LongArrayTag)$$1).getAsLongArray();
        }
        return new long[0];
    }

    public double getDouble(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 6) {
            return ((DoubleTag)$$1).getAsDouble();
        }
        return 0.0;
    }

    public float getFloat(int $$0) {
        Tag $$1;
        if ($$0 >= 0 && $$0 < this.list.size() && ($$1 = (Tag)this.list.get($$0)).getId() == 5) {
            return ((FloatTag)$$1).getAsFloat();
        }
        return 0.0f;
    }

    public String getString(int $$0) {
        if ($$0 < 0 || $$0 >= this.list.size()) {
            return "";
        }
        Tag $$1 = (Tag)this.list.get($$0);
        if ($$1.getId() == 8) {
            return $$1.getAsString();
        }
        return $$1.toString();
    }

    public int size() {
        return this.list.size();
    }

    public Tag get(int $$0) {
        return (Tag)this.list.get($$0);
    }

    @Override
    public Tag set(int $$0, Tag $$1) {
        Tag $$2 = this.get($$0);
        if (!this.setTag($$0, $$1)) {
            throw new UnsupportedOperationException(String.format((Locale)Locale.ROOT, (String)"Trying to add tag of type %d to list of %d", (Object[])new Object[]{$$1.getId(), this.type}));
        }
        return $$2;
    }

    @Override
    public void add(int $$0, Tag $$1) {
        if (!this.addTag($$0, $$1)) {
            throw new UnsupportedOperationException(String.format((Locale)Locale.ROOT, (String)"Trying to add tag of type %d to list of %d", (Object[])new Object[]{$$1.getId(), this.type}));
        }
    }

    @Override
    public boolean setTag(int $$0, Tag $$1) {
        if (this.updateType($$1)) {
            this.list.set($$0, (Object)$$1);
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int $$0, Tag $$1) {
        if (this.updateType($$1)) {
            this.list.add($$0, (Object)$$1);
            return true;
        }
        return false;
    }

    private boolean updateType(Tag $$0) {
        if ($$0.getId() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = $$0.getId();
            return true;
        }
        return this.type == $$0.getId();
    }

    @Override
    public ListTag copy() {
        Iterable $$0 = TagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, Tag::copy);
        ArrayList $$1 = Lists.newArrayList((Iterable)$$0);
        return new ListTag((List<Tag>)$$1, this.type);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof ListTag && Objects.equals(this.list, ((ListTag)$$0).list);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public void accept(TagVisitor $$0) {
        $$0.visitList(this);
    }

    @Override
    public byte getElementType() {
        return this.type;
    }

    public void clear() {
        this.list.clear();
        this.type = 0;
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
        switch ($$0.visitList(TagTypes.getType(this.type), this.list.size())) {
            case HALT: {
                return StreamTagVisitor.ValueResult.HALT;
            }
            case BREAK: {
                return $$0.visitContainerEnd();
            }
        }
        block13: for (int $$1 = 0; $$1 < this.list.size(); ++$$1) {
            Tag $$2 = (Tag)this.list.get($$1);
            switch ($$0.visitElement($$2.getType(), $$1)) {
                case HALT: {
                    return StreamTagVisitor.ValueResult.HALT;
                }
                case SKIP: {
                    continue block13;
                }
                case BREAK: {
                    return $$0.visitContainerEnd();
                }
                default: {
                    switch ($$2.accept($$0)) {
                        case HALT: {
                            return StreamTagVisitor.ValueResult.HALT;
                        }
                        case BREAK: {
                            return $$0.visitContainerEnd();
                        }
                    }
                }
            }
        }
        return $$0.visitContainerEnd();
    }
}