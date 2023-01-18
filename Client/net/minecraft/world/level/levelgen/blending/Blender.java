/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
    private static final Blender EMPTY = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()){

        @Override
        public BlendingOutput blendOffsetAndFactor(int $$0, int $$1) {
            return new BlendingOutput(1.0, 0.0);
        }

        @Override
        public double blendDensity(DensityFunction.FunctionContext $$0, double $$1) {
            return $$1;
        }

        @Override
        public BiomeResolver getBiomeResolver(BiomeResolver $$0) {
            return $$0;
        }
    };
    private static final NormalNoise SHIFT_NOISE = NormalNoise.create(new XoroshiroRandomSource(42L), NoiseData.DEFAULT_SHIFT);
    private static final int HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
    private static final int HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(HEIGHT_BLENDING_RANGE_CELLS + 3);
    private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
    private static final int DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
    private static final double OLD_CHUNK_XZ_RADIUS = 8.0;
    private final Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData;
    private final Long2ObjectOpenHashMap<BlendingData> densityBlendingData;

    public static Blender empty() {
        return EMPTY;
    }

    public static Blender of(@Nullable WorldGenRegion $$0) {
        if ($$0 == null) {
            return EMPTY;
        }
        ChunkPos $$1 = $$0.getCenter();
        if (!$$0.isOldChunkAround($$1, HEIGHT_BLENDING_RANGE_CHUNKS)) {
            return EMPTY;
        }
        Long2ObjectOpenHashMap $$2 = new Long2ObjectOpenHashMap();
        Long2ObjectOpenHashMap $$3 = new Long2ObjectOpenHashMap();
        int $$4 = Mth.square(HEIGHT_BLENDING_RANGE_CHUNKS + 1);
        for (int $$5 = -HEIGHT_BLENDING_RANGE_CHUNKS; $$5 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++$$5) {
            for (int $$6 = -HEIGHT_BLENDING_RANGE_CHUNKS; $$6 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++$$6) {
                int $$8;
                int $$7;
                BlendingData $$9;
                if ($$5 * $$5 + $$6 * $$6 > $$4 || ($$9 = BlendingData.getOrUpdateBlendingData($$0, $$7 = $$1.x + $$5, $$8 = $$1.z + $$6)) == null) continue;
                $$2.put(ChunkPos.asLong($$7, $$8), (Object)$$9);
                if ($$5 < -DENSITY_BLENDING_RANGE_CHUNKS || $$5 > DENSITY_BLENDING_RANGE_CHUNKS || $$6 < -DENSITY_BLENDING_RANGE_CHUNKS || $$6 > DENSITY_BLENDING_RANGE_CHUNKS) continue;
                $$3.put(ChunkPos.asLong($$7, $$8), (Object)$$9);
            }
        }
        if ($$2.isEmpty() && $$3.isEmpty()) {
            return EMPTY;
        }
        return new Blender((Long2ObjectOpenHashMap<BlendingData>)$$2, (Long2ObjectOpenHashMap<BlendingData>)$$3);
    }

    Blender(Long2ObjectOpenHashMap<BlendingData> $$0, Long2ObjectOpenHashMap<BlendingData> $$1) {
        this.heightAndBiomeBlendingData = $$0;
        this.densityBlendingData = $$1;
    }

    public BlendingOutput blendOffsetAndFactor(int $$0, int $$1) {
        int $$3;
        int $$2 = QuartPos.fromBlock($$0);
        double $$4 = this.getBlendingDataValue($$2, 0, $$3 = QuartPos.fromBlock($$1), BlendingData::getHeight);
        if ($$4 != Double.MAX_VALUE) {
            return new BlendingOutput(0.0, Blender.heightToOffset($$4));
        }
        MutableDouble $$5 = new MutableDouble(0.0);
        MutableDouble $$6 = new MutableDouble(0.0);
        MutableDouble $$7 = new MutableDouble(Double.POSITIVE_INFINITY);
        this.heightAndBiomeBlendingData.forEach(($$52, $$62) -> $$62.iterateHeights(QuartPos.fromSection(ChunkPos.getX($$52)), QuartPos.fromSection(ChunkPos.getZ($$52)), ($$5, $$6, $$7) -> {
            double $$8 = Mth.length($$2 - $$5, $$3 - $$6);
            if ($$8 > (double)HEIGHT_BLENDING_RANGE_CELLS) {
                return;
            }
            if ($$8 < $$7.doubleValue()) {
                $$7.setValue($$8);
            }
            double $$9 = 1.0 / ($$8 * $$8 * $$8 * $$8);
            $$6.add($$7 * $$9);
            $$5.add($$9);
        }));
        if ($$7.doubleValue() == Double.POSITIVE_INFINITY) {
            return new BlendingOutput(1.0, 0.0);
        }
        double $$8 = $$6.doubleValue() / $$5.doubleValue();
        double $$9 = Mth.clamp($$7.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
        $$9 = 3.0 * $$9 * $$9 - 2.0 * $$9 * $$9 * $$9;
        return new BlendingOutput($$9, Blender.heightToOffset($$8));
    }

    private static double heightToOffset(double $$0) {
        double $$1 = 1.0;
        double $$2 = $$0 + 0.5;
        double $$3 = Mth.positiveModulo($$2, 8.0);
        return 1.0 * (32.0 * ($$2 - 128.0) - 3.0 * ($$2 - 120.0) * $$3 + 3.0 * $$3 * $$3) / (128.0 * (32.0 - 3.0 * $$3));
    }

    public double blendDensity(DensityFunction.FunctionContext $$0, double $$1) {
        int $$4;
        int $$3;
        int $$2 = QuartPos.fromBlock($$0.blockX());
        double $$5 = this.getBlendingDataValue($$2, $$3 = $$0.blockY() / 8, $$4 = QuartPos.fromBlock($$0.blockZ()), BlendingData::getDensity);
        if ($$5 != Double.MAX_VALUE) {
            return $$5;
        }
        MutableDouble $$6 = new MutableDouble(0.0);
        MutableDouble $$7 = new MutableDouble(0.0);
        MutableDouble $$8 = new MutableDouble(Double.POSITIVE_INFINITY);
        this.densityBlendingData.forEach(($$62, $$72) -> $$72.iterateDensities(QuartPos.fromSection(ChunkPos.getX($$62)), QuartPos.fromSection(ChunkPos.getZ($$62)), $$3 - 1, $$3 + 1, ($$6, $$7, $$8, $$9) -> {
            double $$10 = Mth.length($$2 - $$6, ($$3 - $$7) * 2, $$4 - $$8);
            if ($$10 > 2.0) {
                return;
            }
            if ($$10 < $$8.doubleValue()) {
                $$8.setValue($$10);
            }
            double $$11 = 1.0 / ($$10 * $$10 * $$10 * $$10);
            $$7.add($$9 * $$11);
            $$6.add($$11);
        }));
        if ($$8.doubleValue() == Double.POSITIVE_INFINITY) {
            return $$1;
        }
        double $$9 = $$7.doubleValue() / $$6.doubleValue();
        double $$10 = Mth.clamp($$8.doubleValue() / 3.0, 0.0, 1.0);
        return Mth.lerp($$10, $$9, $$1);
    }

    private double getBlendingDataValue(int $$0, int $$1, int $$2, CellValueGetter $$3) {
        int $$4 = QuartPos.toSection($$0);
        int $$5 = QuartPos.toSection($$2);
        boolean $$6 = ($$0 & 3) == 0;
        boolean $$7 = ($$2 & 3) == 0;
        double $$8 = this.getBlendingDataValue($$3, $$4, $$5, $$0, $$1, $$2);
        if ($$8 == Double.MAX_VALUE) {
            if ($$6 && $$7) {
                $$8 = this.getBlendingDataValue($$3, $$4 - 1, $$5 - 1, $$0, $$1, $$2);
            }
            if ($$8 == Double.MAX_VALUE) {
                if ($$6) {
                    $$8 = this.getBlendingDataValue($$3, $$4 - 1, $$5, $$0, $$1, $$2);
                }
                if ($$8 == Double.MAX_VALUE && $$7) {
                    $$8 = this.getBlendingDataValue($$3, $$4, $$5 - 1, $$0, $$1, $$2);
                }
            }
        }
        return $$8;
    }

    private double getBlendingDataValue(CellValueGetter $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        BlendingData $$6 = (BlendingData)this.heightAndBiomeBlendingData.get(ChunkPos.asLong($$1, $$2));
        if ($$6 != null) {
            return $$0.get($$6, $$3 - QuartPos.fromSection($$1), $$4, $$5 - QuartPos.fromSection($$2));
        }
        return Double.MAX_VALUE;
    }

    public BiomeResolver getBiomeResolver(BiomeResolver $$0) {
        return ($$1, $$2, $$3, $$4) -> {
            Holder<Biome> $$5 = this.blendBiome($$1, $$2, $$3);
            if ($$5 == null) {
                return $$0.getNoiseBiome($$1, $$2, $$3, $$4);
            }
            return $$5;
        };
    }

    @Nullable
    private Holder<Biome> blendBiome(int $$0, int $$1, int $$2) {
        MutableDouble $$3 = new MutableDouble(Double.POSITIVE_INFINITY);
        MutableObject $$4 = new MutableObject();
        this.heightAndBiomeBlendingData.forEach(($$52, $$62) -> $$62.iterateBiomes(QuartPos.fromSection(ChunkPos.getX($$52)), $$1, QuartPos.fromSection(ChunkPos.getZ($$52)), ($$4, $$5, $$6) -> {
            double $$7 = Mth.length($$0 - $$4, $$2 - $$5);
            if ($$7 > (double)HEIGHT_BLENDING_RANGE_CELLS) {
                return;
            }
            if ($$7 < $$3.doubleValue()) {
                $$4.setValue((Object)$$6);
                $$3.setValue($$7);
            }
        }));
        if ($$3.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        }
        double $$5 = SHIFT_NOISE.getValue($$0, 0.0, $$2) * 12.0;
        double $$6 = Mth.clamp(($$3.doubleValue() + $$5) / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
        if ($$6 > 0.5) {
            return null;
        }
        return (Holder)$$4.getValue();
    }

    public static void generateBorderTicks(WorldGenRegion $$0, ChunkAccess $$1) {
        ChunkPos $$2 = $$1.getPos();
        boolean $$3 = $$1.isOldNoiseGeneration();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        BlockPos $$5 = new BlockPos($$2.getMinBlockX(), 0, $$2.getMinBlockZ());
        BlendingData $$6 = $$1.getBlendingData();
        if ($$6 == null) {
            return;
        }
        int $$7 = $$6.getAreaWithOldGeneration().getMinBuildHeight();
        int $$8 = $$6.getAreaWithOldGeneration().getMaxBuildHeight() - 1;
        if ($$3) {
            for (int $$9 = 0; $$9 < 16; ++$$9) {
                for (int $$10 = 0; $$10 < 16; ++$$10) {
                    Blender.generateBorderTick($$1, $$4.setWithOffset($$5, $$9, $$7 - 1, $$10));
                    Blender.generateBorderTick($$1, $$4.setWithOffset($$5, $$9, $$7, $$10));
                    Blender.generateBorderTick($$1, $$4.setWithOffset($$5, $$9, $$8, $$10));
                    Blender.generateBorderTick($$1, $$4.setWithOffset($$5, $$9, $$8 + 1, $$10));
                }
            }
        }
        for (Direction $$11 : Direction.Plane.HORIZONTAL) {
            if ($$0.getChunk($$2.x + $$11.getStepX(), $$2.z + $$11.getStepZ()).isOldNoiseGeneration() == $$3) continue;
            int $$12 = $$11 == Direction.EAST ? 15 : 0;
            int $$13 = $$11 == Direction.WEST ? 0 : 15;
            int $$14 = $$11 == Direction.SOUTH ? 15 : 0;
            int $$15 = $$11 == Direction.NORTH ? 0 : 15;
            for (int $$16 = $$12; $$16 <= $$13; ++$$16) {
                for (int $$17 = $$14; $$17 <= $$15; ++$$17) {
                    int $$18 = Math.min((int)$$8, (int)$$1.getHeight(Heightmap.Types.MOTION_BLOCKING, $$16, $$17)) + 1;
                    for (int $$19 = $$7; $$19 < $$18; ++$$19) {
                        Blender.generateBorderTick($$1, $$4.setWithOffset($$5, $$16, $$19, $$17));
                    }
                }
            }
        }
    }

    private static void generateBorderTick(ChunkAccess $$0, BlockPos $$1) {
        FluidState $$3;
        BlockState $$2 = $$0.getBlockState($$1);
        if ($$2.is(BlockTags.LEAVES)) {
            $$0.markPosForPostprocessing($$1);
        }
        if (!($$3 = $$0.getFluidState($$1)).isEmpty()) {
            $$0.markPosForPostprocessing($$1);
        }
    }

    public static void addAroundOldChunksCarvingMaskFilter(WorldGenLevel $$0, ProtoChunk $$12) {
        ChunkPos $$22 = $$12.getPos();
        ImmutableMap.Builder $$32 = ImmutableMap.builder();
        for (Direction8 $$4 : Direction8.values()) {
            int $$6;
            int $$5 = $$22.x + $$4.getStepX();
            BlendingData $$7 = $$0.getChunk($$5, $$6 = $$22.z + $$4.getStepZ()).getBlendingData();
            if ($$7 == null) continue;
            $$32.put((Object)$$4, (Object)$$7);
        }
        ImmutableMap $$8 = $$32.build();
        if (!$$12.isOldNoiseGeneration() && $$8.isEmpty()) {
            return;
        }
        DistanceGetter $$9 = Blender.makeOldChunkDistanceGetter($$12.getBlendingData(), (Map<Direction8, BlendingData>)$$8);
        CarvingMask.Mask $$10 = ($$1, $$2, $$3) -> {
            double $$6;
            double $$5;
            double $$4 = (double)$$1 + 0.5 + SHIFT_NOISE.getValue($$1, $$2, $$3) * 4.0;
            return $$9.getDistance($$4, $$5 = (double)$$2 + 0.5 + SHIFT_NOISE.getValue($$2, $$3, $$1) * 4.0, $$6 = (double)$$3 + 0.5 + SHIFT_NOISE.getValue($$3, $$1, $$2) * 4.0) < 4.0;
        };
        Stream.of((Object[])GenerationStep.Carving.values()).map($$12::getOrCreateCarvingMask).forEach($$1 -> $$1.setAdditionalMask($$10));
    }

    public static DistanceGetter makeOldChunkDistanceGetter(@Nullable BlendingData $$0, Map<Direction8, BlendingData> $$1) {
        ArrayList $$2 = Lists.newArrayList();
        if ($$0 != null) {
            $$2.add((Object)Blender.makeOffsetOldChunkDistanceGetter(null, $$0));
        }
        $$1.forEach((arg_0, arg_1) -> Blender.lambda$makeOldChunkDistanceGetter$9((List)$$2, arg_0, arg_1));
        return (arg_0, arg_1, arg_2) -> Blender.lambda$makeOldChunkDistanceGetter$10((List)$$2, arg_0, arg_1, arg_2);
    }

    private static DistanceGetter makeOffsetOldChunkDistanceGetter(@Nullable Direction8 $$0, BlendingData $$1) {
        double $$2 = 0.0;
        double $$3 = 0.0;
        if ($$0 != null) {
            for (Direction $$42 : $$0.getDirections()) {
                $$2 += (double)($$42.getStepX() * 16);
                $$3 += (double)($$42.getStepZ() * 16);
            }
        }
        double $$52 = $$2;
        double $$62 = $$3;
        double $$7 = (double)$$1.getAreaWithOldGeneration().getHeight() / 2.0;
        double $$8 = (double)$$1.getAreaWithOldGeneration().getMinBuildHeight() + $$7;
        return ($$4, $$5, $$6) -> Blender.distanceToCube($$4 - 8.0 - $$52, $$5 - $$8, $$6 - 8.0 - $$62, 8.0, $$7, 8.0);
    }

    private static double distanceToCube(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        double $$6 = Math.abs((double)$$0) - $$3;
        double $$7 = Math.abs((double)$$1) - $$4;
        double $$8 = Math.abs((double)$$2) - $$5;
        return Mth.length(Math.max((double)0.0, (double)$$6), Math.max((double)0.0, (double)$$7), Math.max((double)0.0, (double)$$8));
    }

    private static /* synthetic */ double lambda$makeOldChunkDistanceGetter$10(List $$0, double $$1, double $$2, double $$3) {
        double $$4 = Double.POSITIVE_INFINITY;
        for (DistanceGetter $$5 : $$0) {
            double $$6 = $$5.getDistance($$1, $$2, $$3);
            if (!($$6 < $$4)) continue;
            $$4 = $$6;
        }
        return $$4;
    }

    private static /* synthetic */ void lambda$makeOldChunkDistanceGetter$9(List $$0, Direction8 $$1, BlendingData $$2) {
        $$0.add((Object)Blender.makeOffsetOldChunkDistanceGetter($$1, $$2));
    }

    static interface CellValueGetter {
        public double get(BlendingData var1, int var2, int var3, int var4);
    }

    public record BlendingOutput(double alpha, double blendingOffset) {
    }

    public static interface DistanceGetter {
        public double getDistance(double var1, double var3, double var5);
    }
}