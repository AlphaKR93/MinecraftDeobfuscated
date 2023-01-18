/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.doubles.Double2DoubleFunction
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Enum
 *  java.lang.Float
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.util.Arrays
 *  java.util.Optional
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
    private static final Codec<DensityFunction> CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch($$0 -> $$0.codec().codec(), Function.identity());
    protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
    static final Codec<Double> NOISE_VALUE_CODEC = Codec.doubleRange((double)-1000000.0, (double)1000000.0);
    public static final Codec<DensityFunction> DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap($$0 -> (DensityFunction)$$0.map(DensityFunctions::constant, Function.identity()), $$0 -> {
        if ($$0 instanceof Constant) {
            Constant $$1 = (Constant)$$0;
            return Either.left((Object)$$1.value());
        }
        return Either.right((Object)$$0);
    });

    public static Codec<? extends DensityFunction> bootstrap(Registry<Codec<? extends DensityFunction>> $$0) {
        DensityFunctions.register($$0, "blend_alpha", BlendAlpha.CODEC);
        DensityFunctions.register($$0, "blend_offset", BlendOffset.CODEC);
        DensityFunctions.register($$0, "beardifier", BeardifierMarker.CODEC);
        DensityFunctions.register($$0, "old_blended_noise", BlendedNoise.CODEC);
        for (Marker.Type type : Marker.Type.values()) {
            DensityFunctions.register($$0, type.getSerializedName(), type.codec);
        }
        DensityFunctions.register($$0, "noise", Noise.CODEC);
        DensityFunctions.register($$0, "end_islands", EndIslandDensityFunction.CODEC);
        DensityFunctions.register($$0, "weird_scaled_sampler", WeirdScaledSampler.CODEC);
        DensityFunctions.register($$0, "shifted_noise", ShiftedNoise.CODEC);
        DensityFunctions.register($$0, "range_choice", RangeChoice.CODEC);
        DensityFunctions.register($$0, "shift_a", ShiftA.CODEC);
        DensityFunctions.register($$0, "shift_b", ShiftB.CODEC);
        DensityFunctions.register($$0, "shift", Shift.CODEC);
        DensityFunctions.register($$0, "blend_density", BlendDensity.CODEC);
        DensityFunctions.register($$0, "clamp", Clamp.CODEC);
        for (Enum enum_ : Mapped.Type.values()) {
            DensityFunctions.register($$0, ((Mapped.Type)enum_).getSerializedName(), ((Mapped.Type)enum_).codec);
        }
        for (Enum enum_ : TwoArgumentSimpleFunction.Type.values()) {
            DensityFunctions.register($$0, ((TwoArgumentSimpleFunction.Type)enum_).getSerializedName(), ((TwoArgumentSimpleFunction.Type)enum_).codec);
        }
        DensityFunctions.register($$0, "spline", Spline.CODEC);
        DensityFunctions.register($$0, "constant", Constant.CODEC);
        return DensityFunctions.register($$0, "y_clamped_gradient", YClampedGradient.CODEC);
    }

    private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> $$0, String $$1, KeyDispatchDataCodec<? extends DensityFunction> $$2) {
        return Registry.register($$0, $$1, $$2.codec());
    }

    static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(Codec<A> $$0, Function<A, O> $$1, Function<O, A> $$2) {
        return KeyDispatchDataCodec.of($$0.fieldOf("argument").xmap($$1, $$2));
    }

    static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> $$0, Function<O, DensityFunction> $$1) {
        return DensityFunctions.singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, $$0, $$1);
    }

    static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> $$0, Function<O, DensityFunction> $$1, Function<O, DensityFunction> $$2) {
        return KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$3 -> $$3.group((App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter($$1), (App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter($$2)).apply((Applicative)$$3, $$0)));
    }

    static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> $$0) {
        return KeyDispatchDataCodec.of($$0);
    }

    private DensityFunctions() {
    }

    public static DensityFunction interpolated(DensityFunction $$0) {
        return new Marker(Marker.Type.Interpolated, $$0);
    }

    public static DensityFunction flatCache(DensityFunction $$0) {
        return new Marker(Marker.Type.FlatCache, $$0);
    }

    public static DensityFunction cache2d(DensityFunction $$0) {
        return new Marker(Marker.Type.Cache2D, $$0);
    }

    public static DensityFunction cacheOnce(DensityFunction $$0) {
        return new Marker(Marker.Type.CacheOnce, $$0);
    }

    public static DensityFunction cacheAllInCell(DensityFunction $$0) {
        return new Marker(Marker.Type.CacheAllInCell, $$0);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> $$0, @Deprecated double $$1, double $$2, double $$3, double $$4) {
        return DensityFunctions.mapFromUnitTo(new Noise(new DensityFunction.NoiseHolder($$0), $$1, $$2), $$3, $$4);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> $$0, double $$1, double $$2, double $$3) {
        return DensityFunctions.mappedNoise($$0, 1.0, $$1, $$2, $$3);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> $$0, double $$1, double $$2) {
        return DensityFunctions.mappedNoise($$0, 1.0, 1.0, $$1, $$2);
    }

    public static DensityFunction shiftedNoise2d(DensityFunction $$0, DensityFunction $$1, double $$2, Holder<NormalNoise.NoiseParameters> $$3) {
        return new ShiftedNoise($$0, DensityFunctions.zero(), $$1, $$2, 0.0, new DensityFunction.NoiseHolder($$3));
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> $$0) {
        return DensityFunctions.noise($$0, 1.0, 1.0);
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> $$0, double $$1, double $$2) {
        return new Noise(new DensityFunction.NoiseHolder($$0), $$1, $$2);
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> $$0, double $$1) {
        return DensityFunctions.noise($$0, 1.0, $$1);
    }

    public static DensityFunction rangeChoice(DensityFunction $$0, double $$1, double $$2, DensityFunction $$3, DensityFunction $$4) {
        return new RangeChoice($$0, $$1, $$2, $$3, $$4);
    }

    public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> $$0) {
        return new ShiftA(new DensityFunction.NoiseHolder($$0));
    }

    public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> $$0) {
        return new ShiftB(new DensityFunction.NoiseHolder($$0));
    }

    public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> $$0) {
        return new Shift(new DensityFunction.NoiseHolder($$0));
    }

    public static DensityFunction blendDensity(DensityFunction $$0) {
        return new BlendDensity($$0);
    }

    public static DensityFunction endIslands(long $$0) {
        return new EndIslandDensityFunction($$0);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction $$0, Holder<NormalNoise.NoiseParameters> $$1, WeirdScaledSampler.RarityValueMapper $$2) {
        return new WeirdScaledSampler($$0, new DensityFunction.NoiseHolder($$1), $$2);
    }

    public static DensityFunction add(DensityFunction $$0, DensityFunction $$1) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.ADD, $$0, $$1);
    }

    public static DensityFunction mul(DensityFunction $$0, DensityFunction $$1) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MUL, $$0, $$1);
    }

    public static DensityFunction min(DensityFunction $$0, DensityFunction $$1) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MIN, $$0, $$1);
    }

    public static DensityFunction max(DensityFunction $$0, DensityFunction $$1) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MAX, $$0, $$1);
    }

    public static DensityFunction spline(CubicSpline<Spline.Point, Spline.Coordinate> $$0) {
        return new Spline($$0);
    }

    public static DensityFunction zero() {
        return Constant.ZERO;
    }

    public static DensityFunction constant(double $$0) {
        return new Constant($$0);
    }

    public static DensityFunction yClampedGradient(int $$0, int $$1, double $$2, double $$3) {
        return new YClampedGradient($$0, $$1, $$2, $$3);
    }

    public static DensityFunction map(DensityFunction $$0, Mapped.Type $$1) {
        return Mapped.create($$1, $$0);
    }

    private static DensityFunction mapFromUnitTo(DensityFunction $$0, double $$1, double $$2) {
        double $$3 = ($$1 + $$2) * 0.5;
        double $$4 = ($$2 - $$1) * 0.5;
        return DensityFunctions.add(DensityFunctions.constant($$3), DensityFunctions.mul(DensityFunctions.constant($$4), $$0));
    }

    public static DensityFunction blendAlpha() {
        return BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return BlendOffset.INSTANCE;
    }

    public static DensityFunction lerp(DensityFunction $$0, DensityFunction $$1, DensityFunction $$2) {
        if ($$1 instanceof Constant) {
            Constant $$3 = (Constant)$$1;
            return DensityFunctions.lerp($$0, $$3.value, $$2);
        }
        DensityFunction $$4 = DensityFunctions.cacheOnce($$0);
        DensityFunction $$5 = DensityFunctions.add(DensityFunctions.mul($$4, DensityFunctions.constant(-1.0)), DensityFunctions.constant(1.0));
        return DensityFunctions.add(DensityFunctions.mul($$1, $$5), DensityFunctions.mul($$2, $$4));
    }

    public static DensityFunction lerp(DensityFunction $$0, double $$1, DensityFunction $$2) {
        return DensityFunctions.add(DensityFunctions.mul($$0, DensityFunctions.add($$2, DensityFunctions.constant(-$$1))), DensityFunctions.constant($$1));
    }

    protected static enum BlendAlpha implements DensityFunction.SimpleFunction
    {
        INSTANCE;

        public static final KeyDispatchDataCodec<DensityFunction> CODEC;

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return 1.0;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            Arrays.fill((double[])$$0, (double)1.0);
        }

        @Override
        public double minValue() {
            return 1.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        static {
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    protected static enum BlendOffset implements DensityFunction.SimpleFunction
    {
        INSTANCE;

        public static final KeyDispatchDataCodec<DensityFunction> CODEC;

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return 0.0;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            Arrays.fill((double[])$$0, (double)0.0);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 0.0;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        static {
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    protected static enum BeardifierMarker implements BeardifierOrMarker
    {
        INSTANCE;


        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return 0.0;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            Arrays.fill((double[])$$0, (double)0.0);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 0.0;
        }
    }

    protected record Marker(Type type, DensityFunction wrapped) implements MarkerOrMarked
    {
        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.wrapped.compute($$0);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.wrapped.fillArray($$0, $$1);
        }

        @Override
        public double minValue() {
            return this.wrapped.minValue();
        }

        @Override
        public double maxValue() {
            return this.wrapped.maxValue();
        }

        static enum Type implements StringRepresentable
        {
            Interpolated("interpolated"),
            FlatCache("flat_cache"),
            Cache2D("cache_2d"),
            CacheOnce("cache_once"),
            CacheAllInCell("cache_all_in_cell");

            private final String name;
            final KeyDispatchDataCodec<MarkerOrMarked> codec = DensityFunctions.singleFunctionArgumentCodec($$0 -> new Marker(this, (DensityFunction)$$0), MarkerOrMarked::wrapped);

            private Type(String $$02) {
                this.name = $$02;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    protected record Noise(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) implements DensityFunction
    {
        public static final MapCodec<Noise> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(Noise::noise), (App)Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), (App)Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply((Applicative)$$0, Noise::new));
        public static final KeyDispatchDataCodec<Noise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.noise.getValue((double)$$0.blockX() * this.xzScale, (double)$$0.blockY() * this.yScale, (double)$$0.blockZ() * this.xzScale);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new Noise($$0.visitNoise(this.noise), this.xzScale, this.yScale));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise.maxValue();
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected static final class EndIslandDensityFunction
    implements DensityFunction.SimpleFunction {
        public static final KeyDispatchDataCodec<EndIslandDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)new EndIslandDensityFunction(0L)));
        private static final float ISLAND_THRESHOLD = -0.9f;
        private final SimplexNoise islandNoise;

        public EndIslandDensityFunction(long $$0) {
            LegacyRandomSource $$1 = new LegacyRandomSource($$0);
            $$1.consumeCount(17292);
            this.islandNoise = new SimplexNoise($$1);
        }

        private static float getHeightValue(SimplexNoise $$0, int $$1, int $$2) {
            int $$3 = $$1 / 2;
            int $$4 = $$2 / 2;
            int $$5 = $$1 % 2;
            int $$6 = $$2 % 2;
            float $$7 = 100.0f - Mth.sqrt($$1 * $$1 + $$2 * $$2) * 8.0f;
            $$7 = Mth.clamp($$7, -100.0f, 80.0f);
            for (int $$8 = -12; $$8 <= 12; ++$$8) {
                for (int $$9 = -12; $$9 <= 12; ++$$9) {
                    long $$10 = $$3 + $$8;
                    long $$11 = $$4 + $$9;
                    if ($$10 * $$10 + $$11 * $$11 <= 4096L || !($$0.getValue($$10, $$11) < (double)-0.9f)) continue;
                    float $$12 = (Mth.abs($$10) * 3439.0f + Mth.abs($$11) * 147.0f) % 13.0f + 9.0f;
                    float $$13 = $$5 - $$8 * 2;
                    float $$14 = $$6 - $$9 * 2;
                    float $$15 = 100.0f - Mth.sqrt($$13 * $$13 + $$14 * $$14) * $$12;
                    $$15 = Mth.clamp($$15, -100.0f, 80.0f);
                    $$7 = Math.max((float)$$7, (float)$$15);
                }
            }
            return $$7;
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return ((double)EndIslandDensityFunction.getHeightValue(this.islandNoise, $$0.blockX() / 8, $$0.blockZ() / 8) - 8.0) / 128.0;
        }

        @Override
        public double minValue() {
            return -0.84375;
        }

        @Override
        public double maxValue() {
            return 0.5625;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record WeirdScaledSampler(DensityFunction input, DensityFunction.NoiseHolder noise, RarityValueMapper rarityValueMapper) implements TransformerWithContext
    {
        private static final MapCodec<WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(WeirdScaledSampler::input), (App)DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noise), (App)RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply((Applicative)$$0, WeirdScaledSampler::new));
        public static final KeyDispatchDataCodec<WeirdScaledSampler> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double transform(DensityFunction.FunctionContext $$0, double $$1) {
            double $$2 = this.rarityValueMapper.mapper.get($$1);
            return $$2 * Math.abs((double)this.noise.getValue((double)$$0.blockX() / $$2, (double)$$0.blockY() / $$2, (double)$$0.blockZ() / $$2));
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new WeirdScaledSampler(this.input.mapAll($$0), $$0.visitNoise(this.noise), this.rarityValueMapper));
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return this.rarityValueMapper.maxRarity * this.noise.maxValue();
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public static enum RarityValueMapper implements StringRepresentable
        {
            TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0),
            TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0);

            public static final Codec<RarityValueMapper> CODEC;
            private final String name;
            final Double2DoubleFunction mapper;
            final double maxRarity;

            private RarityValueMapper(String $$0, Double2DoubleFunction $$1, double $$2) {
                this.name = $$0;
                this.mapper = $$1;
                this.maxRarity = $$2;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }

            static {
                CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)RarityValueMapper::values));
            }
        }
    }

    protected record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction
    {
        private static final MapCodec<ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), (App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), (App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), (App)Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), (App)Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), (App)DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply((Applicative)$$0, ShiftedNoise::new));
        public static final KeyDispatchDataCodec<ShiftedNoise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            double $$1 = (double)$$0.blockX() * this.xzScale + this.shiftX.compute($$0);
            double $$2 = (double)$$0.blockY() * this.yScale + this.shiftY.compute($$0);
            double $$3 = (double)$$0.blockZ() * this.xzScale + this.shiftZ.compute($$0);
            return this.noise.getValue($$1, $$2, $$3);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new ShiftedNoise(this.shiftX.mapAll($$0), this.shiftY.mapAll($$0), this.shiftZ.mapAll($$0), this.xzScale, this.yScale, $$0.visitNoise(this.noise)));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise.maxValue();
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction
    {
        public static final MapCodec<RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(RangeChoice::input), (App)NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), (App)NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), (App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), (App)DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply((Applicative)$$0, RangeChoice::new));
        public static final KeyDispatchDataCodec<RangeChoice> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            double $$1 = this.input.compute($$0);
            if ($$1 >= this.minInclusive && $$1 < this.maxExclusive) {
                return this.whenInRange.compute($$0);
            }
            return this.whenOutOfRange.compute($$0);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.input.fillArray($$0, $$1);
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                double $$3 = $$0[$$2];
                $$0[$$2] = $$3 >= this.minInclusive && $$3 < this.maxExclusive ? this.whenInRange.compute($$1.forIndex($$2)) : this.whenOutOfRange.compute($$1.forIndex($$2));
            }
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new RangeChoice(this.input.mapAll($$0), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll($$0), this.whenOutOfRange.mapAll($$0)));
        }

        @Override
        public double minValue() {
            return Math.min((double)this.whenInRange.minValue(), (double)this.whenOutOfRange.minValue());
        }

        @Override
        public double maxValue() {
            return Math.max((double)this.whenInRange.maxValue(), (double)this.whenOutOfRange.maxValue());
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise
    {
        static final KeyDispatchDataCodec<ShiftA> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftA::new, ShiftA::offsetNoise);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.compute($$0.blockX(), 0.0, $$0.blockZ());
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new ShiftA($$0.visitNoise(this.offsetNoise)));
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise
    {
        static final KeyDispatchDataCodec<ShiftB> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftB::new, ShiftB::offsetNoise);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.compute($$0.blockZ(), $$0.blockX(), 0.0);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new ShiftB($$0.visitNoise(this.offsetNoise)));
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise
    {
        static final KeyDispatchDataCodec<Shift> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, Shift::new, Shift::offsetNoise);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.compute($$0.blockX(), $$0.blockY(), $$0.blockZ());
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new Shift($$0.visitNoise(this.offsetNoise)));
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    record BlendDensity(DensityFunction input) implements TransformerWithContext
    {
        static final KeyDispatchDataCodec<BlendDensity> CODEC = DensityFunctions.singleFunctionArgumentCodec(BlendDensity::new, BlendDensity::input);

        @Override
        public double transform(DensityFunction.FunctionContext $$0, double $$1) {
            return $$0.getBlender().blendDensity($$0, $$1);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new BlendDensity(this.input.mapAll($$0)));
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record Clamp(DensityFunction input, double minValue, double maxValue) implements PureTransformer
    {
        private static final MapCodec<Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(Clamp::input), (App)NOISE_VALUE_CODEC.fieldOf("min").forGetter(Clamp::minValue), (App)NOISE_VALUE_CODEC.fieldOf("max").forGetter(Clamp::maxValue)).apply((Applicative)$$0, Clamp::new));
        public static final KeyDispatchDataCodec<Clamp> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double transform(double $$0) {
            return Mth.clamp($$0, this.minValue, this.maxValue);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return new Clamp(this.input.mapAll($$0), this.minValue, this.maxValue);
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected record Mapped(Type type, DensityFunction input, double minValue, double maxValue) implements PureTransformer
    {
        public static Mapped create(Type $$0, DensityFunction $$1) {
            double $$2 = $$1.minValue();
            double $$3 = Mapped.transform($$0, $$2);
            double $$4 = Mapped.transform($$0, $$1.maxValue());
            if ($$0 == Type.ABS || $$0 == Type.SQUARE) {
                return new Mapped($$0, $$1, Math.max((double)0.0, (double)$$2), Math.max((double)$$3, (double)$$4));
            }
            return new Mapped($$0, $$1, $$3, $$4);
        }

        private static double transform(Type $$0, double $$1) {
            return switch ($$0) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ABS -> Math.abs((double)$$1);
                case Type.SQUARE -> $$1 * $$1;
                case Type.CUBE -> $$1 * $$1 * $$1;
                case Type.HALF_NEGATIVE -> {
                    if ($$1 > 0.0) {
                        yield $$1;
                    }
                    yield $$1 * 0.5;
                }
                case Type.QUARTER_NEGATIVE -> {
                    if ($$1 > 0.0) {
                        yield $$1;
                    }
                    yield $$1 * 0.25;
                }
                case Type.SQUEEZE -> {
                    double $$2 = Mth.clamp($$1, -1.0, 1.0);
                    yield $$2 / 2.0 - $$2 * $$2 * $$2 / 24.0;
                }
            };
        }

        @Override
        public double transform(double $$0) {
            return Mapped.transform(this.type, $$0);
        }

        @Override
        public Mapped mapAll(DensityFunction.Visitor $$0) {
            return Mapped.create(this.type, this.input.mapAll($$0));
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type.codec;
        }

        static enum Type implements StringRepresentable
        {
            ABS("abs"),
            SQUARE("square"),
            CUBE("cube"),
            HALF_NEGATIVE("half_negative"),
            QUARTER_NEGATIVE("quarter_negative"),
            SQUEEZE("squeeze");

            private final String name;
            final KeyDispatchDataCodec<Mapped> codec = DensityFunctions.singleFunctionArgumentCodec($$0 -> Mapped.create(this, $$0), Mapped::input);

            private Type(String $$02) {
                this.name = $$02;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    static interface TwoArgumentSimpleFunction
    extends DensityFunction {
        public static final Logger LOGGER = LogUtils.getLogger();

        public static TwoArgumentSimpleFunction create(Type $$0, DensityFunction $$1, DensityFunction $$2) {
            double $$10;
            double $$3 = $$1.minValue();
            double $$4 = $$2.minValue();
            double $$5 = $$1.maxValue();
            double $$6 = $$2.maxValue();
            if ($$0 == Type.MIN || $$0 == Type.MAX) {
                boolean $$8;
                boolean $$7 = $$3 >= $$6;
                boolean bl = $$8 = $$4 >= $$5;
                if ($$7 || $$8) {
                    LOGGER.warn("Creating a " + $$0 + " function between two non-overlapping inputs: " + $$1 + " and " + $$2);
                }
            }
            double $$9 = switch ($$0) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ADD -> $$3 + $$4;
                case Type.MAX -> Math.max((double)$$3, (double)$$4);
                case Type.MIN -> Math.min((double)$$3, (double)$$4);
                case Type.MUL -> $$3 > 0.0 && $$4 > 0.0 ? $$3 * $$4 : ($$5 < 0.0 && $$6 < 0.0 ? $$5 * $$6 : Math.min((double)($$3 * $$6), (double)($$5 * $$4)));
            };
            switch ($$0) {
                default: {
                    throw new IncompatibleClassChangeError();
                }
                case ADD: {
                    double d = $$5 + $$6;
                    break;
                }
                case MAX: {
                    double d = Math.max((double)$$5, (double)$$6);
                    break;
                }
                case MIN: {
                    double d = Math.min((double)$$5, (double)$$6);
                    break;
                }
                case MUL: {
                    double d = $$3 > 0.0 && $$4 > 0.0 ? $$5 * $$6 : ($$10 = $$5 < 0.0 && $$6 < 0.0 ? $$3 * $$4 : Math.max((double)($$3 * $$4), (double)($$5 * $$6)));
                }
            }
            if ($$0 == Type.MUL || $$0 == Type.ADD) {
                if ($$1 instanceof Constant) {
                    Constant $$11 = (Constant)$$1;
                    return new MulOrAdd($$0 == Type.ADD ? MulOrAdd.Type.ADD : MulOrAdd.Type.MUL, $$2, $$9, $$10, $$11.value);
                }
                if ($$2 instanceof Constant) {
                    Constant $$12 = (Constant)$$2;
                    return new MulOrAdd($$0 == Type.ADD ? MulOrAdd.Type.ADD : MulOrAdd.Type.MUL, $$1, $$9, $$10, $$12.value);
                }
            }
            return new Ap2($$0, $$1, $$2, $$9, $$10);
        }

        public Type type();

        public DensityFunction argument1();

        public DensityFunction argument2();

        @Override
        default public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type().codec;
        }

        public static enum Type implements StringRepresentable
        {
            ADD("add"),
            MUL("mul"),
            MIN("min"),
            MAX("max");

            final KeyDispatchDataCodec<TwoArgumentSimpleFunction> codec = DensityFunctions.doubleFunctionArgumentCodec(($$0, $$1) -> TwoArgumentSimpleFunction.create(this, $$0, $$1), TwoArgumentSimpleFunction::argument1, TwoArgumentSimpleFunction::argument2);
            private final String name;

            private Type(String $$02) {
                this.name = $$02;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    public record Spline(CubicSpline<Point, Coordinate> spline) implements DensityFunction
    {
        private static final Codec<CubicSpline<Point, Coordinate>> SPLINE_CODEC = CubicSpline.codec(Coordinate.CODEC);
        private static final MapCodec<Spline> DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
        public static final KeyDispatchDataCodec<Spline> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.spline.apply((Object)new Point($$0));
        }

        @Override
        public double minValue() {
            return this.spline.minValue();
        }

        @Override
        public double maxValue() {
            return this.spline.maxValue();
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new Spline(this.spline.mapAll((I $$1) -> $$1.mapAll($$0))));
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public record Point(DensityFunction.FunctionContext context) {
        }

        public record Coordinate(Holder<DensityFunction> function) implements ToFloatFunction<Point>
        {
            public static final Codec<Coordinate> CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);

            public String toString() {
                Optional<ResourceKey<DensityFunction>> $$0 = this.function.unwrapKey();
                if ($$0.isPresent()) {
                    ResourceKey $$1 = (ResourceKey)$$0.get();
                    if ($$1 == NoiseRouterData.CONTINENTS) {
                        return "continents";
                    }
                    if ($$1 == NoiseRouterData.EROSION) {
                        return "erosion";
                    }
                    if ($$1 == NoiseRouterData.RIDGES) {
                        return "weirdness";
                    }
                    if ($$1 == NoiseRouterData.RIDGES_FOLDED) {
                        return "ridges";
                    }
                }
                return "Coordinate[" + this.function + "]";
            }

            @Override
            public float apply(Point $$0) {
                return (float)this.function.value().compute($$0.context());
            }

            @Override
            public float minValue() {
                return this.function.isBound() ? (float)this.function.value().minValue() : Float.NEGATIVE_INFINITY;
            }

            @Override
            public float maxValue() {
                return this.function.isBound() ? (float)this.function.value().maxValue() : Float.POSITIVE_INFINITY;
            }

            public Coordinate mapAll(DensityFunction.Visitor $$0) {
                return new Coordinate(new Holder.Direct<DensityFunction>(this.function.value().mapAll($$0)));
            }
        }
    }

    record Constant(double value) implements DensityFunction.SimpleFunction
    {
        static final KeyDispatchDataCodec<Constant> CODEC = DensityFunctions.singleArgumentCodec(NOISE_VALUE_CODEC, Constant::new, Constant::value);
        static final Constant ZERO = new Constant(0.0);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.value;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            Arrays.fill((double[])$$0, (double)this.value);
        }

        @Override
        public double minValue() {
            return this.value;
        }

        @Override
        public double maxValue() {
            return this.value;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction
    {
        private static final MapCodec<YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.intRange((int)(DimensionType.MIN_Y * 2), (int)(DimensionType.MAX_Y * 2)).fieldOf("from_y").forGetter(YClampedGradient::fromY), (App)Codec.intRange((int)(DimensionType.MIN_Y * 2), (int)(DimensionType.MAX_Y * 2)).fieldOf("to_y").forGetter(YClampedGradient::toY), (App)NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(YClampedGradient::fromValue), (App)NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply((Applicative)$$0, YClampedGradient::new));
        public static final KeyDispatchDataCodec<YClampedGradient> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return Mth.clampedMap((double)$$0.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
        }

        @Override
        public double minValue() {
            return Math.min((double)this.fromValue, (double)this.toValue);
        }

        @Override
        public double maxValue() {
            return Math.max((double)this.fromValue, (double)this.toValue);
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    record Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements TwoArgumentSimpleFunction
    {
        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            double $$1 = this.argument1.compute($$0);
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case TwoArgumentSimpleFunction.Type.ADD -> $$1 + this.argument2.compute($$0);
                case TwoArgumentSimpleFunction.Type.MUL -> {
                    if ($$1 == 0.0) {
                        yield 0.0;
                    }
                    yield $$1 * this.argument2.compute($$0);
                }
                case TwoArgumentSimpleFunction.Type.MIN -> {
                    if ($$1 < this.argument2.minValue()) {
                        yield $$1;
                    }
                    yield Math.min((double)$$1, (double)this.argument2.compute($$0));
                }
                case TwoArgumentSimpleFunction.Type.MAX -> $$1 > this.argument2.maxValue() ? $$1 : Math.max((double)$$1, (double)this.argument2.compute($$0));
            };
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.argument1.fillArray($$0, $$1);
            switch (this.type) {
                case ADD: {
                    double[] $$2 = new double[$$0.length];
                    this.argument2.fillArray($$2, $$1);
                    for (int $$3 = 0; $$3 < $$0.length; ++$$3) {
                        $$0[$$3] = $$0[$$3] + $$2[$$3];
                    }
                    break;
                }
                case MUL: {
                    for (int $$4 = 0; $$4 < $$0.length; ++$$4) {
                        double $$5 = $$0[$$4];
                        $$0[$$4] = $$5 == 0.0 ? 0.0 : $$5 * this.argument2.compute($$1.forIndex($$4));
                    }
                    break;
                }
                case MIN: {
                    double $$6 = this.argument2.minValue();
                    for (int $$7 = 0; $$7 < $$0.length; ++$$7) {
                        double $$8 = $$0[$$7];
                        $$0[$$7] = $$8 < $$6 ? $$8 : Math.min((double)$$8, (double)this.argument2.compute($$1.forIndex($$7)));
                    }
                    break;
                }
                case MAX: {
                    double $$9 = this.argument2.maxValue();
                    for (int $$10 = 0; $$10 < $$0.length; ++$$10) {
                        double $$11 = $$0[$$10];
                        $$0[$$10] = $$11 > $$9 ? $$11 : Math.max((double)$$11, (double)this.argument2.compute($$1.forIndex($$10)));
                    }
                    break;
                }
            }
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll($$0), this.argument2.mapAll($$0)));
        }
    }

    record MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements PureTransformer,
    TwoArgumentSimpleFunction
    {
        @Override
        public TwoArgumentSimpleFunction.Type type() {
            return this.specificType == Type.MUL ? TwoArgumentSimpleFunction.Type.MUL : TwoArgumentSimpleFunction.Type.ADD;
        }

        @Override
        public DensityFunction argument1() {
            return DensityFunctions.constant(this.argument);
        }

        @Override
        public DensityFunction argument2() {
            return this.input;
        }

        @Override
        public double transform(double $$0) {
            return switch (this.specificType) {
                default -> throw new IncompatibleClassChangeError();
                case Type.MUL -> $$0 * this.argument;
                case Type.ADD -> $$0 + this.argument;
            };
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            double $$9;
            double $$8;
            DensityFunction $$1 = this.input.mapAll($$0);
            double $$2 = $$1.minValue();
            double $$3 = $$1.maxValue();
            if (this.specificType == Type.ADD) {
                double $$4 = $$2 + this.argument;
                double $$5 = $$3 + this.argument;
            } else if (this.argument >= 0.0) {
                double $$6 = $$2 * this.argument;
                double $$7 = $$3 * this.argument;
            } else {
                $$8 = $$3 * this.argument;
                $$9 = $$2 * this.argument;
            }
            return new MulOrAdd(this.specificType, $$1, $$8, $$9, this.argument);
        }

        static enum Type {
            MUL,
            ADD;

        }
    }

    static interface ShiftNoise
    extends DensityFunction {
        public DensityFunction.NoiseHolder offsetNoise();

        @Override
        default public double minValue() {
            return -this.maxValue();
        }

        @Override
        default public double maxValue() {
            return this.offsetNoise().maxValue() * 4.0;
        }

        default public double compute(double $$0, double $$1, double $$2) {
            return this.offsetNoise().getValue($$0 * 0.25, $$1 * 0.25, $$2 * 0.25) * 4.0;
        }

        @Override
        default public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }
    }

    public static interface MarkerOrMarked
    extends DensityFunction {
        public Marker.Type type();

        public DensityFunction wrapped();

        @Override
        default public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type().codec;
        }

        @Override
        default public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new Marker(this.type(), this.wrapped().mapAll($$0)));
        }
    }

    @VisibleForDebug
    public record HolderHolder(Holder<DensityFunction> function) implements DensityFunction
    {
        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return this.function.value().compute($$0);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.function.value().fillArray($$0, $$1);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return $$0.apply(new HolderHolder(new Holder.Direct<DensityFunction>(this.function.value().mapAll($$0))));
        }

        @Override
        public double minValue() {
            return this.function.isBound() ? this.function.value().minValue() : Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return this.function.isBound() ? this.function.value().maxValue() : Double.POSITIVE_INFINITY;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
        }
    }

    public static interface BeardifierOrMarker
    extends DensityFunction.SimpleFunction {
        public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)BeardifierMarker.INSTANCE));

        @Override
        default public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    static interface PureTransformer
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double compute(DensityFunction.FunctionContext $$0) {
            return this.transform(this.input().compute($$0));
        }

        @Override
        default public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.input().fillArray($$0, $$1);
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                $$0[$$2] = this.transform($$0[$$2]);
            }
        }

        public double transform(double var1);
    }

    static interface TransformerWithContext
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double compute(DensityFunction.FunctionContext $$0) {
            return this.transform($$0, this.input().compute($$0));
        }

        @Override
        default public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.input().fillArray($$0, $$1);
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                $$0[$$2] = this.transform($$1.forIndex($$2), $$0[$$2]);
            }
        }

        public double transform(DensityFunction.FunctionContext var1, double var2);
    }
}