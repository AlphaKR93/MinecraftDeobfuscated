/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelSource
extends ChunkGenerator {
    public static final Codec<FlatLevelSource> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply((Applicative)$$0, $$0.stable(FlatLevelSource::new)));
    private final FlatLevelGeneratorSettings settings;

    public FlatLevelSource(FlatLevelGeneratorSettings $$0) {
        super(new FixedBiomeSource($$0.getBiome()), Util.memoize($$0::adjustGenerationSettings));
        this.settings = $$0;
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> $$0, RandomState $$1, long $$2) {
        Stream $$3 = (Stream)this.settings.structureOverrides().map(HolderSet::stream).orElseGet(() -> $$0.listElements().map($$0 -> $$0));
        return ChunkGeneratorStructureState.createForFlat($$1, $$2, this.biomeSource, (Stream<Holder<StructureSet>>)$$3);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.settings;
    }

    @Override
    public void buildSurface(WorldGenRegion $$0, StructureManager $$1, RandomState $$2, ChunkAccess $$3) {
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor $$0) {
        return $$0.getMinBuildHeight() + Math.min((int)$$0.getHeight(), (int)this.settings.getLayers().size());
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor $$0, Blender $$1, RandomState $$2, StructureManager $$3, ChunkAccess $$4) {
        List<BlockState> $$5 = this.settings.getLayers();
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        Heightmap $$7 = $$4.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap $$8 = $$4.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        for (int $$9 = 0; $$9 < Math.min((int)$$4.getHeight(), (int)$$5.size()); ++$$9) {
            BlockState $$10 = (BlockState)$$5.get($$9);
            if ($$10 == null) continue;
            int $$11 = $$4.getMinBuildHeight() + $$9;
            for (int $$12 = 0; $$12 < 16; ++$$12) {
                for (int $$13 = 0; $$13 < 16; ++$$13) {
                    $$4.setBlockState($$6.set($$12, $$11, $$13), $$10, false);
                    $$7.update($$12, $$11, $$13, $$10);
                    $$8.update($$12, $$11, $$13, $$10);
                }
            }
        }
        return CompletableFuture.completedFuture((Object)$$4);
    }

    @Override
    public int getBaseHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        List<BlockState> $$5 = this.settings.getLayers();
        for (int $$6 = Math.min((int)$$5.size(), (int)$$3.getMaxBuildHeight()) - 1; $$6 >= 0; --$$6) {
            BlockState $$7 = (BlockState)$$5.get($$6);
            if ($$7 == null || !$$2.isOpaque().test((Object)$$7)) continue;
            return $$3.getMinBuildHeight() + $$6 + 1;
        }
        return $$3.getMinBuildHeight();
    }

    @Override
    public NoiseColumn getBaseColumn(int $$02, int $$1, LevelHeightAccessor $$2, RandomState $$3) {
        return new NoiseColumn($$2.getMinBuildHeight(), (BlockState[])this.settings.getLayers().stream().limit((long)$$2.getHeight()).map($$0 -> $$0 == null ? Blocks.AIR.defaultBlockState() : $$0).toArray(BlockState[]::new));
    }

    @Override
    public void addDebugScreenInfo(List<String> $$0, RandomState $$1, BlockPos $$2) {
    }

    @Override
    public void applyCarvers(WorldGenRegion $$0, long $$1, RandomState $$2, BiomeManager $$3, StructureManager $$4, ChunkAccess $$5, GenerationStep.Carving $$6) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion $$0) {
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }
}