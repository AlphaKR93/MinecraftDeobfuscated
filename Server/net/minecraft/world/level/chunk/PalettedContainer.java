/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.IntUnaryOperator
 *  java.util.function.Predicate
 *  java.util.stream.LongStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.SingleValuePalette;

public class PalettedContainer<T>
implements PaletteResize<T>,
PalettedContainerRO<T> {
    private static final int MIN_PALETTE_BITS = 0;
    private final PaletteResize<T> dummyPaletteResize = ($$0, $$1) -> 0;
    private final IdMap<T> registry;
    private volatile Data<T> data;
    private final Strategy strategy;
    private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

    public void acquire() {
        this.threadingDetector.checkAndLock();
    }

    public void release() {
        this.threadingDetector.checkAndUnlock();
    }

    public static <T> Codec<PalettedContainer<T>> codecRW(IdMap<T> $$0, Codec<T> $$1, Strategy $$2, T $$3) {
        PalettedContainerRO.Unpacker $$4 = PalettedContainer::unpack;
        return PalettedContainer.codec($$0, $$1, $$2, $$3, $$4);
    }

    public static <T> Codec<PalettedContainerRO<T>> codecRO(IdMap<T> $$0, Codec<T> $$12, Strategy $$22, T $$3) {
        PalettedContainerRO.Unpacker $$4 = ($$02, $$1, $$2) -> PalettedContainer.unpack($$02, $$1, $$2).map($$0 -> $$0);
        return PalettedContainer.codec($$0, $$12, $$22, $$3, $$4);
    }

    private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(IdMap<T> $$0, Codec<T> $$1, Strategy $$22, T $$32, PalettedContainerRO.Unpacker<T, C> $$4) {
        return RecordCodecBuilder.create($$2 -> $$2.group((App)$$1.mapResult(ExtraCodecs.orElsePartial($$32)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries), (App)Codec.LONG_STREAM.optionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)).apply((Applicative)$$2, PalettedContainerRO.PackedData::new)).comapFlatMap($$3 -> $$4.read($$0, $$22, (PalettedContainerRO.PackedData)((Object)$$3)), $$2 -> $$2.pack($$0, $$22));
    }

    public PalettedContainer(IdMap<T> $$02, Strategy $$12, Configuration<T> $$2, BitStorage $$3, List<T> $$4) {
        this.registry = $$02;
        this.strategy = $$12;
        this.data = new Data<T>($$2, $$3, $$2.factory().create($$2.bits(), $$02, this, $$4));
    }

    private PalettedContainer(IdMap<T> $$02, Strategy $$12, Data<T> $$2) {
        this.registry = $$02;
        this.strategy = $$12;
        this.data = $$2;
    }

    public PalettedContainer(IdMap<T> $$02, T $$12, Strategy $$2) {
        this.strategy = $$2;
        this.registry = $$02;
        this.data = this.createOrReuseData(null, 0);
        this.data.palette.idFor($$12);
    }

    private Data<T> createOrReuseData(@Nullable Data<T> $$0, int $$1) {
        Configuration<T> $$2 = this.strategy.getConfiguration(this.registry, $$1);
        if ($$0 != null && $$2.equals($$0.configuration())) {
            return $$0;
        }
        return $$2.createData(this.registry, this, this.strategy.size());
    }

    @Override
    public int onResize(int $$0, T $$1) {
        Data<T> $$2 = this.data;
        Data $$3 = this.createOrReuseData($$2, $$0);
        $$3.copyFrom($$2.palette, $$2.storage);
        this.data = $$3;
        return $$3.palette.idFor($$1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getAndSet(int $$0, int $$1, int $$2, T $$3) {
        this.acquire();
        try {
            T t = this.getAndSet(this.strategy.getIndex($$0, $$1, $$2), $$3);
            return t;
        }
        finally {
            this.release();
        }
    }

    public T getAndSetUnchecked(int $$0, int $$1, int $$2, T $$3) {
        return this.getAndSet(this.strategy.getIndex($$0, $$1, $$2), $$3);
    }

    private T getAndSet(int $$0, T $$1) {
        int $$2 = this.data.palette.idFor($$1);
        int $$3 = this.data.storage.getAndSet($$0, $$2);
        return this.data.palette.valueFor($$3);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int $$0, int $$1, int $$2, T $$3) {
        this.acquire();
        try {
            this.set(this.strategy.getIndex($$0, $$1, $$2), $$3);
        }
        finally {
            this.release();
        }
    }

    private void set(int $$0, T $$1) {
        int $$2 = this.data.palette.idFor($$1);
        this.data.storage.set($$0, $$2);
    }

    @Override
    public T get(int $$0, int $$1, int $$2) {
        return this.get(this.strategy.getIndex($$0, $$1, $$2));
    }

    protected T get(int $$0) {
        Data<T> $$1 = this.data;
        return $$1.palette.valueFor($$1.storage.get($$0));
    }

    @Override
    public void getAll(Consumer<T> $$0) {
        Palette $$1 = this.data.palette();
        IntArraySet $$22 = new IntArraySet();
        this.data.storage.getAll(arg_0 -> ((IntSet)$$22).add(arg_0));
        $$22.forEach($$2 -> $$0.accept($$1.valueFor($$2)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void read(FriendlyByteBuf $$0) {
        this.acquire();
        try {
            byte $$1 = $$0.readByte();
            Data<T> $$2 = this.createOrReuseData(this.data, $$1);
            $$2.palette.read($$0);
            $$0.readLongArray($$2.storage.getRaw());
            this.data = $$2;
        }
        finally {
            this.release();
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        this.acquire();
        try {
            this.data.write($$0);
        }
        finally {
            this.release();
        }
    }

    /*
     * WARNING - void declaration
     */
    private static <T> DataResult<PalettedContainer<T>> unpack(IdMap<T> $$02, Strategy $$12, PalettedContainerRO.PackedData<T> $$22) {
        void $$16;
        List<T> $$3 = $$22.paletteEntries();
        int $$4 = $$12.size();
        int $$5 = $$12.calculateBitsForSerialization($$02, $$3.size());
        Configuration<T> $$6 = $$12.getConfiguration($$02, $$5);
        if ($$5 == 0) {
            ZeroBitStorage $$7 = new ZeroBitStorage($$4);
        } else {
            Optional<LongStream> $$8 = $$22.storage();
            if ($$8.isEmpty()) {
                return DataResult.error((String)"Missing values for non-zero storage");
            }
            long[] $$9 = ((LongStream)$$8.get()).toArray();
            try {
                if ($$6.factory() == Strategy.GLOBAL_PALETTE_FACTORY) {
                    HashMapPalette<Object> $$10 = new HashMapPalette<Object>($$02, $$5, ($$0, $$1) -> 0, $$3);
                    SimpleBitStorage $$11 = new SimpleBitStorage($$5, $$4, $$9);
                    int[] $$122 = new int[$$4];
                    $$11.unpack($$122);
                    PalettedContainer.swapPalette($$122, $$2 -> $$02.getId($$10.valueFor($$2)));
                    SimpleBitStorage $$13 = new SimpleBitStorage($$6.bits(), $$4, $$122);
                } else {
                    SimpleBitStorage $$14 = new SimpleBitStorage($$6.bits(), $$4, $$9);
                }
            }
            catch (SimpleBitStorage.InitializationException $$15) {
                return DataResult.error((String)("Failed to read PalettedContainer: " + $$15.getMessage()));
            }
        }
        return DataResult.success(new PalettedContainer<T>($$02, $$12, $$6, (BitStorage)$$16, $$3));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PalettedContainerRO.PackedData<T> pack(IdMap<T> $$0, Strategy $$12) {
        this.acquire();
        try {
            Optional $$8;
            HashMapPalette<T> $$2 = new HashMapPalette<T>($$0, this.data.storage.getBits(), this.dummyPaletteResize);
            int $$3 = $$12.size();
            int[] $$4 = new int[$$3];
            this.data.storage.unpack($$4);
            PalettedContainer.swapPalette($$4, $$1 -> $$2.idFor(this.data.palette.valueFor($$1)));
            int $$5 = $$12.calculateBitsForSerialization($$0, $$2.getSize());
            if ($$5 != 0) {
                SimpleBitStorage $$6 = new SimpleBitStorage($$5, $$3, $$4);
                Optional $$7 = Optional.of((Object)Arrays.stream((long[])$$6.getRaw()));
            } else {
                $$8 = Optional.empty();
            }
            PalettedContainerRO.PackedData<T> packedData = new PalettedContainerRO.PackedData<T>($$2.getEntries(), (Optional<LongStream>)$$8);
            return packedData;
        }
        finally {
            this.release();
        }
    }

    private static <T> void swapPalette(int[] $$0, IntUnaryOperator $$1) {
        int $$2 = -1;
        int $$3 = -1;
        for (int $$4 = 0; $$4 < $$0.length; ++$$4) {
            int $$5 = $$0[$$4];
            if ($$5 != $$2) {
                $$2 = $$5;
                $$3 = $$1.applyAsInt($$5);
            }
            $$0[$$4] = $$3;
        }
    }

    @Override
    public int getSerializedSize() {
        return this.data.getSerializedSize();
    }

    @Override
    public boolean maybeHas(Predicate<T> $$0) {
        return this.data.palette.maybeHas($$0);
    }

    public PalettedContainer<T> copy() {
        return new PalettedContainer<T>(this.registry, this.strategy, this.data.copy());
    }

    @Override
    public PalettedContainer<T> recreate() {
        return new PalettedContainer<T>(this.registry, this.data.palette.valueFor(0), this.strategy);
    }

    @Override
    public void count(CountConsumer<T> $$0) {
        if (this.data.palette.getSize() == 1) {
            $$0.accept(this.data.palette.valueFor(0), this.data.storage.getSize());
            return;
        }
        Int2IntOpenHashMap $$12 = new Int2IntOpenHashMap();
        this.data.storage.getAll($$1 -> $$12.addTo($$1, 1));
        $$12.int2IntEntrySet().forEach($$1 -> $$0.accept(this.data.palette.valueFor($$1.getIntKey()), $$1.getIntValue()));
    }

    public static abstract class Strategy {
        public static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
        public static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
        public static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
        static final Palette.Factory GLOBAL_PALETTE_FACTORY = GlobalPalette::create;
        public static final Strategy SECTION_STATES = new Strategy(4){

            @Override
            public <A> Configuration<A> getConfiguration(IdMap<A> $$0, int $$1) {
                return switch ($$1) {
                    case 0 -> new Configuration(SINGLE_VALUE_PALETTE_FACTORY, $$1);
                    case 1, 2, 3, 4 -> new Configuration(LINEAR_PALETTE_FACTORY, 4);
                    case 5, 6, 7, 8 -> new Configuration(HASHMAP_PALETTE_FACTORY, $$1);
                    default -> new Configuration(GLOBAL_PALETTE_FACTORY, Mth.ceillog2($$0.size()));
                };
            }
        };
        public static final Strategy SECTION_BIOMES = new Strategy(2){

            @Override
            public <A> Configuration<A> getConfiguration(IdMap<A> $$0, int $$1) {
                return switch ($$1) {
                    case 0 -> new Configuration(SINGLE_VALUE_PALETTE_FACTORY, $$1);
                    case 1, 2, 3 -> new Configuration(LINEAR_PALETTE_FACTORY, $$1);
                    default -> new Configuration(GLOBAL_PALETTE_FACTORY, Mth.ceillog2($$0.size()));
                };
            }
        };
        private final int sizeBits;

        Strategy(int $$0) {
            this.sizeBits = $$0;
        }

        public int size() {
            return 1 << this.sizeBits * 3;
        }

        public int getIndex(int $$0, int $$1, int $$2) {
            return ($$1 << this.sizeBits | $$2) << this.sizeBits | $$0;
        }

        public abstract <A> Configuration<A> getConfiguration(IdMap<A> var1, int var2);

        <A> int calculateBitsForSerialization(IdMap<A> $$0, int $$1) {
            int $$2 = Mth.ceillog2($$1);
            Configuration<A> $$3 = this.getConfiguration($$0, $$2);
            return $$3.factory() == GLOBAL_PALETTE_FACTORY ? $$2 : $$3.bits();
        }
    }

    record Data<T>(Configuration<T> configuration, BitStorage storage, Palette<T> palette) {
        public void copyFrom(Palette<T> $$0, BitStorage $$1) {
            for (int $$2 = 0; $$2 < $$1.getSize(); ++$$2) {
                T $$3 = $$0.valueFor($$1.get($$2));
                this.storage.set($$2, this.palette.idFor($$3));
            }
        }

        public int getSerializedSize() {
            return 1 + this.palette.getSerializedSize() + FriendlyByteBuf.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeByte(this.storage.getBits());
            this.palette.write($$0);
            $$0.writeLongArray(this.storage.getRaw());
        }

        public Data<T> copy() {
            return new Data<T>(this.configuration, this.storage.copy(), this.palette.copy());
        }
    }

    record Configuration<T>(Palette.Factory factory, int bits) {
        public Data<T> createData(IdMap<T> $$0, PaletteResize<T> $$1, int $$2) {
            BitStorage $$3 = this.bits == 0 ? new ZeroBitStorage($$2) : new SimpleBitStorage(this.bits, $$2);
            Palette<T> $$4 = this.factory.create(this.bits, $$0, $$1, List.of());
            return new Data<T>(this, $$3, $$4);
        }
    }

    @FunctionalInterface
    public static interface CountConsumer<T> {
        public void accept(T var1, int var2);
    }
}