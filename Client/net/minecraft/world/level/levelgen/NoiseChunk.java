/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  java.lang.Double
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.System
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;

public class NoiseChunk
implements DensityFunction.ContextProvider,
DensityFunction.FunctionContext {
    private final NoiseSettings noiseSettings;
    final int cellCountXZ;
    final int cellCountY;
    final int cellNoiseMinY;
    private final int firstCellX;
    private final int firstCellZ;
    final int firstNoiseX;
    final int firstNoiseZ;
    final List<NoiseInterpolator> interpolators;
    final List<CacheAllInCell> cellCaches;
    private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();
    private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
    private final Aquifer aquifer;
    private final DensityFunction initialDensityNoJaggedness;
    private final BlockStateFiller blockStateRule;
    private final Blender blender;
    private final FlatCache blendAlpha;
    private final FlatCache blendOffset;
    private final DensityFunctions.BeardifierOrMarker beardifier;
    private long lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
    private Blender.BlendingOutput lastBlendingOutput = new Blender.BlendingOutput(1.0, 0.0);
    final int noiseSizeXZ;
    final int cellWidth;
    final int cellHeight;
    boolean interpolating;
    boolean fillingCell;
    private int cellStartBlockX;
    int cellStartBlockY;
    private int cellStartBlockZ;
    int inCellX;
    int inCellY;
    int inCellZ;
    long interpolationCounter;
    long arrayInterpolationCounter;
    int arrayIndex;
    private final DensityFunction.ContextProvider sliceFillingContextProvider = new DensityFunction.ContextProvider(){

        @Override
        public DensityFunction.FunctionContext forIndex(int $$0) {
            NoiseChunk.this.cellStartBlockY = ($$0 + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
            ++NoiseChunk.this.interpolationCounter;
            NoiseChunk.this.inCellY = 0;
            NoiseChunk.this.arrayIndex = $$0;
            return NoiseChunk.this;
        }

        @Override
        public void fillAllDirectly(double[] $$0, DensityFunction $$1) {
            for (int $$2 = 0; $$2 < NoiseChunk.this.cellCountY + 1; ++$$2) {
                NoiseChunk.this.cellStartBlockY = ($$2 + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
                ++NoiseChunk.this.interpolationCounter;
                NoiseChunk.this.inCellY = 0;
                NoiseChunk.this.arrayIndex = $$2;
                $$0[$$2] = $$1.compute(NoiseChunk.this);
            }
        }
    };

    public static NoiseChunk forChunk(ChunkAccess $$0, RandomState $$1, DensityFunctions.BeardifierOrMarker $$2, NoiseGeneratorSettings $$3, Aquifer.FluidPicker $$4, Blender $$5) {
        NoiseSettings $$6 = $$3.noiseSettings().clampToHeightAccessor($$0);
        ChunkPos $$7 = $$0.getPos();
        int $$8 = 16 / $$6.getCellWidth();
        return new NoiseChunk($$8, $$1, $$7.getMinBlockX(), $$7.getMinBlockZ(), $$6, $$2, $$3, $$4, $$5);
    }

    public NoiseChunk(int $$0, RandomState $$12, int $$2, int $$3, NoiseSettings $$4, DensityFunctions.BeardifierOrMarker $$5, NoiseGeneratorSettings $$6, Aquifer.FluidPicker $$7, Blender $$8) {
        this.noiseSettings = $$4;
        this.cellWidth = $$4.getCellWidth();
        this.cellHeight = $$4.getCellHeight();
        this.cellCountXZ = $$0;
        this.cellCountY = Mth.floorDiv($$4.height(), this.cellHeight);
        this.cellNoiseMinY = Mth.floorDiv($$4.minY(), this.cellHeight);
        this.firstCellX = Math.floorDiv((int)$$2, (int)this.cellWidth);
        this.firstCellZ = Math.floorDiv((int)$$3, (int)this.cellWidth);
        this.interpolators = Lists.newArrayList();
        this.cellCaches = Lists.newArrayList();
        this.firstNoiseX = QuartPos.fromBlock($$2);
        this.firstNoiseZ = QuartPos.fromBlock($$3);
        this.noiseSizeXZ = QuartPos.fromBlock($$0 * this.cellWidth);
        this.blender = $$8;
        this.beardifier = $$5;
        this.blendAlpha = new FlatCache(new BlendAlpha(), false);
        this.blendOffset = new FlatCache(new BlendOffset(), false);
        for (int $$9 = 0; $$9 <= this.noiseSizeXZ; ++$$9) {
            int $$10 = this.firstNoiseX + $$9;
            int $$11 = QuartPos.toBlock($$10);
            for (int $$122 = 0; $$122 <= this.noiseSizeXZ; ++$$122) {
                int $$13 = this.firstNoiseZ + $$122;
                int $$14 = QuartPos.toBlock($$13);
                Blender.BlendingOutput $$15 = $$8.blendOffsetAndFactor($$11, $$14);
                this.blendAlpha.values[$$9][$$122] = $$15.alpha();
                this.blendOffset.values[$$9][$$122] = $$15.blendingOffset();
            }
        }
        NoiseRouter $$16 = $$12.router();
        NoiseRouter $$17 = $$16.mapAll(this::wrap);
        if (!$$6.isAquifersEnabled()) {
            this.aquifer = Aquifer.createDisabled($$7);
        } else {
            int $$18 = SectionPos.blockToSectionCoord($$2);
            int $$19 = SectionPos.blockToSectionCoord($$3);
            this.aquifer = Aquifer.create(this, new ChunkPos($$18, $$19), $$17, $$12.aquiferRandom(), $$4.minY(), $$4.height(), $$7);
        }
        ImmutableList.Builder $$20 = ImmutableList.builder();
        DensityFunction $$21 = DensityFunctions.cacheAllInCell(DensityFunctions.add($$17.finalDensity(), DensityFunctions.BeardifierMarker.INSTANCE)).mapAll(this::wrap);
        $$20.add($$1 -> this.aquifer.computeSubstance($$1, $$21.compute($$1)));
        if ($$6.oreVeinsEnabled()) {
            $$20.add((Object)OreVeinifier.create($$17.veinToggle(), $$17.veinRidged(), $$17.veinGap(), $$12.oreRandom()));
        }
        this.blockStateRule = new MaterialRuleList((List<BlockStateFiller>)$$20.build());
        this.initialDensityNoJaggedness = $$17.initialDensityWithoutJaggedness();
    }

    protected Climate.Sampler cachedClimateSampler(NoiseRouter $$0, List<Climate.ParameterPoint> $$1) {
        return new Climate.Sampler($$0.temperature().mapAll(this::wrap), $$0.vegetation().mapAll(this::wrap), $$0.continents().mapAll(this::wrap), $$0.erosion().mapAll(this::wrap), $$0.depth().mapAll(this::wrap), $$0.ridges().mapAll(this::wrap), $$1);
    }

    @Nullable
    protected BlockState getInterpolatedState() {
        return this.blockStateRule.calculate(this);
    }

    @Override
    public int blockX() {
        return this.cellStartBlockX + this.inCellX;
    }

    @Override
    public int blockY() {
        return this.cellStartBlockY + this.inCellY;
    }

    @Override
    public int blockZ() {
        return this.cellStartBlockZ + this.inCellZ;
    }

    public int preliminarySurfaceLevel(int $$0, int $$1) {
        int $$2 = QuartPos.toBlock(QuartPos.fromBlock($$0));
        int $$3 = QuartPos.toBlock(QuartPos.fromBlock($$1));
        return this.preliminarySurfaceLevel.computeIfAbsent(ColumnPos.asLong($$2, $$3), this::computePreliminarySurfaceLevel);
    }

    private int computePreliminarySurfaceLevel(long $$0) {
        int $$1 = ColumnPos.getX($$0);
        int $$2 = ColumnPos.getZ($$0);
        int $$3 = this.noiseSettings.minY();
        for (int $$4 = $$3 + this.noiseSettings.height(); $$4 >= $$3; $$4 -= this.cellHeight) {
            DensityFunction.SinglePointContext singlePointContext = new DensityFunction.SinglePointContext($$1, $$4, $$2);
            if (!(this.initialDensityNoJaggedness.compute(singlePointContext) > 0.390625)) continue;
            return $$4;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public Blender getBlender() {
        return this.blender;
    }

    private void fillSlice(boolean $$0, int $$1) {
        this.cellStartBlockX = $$1 * this.cellWidth;
        this.inCellX = 0;
        for (int $$2 = 0; $$2 < this.cellCountXZ + 1; ++$$2) {
            int $$3 = this.firstCellZ + $$2;
            this.cellStartBlockZ = $$3 * this.cellWidth;
            this.inCellZ = 0;
            ++this.arrayInterpolationCounter;
            for (NoiseInterpolator $$4 : this.interpolators) {
                double[] $$5 = ($$0 ? $$4.slice0 : $$4.slice1)[$$2];
                $$4.fillArray($$5, this.sliceFillingContextProvider);
            }
        }
        ++this.arrayInterpolationCounter;
    }

    public void initializeForFirstCellX() {
        if (this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.interpolating = true;
        this.interpolationCounter = 0L;
        this.fillSlice(true, this.firstCellX);
    }

    public void advanceCellX(int $$0) {
        this.fillSlice(false, this.firstCellX + $$0 + 1);
        this.cellStartBlockX = (this.firstCellX + $$0) * this.cellWidth;
    }

    @Override
    public NoiseChunk forIndex(int $$0) {
        int $$1 = Math.floorMod((int)$$0, (int)this.cellWidth);
        int $$2 = Math.floorDiv((int)$$0, (int)this.cellWidth);
        int $$3 = Math.floorMod((int)$$2, (int)this.cellWidth);
        int $$4 = this.cellHeight - 1 - Math.floorDiv((int)$$2, (int)this.cellWidth);
        this.inCellX = $$3;
        this.inCellY = $$4;
        this.inCellZ = $$1;
        this.arrayIndex = $$0;
        return this;
    }

    @Override
    public void fillAllDirectly(double[] $$0, DensityFunction $$1) {
        this.arrayIndex = 0;
        for (int $$2 = this.cellHeight - 1; $$2 >= 0; --$$2) {
            this.inCellY = $$2;
            for (int $$3 = 0; $$3 < this.cellWidth; ++$$3) {
                this.inCellX = $$3;
                int $$4 = 0;
                while ($$4 < this.cellWidth) {
                    this.inCellZ = $$4++;
                    $$0[this.arrayIndex++] = $$1.compute(this);
                }
            }
        }
    }

    public void selectCellYZ(int $$0, int $$1) {
        this.interpolators.forEach($$2 -> $$2.selectCellYZ($$0, $$1));
        this.fillingCell = true;
        this.cellStartBlockY = ($$0 + this.cellNoiseMinY) * this.cellHeight;
        this.cellStartBlockZ = (this.firstCellZ + $$1) * this.cellWidth;
        ++this.arrayInterpolationCounter;
        for (CacheAllInCell $$22 : this.cellCaches) {
            $$22.noiseFiller.fillArray($$22.values, this);
        }
        ++this.arrayInterpolationCounter;
        this.fillingCell = false;
    }

    public void updateForY(int $$0, double $$12) {
        this.inCellY = $$0 - this.cellStartBlockY;
        this.interpolators.forEach($$1 -> $$1.updateForY($$12));
    }

    public void updateForX(int $$0, double $$12) {
        this.inCellX = $$0 - this.cellStartBlockX;
        this.interpolators.forEach($$1 -> $$1.updateForX($$12));
    }

    public void updateForZ(int $$0, double $$12) {
        this.inCellZ = $$0 - this.cellStartBlockZ;
        ++this.interpolationCounter;
        this.interpolators.forEach($$1 -> $$1.updateForZ($$12));
    }

    public void stopInterpolation() {
        if (!this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        }
        this.interpolating = false;
    }

    public void swapSlices() {
        this.interpolators.forEach(NoiseInterpolator::swapSlices);
    }

    public Aquifer aquifer() {
        return this.aquifer;
    }

    protected int cellWidth() {
        return this.cellWidth;
    }

    protected int cellHeight() {
        return this.cellHeight;
    }

    Blender.BlendingOutput getOrComputeBlendingOutput(int $$0, int $$1) {
        Blender.BlendingOutput $$3;
        long $$2 = ChunkPos.asLong($$0, $$1);
        if (this.lastBlendingDataPos == $$2) {
            return this.lastBlendingOutput;
        }
        this.lastBlendingDataPos = $$2;
        this.lastBlendingOutput = $$3 = this.blender.blendOffsetAndFactor($$0, $$1);
        return $$3;
    }

    protected DensityFunction wrap(DensityFunction $$0) {
        return (DensityFunction)this.wrapped.computeIfAbsent((Object)$$0, this::wrapNew);
    }

    private DensityFunction wrapNew(DensityFunction $$0) {
        if ($$0 instanceof DensityFunctions.Marker) {
            DensityFunctions.Marker $$1 = (DensityFunctions.Marker)$$0;
            return switch ($$1.type()) {
                default -> throw new IncompatibleClassChangeError();
                case DensityFunctions.Marker.Type.Interpolated -> new NoiseInterpolator($$1.wrapped());
                case DensityFunctions.Marker.Type.FlatCache -> new FlatCache($$1.wrapped(), true);
                case DensityFunctions.Marker.Type.Cache2D -> new Cache2D($$1.wrapped());
                case DensityFunctions.Marker.Type.CacheOnce -> new CacheOnce($$1.wrapped());
                case DensityFunctions.Marker.Type.CacheAllInCell -> new CacheAllInCell($$1.wrapped());
            };
        }
        if (this.blender != Blender.empty()) {
            if ($$0 == DensityFunctions.BlendAlpha.INSTANCE) {
                return this.blendAlpha;
            }
            if ($$0 == DensityFunctions.BlendOffset.INSTANCE) {
                return this.blendOffset;
            }
        }
        if ($$0 == DensityFunctions.BeardifierMarker.INSTANCE) {
            return this.beardifier;
        }
        if ($$0 instanceof DensityFunctions.HolderHolder) {
            DensityFunctions.HolderHolder $$2 = (DensityFunctions.HolderHolder)$$0;
            return $$2.function().value();
        }
        return $$0;
    }

    class FlatCache
    implements DensityFunctions.MarkerOrMarked,
    NoiseChunkDensityFunction {
        private final DensityFunction noiseFiller;
        final double[][] values;

        FlatCache(DensityFunction $$0, boolean $$1) {
            this.noiseFiller = $$0;
            this.values = new double[NoiseChunk.this.noiseSizeXZ + 1][NoiseChunk.this.noiseSizeXZ + 1];
            if ($$1) {
                for (int $$2 = 0; $$2 <= NoiseChunk.this.noiseSizeXZ; ++$$2) {
                    int $$3 = NoiseChunk.this.firstNoiseX + $$2;
                    int $$4 = QuartPos.toBlock($$3);
                    for (int $$5 = 0; $$5 <= NoiseChunk.this.noiseSizeXZ; ++$$5) {
                        int $$6 = NoiseChunk.this.firstNoiseZ + $$5;
                        int $$7 = QuartPos.toBlock($$6);
                        this.values[$$2][$$5] = $$0.compute(new DensityFunction.SinglePointContext($$4, 0, $$7));
                    }
                }
            }
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            int $$1 = QuartPos.fromBlock($$0.blockX());
            int $$2 = QuartPos.fromBlock($$0.blockZ());
            int $$3 = $$1 - NoiseChunk.this.firstNoiseX;
            int $$4 = $$2 - NoiseChunk.this.firstNoiseZ;
            int $$5 = this.values.length;
            if ($$3 >= 0 && $$4 >= 0 && $$3 < $$5 && $$4 < $$5) {
                return this.values[$$3][$$4];
            }
            return this.noiseFiller.compute($$0);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.FlatCache;
        }
    }

    class BlendAlpha
    implements NoiseChunkDensityFunction {
        BlendAlpha() {
        }

        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendAlpha.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return this.wrapped().mapAll($$0);
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return NoiseChunk.this.getOrComputeBlendingOutput($$0.blockX(), $$0.blockZ()).alpha();
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return DensityFunctions.BlendAlpha.CODEC;
        }
    }

    class BlendOffset
    implements NoiseChunkDensityFunction {
        BlendOffset() {
        }

        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendOffset.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor $$0) {
            return this.wrapped().mapAll($$0);
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            return NoiseChunk.this.getOrComputeBlendingOutput($$0.blockX(), $$0.blockZ()).blendingOffset();
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
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
            return DensityFunctions.BlendOffset.CODEC;
        }
    }

    @FunctionalInterface
    public static interface BlockStateFiller {
        @Nullable
        public BlockState calculate(DensityFunction.FunctionContext var1);
    }

    public class NoiseInterpolator
    implements DensityFunctions.MarkerOrMarked,
    NoiseChunkDensityFunction {
        double[][] slice0;
        double[][] slice1;
        private final DensityFunction noiseFiller;
        private double noise000;
        private double noise001;
        private double noise100;
        private double noise101;
        private double noise010;
        private double noise011;
        private double noise110;
        private double noise111;
        private double valueXZ00;
        private double valueXZ10;
        private double valueXZ01;
        private double valueXZ11;
        private double valueZ0;
        private double valueZ1;
        private double value;

        NoiseInterpolator(DensityFunction $$1) {
            this.noiseFiller = $$1;
            this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
            this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
            NoiseChunk.this.interpolators.add((Object)this);
        }

        private double[][] allocateSlice(int $$0, int $$1) {
            int $$2 = $$1 + 1;
            int $$3 = $$0 + 1;
            double[][] $$4 = new double[$$2][$$3];
            for (int $$5 = 0; $$5 < $$2; ++$$5) {
                $$4[$$5] = new double[$$3];
            }
            return $$4;
        }

        void selectCellYZ(int $$0, int $$1) {
            this.noise000 = this.slice0[$$1][$$0];
            this.noise001 = this.slice0[$$1 + 1][$$0];
            this.noise100 = this.slice1[$$1][$$0];
            this.noise101 = this.slice1[$$1 + 1][$$0];
            this.noise010 = this.slice0[$$1][$$0 + 1];
            this.noise011 = this.slice0[$$1 + 1][$$0 + 1];
            this.noise110 = this.slice1[$$1][$$0 + 1];
            this.noise111 = this.slice1[$$1 + 1][$$0 + 1];
        }

        void updateForY(double $$0) {
            this.valueXZ00 = Mth.lerp($$0, this.noise000, this.noise010);
            this.valueXZ10 = Mth.lerp($$0, this.noise100, this.noise110);
            this.valueXZ01 = Mth.lerp($$0, this.noise001, this.noise011);
            this.valueXZ11 = Mth.lerp($$0, this.noise101, this.noise111);
        }

        void updateForX(double $$0) {
            this.valueZ0 = Mth.lerp($$0, this.valueXZ00, this.valueXZ10);
            this.valueZ1 = Mth.lerp($$0, this.valueXZ01, this.valueXZ11);
        }

        void updateForZ(double $$0) {
            this.value = Mth.lerp($$0, this.valueZ0, this.valueZ1);
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            if ($$0 != NoiseChunk.this) {
                return this.noiseFiller.compute($$0);
            }
            if (!NoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            if (NoiseChunk.this.fillingCell) {
                return Mth.lerp3((double)NoiseChunk.this.inCellX / (double)NoiseChunk.this.cellWidth, (double)NoiseChunk.this.inCellY / (double)NoiseChunk.this.cellHeight, (double)NoiseChunk.this.inCellZ / (double)NoiseChunk.this.cellWidth, this.noise000, this.noise100, this.noise010, this.noise110, this.noise001, this.noise101, this.noise011, this.noise111);
            }
            return this.value;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            if (NoiseChunk.this.fillingCell) {
                $$1.fillAllDirectly($$0, this);
                return;
            }
            this.wrapped().fillArray($$0, $$1);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        private void swapSlices() {
            double[][] $$0 = this.slice0;
            this.slice0 = this.slice1;
            this.slice1 = $$0;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.Interpolated;
        }
    }

    class CacheAllInCell
    implements DensityFunctions.MarkerOrMarked,
    NoiseChunkDensityFunction {
        final DensityFunction noiseFiller;
        final double[] values;

        CacheAllInCell(DensityFunction $$0) {
            this.noiseFiller = $$0;
            this.values = new double[NoiseChunk.this.cellWidth * NoiseChunk.this.cellWidth * NoiseChunk.this.cellHeight];
            NoiseChunk.this.cellCaches.add((Object)this);
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            if ($$0 != NoiseChunk.this) {
                return this.noiseFiller.compute($$0);
            }
            if (!NoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }
            int $$1 = NoiseChunk.this.inCellX;
            int $$2 = NoiseChunk.this.inCellY;
            int $$3 = NoiseChunk.this.inCellZ;
            if ($$1 >= 0 && $$2 >= 0 && $$3 >= 0 && $$1 < NoiseChunk.this.cellWidth && $$2 < NoiseChunk.this.cellHeight && $$3 < NoiseChunk.this.cellWidth) {
                return this.values[((NoiseChunk.this.cellHeight - 1 - $$2) * NoiseChunk.this.cellWidth + $$1) * NoiseChunk.this.cellWidth + $$3];
            }
            return this.noiseFiller.compute($$0);
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            $$1.fillAllDirectly($$0, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.CacheAllInCell;
        }
    }

    static class Cache2D
    implements DensityFunctions.MarkerOrMarked,
    NoiseChunkDensityFunction {
        private final DensityFunction function;
        private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
        private double lastValue;

        Cache2D(DensityFunction $$0) {
            this.function = $$0;
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            double $$4;
            int $$2;
            int $$1 = $$0.blockX();
            long $$3 = ChunkPos.asLong($$1, $$2 = $$0.blockZ());
            if (this.lastPos2D == $$3) {
                return this.lastValue;
            }
            this.lastPos2D = $$3;
            this.lastValue = $$4 = this.function.compute($$0);
            return $$4;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            this.function.fillArray($$0, $$1);
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.Cache2D;
        }
    }

    class CacheOnce
    implements DensityFunctions.MarkerOrMarked,
    NoiseChunkDensityFunction {
        private final DensityFunction function;
        private long lastCounter;
        private long lastArrayCounter;
        private double lastValue;
        @Nullable
        private double[] lastArray;

        CacheOnce(DensityFunction $$0) {
            this.function = $$0;
        }

        @Override
        public double compute(DensityFunction.FunctionContext $$0) {
            double $$1;
            if ($$0 != NoiseChunk.this) {
                return this.function.compute($$0);
            }
            if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
                return this.lastArray[NoiseChunk.this.arrayIndex];
            }
            if (this.lastCounter == NoiseChunk.this.interpolationCounter) {
                return this.lastValue;
            }
            this.lastCounter = NoiseChunk.this.interpolationCounter;
            this.lastValue = $$1 = this.function.compute($$0);
            return $$1;
        }

        @Override
        public void fillArray(double[] $$0, DensityFunction.ContextProvider $$1) {
            if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
                System.arraycopy((Object)this.lastArray, (int)0, (Object)$$0, (int)0, (int)$$0.length);
                return;
            }
            this.wrapped().fillArray($$0, $$1);
            if (this.lastArray != null && this.lastArray.length == $$0.length) {
                System.arraycopy((Object)$$0, (int)0, (Object)this.lastArray, (int)0, (int)$$0.length);
            } else {
                this.lastArray = (double[])$$0.clone();
            }
            this.lastArrayCounter = NoiseChunk.this.arrayInterpolationCounter;
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.CacheOnce;
        }
    }

    static interface NoiseChunkDensityFunction
    extends DensityFunction {
        public DensityFunction wrapped();

        @Override
        default public double minValue() {
            return this.wrapped().minValue();
        }

        @Override
        default public double maxValue() {
            return this.wrapped().maxValue();
        }
    }
}