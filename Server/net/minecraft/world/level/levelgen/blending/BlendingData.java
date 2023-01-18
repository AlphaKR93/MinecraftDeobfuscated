/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.stream.DoubleStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class BlendingData {
    private static final double BLENDING_DENSITY_FACTOR = 0.1;
    protected static final int CELL_WIDTH = 4;
    protected static final int CELL_HEIGHT = 8;
    protected static final int CELL_RATIO = 2;
    private static final double SOLID_DENSITY = 1.0;
    private static final double AIR_DENSITY = -1.0;
    private static final int CELLS_PER_SECTION_Y = 2;
    private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
    private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
    private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
    private static final int CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
    private static final int CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
    private static final int CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
    private final LevelHeightAccessor areaWithOldGeneration;
    private static final List<Block> SURFACE_BLOCKS = List.of((Object[])new Block[]{Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT});
    protected static final double NO_VALUE = Double.MAX_VALUE;
    private boolean hasCalculatedData;
    private final double[] heights;
    private final List<List<Holder<Biome>>> biomes;
    private final transient double[][] densities;
    private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
    public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create($$03 -> $$03.group((App)Codec.INT.fieldOf("min_section").forGetter($$0 -> $$0.areaWithOldGeneration.getMinSection()), (App)Codec.INT.fieldOf("max_section").forGetter($$0 -> $$0.areaWithOldGeneration.getMaxSection()), (App)DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter($$02 -> DoubleStream.of((double[])$$02.heights).anyMatch($$0 -> $$0 != Double.MAX_VALUE) ? Optional.of((Object)$$02.heights) : Optional.empty())).apply((Applicative)$$03, BlendingData::new)).comapFlatMap(BlendingData::validateArraySize, Function.identity());

    private static DataResult<BlendingData> validateArraySize(BlendingData $$0) {
        if ($$0.heights.length != CELL_COLUMN_COUNT) {
            return DataResult.error((String)("heights has to be of length " + CELL_COLUMN_COUNT));
        }
        return DataResult.success((Object)$$0);
    }

    private BlendingData(int $$02, int $$1, Optional<double[]> $$2) {
        this.heights = (double[])$$2.orElse((Object)Util.make(new double[CELL_COLUMN_COUNT], $$0 -> Arrays.fill((double[])$$0, (double)Double.MAX_VALUE)));
        this.densities = new double[CELL_COLUMN_COUNT][];
        ObjectArrayList $$3 = new ObjectArrayList(CELL_COLUMN_COUNT);
        $$3.size(CELL_COLUMN_COUNT);
        this.biomes = $$3;
        int $$4 = SectionPos.sectionToBlockCoord($$02);
        int $$5 = SectionPos.sectionToBlockCoord($$1) - $$4;
        this.areaWithOldGeneration = LevelHeightAccessor.create($$4, $$5);
    }

    @Nullable
    public static BlendingData getOrUpdateBlendingData(WorldGenRegion $$0, int $$1, int $$2) {
        ChunkAccess $$3 = $$0.getChunk($$1, $$2);
        BlendingData $$4 = $$3.getBlendingData();
        if ($$4 == null) {
            return null;
        }
        $$4.calculateData($$3, BlendingData.sideByGenerationAge($$0, $$1, $$2, false));
        return $$4;
    }

    public static Set<Direction8> sideByGenerationAge(WorldGenLevel $$0, int $$1, int $$2, boolean $$3) {
        EnumSet $$4 = EnumSet.noneOf(Direction8.class);
        for (Direction8 $$5 : Direction8.values()) {
            int $$7;
            int $$6 = $$1 + $$5.getStepX();
            if ($$0.getChunk($$6, $$7 = $$2 + $$5.getStepZ()).isOldNoiseGeneration() != $$3) continue;
            $$4.add((Object)$$5);
        }
        return $$4;
    }

    private void calculateData(ChunkAccess $$0, Set<Direction8> $$1) {
        if (this.hasCalculatedData) {
            return;
        }
        if ($$1.contains((Object)Direction8.NORTH) || $$1.contains((Object)Direction8.WEST) || $$1.contains((Object)Direction8.NORTH_WEST)) {
            this.addValuesForColumn(BlendingData.getInsideIndex(0, 0), $$0, 0, 0);
        }
        if ($$1.contains((Object)Direction8.NORTH)) {
            for (int $$2 = 1; $$2 < QUARTS_PER_SECTION; ++$$2) {
                this.addValuesForColumn(BlendingData.getInsideIndex($$2, 0), $$0, 4 * $$2, 0);
            }
        }
        if ($$1.contains((Object)Direction8.WEST)) {
            for (int $$3 = 1; $$3 < QUARTS_PER_SECTION; ++$$3) {
                this.addValuesForColumn(BlendingData.getInsideIndex(0, $$3), $$0, 0, 4 * $$3);
            }
        }
        if ($$1.contains((Object)Direction8.EAST)) {
            for (int $$4 = 1; $$4 < QUARTS_PER_SECTION; ++$$4) {
                this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, $$4), $$0, 15, 4 * $$4);
            }
        }
        if ($$1.contains((Object)Direction8.SOUTH)) {
            for (int $$5 = 0; $$5 < QUARTS_PER_SECTION; ++$$5) {
                this.addValuesForColumn(BlendingData.getOutsideIndex($$5, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), $$0, 4 * $$5, 15);
            }
        }
        if ($$1.contains((Object)Direction8.EAST) && $$1.contains((Object)Direction8.NORTH_EAST)) {
            this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), $$0, 15, 0);
        }
        if ($$1.contains((Object)Direction8.EAST) && $$1.contains((Object)Direction8.SOUTH) && $$1.contains((Object)Direction8.SOUTH_EAST)) {
            this.addValuesForColumn(BlendingData.getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), $$0, 15, 15);
        }
        this.hasCalculatedData = true;
    }

    private void addValuesForColumn(int $$0, ChunkAccess $$1, int $$2, int $$3) {
        if (this.heights[$$0] == Double.MAX_VALUE) {
            this.heights[$$0] = this.getHeightAtXZ($$1, $$2, $$3);
        }
        this.densities[$$0] = this.getDensityColumn($$1, $$2, $$3, Mth.floor(this.heights[$$0]));
        this.biomes.set($$0, this.getBiomeColumn($$1, $$2, $$3));
    }

    private int getHeightAtXZ(ChunkAccess $$0, int $$1, int $$2) {
        int $$4;
        if ($$0.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
            int $$3 = Math.min((int)($$0.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$1, $$2) + 1), (int)this.areaWithOldGeneration.getMaxBuildHeight());
        } else {
            $$4 = this.areaWithOldGeneration.getMaxBuildHeight();
        }
        int $$5 = this.areaWithOldGeneration.getMinBuildHeight();
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos($$1, $$4, $$2);
        while ($$6.getY() > $$5) {
            $$6.move(Direction.DOWN);
            if (!SURFACE_BLOCKS.contains((Object)$$0.getBlockState($$6).getBlock())) continue;
            return $$6.getY();
        }
        return $$5;
    }

    private static double read1(ChunkAccess $$0, BlockPos.MutableBlockPos $$1) {
        return BlendingData.isGround($$0, $$1.move(Direction.DOWN)) ? 1.0 : -1.0;
    }

    private static double read7(ChunkAccess $$0, BlockPos.MutableBlockPos $$1) {
        double $$2 = 0.0;
        for (int $$3 = 0; $$3 < 7; ++$$3) {
            $$2 += BlendingData.read1($$0, $$1);
        }
        return $$2;
    }

    private double[] getDensityColumn(ChunkAccess $$0, int $$1, int $$2, int $$3) {
        double[] $$4 = new double[this.cellCountPerColumn()];
        Arrays.fill((double[])$$4, (double)-1.0);
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos($$1, this.areaWithOldGeneration.getMaxBuildHeight(), $$2);
        double $$6 = BlendingData.read7($$0, $$5);
        for (int $$7 = $$4.length - 2; $$7 >= 0; --$$7) {
            double $$8 = BlendingData.read1($$0, $$5);
            double $$9 = BlendingData.read7($$0, $$5);
            $$4[$$7] = ($$6 + $$8 + $$9) / 15.0;
            $$6 = $$9;
        }
        int $$10 = this.getCellYIndex(Mth.intFloorDiv($$3, 8));
        if ($$10 >= 0 && $$10 < $$4.length - 1) {
            double $$11 = ((double)$$3 + 0.5) % 8.0 / 8.0;
            double $$12 = (1.0 - $$11) / $$11;
            double $$13 = Math.max((double)$$12, (double)1.0) * 0.25;
            $$4[$$10 + 1] = -$$12 / $$13;
            $$4[$$10] = 1.0 / $$13;
        }
        return $$4;
    }

    private List<Holder<Biome>> getBiomeColumn(ChunkAccess $$0, int $$1, int $$2) {
        ObjectArrayList $$3 = new ObjectArrayList(this.quartCountPerColumn());
        $$3.size(this.quartCountPerColumn());
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            int $$5 = $$4 + QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
            $$3.set($$4, $$0.getNoiseBiome(QuartPos.fromBlock($$1), $$5, QuartPos.fromBlock($$2)));
        }
        return $$3;
    }

    private static boolean isGround(ChunkAccess $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        if ($$2.isAir()) {
            return false;
        }
        if ($$2.is(BlockTags.LEAVES)) {
            return false;
        }
        if ($$2.is(BlockTags.LOGS)) {
            return false;
        }
        if ($$2.is(Blocks.BROWN_MUSHROOM_BLOCK) || $$2.is(Blocks.RED_MUSHROOM_BLOCK)) {
            return false;
        }
        return !$$2.getCollisionShape($$0, $$1).isEmpty();
    }

    protected double getHeight(int $$0, int $$1, int $$2) {
        if ($$0 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || $$2 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
            return this.heights[BlendingData.getOutsideIndex($$0, $$2)];
        }
        if ($$0 == 0 || $$2 == 0) {
            return this.heights[BlendingData.getInsideIndex($$0, $$2)];
        }
        return Double.MAX_VALUE;
    }

    private double getDensity(@Nullable double[] $$0, int $$1) {
        if ($$0 == null) {
            return Double.MAX_VALUE;
        }
        int $$2 = this.getCellYIndex($$1);
        if ($$2 < 0 || $$2 >= $$0.length) {
            return Double.MAX_VALUE;
        }
        return $$0[$$2] * 0.1;
    }

    protected double getDensity(int $$0, int $$1, int $$2) {
        if ($$1 == this.getMinY()) {
            return 0.1;
        }
        if ($$0 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || $$2 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
            return this.getDensity(this.densities[BlendingData.getOutsideIndex($$0, $$2)], $$1);
        }
        if ($$0 == 0 || $$2 == 0) {
            return this.getDensity(this.densities[BlendingData.getInsideIndex($$0, $$2)], $$1);
        }
        return Double.MAX_VALUE;
    }

    protected void iterateBiomes(int $$0, int $$1, int $$2, BiomeConsumer $$3) {
        if ($$1 < QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight()) || $$1 >= QuartPos.fromBlock(this.areaWithOldGeneration.getMaxBuildHeight())) {
            return;
        }
        int $$4 = $$1 - QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
        for (int $$5 = 0; $$5 < this.biomes.size(); ++$$5) {
            Holder $$6;
            if (this.biomes.get($$5) == null || ($$6 = (Holder)((List)this.biomes.get($$5)).get($$4)) == null) continue;
            $$3.consume($$0 + BlendingData.getX($$5), $$2 + BlendingData.getZ($$5), $$6);
        }
    }

    protected void iterateHeights(int $$0, int $$1, HeightConsumer $$2) {
        for (int $$3 = 0; $$3 < this.heights.length; ++$$3) {
            double $$4 = this.heights[$$3];
            if ($$4 == Double.MAX_VALUE) continue;
            $$2.consume($$0 + BlendingData.getX($$3), $$1 + BlendingData.getZ($$3), $$4);
        }
    }

    protected void iterateDensities(int $$0, int $$1, int $$2, int $$3, DensityConsumer $$4) {
        int $$5 = this.getColumnMinY();
        int $$6 = Math.max((int)0, (int)($$2 - $$5));
        int $$7 = Math.min((int)this.cellCountPerColumn(), (int)($$3 - $$5));
        for (int $$8 = 0; $$8 < this.densities.length; ++$$8) {
            double[] $$9 = this.densities[$$8];
            if ($$9 == null) continue;
            int $$10 = $$0 + BlendingData.getX($$8);
            int $$11 = $$1 + BlendingData.getZ($$8);
            for (int $$12 = $$6; $$12 < $$7; ++$$12) {
                $$4.consume($$10, $$12 + $$5, $$11, $$9[$$12] * 0.1);
            }
        }
    }

    private int cellCountPerColumn() {
        return this.areaWithOldGeneration.getSectionsCount() * 2;
    }

    private int quartCountPerColumn() {
        return QuartPos.fromSection(this.areaWithOldGeneration.getSectionsCount());
    }

    private int getColumnMinY() {
        return this.getMinY() + 1;
    }

    private int getMinY() {
        return this.areaWithOldGeneration.getMinSection() * 2;
    }

    private int getCellYIndex(int $$0) {
        return $$0 - this.getColumnMinY();
    }

    private static int getInsideIndex(int $$0, int $$1) {
        return CELL_HORIZONTAL_MAX_INDEX_INSIDE - $$0 + $$1;
    }

    private static int getOutsideIndex(int $$0, int $$1) {
        return CELL_COLUMN_INSIDE_COUNT + $$0 + CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - $$1;
    }

    private static int getX(int $$0) {
        if ($$0 < CELL_COLUMN_INSIDE_COUNT) {
            return BlendingData.zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_INSIDE - $$0);
        }
        int $$1 = $$0 - CELL_COLUMN_INSIDE_COUNT;
        return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - BlendingData.zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - $$1);
    }

    private static int getZ(int $$0) {
        if ($$0 < CELL_COLUMN_INSIDE_COUNT) {
            return BlendingData.zeroIfNegative($$0 - CELL_HORIZONTAL_MAX_INDEX_INSIDE);
        }
        int $$1 = $$0 - CELL_COLUMN_INSIDE_COUNT;
        return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - BlendingData.zeroIfNegative($$1 - CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
    }

    private static int zeroIfNegative(int $$0) {
        return $$0 & ~($$0 >> 31);
    }

    public LevelHeightAccessor getAreaWithOldGeneration() {
        return this.areaWithOldGeneration;
    }

    protected static interface BiomeConsumer {
        public void consume(int var1, int var2, Holder<Biome> var3);
    }

    protected static interface HeightConsumer {
        public void consume(int var1, int var2, double var3);
    }

    protected static interface DensityConsumer {
        public void consume(int var1, int var2, int var3, double var4);
    }
}