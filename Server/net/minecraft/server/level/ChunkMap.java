/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.io.IOException
 *  java.io.Writer
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Queue
 *  java.util.Set
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  java.util.function.IntSupplier
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.TickingTracker;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class ChunkMap
extends ChunkStorage
implements ChunkHolder.PlayerProvider {
    private static final byte CHUNK_TYPE_REPLACEABLE = -1;
    private static final byte CHUNK_TYPE_UNKNOWN = 0;
    private static final byte CHUNK_TYPE_FULL = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CHUNK_SAVED_PER_TICK = 200;
    private static final int CHUNK_SAVED_EAGERLY_PER_TICK = 20;
    private static final int EAGER_CHUNK_SAVE_COOLDOWN_IN_MILLIS = 10000;
    private static final int MIN_VIEW_DISTANCE = 3;
    public static final int MAX_VIEW_DISTANCE = 33;
    public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.maxDistance();
    public static final int FORCED_TICKET_LEVEL = 31;
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap();
    private final LongSet entitiesInLevel = new LongOpenHashSet();
    final ServerLevel level;
    private final ThreadedLevelLightEngine lightEngine;
    private final BlockableEventLoop<Runnable> mainThreadExecutor;
    private ChunkGenerator generator;
    private final RandomState randomState;
    private final ChunkGeneratorStructureState chunkGeneratorState;
    private final Supplier<DimensionDataStorage> overworldDataStorage;
    private final PoiManager poiManager;
    final LongSet toDrop = new LongOpenHashSet();
    private boolean modified;
    private final ChunkTaskPriorityQueueSorter queueSorter;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldgenMailbox;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailbox;
    private final ChunkProgressListener progressListener;
    private final ChunkStatusUpdateListener chunkStatusListener;
    private final DistanceManager distanceManager;
    private final AtomicInteger tickingGenerated = new AtomicInteger();
    private final StructureTemplateManager structureTemplateManager;
    private final String storageName;
    private final PlayerMap playerMap = new PlayerMap();
    private final Int2ObjectMap<TrackedEntity> entityMap = new Int2ObjectOpenHashMap();
    private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
    private final Long2LongMap chunkSaveCooldowns = new Long2LongOpenHashMap();
    private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
    int viewDistance;

    public ChunkMap(ServerLevel $$0, LevelStorageSource.LevelStorageAccess $$1, DataFixer $$2, StructureTemplateManager $$3, Executor $$4, BlockableEventLoop<Runnable> $$5, LightChunkGetter $$6, ChunkGenerator $$7, ChunkProgressListener $$8, ChunkStatusUpdateListener $$9, Supplier<DimensionDataStorage> $$10, int $$11, boolean $$12) {
        super($$1.getDimensionPath($$0.dimension()).resolve("region"), $$2, $$12);
        this.structureTemplateManager = $$3;
        Path $$13 = $$1.getDimensionPath($$0.dimension());
        this.storageName = $$13.getFileName().toString();
        this.level = $$0;
        this.generator = $$7;
        RegistryAccess $$14 = $$0.registryAccess();
        long $$15 = $$0.getSeed();
        if ($$7 instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator $$16 = (NoiseBasedChunkGenerator)$$7;
            this.randomState = RandomState.create($$16.generatorSettings().value(), $$14.lookupOrThrow(Registries.NOISE), $$15);
        } else {
            this.randomState = RandomState.create(NoiseGeneratorSettings.dummy(), $$14.lookupOrThrow(Registries.NOISE), $$15);
        }
        this.chunkGeneratorState = $$7.createState($$14.lookupOrThrow(Registries.STRUCTURE_SET), this.randomState, $$15);
        this.mainThreadExecutor = $$5;
        ProcessorMailbox<Runnable> $$17 = ProcessorMailbox.create($$4, "worldgen");
        ProcessorHandle $$18 = ProcessorHandle.of("main", $$5::tell);
        this.progressListener = $$8;
        this.chunkStatusListener = $$9;
        ProcessorMailbox<Runnable> $$19 = ProcessorMailbox.create($$4, "light");
        this.queueSorter = new ChunkTaskPriorityQueueSorter((List<ProcessorHandle<?>>)ImmutableList.of($$17, $$18, $$19), $$4, Integer.MAX_VALUE);
        this.worldgenMailbox = this.queueSorter.getProcessor($$17, false);
        this.mainThreadMailbox = this.queueSorter.getProcessor($$18, false);
        this.lightEngine = new ThreadedLevelLightEngine($$6, this, this.level.dimensionType().hasSkyLight(), $$19, this.queueSorter.getProcessor($$19, false));
        this.distanceManager = new DistanceManager($$4, $$5);
        this.overworldDataStorage = $$10;
        this.poiManager = new PoiManager($$13.resolve("poi"), $$2, $$12, $$14, $$0);
        this.setViewDistance($$11);
    }

    protected ChunkGenerator generator() {
        return this.generator;
    }

    protected ChunkGeneratorStructureState generatorState() {
        return this.chunkGeneratorState;
    }

    protected RandomState randomState() {
        return this.randomState;
    }

    public void debugReloadGenerator() {
        DataResult $$02 = ChunkGenerator.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.generator);
        DataResult $$1 = $$02.flatMap($$0 -> ChunkGenerator.CODEC.parse((DynamicOps)JsonOps.INSTANCE, $$0));
        $$1.result().ifPresent($$0 -> {
            this.generator = $$0;
        });
    }

    private static double euclideanDistanceSquared(ChunkPos $$0, Entity $$1) {
        double $$2 = SectionPos.sectionToBlockCoord($$0.x, 8);
        double $$3 = SectionPos.sectionToBlockCoord($$0.z, 8);
        double $$4 = $$2 - $$1.getX();
        double $$5 = $$3 - $$1.getZ();
        return $$4 * $$4 + $$5 * $$5;
    }

    public static boolean isChunkInRange(int $$0, int $$1, int $$2, int $$3, int $$4) {
        int $$10;
        int $$11;
        int $$5 = Math.max((int)0, (int)(Math.abs((int)($$0 - $$2)) - 1));
        int $$6 = Math.max((int)0, (int)(Math.abs((int)($$1 - $$3)) - 1));
        long $$7 = Math.max((int)0, (int)(Math.max((int)$$5, (int)$$6) - 1));
        long $$8 = Math.min((int)$$5, (int)$$6);
        long $$9 = $$8 * $$8 + $$7 * $$7;
        return $$9 <= (long)($$11 = ($$10 = $$4 - 1) * $$10);
    }

    private static boolean isChunkOnRangeBorder(int $$0, int $$1, int $$2, int $$3, int $$4) {
        if (!ChunkMap.isChunkInRange($$0, $$1, $$2, $$3, $$4)) {
            return false;
        }
        if (!ChunkMap.isChunkInRange($$0 + 1, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if (!ChunkMap.isChunkInRange($$0, $$1 + 1, $$2, $$3, $$4)) {
            return true;
        }
        if (!ChunkMap.isChunkInRange($$0 - 1, $$1, $$2, $$3, $$4)) {
            return true;
        }
        return !ChunkMap.isChunkInRange($$0, $$1 - 1, $$2, $$3, $$4);
    }

    protected ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    protected ChunkHolder getUpdatingChunkIfPresent(long $$0) {
        return (ChunkHolder)this.updatingChunkMap.get($$0);
    }

    @Nullable
    protected ChunkHolder getVisibleChunkIfPresent(long $$0) {
        return (ChunkHolder)this.visibleChunkMap.get($$0);
    }

    protected IntSupplier getChunkQueueLevel(long $$0) {
        return () -> {
            ChunkHolder $$1 = this.getVisibleChunkIfPresent($$0);
            if ($$1 == null) {
                return ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1;
            }
            return Math.min((int)$$1.getQueueLevel(), (int)(ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1));
        };
    }

    public String getChunkDebugData(ChunkPos $$0) {
        ChunkHolder $$1 = this.getVisibleChunkIfPresent($$0.toLong());
        if ($$1 == null) {
            return "null";
        }
        String $$2 = $$1.getTicketLevel() + "\n";
        ChunkStatus $$3 = $$1.getLastAvailableStatus();
        ChunkAccess $$4 = $$1.getLastAvailable();
        if ($$3 != null) {
            $$2 = $$2 + "St: \u00a7" + $$3.getIndex() + $$3 + "\u00a7r\n";
        }
        if ($$4 != null) {
            $$2 = $$2 + "Ch: \u00a7" + $$4.getStatus().getIndex() + $$4.getStatus() + "\u00a7r\n";
        }
        ChunkHolder.FullChunkStatus $$5 = $$1.getFullStatus();
        $$2 = $$2 + "\u00a7" + $$5.ordinal() + $$5;
        return $$2 + "\u00a7r";
    }

    private CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> getChunkRangeFuture(ChunkPos $$0, final int $$1, IntFunction<ChunkStatus> $$2) {
        ArrayList $$32 = new ArrayList();
        ArrayList $$4 = new ArrayList();
        final int $$5 = $$0.x;
        final int $$6 = $$0.z;
        for (int $$7 = -$$1; $$7 <= $$1; ++$$7) {
            for (int $$8 = -$$1; $$8 <= $$1; ++$$8) {
                int $$9 = Math.max((int)Math.abs((int)$$8), (int)Math.abs((int)$$7));
                final ChunkPos $$10 = new ChunkPos($$5 + $$8, $$6 + $$7);
                long $$11 = $$10.toLong();
                ChunkHolder $$12 = this.getUpdatingChunkIfPresent($$11);
                if ($$12 == null) {
                    return CompletableFuture.completedFuture((Object)Either.right((Object)new ChunkHolder.ChunkLoadingFailure(){

                        public String toString() {
                            return "Unloaded " + $$10;
                        }
                    }));
                }
                ChunkStatus $$13 = (ChunkStatus)$$2.apply($$9);
                CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$14 = $$12.getOrScheduleFuture($$13, this);
                $$4.add((Object)$$12);
                $$32.add($$14);
            }
        }
        CompletableFuture $$15 = Util.sequence($$32);
        CompletableFuture $$16 = $$15.thenApply($$3 -> {
            ArrayList $$4 = Lists.newArrayList();
            int $$5 = 0;
            for (final Either $$6 : $$3) {
                if ($$6 == null) {
                    throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
                }
                Optional $$7 = $$6.left();
                if (!$$7.isPresent()) {
                    final int $$8 = $$5;
                    return Either.right((Object)new ChunkHolder.ChunkLoadingFailure(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos($$5 + $$8 % ($$1 * 2 + 1), $$6 + $$8 / ($$1 * 2 + 1)) + " " + $$6.right().get();
                        }
                    });
                }
                $$4.add((Object)((ChunkAccess)$$7.get()));
                ++$$5;
            }
            return Either.left((Object)$$4);
        });
        for (ChunkHolder $$17 : $$4) {
            $$17.addSaveDependency("getChunkRangeFuture " + $$0 + " " + $$1, $$16);
        }
        return $$16;
    }

    public ReportedException debugFuturesAndCreateReportedException(IllegalStateException $$0, String $$12) {
        StringBuilder $$2 = new StringBuilder();
        Consumer $$3 = $$1 -> $$1.getAllFutures().forEach($$2 -> {
            ChunkStatus $$3 = (ChunkStatus)$$2.getFirst();
            CompletableFuture $$4 = (CompletableFuture)$$2.getSecond();
            if ($$4 != null && $$4.isDone() && $$4.join() == null) {
                $$2.append((Object)$$1.getPos()).append(" - status: ").append((Object)$$3).append(" future: ").append((Object)$$4).append(System.lineSeparator());
            }
        });
        $$2.append("Updating:").append(System.lineSeparator());
        this.updatingChunkMap.values().forEach($$3);
        $$2.append("Visible:").append(System.lineSeparator());
        this.visibleChunkMap.values().forEach($$3);
        CrashReport $$4 = CrashReport.forThrowable((Throwable)$$0, "Chunk loading");
        CrashReportCategory $$5 = $$4.addCategory("Chunk loading");
        $$5.setDetail("Details", $$12);
        $$5.setDetail("Futures", $$2);
        return new ReportedException($$4);
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareEntityTickingChunk(ChunkPos $$03) {
        return this.getChunkRangeFuture($$03, 2, (IntFunction<ChunkStatus>)((IntFunction)$$0 -> ChunkStatus.FULL)).thenApplyAsync($$02 -> $$02.mapLeft($$0 -> (LevelChunk)$$0.get($$0.size() / 2)), this.mainThreadExecutor);
    }

    @Nullable
    ChunkHolder updateChunkScheduling(long $$0, int $$1, @Nullable ChunkHolder $$2, int $$3) {
        if ($$3 > MAX_CHUNK_DISTANCE && $$1 > MAX_CHUNK_DISTANCE) {
            return $$2;
        }
        if ($$2 != null) {
            $$2.setTicketLevel($$1);
        }
        if ($$2 != null) {
            if ($$1 > MAX_CHUNK_DISTANCE) {
                this.toDrop.add($$0);
            } else {
                this.toDrop.remove($$0);
            }
        }
        if ($$1 <= MAX_CHUNK_DISTANCE && $$2 == null) {
            $$2 = (ChunkHolder)this.pendingUnloads.remove($$0);
            if ($$2 != null) {
                $$2.setTicketLevel($$1);
            } else {
                $$2 = new ChunkHolder(new ChunkPos($$0), $$1, this.level, this.lightEngine, this.queueSorter, this);
            }
            this.updatingChunkMap.put($$0, (Object)$$2);
            this.modified = true;
        }
        return $$2;
    }

    @Override
    public void close() throws IOException {
        try {
            this.queueSorter.close();
            this.poiManager.close();
        }
        finally {
            super.close();
        }
    }

    protected void saveAllChunks(boolean $$02) {
        if ($$02) {
            List $$12 = (List)this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).collect(Collectors.toList());
            MutableBoolean $$2 = new MutableBoolean();
            do {
                $$2.setFalse();
                $$12.stream().map($$0 -> {
                    CompletableFuture<ChunkAccess> $$1;
                    do {
                        $$1 = $$0.getChunkToSave();
                        this.mainThreadExecutor.managedBlock(() -> $$1.isDone());
                    } while ($$1 != $$0.getChunkToSave());
                    return (ChunkAccess)$$1.join();
                }).filter($$0 -> $$0 instanceof ImposterProtoChunk || $$0 instanceof LevelChunk).filter(this::save).forEach($$1 -> $$2.setTrue());
            } while ($$2.isTrue());
            this.processUnloads(() -> true);
            this.flushWorker();
        } else {
            this.visibleChunkMap.values().forEach(this::saveChunkIfNeeded);
        }
    }

    protected void tick(BooleanSupplier $$0) {
        ProfilerFiller $$1 = this.level.getProfiler();
        $$1.push("poi");
        this.poiManager.tick($$0);
        $$1.popPush("chunk_unload");
        if (!this.level.noSave()) {
            this.processUnloads($$0);
        }
        $$1.pop();
    }

    public boolean hasWork() {
        return this.lightEngine.hasLightWork() || !this.pendingUnloads.isEmpty() || !this.updatingChunkMap.isEmpty() || this.poiManager.hasWork() || !this.toDrop.isEmpty() || !this.unloadQueue.isEmpty() || this.queueSorter.hasWork() || this.distanceManager.hasTickets();
    }

    private void processUnloads(BooleanSupplier $$0) {
        Runnable $$6;
        LongIterator $$1 = this.toDrop.iterator();
        int $$2 = 0;
        while ($$1.hasNext() && ($$0.getAsBoolean() || $$2 < 200 || this.toDrop.size() > 2000)) {
            long $$3 = $$1.nextLong();
            ChunkHolder $$4 = (ChunkHolder)this.updatingChunkMap.remove($$3);
            if ($$4 != null) {
                this.pendingUnloads.put($$3, (Object)$$4);
                this.modified = true;
                ++$$2;
                this.scheduleUnload($$3, $$4);
            }
            $$1.remove();
        }
        for (int $$5 = Math.max((int)0, (int)(this.unloadQueue.size() - 2000)); ($$0.getAsBoolean() || $$5 > 0) && ($$6 = (Runnable)this.unloadQueue.poll()) != null; --$$5) {
            $$6.run();
        }
        int $$7 = 0;
        ObjectIterator $$8 = this.visibleChunkMap.values().iterator();
        while ($$7 < 20 && $$0.getAsBoolean() && $$8.hasNext()) {
            if (!this.saveChunkIfNeeded((ChunkHolder)$$8.next())) continue;
            ++$$7;
        }
    }

    private void scheduleUnload(long $$0, ChunkHolder $$12) {
        CompletableFuture<ChunkAccess> $$22 = $$12.getChunkToSave();
        $$22.thenAcceptAsync($$3 -> {
            CompletableFuture<ChunkAccess> $$4 = $$12.getChunkToSave();
            if ($$4 != $$22) {
                this.scheduleUnload($$0, $$12);
                return;
            }
            if (this.pendingUnloads.remove($$0, (Object)$$12) && $$3 != null) {
                if ($$3 instanceof LevelChunk) {
                    ((LevelChunk)$$3).setLoaded(false);
                }
                this.save((ChunkAccess)$$3);
                if (this.entitiesInLevel.remove($$0) && $$3 instanceof LevelChunk) {
                    LevelChunk $$5 = (LevelChunk)$$3;
                    this.level.unload($$5);
                }
                this.lightEngine.updateChunkStatus($$3.getPos());
                this.lightEngine.tryScheduleUpdate();
                this.progressListener.onStatusChange($$3.getPos(), null);
                this.chunkSaveCooldowns.remove($$3.getPos().toLong());
            }
        }, arg_0 -> this.unloadQueue.add(arg_0)).whenComplete(($$1, $$2) -> {
            if ($$2 != null) {
                LOGGER.error("Failed to save chunk {}", (Object)$$12.getPos(), $$2);
            }
        });
    }

    protected boolean promoteChunkMap() {
        if (!this.modified) {
            return false;
        }
        this.visibleChunkMap = this.updatingChunkMap.clone();
        this.modified = false;
        return true;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> schedule(ChunkHolder $$0, ChunkStatus $$12) {
        Optional $$3;
        ChunkPos $$2 = $$0.getPos();
        if ($$12 == ChunkStatus.EMPTY) {
            return this.scheduleChunkLoad($$2);
        }
        if ($$12 == ChunkStatus.LIGHT) {
            this.distanceManager.addTicket(TicketType.LIGHT, $$2, 33 + ChunkStatus.getDistance(ChunkStatus.LIGHT), $$2);
        }
        if (($$3 = ((Either)$$0.getOrScheduleFuture($$12.getParent(), this).getNow(ChunkHolder.UNLOADED_CHUNK)).left()).isPresent() && ((ChunkAccess)$$3.get()).getStatus().isOrAfter($$12)) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$4 = $$12.load(this.level, this.structureTemplateManager, this.lightEngine, (Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>)((Function)$$1 -> this.protoChunkToFullChunk($$0)), (ChunkAccess)$$3.get());
            this.progressListener.onStatusChange($$2, $$12);
            return $$4;
        }
        return this.scheduleChunkGeneration($$0, $$12);
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoad(ChunkPos $$0) {
        return this.readChunk($$0).thenApply($$12 -> $$12.filter($$1 -> {
            boolean $$2 = ChunkMap.isChunkDataValid($$1);
            if (!$$2) {
                LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)$$0);
            }
            return $$2;
        })).thenApplyAsync($$1 -> {
            this.level.getProfiler().incrementCounter("chunkLoad");
            if ($$1.isPresent()) {
                ProtoChunk $$2 = ChunkSerializer.read(this.level, this.poiManager, $$0, (CompoundTag)$$1.get());
                this.markPosition($$0, ((ChunkAccess)$$2).getStatus().getChunkType());
                return Either.left((Object)$$2);
            }
            return Either.left((Object)this.createEmptyChunk($$0));
        }, this.mainThreadExecutor).exceptionallyAsync($$1 -> this.handleChunkLoadFailure((Throwable)$$1, $$0), this.mainThreadExecutor);
    }

    private static boolean isChunkDataValid(CompoundTag $$0) {
        return $$0.contains("Status", 8);
    }

    /*
     * Enabled aggressive block sorting
     */
    private Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> handleChunkLoadFailure(Throwable $$0, ChunkPos $$1) {
        if (!($$0 instanceof ReportedException)) {
            if (!($$0 instanceof IOException)) return Either.left((Object)this.createEmptyChunk($$1));
            LOGGER.error("Couldn't load chunk {}", (Object)$$1, (Object)$$0);
            return Either.left((Object)this.createEmptyChunk($$1));
        }
        ReportedException $$2 = (ReportedException)$$0;
        Throwable $$3 = $$2.getCause();
        if ($$3 instanceof IOException) {
            LOGGER.error("Couldn't load chunk {}", (Object)$$1, (Object)$$3);
            return Either.left((Object)this.createEmptyChunk($$1));
        }
        this.markPositionReplaceable($$1);
        throw $$2;
    }

    private ChunkAccess createEmptyChunk(ChunkPos $$0) {
        this.markPositionReplaceable($$0);
        return new ProtoChunk($$0, UpgradeData.EMPTY, this.level, this.level.registryAccess().registryOrThrow(Registries.BIOME), null);
    }

    private void markPositionReplaceable(ChunkPos $$0) {
        this.chunkTypeCache.put($$0.toLong(), (byte)-1);
    }

    private byte markPosition(ChunkPos $$0, ChunkStatus.ChunkType $$1) {
        return this.chunkTypeCache.put($$0.toLong(), $$1 == ChunkStatus.ChunkType.PROTOCHUNK ? (byte)-1 : 1);
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkGeneration(ChunkHolder $$0, ChunkStatus $$12) {
        ChunkPos $$2 = $$0.getPos();
        CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> $$3 = this.getChunkRangeFuture($$2, $$12.getRange(), (IntFunction<ChunkStatus>)((IntFunction)$$1 -> this.getDependencyStatus($$12, $$1)));
        this.level.getProfiler().incrementCounter((Supplier<String>)((Supplier)() -> "chunkGenerate " + $$12.getName()));
        Executor $$4 = $$1 -> this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message($$0, $$1));
        return $$3.thenComposeAsync($$42 -> (CompletionStage)$$42.map($$4 -> {
            try {
                CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$5 = $$12.generate($$4, this.level, this.generator, this.structureTemplateManager, this.lightEngine, (Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>)((Function)$$1 -> this.protoChunkToFullChunk($$0)), (List<ChunkAccess>)$$4, false);
                this.progressListener.onStatusChange($$2, $$12);
                return $$5;
            }
            catch (Exception $$6) {
                $$6.getStackTrace();
                CrashReport $$7 = CrashReport.forThrowable($$6, "Exception generating new chunk");
                CrashReportCategory $$8 = $$7.addCategory("Chunk to be generated");
                $$8.setDetail("Location", String.format((Locale)Locale.ROOT, (String)"%d,%d", (Object[])new Object[]{$$0.x, $$0.z}));
                $$8.setDetail("Position hash", ChunkPos.asLong($$0.x, $$0.z));
                $$8.setDetail("Generator", this.generator);
                this.mainThreadExecutor.execute(() -> {
                    throw new ReportedException($$7);
                });
                throw new ReportedException($$7);
            }
        }, $$1 -> {
            this.releaseLightTicket($$2);
            return CompletableFuture.completedFuture((Object)Either.right((Object)$$1));
        }), $$4);
    }

    protected void releaseLightTicket(ChunkPos $$0) {
        this.mainThreadExecutor.tell(Util.name(() -> this.distanceManager.removeTicket(TicketType.LIGHT, $$0, 33 + ChunkStatus.getDistance(ChunkStatus.LIGHT), $$0), (Supplier<String>)((Supplier)() -> "release light ticket " + $$0)));
    }

    private ChunkStatus getDependencyStatus(ChunkStatus $$0, int $$1) {
        ChunkStatus $$3;
        if ($$1 == 0) {
            ChunkStatus $$2 = $$0.getParent();
        } else {
            $$3 = ChunkStatus.getStatusAroundFullChunk(ChunkStatus.getDistance($$0) + $$1);
        }
        return $$3;
    }

    private static void postLoadProtoChunk(ServerLevel $$0, List<CompoundTag> $$1) {
        if (!$$1.isEmpty()) {
            $$0.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive($$1, $$0));
        }
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> protoChunkToFullChunk(ChunkHolder $$0) {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$12 = $$0.getFutureIfPresentUnchecked(ChunkStatus.FULL.getParent());
        return $$12.thenApplyAsync($$1 -> {
            ChunkStatus $$2 = ChunkHolder.getStatus($$0.getTicketLevel());
            if (!$$2.isOrAfter(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return $$1.mapLeft($$12 -> {
                LevelChunk $$5;
                ChunkPos $$2 = $$0.getPos();
                ProtoChunk $$3 = (ProtoChunk)$$12;
                if ($$3 instanceof ImposterProtoChunk) {
                    LevelChunk $$4 = ((ImposterProtoChunk)$$3).getWrapped();
                } else {
                    $$5 = new LevelChunk(this.level, $$3, $$1 -> ChunkMap.postLoadProtoChunk(this.level, $$3.getEntities()));
                    $$0.replaceProtoChunk(new ImposterProtoChunk($$5, false));
                }
                $$5.setFullStatus((Supplier<ChunkHolder.FullChunkStatus>)((Supplier)() -> ChunkHolder.getFullChunkStatus($$0.getTicketLevel())));
                $$5.runPostLoad();
                if (this.entitiesInLevel.add($$2.toLong())) {
                    $$5.setLoaded(true);
                    $$5.registerAllBlockEntitiesAfterLevelLoad();
                    $$5.registerTickContainerInLevel(this.level);
                }
                return $$5;
            });
        }, $$1 -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message($$1, $$0.getPos().toLong(), $$0::getTicketLevel)));
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareTickingChunk(ChunkHolder $$03) {
        ChunkPos $$13 = $$03.getPos();
        CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> $$2 = this.getChunkRangeFuture($$13, 1, (IntFunction<ChunkStatus>)((IntFunction)$$0 -> ChunkStatus.FULL));
        CompletableFuture $$3 = $$2.thenApplyAsync($$02 -> $$02.mapLeft($$0 -> (LevelChunk)$$0.get($$0.size() / 2)), $$1 -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message($$03, $$1))).thenApplyAsync($$02 -> $$02.ifLeft($$0 -> {
            $$0.postProcessGeneration();
            this.level.startTickingChunk((LevelChunk)$$0);
        }), this.mainThreadExecutor);
        $$3.thenAcceptAsync($$12 -> $$12.ifLeft($$1 -> {
            this.tickingGenerated.getAndIncrement();
            MutableObject $$22 = new MutableObject();
            this.getPlayers($$13, false).forEach($$2 -> this.playerLoadedChunk((ServerPlayer)$$2, (MutableObject<ClientboundLevelChunkWithLightPacket>)$$22, (LevelChunk)$$1));
        }), $$1 -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message($$03, $$1)));
        return $$3;
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareAccessibleChunk(ChunkHolder $$0) {
        return this.getChunkRangeFuture($$0.getPos(), 1, (IntFunction<ChunkStatus>)((IntFunction)ChunkStatus::getStatusAroundFullChunk)).thenApplyAsync($$02 -> $$02.mapLeft($$0 -> {
            LevelChunk $$1 = (LevelChunk)$$0.get($$0.size() / 2);
            return $$1;
        }), $$1 -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message($$0, $$1)));
    }

    public int getTickingGenerated() {
        return this.tickingGenerated.get();
    }

    private boolean saveChunkIfNeeded(ChunkHolder $$0) {
        if (!$$0.wasAccessibleSinceLastSave()) {
            return false;
        }
        ChunkAccess $$1 = (ChunkAccess)$$0.getChunkToSave().getNow(null);
        if ($$1 instanceof ImposterProtoChunk || $$1 instanceof LevelChunk) {
            long $$2 = $$1.getPos().toLong();
            long $$3 = this.chunkSaveCooldowns.getOrDefault($$2, -1L);
            long $$4 = System.currentTimeMillis();
            if ($$4 < $$3) {
                return false;
            }
            boolean $$5 = this.save($$1);
            $$0.refreshAccessibility();
            if ($$5) {
                this.chunkSaveCooldowns.put($$2, $$4 + 10000L);
            }
            return $$5;
        }
        return false;
    }

    private boolean save(ChunkAccess $$0) {
        this.poiManager.flush($$0.getPos());
        if (!$$0.isUnsaved()) {
            return false;
        }
        $$0.setUnsaved(false);
        ChunkPos $$1 = $$0.getPos();
        try {
            ChunkStatus $$2 = $$0.getStatus();
            if ($$2.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
                if (this.isExistingChunkFull($$1)) {
                    return false;
                }
                if ($$2 == ChunkStatus.EMPTY && $$0.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                    return false;
                }
            }
            this.level.getProfiler().incrementCounter("chunkSave");
            CompoundTag $$3 = ChunkSerializer.write(this.level, $$0);
            this.write($$1, $$3);
            this.markPosition($$1, $$2.getChunkType());
            return true;
        }
        catch (Exception $$4) {
            LOGGER.error("Failed to save chunk {},{}", new Object[]{$$1.x, $$1.z, $$4});
            return false;
        }
    }

    /*
     * WARNING - void declaration
     */
    private boolean isExistingChunkFull(ChunkPos $$0) {
        void $$4;
        byte $$1 = this.chunkTypeCache.get($$0.toLong());
        if ($$1 != 0) {
            return $$1 == 1;
        }
        try {
            CompoundTag $$2 = (CompoundTag)((Optional)this.readChunk($$0).join()).orElse(null);
            if ($$2 == null) {
                this.markPositionReplaceable($$0);
                return false;
            }
        }
        catch (Exception $$3) {
            LOGGER.error("Failed to read chunk {}", (Object)$$0, (Object)$$3);
            this.markPositionReplaceable($$0);
            return false;
        }
        ChunkStatus.ChunkType $$5 = ChunkSerializer.getChunkTypeFromTag((CompoundTag)$$4);
        return this.markPosition($$0, $$5) == 1;
    }

    protected void setViewDistance(int $$0) {
        int $$1 = Mth.clamp($$0 + 1, 3, 33);
        if ($$1 != this.viewDistance) {
            int $$2 = this.viewDistance;
            this.viewDistance = $$1;
            this.distanceManager.updatePlayerTickets(this.viewDistance + 1);
            for (ChunkHolder $$32 : this.updatingChunkMap.values()) {
                ChunkPos $$4 = $$32.getPos();
                MutableObject $$5 = new MutableObject();
                this.getPlayers($$4, false).forEach($$3 -> {
                    SectionPos $$4 = $$3.getLastSectionPos();
                    boolean $$5 = ChunkMap.isChunkInRange($$0.x, $$0.z, $$4.x(), $$4.z(), $$2);
                    boolean $$6 = ChunkMap.isChunkInRange($$0.x, $$0.z, $$4.x(), $$4.z(), this.viewDistance);
                    this.updateChunkTracking((ServerPlayer)$$3, $$4, (MutableObject<ClientboundLevelChunkWithLightPacket>)$$5, $$5, $$6);
                });
            }
        }
    }

    protected void updateChunkTracking(ServerPlayer $$0, ChunkPos $$1, MutableObject<ClientboundLevelChunkWithLightPacket> $$2, boolean $$3, boolean $$4) {
        ChunkHolder $$5;
        if ($$0.level != this.level) {
            return;
        }
        if ($$4 && !$$3 && ($$5 = this.getVisibleChunkIfPresent($$1.toLong())) != null) {
            LevelChunk $$6 = $$5.getTickingChunk();
            if ($$6 != null) {
                this.playerLoadedChunk($$0, $$2, $$6);
            }
            DebugPackets.sendPoiPacketsForChunk(this.level, $$1);
        }
        if (!$$4 && $$3) {
            $$0.untrackChunk($$1);
        }
    }

    public int size() {
        return this.visibleChunkMap.size();
    }

    public net.minecraft.server.level.DistanceManager getDistanceManager() {
        return this.distanceManager;
    }

    protected Iterable<ChunkHolder> getChunks() {
        return Iterables.unmodifiableIterable((Iterable)this.visibleChunkMap.values());
    }

    void dumpChunks(Writer $$02) throws IOException {
        CsvOutput $$1 = CsvOutput.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("block_entity_count").addColumn("ticking_ticket").addColumn("ticking_level").addColumn("block_ticks").addColumn("fluid_ticks").build($$02);
        TickingTracker $$2 = this.distanceManager.tickingTracker();
        for (Long2ObjectMap.Entry $$3 : this.visibleChunkMap.long2ObjectEntrySet()) {
            long $$4 = $$3.getLongKey();
            ChunkPos $$5 = new ChunkPos($$4);
            ChunkHolder $$6 = (ChunkHolder)$$3.getValue();
            Optional $$7 = Optional.ofNullable((Object)$$6.getLastAvailable());
            Optional $$8 = $$7.flatMap($$0 -> $$0 instanceof LevelChunk ? Optional.of((Object)((LevelChunk)$$0)) : Optional.empty());
            $$1.writeRow($$5.x, $$5.z, $$6.getTicketLevel(), $$7.isPresent(), $$7.map(ChunkAccess::getStatus).orElse(null), $$8.map(LevelChunk::getFullStatus).orElse(null), ChunkMap.printFuture($$6.getFullChunkFuture()), ChunkMap.printFuture($$6.getTickingChunkFuture()), ChunkMap.printFuture($$6.getEntityTickingChunkFuture()), this.distanceManager.getTicketDebugString($$4), this.anyPlayerCloseEnoughForSpawning($$5), $$8.map($$0 -> $$0.getBlockEntities().size()).orElse((Object)0), $$2.getTicketDebugString($$4), $$2.getLevel($$4), $$8.map($$0 -> $$0.getBlockTicks().count()).orElse((Object)0), $$8.map($$0 -> $$0.getFluidTicks().count()).orElse((Object)0));
        }
    }

    private static String printFuture(CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> $$02) {
        try {
            Either $$1 = (Either)$$02.getNow(null);
            if ($$1 != null) {
                return (String)$$1.map($$0 -> "done", $$0 -> "unloaded");
            }
            return "not completed";
        }
        catch (CompletionException $$2) {
            return "failed " + $$2.getCause().getMessage();
        }
        catch (CancellationException $$3) {
            return "cancelled";
        }
    }

    private CompletableFuture<Optional<CompoundTag>> readChunk(ChunkPos $$02) {
        return this.read($$02).thenApplyAsync($$0 -> $$0.map(this::upgradeChunkTag), (Executor)Util.backgroundExecutor());
    }

    private CompoundTag upgradeChunkTag(CompoundTag $$0) {
        return this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, $$0, this.generator.getTypeNameForDataFixer());
    }

    boolean anyPlayerCloseEnoughForSpawning(ChunkPos $$0) {
        long $$1 = $$0.toLong();
        if (!this.distanceManager.hasPlayersNearby($$1)) {
            return false;
        }
        for (ServerPlayer $$2 : this.playerMap.getPlayers($$1)) {
            if (!this.playerIsCloseEnoughForSpawning($$2, $$0)) continue;
            return true;
        }
        return false;
    }

    public List<ServerPlayer> getPlayersCloseForSpawning(ChunkPos $$0) {
        long $$1 = $$0.toLong();
        if (!this.distanceManager.hasPlayersNearby($$1)) {
            return List.of();
        }
        ImmutableList.Builder $$2 = ImmutableList.builder();
        for (ServerPlayer $$3 : this.playerMap.getPlayers($$1)) {
            if (!this.playerIsCloseEnoughForSpawning($$3, $$0)) continue;
            $$2.add((Object)$$3);
        }
        return $$2.build();
    }

    private boolean playerIsCloseEnoughForSpawning(ServerPlayer $$0, ChunkPos $$1) {
        if ($$0.isSpectator()) {
            return false;
        }
        double $$2 = ChunkMap.euclideanDistanceSquared($$1, $$0);
        return $$2 < 16384.0;
    }

    private boolean skipPlayer(ServerPlayer $$0) {
        return $$0.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
    }

    void updatePlayerStatus(ServerPlayer $$0, boolean $$1) {
        boolean $$2 = this.skipPlayer($$0);
        boolean $$3 = this.playerMap.ignoredOrUnknown($$0);
        int $$4 = SectionPos.blockToSectionCoord($$0.getBlockX());
        int $$5 = SectionPos.blockToSectionCoord($$0.getBlockZ());
        if ($$1) {
            this.playerMap.addPlayer(ChunkPos.asLong($$4, $$5), $$0, $$2);
            this.updatePlayerPos($$0);
            if (!$$2) {
                this.distanceManager.addPlayer(SectionPos.of($$0), $$0);
            }
        } else {
            SectionPos $$6 = $$0.getLastSectionPos();
            this.playerMap.removePlayer($$6.chunk().toLong(), $$0);
            if (!$$3) {
                this.distanceManager.removePlayer($$6, $$0);
            }
        }
        for (int $$7 = $$4 - this.viewDistance - 1; $$7 <= $$4 + this.viewDistance + 1; ++$$7) {
            for (int $$8 = $$5 - this.viewDistance - 1; $$8 <= $$5 + this.viewDistance + 1; ++$$8) {
                if (!ChunkMap.isChunkInRange($$7, $$8, $$4, $$5, this.viewDistance)) continue;
                ChunkPos $$9 = new ChunkPos($$7, $$8);
                this.updateChunkTracking($$0, $$9, (MutableObject<ClientboundLevelChunkWithLightPacket>)new MutableObject(), !$$1, $$1);
            }
        }
    }

    private SectionPos updatePlayerPos(ServerPlayer $$0) {
        SectionPos $$1 = SectionPos.of($$0);
        $$0.setLastSectionPos($$1);
        $$0.connection.send(new ClientboundSetChunkCacheCenterPacket($$1.x(), $$1.z()));
        return $$1;
    }

    public void move(ServerPlayer $$0) {
        boolean $$10;
        for (TrackedEntity $$1 : this.entityMap.values()) {
            if ($$1.entity == $$0) {
                $$1.updatePlayers(this.level.players());
                continue;
            }
            $$1.updatePlayer($$0);
        }
        int $$2 = SectionPos.blockToSectionCoord($$0.getBlockX());
        int $$3 = SectionPos.blockToSectionCoord($$0.getBlockZ());
        SectionPos $$4 = $$0.getLastSectionPos();
        SectionPos $$5 = SectionPos.of($$0);
        long $$6 = $$4.chunk().toLong();
        long $$7 = $$5.chunk().toLong();
        boolean $$8 = this.playerMap.ignored($$0);
        boolean $$9 = this.skipPlayer($$0);
        boolean bl = $$10 = $$4.asLong() != $$5.asLong();
        if ($$10 || $$8 != $$9) {
            this.updatePlayerPos($$0);
            if (!$$8) {
                this.distanceManager.removePlayer($$4, $$0);
            }
            if (!$$9) {
                this.distanceManager.addPlayer($$5, $$0);
            }
            if (!$$8 && $$9) {
                this.playerMap.ignorePlayer($$0);
            }
            if ($$8 && !$$9) {
                this.playerMap.unIgnorePlayer($$0);
            }
            if ($$6 != $$7) {
                this.playerMap.updatePlayer($$6, $$7, $$0);
            }
        }
        int $$11 = $$4.x();
        int $$12 = $$4.z();
        if (Math.abs((int)($$11 - $$2)) <= this.viewDistance * 2 && Math.abs((int)($$12 - $$3)) <= this.viewDistance * 2) {
            int $$13 = Math.min((int)$$2, (int)$$11) - this.viewDistance - 1;
            int $$14 = Math.min((int)$$3, (int)$$12) - this.viewDistance - 1;
            int $$15 = Math.max((int)$$2, (int)$$11) + this.viewDistance + 1;
            int $$16 = Math.max((int)$$3, (int)$$12) + this.viewDistance + 1;
            for (int $$17 = $$13; $$17 <= $$15; ++$$17) {
                for (int $$18 = $$14; $$18 <= $$16; ++$$18) {
                    boolean $$19 = ChunkMap.isChunkInRange($$17, $$18, $$11, $$12, this.viewDistance);
                    boolean $$20 = ChunkMap.isChunkInRange($$17, $$18, $$2, $$3, this.viewDistance);
                    this.updateChunkTracking($$0, new ChunkPos($$17, $$18), (MutableObject<ClientboundLevelChunkWithLightPacket>)new MutableObject(), $$19, $$20);
                }
            }
        } else {
            for (int $$21 = $$11 - this.viewDistance - 1; $$21 <= $$11 + this.viewDistance + 1; ++$$21) {
                for (int $$22 = $$12 - this.viewDistance - 1; $$22 <= $$12 + this.viewDistance + 1; ++$$22) {
                    if (!ChunkMap.isChunkInRange($$21, $$22, $$11, $$12, this.viewDistance)) continue;
                    boolean $$23 = true;
                    boolean $$24 = false;
                    this.updateChunkTracking($$0, new ChunkPos($$21, $$22), (MutableObject<ClientboundLevelChunkWithLightPacket>)new MutableObject(), true, false);
                }
            }
            for (int $$25 = $$2 - this.viewDistance - 1; $$25 <= $$2 + this.viewDistance + 1; ++$$25) {
                for (int $$26 = $$3 - this.viewDistance - 1; $$26 <= $$3 + this.viewDistance + 1; ++$$26) {
                    if (!ChunkMap.isChunkInRange($$25, $$26, $$2, $$3, this.viewDistance)) continue;
                    boolean $$27 = false;
                    boolean $$28 = true;
                    this.updateChunkTracking($$0, new ChunkPos($$25, $$26), (MutableObject<ClientboundLevelChunkWithLightPacket>)new MutableObject(), false, true);
                }
            }
        }
    }

    @Override
    public List<ServerPlayer> getPlayers(ChunkPos $$0, boolean $$1) {
        Set<ServerPlayer> $$2 = this.playerMap.getPlayers($$0.toLong());
        ImmutableList.Builder $$3 = ImmutableList.builder();
        for (ServerPlayer $$4 : $$2) {
            SectionPos $$5 = $$4.getLastSectionPos();
            if ((!$$1 || !ChunkMap.isChunkOnRangeBorder($$0.x, $$0.z, $$5.x(), $$5.z(), this.viewDistance)) && ($$1 || !ChunkMap.isChunkInRange($$0.x, $$0.z, $$5.x(), $$5.z(), this.viewDistance))) continue;
            $$3.add((Object)$$4);
        }
        return $$3.build();
    }

    protected void addEntity(Entity $$0) {
        if ($$0 instanceof EnderDragonPart) {
            return;
        }
        EntityType<?> $$1 = $$0.getType();
        int $$2 = $$1.clientTrackingRange() * 16;
        if ($$2 == 0) {
            return;
        }
        int $$3 = $$1.updateInterval();
        if (this.entityMap.containsKey($$0.getId())) {
            throw Util.pauseInIde(new IllegalStateException("Entity is already tracked!"));
        }
        TrackedEntity $$4 = new TrackedEntity($$0, $$2, $$3, $$1.trackDeltas());
        this.entityMap.put($$0.getId(), (Object)$$4);
        $$4.updatePlayers(this.level.players());
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$5 = (ServerPlayer)$$0;
            this.updatePlayerStatus($$5, true);
            for (TrackedEntity $$6 : this.entityMap.values()) {
                if ($$6.entity == $$5) continue;
                $$6.updatePlayer($$5);
            }
        }
    }

    protected void removeEntity(Entity $$0) {
        TrackedEntity $$3;
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$1 = (ServerPlayer)$$0;
            this.updatePlayerStatus($$1, false);
            for (TrackedEntity $$2 : this.entityMap.values()) {
                $$2.removePlayer($$1);
            }
        }
        if (($$3 = (TrackedEntity)this.entityMap.remove($$0.getId())) != null) {
            $$3.broadcastRemoved();
        }
    }

    protected void tick() {
        ArrayList $$0 = Lists.newArrayList();
        List<ServerPlayer> $$1 = this.level.players();
        for (TrackedEntity $$2 : this.entityMap.values()) {
            boolean $$5;
            SectionPos $$3 = $$2.lastSectionPos;
            SectionPos $$4 = SectionPos.of($$2.entity);
            boolean bl = $$5 = !Objects.equals((Object)$$3, (Object)$$4);
            if ($$5) {
                $$2.updatePlayers($$1);
                Entity $$6 = $$2.entity;
                if ($$6 instanceof ServerPlayer) {
                    $$0.add((Object)((ServerPlayer)$$6));
                }
                $$2.lastSectionPos = $$4;
            }
            if (!$$5 && !this.distanceManager.inEntityTickingRange($$4.chunk().toLong())) continue;
            $$2.serverEntity.sendChanges();
        }
        if (!$$0.isEmpty()) {
            for (TrackedEntity $$7 : this.entityMap.values()) {
                $$7.updatePlayers((List<ServerPlayer>)$$0);
            }
        }
    }

    public void broadcast(Entity $$0, Packet<?> $$1) {
        TrackedEntity $$2 = (TrackedEntity)this.entityMap.get($$0.getId());
        if ($$2 != null) {
            $$2.broadcast($$1);
        }
    }

    protected void broadcastAndSend(Entity $$0, Packet<?> $$1) {
        TrackedEntity $$2 = (TrackedEntity)this.entityMap.get($$0.getId());
        if ($$2 != null) {
            $$2.broadcastAndSend($$1);
        }
    }

    public void resendChunk(ChunkAccess $$0) {
        LevelChunk $$4;
        ChunkPos $$1 = $$0.getPos();
        if ($$0 instanceof LevelChunk) {
            LevelChunk $$2;
            LevelChunk $$3 = $$2 = (LevelChunk)$$0;
        } else {
            $$4 = this.level.getChunk($$1.x, $$1.z);
        }
        MutableObject $$5 = new MutableObject();
        for (ServerPlayer $$6 : this.getPlayers($$1, false)) {
            if ($$5.getValue() == null) {
                $$5.setValue((Object)new ClientboundLevelChunkWithLightPacket($$4, this.lightEngine, null, null, true));
            }
            $$6.trackChunk($$1, (Packet)$$5.getValue());
        }
    }

    private void playerLoadedChunk(ServerPlayer $$0, MutableObject<ClientboundLevelChunkWithLightPacket> $$1, LevelChunk $$2) {
        if ($$1.getValue() == null) {
            $$1.setValue((Object)new ClientboundLevelChunkWithLightPacket($$2, this.lightEngine, null, null, true));
        }
        $$0.trackChunk($$2.getPos(), (Packet)$$1.getValue());
        DebugPackets.sendPoiPacketsForChunk(this.level, $$2.getPos());
        ArrayList $$3 = Lists.newArrayList();
        ArrayList $$4 = Lists.newArrayList();
        for (TrackedEntity $$5 : this.entityMap.values()) {
            Entity $$6 = $$5.entity;
            if ($$6 == $$0 || !$$6.chunkPosition().equals($$2.getPos())) continue;
            $$5.updatePlayer($$0);
            if ($$6 instanceof Mob && ((Mob)$$6).getLeashHolder() != null) {
                $$3.add((Object)$$6);
            }
            if ($$6.getPassengers().isEmpty()) continue;
            $$4.add((Object)$$6);
        }
        if (!$$3.isEmpty()) {
            for (Entity $$7 : $$3) {
                $$0.connection.send(new ClientboundSetEntityLinkPacket($$7, ((Mob)$$7).getLeashHolder()));
            }
        }
        if (!$$4.isEmpty()) {
            for (Entity $$8 : $$4) {
                $$0.connection.send(new ClientboundSetPassengersPacket($$8));
            }
        }
    }

    protected PoiManager getPoiManager() {
        return this.poiManager;
    }

    public String getStorageName() {
        return this.storageName;
    }

    void onFullChunkStatusChange(ChunkPos $$0, ChunkHolder.FullChunkStatus $$1) {
        this.chunkStatusListener.onChunkStatusChange($$0, $$1);
    }

    class DistanceManager
    extends net.minecraft.server.level.DistanceManager {
        protected DistanceManager(Executor $$0, Executor $$1) {
            super($$0, $$1);
        }

        @Override
        protected boolean isChunkToRemove(long $$0) {
            return ChunkMap.this.toDrop.contains($$0);
        }

        @Override
        @Nullable
        protected ChunkHolder getChunk(long $$0) {
            return ChunkMap.this.getUpdatingChunkIfPresent($$0);
        }

        @Override
        @Nullable
        protected ChunkHolder updateChunkScheduling(long $$0, int $$1, @Nullable ChunkHolder $$2, int $$3) {
            return ChunkMap.this.updateChunkScheduling($$0, $$1, $$2, $$3);
        }
    }

    class TrackedEntity {
        final ServerEntity serverEntity;
        final Entity entity;
        private final int range;
        SectionPos lastSectionPos;
        private final Set<ServerPlayerConnection> seenBy = Sets.newIdentityHashSet();

        public TrackedEntity(Entity $$0, int $$1, int $$2, boolean $$3) {
            this.serverEntity = new ServerEntity(ChunkMap.this.level, $$0, $$2, $$3, this::broadcast);
            this.entity = $$0;
            this.range = $$1;
            this.lastSectionPos = SectionPos.of($$0);
        }

        public boolean equals(Object $$0) {
            if ($$0 instanceof TrackedEntity) {
                return ((TrackedEntity)$$0).entity.getId() == this.entity.getId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getId();
        }

        public void broadcast(Packet<?> $$0) {
            for (ServerPlayerConnection $$1 : this.seenBy) {
                $$1.send($$0);
            }
        }

        public void broadcastAndSend(Packet<?> $$0) {
            this.broadcast($$0);
            if (this.entity instanceof ServerPlayer) {
                ((ServerPlayer)this.entity).connection.send($$0);
            }
        }

        public void broadcastRemoved() {
            for (ServerPlayerConnection $$0 : this.seenBy) {
                this.serverEntity.removePairing($$0.getPlayer());
            }
        }

        public void removePlayer(ServerPlayer $$0) {
            if (this.seenBy.remove((Object)$$0.connection)) {
                this.serverEntity.removePairing($$0);
            }
        }

        public void updatePlayer(ServerPlayer $$0) {
            boolean $$5;
            if ($$0 == this.entity) {
                return;
            }
            Vec3 $$1 = $$0.position().subtract(this.entity.position());
            double $$3 = $$1.x * $$1.x + $$1.z * $$1.z;
            double $$2 = Math.min((int)this.getEffectiveRange(), (int)((ChunkMap.this.viewDistance - 1) * 16));
            double $$4 = $$2 * $$2;
            boolean bl = $$5 = $$3 <= $$4 && this.entity.broadcastToPlayer($$0);
            if ($$5) {
                if (this.seenBy.add((Object)$$0.connection)) {
                    this.serverEntity.addPairing($$0);
                }
            } else if (this.seenBy.remove((Object)$$0.connection)) {
                this.serverEntity.removePairing($$0);
            }
        }

        private int scaledRange(int $$0) {
            return ChunkMap.this.level.getServer().getScaledTrackingDistance($$0);
        }

        private int getEffectiveRange() {
            int $$0 = this.range;
            for (Entity $$1 : this.entity.getIndirectPassengers()) {
                int $$2 = $$1.getType().clientTrackingRange() * 16;
                if ($$2 <= $$0) continue;
                $$0 = $$2;
            }
            return this.scaledRange($$0);
        }

        public void updatePlayers(List<ServerPlayer> $$0) {
            for (ServerPlayer $$1 : $$0) {
                this.updatePlayer($$1);
            }
        }
    }
}