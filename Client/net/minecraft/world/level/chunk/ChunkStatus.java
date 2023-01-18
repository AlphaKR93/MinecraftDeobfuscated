/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ChunkStatus {
    public static final int MAX_STRUCTURE_DISTANCE = 8;
    private static final EnumSet<Heightmap.Types> PRE_FEATURES = EnumSet.of((Enum)Heightmap.Types.OCEAN_FLOOR_WG, (Enum)Heightmap.Types.WORLD_SURFACE_WG);
    public static final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of((Enum)Heightmap.Types.OCEAN_FLOOR, (Enum)Heightmap.Types.WORLD_SURFACE, (Enum)Heightmap.Types.MOTION_BLOCKING, (Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    private static final LoadingTask PASSTHROUGH_LOAD_TASK = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
        if ($$5 instanceof ProtoChunk) {
            ProtoChunk $$6 = (ProtoChunk)$$5;
            if (!$$5.getStatus().isOrAfter($$0)) {
                $$6.setStatus($$0);
            }
        }
        return CompletableFuture.completedFuture((Object)Either.left((Object)$$5));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.registerSimple("empty", null, -1, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> {
        if (!$$8.getStatus().isOrAfter($$0)) {
            if ($$2.getServer().getWorldData().worldGenOptions().generateStructures()) {
                $$3.createStructures($$2.registryAccess(), $$2.getChunkSource().getGeneratorState(), $$2.structureManager(), $$8, $$4);
            }
            if ($$8 instanceof ProtoChunk) {
                ProtoChunk $$10 = (ProtoChunk)$$8;
                $$10.setStatus($$0);
            }
            $$2.onStructureStartsAvailable($$8);
        }
        return CompletableFuture.completedFuture((Object)Either.left((Object)$$8));
    }, ($$0, $$1, $$2, $$3, $$4, $$5) -> {
        if (!$$5.getStatus().isOrAfter($$0)) {
            if ($$5 instanceof ProtoChunk) {
                ProtoChunk $$6 = (ProtoChunk)$$5;
                $$6.setStatus($$0);
            }
            $$1.onStructureStartsAvailable($$5);
        }
        return CompletableFuture.completedFuture((Object)Either.left((Object)$$5));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.registerSimple("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {
        WorldGenRegion $$5 = new WorldGenRegion($$1, (List<ChunkAccess>)$$3, $$0, -1);
        $$2.createReferences($$5, $$1.structureManager().forWorldGenRegion($$5), $$4);
    });
    public static final ChunkStatus BIOMES = ChunkStatus.register("biomes", STRUCTURE_REFERENCES, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$12, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> {
        if ($$9 || !$$8.getStatus().isOrAfter($$0)) {
            WorldGenRegion $$10 = new WorldGenRegion($$2, (List<ChunkAccess>)$$7, $$0, -1);
            return $$3.createBiomes($$12, $$2.getChunkSource().randomState(), Blender.of($$10), $$2.structureManager().forWorldGenRegion($$10), $$8).thenApply($$1 -> {
                if ($$1 instanceof ProtoChunk) {
                    ((ProtoChunk)$$1).setStatus($$0);
                }
                return Either.left((Object)$$1);
            });
        }
        return CompletableFuture.completedFuture((Object)Either.left((Object)$$8));
    });
    public static final ChunkStatus NOISE = ChunkStatus.register("noise", BIOMES, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$12, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> {
        if ($$9 || !$$8.getStatus().isOrAfter($$0)) {
            WorldGenRegion $$10 = new WorldGenRegion($$2, (List<ChunkAccess>)$$7, $$0, 0);
            return $$3.fillFromNoise($$12, Blender.of($$10), $$2.getChunkSource().randomState(), $$2.structureManager().forWorldGenRegion($$10), $$8).thenApply($$1 -> {
                if ($$1 instanceof ProtoChunk) {
                    ServerLevel $$2 = (ProtoChunk)$$1;
                    ChunkGenerator $$3 = $$2.getBelowZeroRetrogen();
                    if ($$3 != null) {
                        BelowZeroRetrogen.replaceOldBedrock($$2);
                        if ($$3.hasBedrockHoles()) {
                            $$3.applyBedrockMask((ProtoChunk)((Object)$$2));
                        }
                    }
                    $$2.setStatus($$0);
                }
                return Either.left((Object)$$1);
            });
        }
        return CompletableFuture.completedFuture((Object)Either.left((Object)$$8));
    });
    public static final ChunkStatus SURFACE = ChunkStatus.registerSimple("surface", NOISE, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {
        WorldGenRegion $$5 = new WorldGenRegion($$1, (List<ChunkAccess>)$$3, $$0, 0);
        $$2.buildSurface($$5, $$1.structureManager().forWorldGenRegion($$5), $$1.getChunkSource().randomState(), $$4);
    });
    public static final ChunkStatus CARVERS = ChunkStatus.registerSimple("carvers", SURFACE, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {
        WorldGenRegion $$5 = new WorldGenRegion($$1, (List<ChunkAccess>)$$3, $$0, 0);
        if ($$4 instanceof ProtoChunk) {
            ProtoChunk $$6 = (ProtoChunk)$$4;
            Blender.addAroundOldChunksCarvingMaskFilter($$5, $$6);
        }
        $$2.applyCarvers($$5, $$1.getSeed(), $$1.getChunkSource().randomState(), $$1.getBiomeManager(), $$1.structureManager().forWorldGenRegion($$5), $$4, GenerationStep.Carving.AIR);
    });
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.registerSimple("liquid_carvers", CARVERS, 8, POST_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {});
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> {
        ProtoChunk $$10 = (ProtoChunk)$$8;
        $$10.setLightEngine($$5);
        if ($$9 || !$$8.getStatus().isOrAfter($$0)) {
            Heightmap.primeHeightmaps($$8, (Set<Heightmap.Types>)EnumSet.of((Enum)Heightmap.Types.MOTION_BLOCKING, (Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (Enum)Heightmap.Types.OCEAN_FLOOR, (Enum)Heightmap.Types.WORLD_SURFACE));
            WorldGenRegion $$11 = new WorldGenRegion($$2, (List<ChunkAccess>)$$7, $$0, 1);
            $$3.applyBiomeDecoration($$11, $$8, $$2.structureManager().forWorldGenRegion($$11));
            Blender.generateBorderTicks($$11, $$8);
            $$10.setStatus($$0);
        }
        return $$5.retainData($$8).thenApply(Either::left);
    }, ($$0, $$1, $$2, $$3, $$4, $$5) -> $$3.retainData($$5).thenApply(Either::left));
    public static final ChunkStatus LIGHT = ChunkStatus.register("light", FEATURES, 1, POST_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> ChunkStatus.lightChunk($$0, $$5, $$8), ($$0, $$1, $$2, $$3, $$4, $$5) -> ChunkStatus.lightChunk($$0, $$3, $$5));
    public static final ChunkStatus SPAWN = ChunkStatus.registerSimple("spawn", LIGHT, 0, POST_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {
        if (!$$4.isUpgrading()) {
            $$2.spawnOriginalMobs(new WorldGenRegion($$1, (List<ChunkAccess>)$$3, $$0, -1));
        }
    });
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.registerSimple("heightmaps", SPAWN, 0, POST_FEATURES, ChunkType.PROTOCHUNK, ($$0, $$1, $$2, $$3, $$4) -> {});
    public static final ChunkStatus FULL = ChunkStatus.register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkType.LEVELCHUNK, ($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9) -> (CompletableFuture)$$6.apply((Object)$$8), ($$0, $$1, $$2, $$3, $$4, $$5) -> (CompletableFuture)$$4.apply((Object)$$5));
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of((Object)FULL, (Object)FEATURES, (Object)LIQUID_CARVERS, (Object)BIOMES, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object[])new ChunkStatus[0]);
    private static final IntList RANGE_BY_STATUS = (IntList)Util.make(new IntArrayList(ChunkStatus.getStatusList().size()), $$0 -> {
        int $$1 = 0;
        for (int $$2 = ChunkStatus.getStatusList().size() - 1; $$2 >= 0; --$$2) {
            while ($$1 + 1 < STATUS_BY_RANGE.size() && $$2 <= ((ChunkStatus)STATUS_BY_RANGE.get($$1 + 1)).getIndex()) {
                ++$$1;
            }
            $$0.add(0, $$1);
        }
    });
    private final String name;
    private final int index;
    private final ChunkStatus parent;
    private final GenerationTask generationTask;
    private final LoadingTask loadingTask;
    private final int range;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Types> heightmapsAfter;

    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(ChunkStatus $$0, ThreadedLevelLightEngine $$1, ChunkAccess $$2) {
        boolean $$3 = ChunkStatus.isLighted($$0, $$2);
        if (!$$2.getStatus().isOrAfter($$0)) {
            ((ProtoChunk)$$2).setStatus($$0);
        }
        return $$1.lightChunk($$2, $$3).thenApply(Either::left);
    }

    private static ChunkStatus registerSimple(String $$0, @Nullable ChunkStatus $$1, int $$2, EnumSet<Heightmap.Types> $$3, ChunkType $$4, SimpleGenerationTask $$5) {
        return ChunkStatus.register($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static ChunkStatus register(String $$0, @Nullable ChunkStatus $$1, int $$2, EnumSet<Heightmap.Types> $$3, ChunkType $$4, GenerationTask $$5) {
        return ChunkStatus.register($$0, $$1, $$2, $$3, $$4, $$5, PASSTHROUGH_LOAD_TASK);
    }

    private static ChunkStatus register(String $$0, @Nullable ChunkStatus $$1, int $$2, EnumSet<Heightmap.Types> $$3, ChunkType $$4, GenerationTask $$5, LoadingTask $$6) {
        return Registry.register(BuiltInRegistries.CHUNK_STATUS, $$0, new ChunkStatus($$0, $$1, $$2, $$3, $$4, $$5, $$6));
    }

    public static List<ChunkStatus> getStatusList() {
        ChunkStatus $$1;
        ArrayList $$0 = Lists.newArrayList();
        for ($$1 = FULL; $$1.getParent() != $$1; $$1 = $$1.getParent()) {
            $$0.add((Object)$$1);
        }
        $$0.add((Object)$$1);
        Collections.reverse((List)$$0);
        return $$0;
    }

    private static boolean isLighted(ChunkStatus $$0, ChunkAccess $$1) {
        return $$1.getStatus().isOrAfter($$0) && $$1.isLightCorrect();
    }

    public static ChunkStatus getStatusAroundFullChunk(int $$0) {
        if ($$0 >= STATUS_BY_RANGE.size()) {
            return EMPTY;
        }
        if ($$0 < 0) {
            return FULL;
        }
        return (ChunkStatus)STATUS_BY_RANGE.get($$0);
    }

    public static int maxDistance() {
        return STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus $$0) {
        return RANGE_BY_STATUS.getInt($$0.getIndex());
    }

    ChunkStatus(String $$0, @Nullable ChunkStatus $$1, int $$2, EnumSet<Heightmap.Types> $$3, ChunkType $$4, GenerationTask $$5, LoadingTask $$6) {
        this.name = $$0;
        this.parent = $$1 == null ? this : $$1;
        this.generationTask = $$5;
        this.loadingTask = $$6;
        this.range = $$2;
        this.chunkType = $$4;
        this.heightmapsAfter = $$3;
        this.index = $$1 == null ? 0 : $$1.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public ChunkStatus getParent() {
        return this.parent;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(Executor $$0, ServerLevel $$12, ChunkGenerator $$2, StructureTemplateManager $$3, ThreadedLevelLightEngine $$4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> $$5, List<ChunkAccess> $$6, boolean $$7) {
        ChunkAccess $$8 = (ChunkAccess)$$6.get($$6.size() / 2);
        ProfiledDuration $$9 = JvmProfiler.INSTANCE.onChunkGenerate($$8.getPos(), $$12.dimension(), this.name);
        CompletableFuture $$10 = this.generationTask.doWork(this, $$0, $$12, $$2, $$3, $$4, $$5, $$6, $$8, $$7);
        return $$9 != null ? $$10.thenApply($$1 -> {
            $$9.finish();
            return $$1;
        }) : $$10;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(ServerLevel $$0, StructureTemplateManager $$1, ThreadedLevelLightEngine $$2, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> $$3, ChunkAccess $$4) {
        return this.loadingTask.doWork(this, $$0, $$1, $$2, $$3, $$4);
    }

    public int getRange() {
        return this.range;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byName(String $$0) {
        return BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse($$0));
    }

    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    public boolean isOrAfter(ChunkStatus $$0) {
        return this.getIndex() >= $$0.getIndex();
    }

    public String toString() {
        return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
    }

    public static enum ChunkType {
        PROTOCHUNK,
        LEVELCHUNK;

    }

    static interface GenerationTask {
        public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, Executor var2, ServerLevel var3, ChunkGenerator var4, StructureTemplateManager var5, ThreadedLevelLightEngine var6, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7, List<ChunkAccess> var8, ChunkAccess var9, boolean var10);
    }

    static interface LoadingTask {
        public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, ServerLevel var2, StructureTemplateManager var3, ThreadedLevelLightEngine var4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var5, ChunkAccess var6);
    }

    static interface SimpleGenerationTask
    extends GenerationTask {
        @Override
        default public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus $$0, Executor $$1, ServerLevel $$2, ChunkGenerator $$3, StructureTemplateManager $$4, ThreadedLevelLightEngine $$5, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> $$6, List<ChunkAccess> $$7, ChunkAccess $$8, boolean $$9) {
            if ($$9 || !$$8.getStatus().isOrAfter($$0)) {
                this.doWork($$0, $$2, $$3, $$7, $$8);
                if ($$8 instanceof ProtoChunk) {
                    ProtoChunk $$10 = (ProtoChunk)$$8;
                    $$10.setStatus($$0);
                }
            }
            return CompletableFuture.completedFuture((Object)Either.left((Object)$$8));
        }

        public void doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, List<ChunkAccess> var4, ChunkAccess var5);
    }
}