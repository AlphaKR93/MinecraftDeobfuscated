/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.text.DecimalFormat
 *  java.util.HashSet
 *  java.util.List
 *  java.util.OptionalInt
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.apache.commons.lang3.mutable.MutableObject;

public final class NoiseBasedChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BiomeSource.CODEC.fieldOf("biome_source").forGetter($$0 -> $$0.biomeSource), (App)NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter($$0 -> $$0.settings)).apply((Applicative)$$02, $$02.stable(NoiseBasedChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private final Holder<NoiseGeneratorSettings> settings;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    public NoiseBasedChunkGenerator(BiomeSource $$0, Holder<NoiseGeneratorSettings> $$1) {
        super($$0);
        this.settings = $$1;
        this.globalFluidPicker = Suppliers.memoize(() -> NoiseBasedChunkGenerator.createFluidPicker((NoiseGeneratorSettings)((Object)((Object)$$1.value()))));
    }

    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings $$0) {
        Aquifer.FluidStatus $$1 = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int $$2 = $$0.seaLevel();
        Aquifer.FluidStatus $$3 = new Aquifer.FluidStatus($$2, $$0.defaultFluid());
        Aquifer.FluidStatus $$42 = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return ($$4, $$5, $$6) -> {
            if ($$5 < Math.min((int)-54, (int)$$2)) {
                return $$1;
            }
            return $$3;
        };
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Executor $$0, RandomState $$1, Blender $$2, StructureManager $$3, ChunkAccess $$4) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes($$2, $$1, $$3, $$4);
            return $$4;
        }), (Executor)Util.backgroundExecutor());
    }

    private void doCreateBiomes(Blender $$0, RandomState $$1, StructureManager $$2, ChunkAccess $$32) {
        NoiseChunk $$4 = $$32.getOrCreateNoiseChunk((Function<ChunkAccess, NoiseChunk>)((Function)$$3 -> this.createNoiseChunk((ChunkAccess)$$3, $$2, $$0, $$1)));
        BiomeResolver $$5 = BelowZeroRetrogen.getBiomeResolver($$0.getBiomeResolver(this.biomeSource), $$32);
        $$32.fillBiomesFromNoise($$5, $$4.cachedClimateSampler($$1.router(), this.settings.value().spawnTarget()));
    }

    private NoiseChunk createNoiseChunk(ChunkAccess $$0, StructureManager $$1, Blender $$2, RandomState $$3) {
        return NoiseChunk.forChunk($$0, $$3, Beardifier.forStructuresInChunk($$1, $$0.getPos()), this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), $$2);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public Holder<NoiseGeneratorSettings> generatorSettings() {
        return this.settings;
    }

    public boolean stable(ResourceKey<NoiseGeneratorSettings> $$0) {
        return this.settings.is($$0);
    }

    @Override
    public int getBaseHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        return this.iterateNoiseColumn($$3, $$4, $$0, $$1, null, $$2.isOpaque()).orElse($$3.getMinBuildHeight());
    }

    @Override
    public NoiseColumn getBaseColumn(int $$0, int $$1, LevelHeightAccessor $$2, RandomState $$3) {
        MutableObject $$4 = new MutableObject();
        this.iterateNoiseColumn($$2, $$3, $$0, $$1, (MutableObject<NoiseColumn>)$$4, null);
        return (NoiseColumn)$$4.getValue();
    }

    @Override
    public void addDebugScreenInfo(List<String> $$0, RandomState $$1, BlockPos $$2) {
        DecimalFormat $$3 = new DecimalFormat("0.000");
        NoiseRouter $$4 = $$1.router();
        DensityFunction.SinglePointContext $$5 = new DensityFunction.SinglePointContext($$2.getX(), $$2.getY(), $$2.getZ());
        double $$6 = $$4.ridges().compute($$5);
        $$0.add((Object)("NoiseRouter T: " + $$3.format($$4.temperature().compute($$5)) + " V: " + $$3.format($$4.vegetation().compute($$5)) + " C: " + $$3.format($$4.continents().compute($$5)) + " E: " + $$3.format($$4.erosion().compute($$5)) + " D: " + $$3.format($$4.depth().compute($$5)) + " W: " + $$3.format($$6) + " PV: " + $$3.format((double)NoiseRouterData.peaksAndValleys((float)$$6)) + " AS: " + $$3.format($$4.initialDensityWithoutJaggedness().compute($$5)) + " N: " + $$3.format($$4.finalDensity().compute($$5))));
    }

    private OptionalInt iterateNoiseColumn(LevelHeightAccessor $$0, RandomState $$1, int $$2, int $$3, @Nullable MutableObject<NoiseColumn> $$4, @Nullable Predicate<BlockState> $$5) {
        BlockState[] $$12;
        NoiseSettings $$6 = this.settings.value().noiseSettings().clampToHeightAccessor($$0);
        int $$7 = $$6.getCellHeight();
        int $$8 = $$6.minY();
        int $$9 = Mth.floorDiv($$8, $$7);
        int $$10 = Mth.floorDiv($$6.height(), $$7);
        if ($$10 <= 0) {
            return OptionalInt.empty();
        }
        if ($$4 == null) {
            Object $$11 = null;
        } else {
            $$12 = new BlockState[$$6.height()];
            $$4.setValue((Object)new NoiseColumn($$8, $$12));
        }
        int $$13 = $$6.getCellWidth();
        int $$14 = Math.floorDiv((int)$$2, (int)$$13);
        int $$15 = Math.floorDiv((int)$$3, (int)$$13);
        int $$16 = Math.floorMod((int)$$2, (int)$$13);
        int $$17 = Math.floorMod((int)$$3, (int)$$13);
        int $$18 = $$14 * $$13;
        int $$19 = $$15 * $$13;
        double $$20 = (double)$$16 / (double)$$13;
        double $$21 = (double)$$17 / (double)$$13;
        NoiseChunk $$22 = new NoiseChunk(1, $$1, $$18, $$19, $$6, DensityFunctions.BeardifierMarker.INSTANCE, this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), Blender.empty());
        $$22.initializeForFirstCellX();
        $$22.advanceCellX(0);
        for (int $$23 = $$10 - 1; $$23 >= 0; --$$23) {
            $$22.selectCellYZ($$23, 0);
            for (int $$24 = $$7 - 1; $$24 >= 0; --$$24) {
                BlockState $$28;
                int $$25 = ($$9 + $$23) * $$7 + $$24;
                double $$26 = (double)$$24 / (double)$$7;
                $$22.updateForY($$25, $$26);
                $$22.updateForX($$2, $$20);
                $$22.updateForZ($$3, $$21);
                BlockState $$27 = $$22.getInterpolatedState();
                BlockState blockState = $$28 = $$27 == null ? this.settings.value().defaultBlock() : $$27;
                if ($$12 != null) {
                    int $$29 = $$23 * $$7 + $$24;
                    $$12[$$29] = $$28;
                }
                if ($$5 == null || !$$5.test((Object)$$28)) continue;
                $$22.stopInterpolation();
                return OptionalInt.of((int)($$25 + 1));
            }
        }
        $$22.stopInterpolation();
        return OptionalInt.empty();
    }

    @Override
    public void buildSurface(WorldGenRegion $$0, StructureManager $$1, RandomState $$2, ChunkAccess $$3) {
        if (SharedConstants.debugVoidTerrain($$3.getPos())) {
            return;
        }
        WorldGenerationContext $$4 = new WorldGenerationContext(this, $$0);
        this.buildSurface($$3, $$4, $$2, $$1, $$0.getBiomeManager(), $$0.registryAccess().registryOrThrow(Registries.BIOME), Blender.of($$0));
    }

    @VisibleForTesting
    public void buildSurface(ChunkAccess $$0, WorldGenerationContext $$1, RandomState $$2, StructureManager $$32, BiomeManager $$4, Registry<Biome> $$5, Blender $$6) {
        NoiseChunk $$7 = $$0.getOrCreateNoiseChunk((Function<ChunkAccess, NoiseChunk>)((Function)$$3 -> this.createNoiseChunk((ChunkAccess)$$3, $$32, $$6, $$2)));
        NoiseGeneratorSettings $$8 = this.settings.value();
        $$2.surfaceSystem().buildSurface($$2, $$4, $$5, $$8.useLegacyRandomSource(), $$1, $$0, $$7, $$8.surfaceRule());
    }

    @Override
    public void applyCarvers(WorldGenRegion $$0, long $$12, RandomState $$22, BiomeManager $$32, StructureManager $$4, ChunkAccess $$5, GenerationStep.Carving $$6) {
        BiomeManager $$7 = $$32.withDifferentSource(($$1, $$2, $$3) -> this.biomeSource.getNoiseBiome($$1, $$2, $$3, $$22.sampler()));
        WorldgenRandom $$8 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        int $$9 = 8;
        ChunkPos $$10 = $$5.getPos();
        NoiseChunk $$11 = $$5.getOrCreateNoiseChunk((Function<ChunkAccess, NoiseChunk>)((Function)$$3 -> this.createNoiseChunk((ChunkAccess)$$3, $$4, Blender.of($$0), $$22)));
        Aquifer $$122 = $$11.aquifer();
        CarvingContext $$13 = new CarvingContext(this, $$0.registryAccess(), $$5.getHeightAccessorForGeneration(), $$11, $$22, this.settings.value().surfaceRule());
        CarvingMask $$14 = ((ProtoChunk)$$5).getOrCreateCarvingMask($$6);
        for (int $$15 = -8; $$15 <= 8; ++$$15) {
            for (int $$16 = -8; $$16 <= 8; ++$$16) {
                ChunkPos $$17 = new ChunkPos($$10.x + $$15, $$10.z + $$16);
                ChunkAccess $$18 = $$0.getChunk($$17.x, $$17.z);
                BiomeGenerationSettings $$19 = $$18.carverBiome((Supplier<BiomeGenerationSettings>)((Supplier)() -> this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock($$17.getMinBlockX()), 0, QuartPos.fromBlock($$17.getMinBlockZ()), $$22.sampler()))));
                Iterable<Holder<ConfiguredWorldCarver<?>>> $$20 = $$19.getCarvers($$6);
                int $$21 = 0;
                for (Holder $$222 : $$20) {
                    ConfiguredWorldCarver $$23 = (ConfiguredWorldCarver)((Object)$$222.value());
                    $$8.setLargeFeatureSeed($$12 + (long)$$21, $$17.x, $$17.z);
                    if ($$23.isStartChunk($$8)) {
                        $$23.carve($$13, $$5, (Function<BlockPos, Holder<Biome>>)((Function)$$7::getBiome), $$8, $$122, $$17, $$14);
                    }
                    ++$$21;
                }
            }
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor $$0, Blender $$1, RandomState $$2, StructureManager $$3, ChunkAccess $$4) {
        NoiseSettings $$5 = this.settings.value().noiseSettings().clampToHeightAccessor($$4.getHeightAccessorForGeneration());
        int $$6 = $$5.minY();
        int $$7 = Mth.floorDiv($$6, $$5.getCellHeight());
        int $$8 = Mth.floorDiv($$5.height(), $$5.getCellHeight());
        if ($$8 <= 0) {
            return CompletableFuture.completedFuture((Object)$$4);
        }
        int $$9 = $$4.getSectionIndex($$8 * $$5.getCellHeight() - 1 + $$6);
        int $$10 = $$4.getSectionIndex($$6);
        HashSet $$11 = Sets.newHashSet();
        for (int $$12 = $$9; $$12 >= $$10; --$$12) {
            LevelChunkSection $$13 = $$4.getSection($$12);
            $$13.acquire();
            $$11.add((Object)$$13);
        }
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> this.doFill($$1, $$3, $$2, $$4, $$7, $$8)), (Executor)Util.backgroundExecutor()).whenCompleteAsync((arg_0, arg_1) -> NoiseBasedChunkGenerator.lambda$fillFromNoise$12((Set)$$11, arg_0, arg_1), $$0);
    }

    private ChunkAccess doFill(Blender $$0, StructureManager $$1, RandomState $$2, ChunkAccess $$32, int $$4, int $$5) {
        NoiseChunk $$6 = $$32.getOrCreateNoiseChunk((Function<ChunkAccess, NoiseChunk>)((Function)$$3 -> this.createNoiseChunk((ChunkAccess)$$3, $$1, $$0, $$2)));
        Heightmap $$7 = $$32.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap $$8 = $$32.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        ChunkPos $$9 = $$32.getPos();
        int $$10 = $$9.getMinBlockX();
        int $$11 = $$9.getMinBlockZ();
        Aquifer $$12 = $$6.aquifer();
        $$6.initializeForFirstCellX();
        BlockPos.MutableBlockPos $$13 = new BlockPos.MutableBlockPos();
        int $$14 = $$6.cellWidth();
        int $$15 = $$6.cellHeight();
        int $$16 = 16 / $$14;
        int $$17 = 16 / $$14;
        for (int $$18 = 0; $$18 < $$16; ++$$18) {
            $$6.advanceCellX($$18);
            for (int $$19 = 0; $$19 < $$17; ++$$19) {
                LevelChunkSection $$20 = $$32.getSection($$32.getSectionsCount() - 1);
                for (int $$21 = $$5 - 1; $$21 >= 0; --$$21) {
                    $$6.selectCellYZ($$21, $$19);
                    for (int $$22 = $$15 - 1; $$22 >= 0; --$$22) {
                        int $$23 = ($$4 + $$21) * $$15 + $$22;
                        int $$24 = $$23 & 0xF;
                        int $$25 = $$32.getSectionIndex($$23);
                        if ($$32.getSectionIndex($$20.bottomBlockY()) != $$25) {
                            $$20 = $$32.getSection($$25);
                        }
                        double $$26 = (double)$$22 / (double)$$15;
                        $$6.updateForY($$23, $$26);
                        for (int $$27 = 0; $$27 < $$14; ++$$27) {
                            int $$28 = $$10 + $$18 * $$14 + $$27;
                            int $$29 = $$28 & 0xF;
                            double $$30 = (double)$$27 / (double)$$14;
                            $$6.updateForX($$28, $$30);
                            for (int $$31 = 0; $$31 < $$14; ++$$31) {
                                int $$322 = $$11 + $$19 * $$14 + $$31;
                                int $$33 = $$322 & 0xF;
                                double $$34 = (double)$$31 / (double)$$14;
                                $$6.updateForZ($$322, $$34);
                                BlockState $$35 = $$6.getInterpolatedState();
                                if ($$35 == null) {
                                    $$35 = this.settings.value().defaultBlock();
                                }
                                if (($$35 = this.debugPreliminarySurfaceLevel($$6, $$28, $$23, $$322, $$35)) == AIR || SharedConstants.debugVoidTerrain($$32.getPos())) continue;
                                if ($$35.getLightEmission() != 0 && $$32 instanceof ProtoChunk) {
                                    $$13.set($$28, $$23, $$322);
                                    ((ProtoChunk)$$32).addLight($$13);
                                }
                                $$20.setBlockState($$29, $$24, $$33, $$35, false);
                                $$7.update($$29, $$23, $$33, $$35);
                                $$8.update($$29, $$23, $$33, $$35);
                                if (!$$12.shouldScheduleFluidUpdate() || $$35.getFluidState().isEmpty()) continue;
                                $$13.set($$28, $$23, $$322);
                                $$32.markPosForPostprocessing($$13);
                            }
                        }
                    }
                }
            }
            $$6.swapSlices();
        }
        $$6.stopInterpolation();
        return $$32;
    }

    private BlockState debugPreliminarySurfaceLevel(NoiseChunk $$0, int $$1, int $$2, int $$3, BlockState $$4) {
        return $$4;
    }

    @Override
    public int getGenDepth() {
        return this.settings.value().noiseSettings().height();
    }

    @Override
    public int getSeaLevel() {
        return this.settings.value().seaLevel();
    }

    @Override
    public int getMinY() {
        return this.settings.value().noiseSettings().minY();
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion $$0) {
        if (this.settings.value().disableMobGeneration()) {
            return;
        }
        ChunkPos $$1 = $$0.getCenter();
        Holder $$2 = $$0.getBiome($$1.getWorldPosition().atY($$0.getMaxBuildHeight() - 1));
        WorldgenRandom $$3 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        $$3.setDecorationSeed($$0.getSeed(), $$1.getMinBlockX(), $$1.getMinBlockZ());
        NaturalSpawner.spawnMobsForChunkGeneration($$0, $$2, $$1, $$3);
    }

    private static /* synthetic */ void lambda$fillFromNoise$12(Set $$0, ChunkAccess $$1, Throwable $$2) {
        for (LevelChunkSection $$3 : $$0) {
            $$3.release();
        }
    }
}