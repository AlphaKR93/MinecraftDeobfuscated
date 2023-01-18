/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$AbstractStringBuilder
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.stream.IntStream
 *  java.util.stream.LongStream
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NbtOps
implements DynamicOps<Tag> {
    public static final NbtOps INSTANCE = new NbtOps();
    private static final String WRAPPER_MARKER = "";

    protected NbtOps() {
    }

    public Tag empty() {
        return EndTag.INSTANCE;
    }

    public <U> U convertTo(DynamicOps<U> $$0, Tag $$1) {
        switch ($$1.getId()) {
            case 0: {
                return (U)$$0.empty();
            }
            case 1: {
                return (U)$$0.createByte(((NumericTag)$$1).getAsByte());
            }
            case 2: {
                return (U)$$0.createShort(((NumericTag)$$1).getAsShort());
            }
            case 3: {
                return (U)$$0.createInt(((NumericTag)$$1).getAsInt());
            }
            case 4: {
                return (U)$$0.createLong(((NumericTag)$$1).getAsLong());
            }
            case 5: {
                return (U)$$0.createFloat(((NumericTag)$$1).getAsFloat());
            }
            case 6: {
                return (U)$$0.createDouble(((NumericTag)$$1).getAsDouble());
            }
            case 7: {
                return (U)$$0.createByteList(ByteBuffer.wrap((byte[])((ByteArrayTag)$$1).getAsByteArray()));
            }
            case 8: {
                return (U)$$0.createString($$1.getAsString());
            }
            case 9: {
                return (U)this.convertList($$0, $$1);
            }
            case 10: {
                return (U)this.convertMap($$0, $$1);
            }
            case 11: {
                return (U)$$0.createIntList(Arrays.stream((int[])((IntArrayTag)$$1).getAsIntArray()));
            }
            case 12: {
                return (U)$$0.createLongList(Arrays.stream((long[])((LongArrayTag)$$1).getAsLongArray()));
            }
        }
        throw new IllegalStateException("Unknown tag type: " + $$1);
    }

    public DataResult<Number> getNumberValue(Tag $$0) {
        if ($$0 instanceof NumericTag) {
            NumericTag $$1 = (NumericTag)$$0;
            return DataResult.success((Object)$$1.getAsNumber());
        }
        return DataResult.error((String)"Not a number");
    }

    public Tag createNumeric(Number $$0) {
        return DoubleTag.valueOf($$0.doubleValue());
    }

    public Tag createByte(byte $$0) {
        return ByteTag.valueOf($$0);
    }

    public Tag createShort(short $$0) {
        return ShortTag.valueOf($$0);
    }

    public Tag createInt(int $$0) {
        return IntTag.valueOf($$0);
    }

    public Tag createLong(long $$0) {
        return LongTag.valueOf($$0);
    }

    public Tag createFloat(float $$0) {
        return FloatTag.valueOf($$0);
    }

    public Tag createDouble(double $$0) {
        return DoubleTag.valueOf($$0);
    }

    public Tag createBoolean(boolean $$0) {
        return ByteTag.valueOf($$0);
    }

    public DataResult<String> getStringValue(Tag $$0) {
        if ($$0 instanceof StringTag) {
            StringTag $$1 = (StringTag)$$0;
            return DataResult.success((Object)$$1.getAsString());
        }
        return DataResult.error((String)"Not a string");
    }

    public Tag createString(String $$0) {
        return StringTag.valueOf($$0);
    }

    public DataResult<Tag> mergeToList(Tag $$0, Tag $$12) {
        return (DataResult)NbtOps.createCollector($$0).map($$1 -> DataResult.success((Object)$$1.accept($$12).result())).orElseGet(() -> DataResult.error((String)("mergeToList called with not a list: " + $$0), (Object)$$0));
    }

    public DataResult<Tag> mergeToList(Tag $$0, List<Tag> $$12) {
        return (DataResult)NbtOps.createCollector($$0).map($$1 -> DataResult.success((Object)$$1.acceptAll((Iterable<Tag>)$$12).result())).orElseGet(() -> DataResult.error((String)("mergeToList called with not a list: " + $$0), (Object)$$0));
    }

    public DataResult<Tag> mergeToMap(Tag $$0, Tag $$1, Tag $$22) {
        if (!($$0 instanceof CompoundTag) && !($$0 instanceof EndTag)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + $$0), (Object)$$0);
        }
        if (!($$1 instanceof StringTag)) {
            return DataResult.error((String)("key is not a string: " + $$1), (Object)$$0);
        }
        CompoundTag $$3 = new CompoundTag();
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$4 = (CompoundTag)$$0;
            $$4.getAllKeys().forEach($$2 -> $$3.put((String)$$2, $$4.get((String)$$2)));
        }
        $$3.put($$1.getAsString(), $$22);
        return DataResult.success((Object)$$3);
    }

    public DataResult<Tag> mergeToMap(Tag $$0, MapLike<Tag> $$1) {
        if (!($$0 instanceof CompoundTag) && !($$0 instanceof EndTag)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + $$0), (Object)$$0);
        }
        CompoundTag $$22 = new CompoundTag();
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$3 = (CompoundTag)$$0;
            $$3.getAllKeys().forEach($$2 -> $$22.put((String)$$2, $$3.get((String)$$2)));
        }
        ArrayList $$4 = Lists.newArrayList();
        $$1.entries().forEach(arg_0 -> NbtOps.lambda$mergeToMap$6((List)$$4, $$22, arg_0));
        if (!$$4.isEmpty()) {
            return DataResult.error((String)("some keys are not strings: " + (List)$$4), (Object)$$22);
        }
        return DataResult.success((Object)$$22);
    }

    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag $$0) {
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$12 = (CompoundTag)$$0;
            return DataResult.success((Object)$$12.getAllKeys().stream().map($$1 -> Pair.of((Object)this.createString((String)$$1), (Object)$$12.get((String)$$1))));
        }
        return DataResult.error((String)("Not a map: " + $$0));
    }

    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag $$0) {
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$12 = (CompoundTag)$$0;
            return DataResult.success($$1 -> $$12.getAllKeys().forEach($$2 -> $$1.accept((Object)this.createString((String)$$2), (Object)$$12.get((String)$$2))));
        }
        return DataResult.error((String)("Not a map: " + $$0));
    }

    public DataResult<MapLike<Tag>> getMap(Tag $$0) {
        if ($$0 instanceof CompoundTag) {
            final CompoundTag $$1 = (CompoundTag)$$0;
            return DataResult.success((Object)new MapLike<Tag>(){

                @Nullable
                public Tag get(Tag $$0) {
                    return $$1.get($$0.getAsString());
                }

                @Nullable
                public Tag get(String $$0) {
                    return $$1.get($$0);
                }

                public Stream<Pair<Tag, Tag>> entries() {
                    return $$1.getAllKeys().stream().map($$1 -> Pair.of((Object)NbtOps.this.createString((String)$$1), (Object)$$1.get((String)$$1)));
                }

                public String toString() {
                    return "MapLike[" + $$1 + "]";
                }
            });
        }
        return DataResult.error((String)("Not a map: " + $$0));
    }

    public Tag createMap(Stream<Pair<Tag, Tag>> $$0) {
        CompoundTag $$12 = new CompoundTag();
        $$0.forEach($$1 -> $$12.put(((Tag)$$1.getFirst()).getAsString(), (Tag)$$1.getSecond()));
        return $$12;
    }

    private static Tag tryUnwrap(CompoundTag $$0) {
        Tag $$1;
        if ($$0.size() == 1 && ($$1 = $$0.get(WRAPPER_MARKER)) != null) {
            return $$1;
        }
        return $$0;
    }

    public DataResult<Stream<Tag>> getStream(Tag $$02) {
        if ($$02 instanceof ListTag) {
            ListTag $$1 = (ListTag)$$02;
            if ($$1.getElementType() == 10) {
                return DataResult.success((Object)$$1.stream().map($$0 -> NbtOps.tryUnwrap((CompoundTag)$$0)));
            }
            return DataResult.success((Object)$$1.stream());
        }
        if ($$02 instanceof CollectionTag) {
            CollectionTag $$2 = (CollectionTag)$$02;
            return DataResult.success((Object)$$2.stream().map($$0 -> $$0));
        }
        return DataResult.error((String)"Not a list");
    }

    public DataResult<Consumer<Consumer<Tag>>> getList(Tag $$0) {
        if ($$0 instanceof ListTag) {
            ListTag $$1 = (ListTag)$$0;
            if ($$1.getElementType() == 10) {
                return DataResult.success($$12 -> $$1.forEach($$1 -> $$12.accept((Object)NbtOps.tryUnwrap((CompoundTag)$$1))));
            }
            return DataResult.success(arg_0 -> ((ListTag)$$1).forEach(arg_0));
        }
        if ($$0 instanceof CollectionTag) {
            CollectionTag $$2 = (CollectionTag)$$0;
            return DataResult.success(arg_0 -> ((CollectionTag)$$2).forEach(arg_0));
        }
        return DataResult.error((String)("Not a list: " + $$0));
    }

    public DataResult<ByteBuffer> getByteBuffer(Tag $$0) {
        if ($$0 instanceof ByteArrayTag) {
            ByteArrayTag $$1 = (ByteArrayTag)$$0;
            return DataResult.success((Object)ByteBuffer.wrap((byte[])$$1.getAsByteArray()));
        }
        return super.getByteBuffer((Object)$$0);
    }

    public Tag createByteList(ByteBuffer $$0) {
        return new ByteArrayTag(DataFixUtils.toArray((ByteBuffer)$$0));
    }

    public DataResult<IntStream> getIntStream(Tag $$0) {
        if ($$0 instanceof IntArrayTag) {
            IntArrayTag $$1 = (IntArrayTag)$$0;
            return DataResult.success((Object)Arrays.stream((int[])$$1.getAsIntArray()));
        }
        return super.getIntStream((Object)$$0);
    }

    public Tag createIntList(IntStream $$0) {
        return new IntArrayTag($$0.toArray());
    }

    public DataResult<LongStream> getLongStream(Tag $$0) {
        if ($$0 instanceof LongArrayTag) {
            LongArrayTag $$1 = (LongArrayTag)$$0;
            return DataResult.success((Object)Arrays.stream((long[])$$1.getAsLongArray()));
        }
        return super.getLongStream((Object)$$0);
    }

    public Tag createLongList(LongStream $$0) {
        return new LongArrayTag($$0.toArray());
    }

    public Tag createList(Stream<Tag> $$0) {
        return InitialListCollector.INSTANCE.acceptAll($$0).result();
    }

    public Tag remove(Tag $$0, String $$12) {
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$22 = (CompoundTag)$$0;
            CompoundTag $$3 = new CompoundTag();
            $$22.getAllKeys().stream().filter($$1 -> !Objects.equals((Object)$$1, (Object)$$12)).forEach($$2 -> $$3.put((String)$$2, $$22.get((String)$$2)));
            return $$3;
        }
        return $$0;
    }

    public String toString() {
        return "NBT";
    }

    public RecordBuilder<Tag> mapBuilder() {
        return new NbtRecordBuilder();
    }

    private static Optional<ListCollector> createCollector(Tag $$0) {
        if ($$0 instanceof EndTag) {
            return Optional.of((Object)InitialListCollector.INSTANCE);
        }
        if ($$0 instanceof CollectionTag) {
            CollectionTag $$1 = (CollectionTag)$$0;
            if ($$1.isEmpty()) {
                return Optional.of((Object)InitialListCollector.INSTANCE);
            }
            if ($$1 instanceof ListTag) {
                ListTag $$2 = (ListTag)$$1;
                return switch ($$2.getElementType()) {
                    case 0 -> Optional.of((Object)InitialListCollector.INSTANCE);
                    case 10 -> Optional.of((Object)new HeterogenousListCollector((Collection<Tag>)$$2));
                    default -> Optional.of((Object)new HomogenousListCollector($$2));
                };
            }
            if ($$1 instanceof ByteArrayTag) {
                ByteArrayTag $$3 = (ByteArrayTag)$$1;
                return Optional.of((Object)new ByteListCollector($$3.getAsByteArray()));
            }
            if ($$1 instanceof IntArrayTag) {
                IntArrayTag $$4 = (IntArrayTag)$$1;
                return Optional.of((Object)new IntListCollector($$4.getAsIntArray()));
            }
            if ($$1 instanceof LongArrayTag) {
                LongArrayTag $$5 = (LongArrayTag)$$1;
                return Optional.of((Object)new LongListCollector($$5.getAsLongArray()));
            }
        }
        return Optional.empty();
    }

    private static /* synthetic */ void lambda$mergeToMap$6(List $$0, CompoundTag $$1, Pair $$2) {
        Tag $$3 = (Tag)$$2.getFirst();
        if (!($$3 instanceof StringTag)) {
            $$0.add((Object)$$3);
            return;
        }
        $$1.put($$3.getAsString(), (Tag)$$2.getSecond());
    }

    static class InitialListCollector
    implements ListCollector {
        public static final InitialListCollector INSTANCE = new InitialListCollector();

        private InitialListCollector() {
        }

        @Override
        public ListCollector accept(Tag $$0) {
            if ($$0 instanceof CompoundTag) {
                CompoundTag $$1 = (CompoundTag)$$0;
                return new HeterogenousListCollector().accept($$1);
            }
            if ($$0 instanceof ByteTag) {
                ByteTag $$2 = (ByteTag)$$0;
                return new ByteListCollector($$2.getAsByte());
            }
            if ($$0 instanceof IntTag) {
                IntTag $$3 = (IntTag)$$0;
                return new IntListCollector($$3.getAsInt());
            }
            if ($$0 instanceof LongTag) {
                LongTag $$4 = (LongTag)$$0;
                return new LongListCollector($$4.getAsLong());
            }
            return new HomogenousListCollector($$0);
        }

        @Override
        public Tag result() {
            return new ListTag();
        }
    }

    static interface ListCollector {
        public ListCollector accept(Tag var1);

        default public ListCollector acceptAll(Iterable<Tag> $$0) {
            ListCollector $$1 = this;
            for (Tag $$2 : $$0) {
                $$1 = $$1.accept($$2);
            }
            return $$1;
        }

        default public ListCollector acceptAll(Stream<Tag> $$0) {
            return this.acceptAll((Iterable<Tag>)((Iterable)() -> $$0.iterator()));
        }

        public Tag result();
    }

    class NbtRecordBuilder
    extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
        protected NbtRecordBuilder() {
            super((DynamicOps)NbtOps.this);
        }

        protected CompoundTag initBuilder() {
            return new CompoundTag();
        }

        protected CompoundTag append(String $$0, Tag $$1, CompoundTag $$2) {
            $$2.put($$0, $$1);
            return $$2;
        }

        protected DataResult<Tag> build(CompoundTag $$0, Tag $$1) {
            if ($$1 == null || $$1 == EndTag.INSTANCE) {
                return DataResult.success((Object)$$0);
            }
            if ($$1 instanceof CompoundTag) {
                CompoundTag $$2 = (CompoundTag)$$1;
                CompoundTag $$3 = new CompoundTag((Map<String, Tag>)Maps.newHashMap($$2.entries()));
                for (Map.Entry $$4 : $$0.entries().entrySet()) {
                    $$3.put((String)$$4.getKey(), (Tag)$$4.getValue());
                }
                return DataResult.success((Object)$$3);
            }
            return DataResult.error((String)("mergeToMap called with not a map: " + $$1), (Object)$$1);
        }
    }

    static class HeterogenousListCollector
    implements ListCollector {
        private final ListTag result = new ListTag();

        public HeterogenousListCollector() {
        }

        public HeterogenousListCollector(Collection<Tag> $$0) {
            this.result.addAll($$0);
        }

        public HeterogenousListCollector(IntArrayList $$02) {
            $$02.forEach($$0 -> this.result.add(HeterogenousListCollector.wrapElement(IntTag.valueOf($$0))));
        }

        public HeterogenousListCollector(ByteArrayList $$02) {
            $$02.forEach($$0 -> this.result.add(HeterogenousListCollector.wrapElement(ByteTag.valueOf($$0))));
        }

        public HeterogenousListCollector(LongArrayList $$02) {
            $$02.forEach($$0 -> this.result.add(HeterogenousListCollector.wrapElement(LongTag.valueOf($$0))));
        }

        private static boolean isWrapper(CompoundTag $$0) {
            return $$0.size() == 1 && $$0.contains(NbtOps.WRAPPER_MARKER);
        }

        private static Tag wrapIfNeeded(Tag $$0) {
            CompoundTag $$1;
            if ($$0 instanceof CompoundTag && !HeterogenousListCollector.isWrapper($$1 = (CompoundTag)$$0)) {
                return $$1;
            }
            return HeterogenousListCollector.wrapElement($$0);
        }

        private static CompoundTag wrapElement(Tag $$0) {
            CompoundTag $$1 = new CompoundTag();
            $$1.put(NbtOps.WRAPPER_MARKER, $$0);
            return $$1;
        }

        @Override
        public ListCollector accept(Tag $$0) {
            this.result.add(HeterogenousListCollector.wrapIfNeeded($$0));
            return this;
        }

        @Override
        public Tag result() {
            return this.result;
        }
    }

    static class HomogenousListCollector
    implements ListCollector {
        private final ListTag result = new ListTag();

        HomogenousListCollector(Tag $$0) {
            this.result.add($$0);
        }

        HomogenousListCollector(ListTag $$0) {
            this.result.addAll((Collection)$$0);
        }

        @Override
        public ListCollector accept(Tag $$0) {
            if ($$0.getId() != this.result.getElementType()) {
                return new HeterogenousListCollector().acceptAll((Iterable)this.result).accept($$0);
            }
            this.result.add($$0);
            return this;
        }

        @Override
        public Tag result() {
            return this.result;
        }
    }

    static class ByteListCollector
    implements ListCollector {
        private final ByteArrayList values = new ByteArrayList();

        public ByteListCollector(byte $$0) {
            this.values.add($$0);
        }

        public ByteListCollector(byte[] $$0) {
            this.values.addElements(0, $$0);
        }

        @Override
        public ListCollector accept(Tag $$0) {
            if ($$0 instanceof ByteTag) {
                ByteTag $$1 = (ByteTag)$$0;
                this.values.add($$1.getAsByte());
                return this;
            }
            return new HeterogenousListCollector(this.values).accept($$0);
        }

        @Override
        public Tag result() {
            return new ByteArrayTag(this.values.toByteArray());
        }
    }

    static class IntListCollector
    implements ListCollector {
        private final IntArrayList values = new IntArrayList();

        public IntListCollector(int $$0) {
            this.values.add($$0);
        }

        public IntListCollector(int[] $$0) {
            this.values.addElements(0, $$0);
        }

        @Override
        public ListCollector accept(Tag $$0) {
            if ($$0 instanceof IntTag) {
                IntTag $$1 = (IntTag)$$0;
                this.values.add($$1.getAsInt());
                return this;
            }
            return new HeterogenousListCollector(this.values).accept($$0);
        }

        @Override
        public Tag result() {
            return new IntArrayTag(this.values.toIntArray());
        }
    }

    static class LongListCollector
    implements ListCollector {
        private final LongArrayList values = new LongArrayList();

        public LongListCollector(long $$0) {
            this.values.add($$0);
        }

        public LongListCollector(long[] $$0) {
            this.values.addElements(0, $$0);
        }

        @Override
        public ListCollector accept(Tag $$0) {
            if ($$0 instanceof LongTag) {
                LongTag $$1 = (LongTag)$$0;
                this.values.add($$1.getAsLong());
                return this;
            }
            return new HeterogenousListCollector(this.values).accept($$0);
        }

        @Override
        public Tag result() {
            return new LongArrayTag(this.values.toLongArray());
        }
    }
}