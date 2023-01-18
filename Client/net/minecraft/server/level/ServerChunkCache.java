/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  java.io.File
 *  java.io.IOException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;

public class ServerChunkCache
extends ChunkSource {
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
    private final DistanceManager distanceManager;
    final ServerLevel level;
    final Thread mainThread;
    final ThreadedLevelLightEngine lightEngine;
    private final MainThreadExecutor mainThreadProcessor;
    public final ChunkMap chunkMap;
    private final DimensionDataStorage dataStorage;
    private long lastInhabitedUpdate;
    private boolean spawnEnemies = true;
    private boolean spawnFriendlies = true;
    private static final int CACHE_SIZE = 4;
    private final long[] lastChunkPos = new long[4];
    private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
    private final ChunkAccess[] lastChunk = new ChunkAccess[4];
    @Nullable
    @VisibleForDebug
    private NaturalSpawner.SpawnState lastSpawnState;

    public ServerChunkCache(ServerLevel $$0, LevelStorageSource.LevelStorageAccess $$1, DataFixer $$2, StructureTemplateManager $$3, Executor $$4, ChunkGenerator $$5, int $$6, int $$7, boolean $$8, ChunkProgressListener $$9, ChunkStatusUpdateListener $$10, Supplier<DimensionDataStorage> $$11) {
        this.level = $$0;
        this.mainThreadProcessor = new MainThreadExecutor($$0);
        this.mainThread = Thread.currentThread();
        File $$12 = $$1.getDimensionPath($$0.dimension()).resolve("data").toFile();
        $$12.mkdirs();
        this.dataStorage = new DimensionDataStorage($$12, $$2);
        this.chunkMap = new ChunkMap($$0, $$1, $$2, $$3, $$4, this.mainThreadProcessor, this, $$5, $$9, $$10, $$11, $$6, $$8);
        this.lightEngine = this.chunkMap.getLightEngine();
        this.distanceManager = this.chunkMap.getDistanceManager();
        this.distanceManager.updateSimulationDistance($$7);
        this.clearCache();
    }

    @Override
    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private ChunkHolder getVisibleChunkIfPresent(long $$0) {
        return this.chunkMap.getVisibleChunkIfPresent($$0);
    }

    public int getTickingGenerated() {
        return this.chunkMap.getTickingGenerated();
    }

    private void storeInCache(long $$0, ChunkAccess $$1, ChunkStatus $$2) {
        for (int $$3 = 3; $$3 > 0; --$$3) {
            this.lastChunkPos[$$3] = this.lastChunkPos[$$3 - 1];
            this.lastChunkStatus[$$3] = this.lastChunkStatus[$$3 - 1];
            this.lastChunk[$$3] = this.lastChunk[$$3 - 1];
        }
        this.lastChunkPos[0] = $$0;
        this.lastChunkStatus[0] = $$2;
        this.lastChunk[0] = $$1;
    }

    @Override
    @Nullable
    public ChunkAccess getChunk(int $$02, int $$12, ChunkStatus $$2, boolean $$3) {
        if (Thread.currentThread() != this.mainThread) {
            return (ChunkAccess)CompletableFuture.supplyAsync(() -> this.getChunk($$02, $$12, $$2, $$3), (Executor)this.mainThreadProcessor).join();
        }
        ProfilerFiller $$4 = this.level.getProfiler();
        $$4.incrementCounter("getChunk");
        long $$5 = ChunkPos.asLong($$02, $$12);
        for (int $$6 = 0; $$6 < 4; ++$$6) {
            ChunkAccess $$7;
            if ($$5 != this.lastChunkPos[$$6] || $$2 != this.lastChunkStatus[$$6] || ($$7 = this.lastChunk[$$6]) == null && $$3) continue;
            return $$7;
        }
        $$4.incrementCounter("getChunkCacheMiss");
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$8 = this.getChunkFutureMainThread($$02, $$12, $$2, $$3);
        this.mainThreadProcessor.managedBlock(() -> $$8.isDone());
        ChunkAccess $$9 = (ChunkAccess)((Either)$$8.join()).map($$0 -> $$0, $$1 -> {
            if ($$3) {
                throw Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + $$1));
            }
            return null;
        });
        this.storeInCache($$5, $$9, $$2);
        return $$9;
    }

    @Override
    @Nullable
    public LevelChunk getChunkNow(int $$0, int $$1) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        }
        this.level.getProfiler().incrementCounter("getChunkNow");
        long $$2 = ChunkPos.asLong($$0, $$1);
        for (int $$3 = 0; $$3 < 4; ++$$3) {
            if ($$2 != this.lastChunkPos[$$3] || this.lastChunkStatus[$$3] != ChunkStatus.FULL) continue;
            ChunkAccess $$4 = this.lastChunk[$$3];
            return $$4 instanceof LevelChunk ? (LevelChunk)$$4 : null;
        }
        ChunkHolder $$5 = this.getVisibleChunkIfPresent($$2);
        if ($$5 == null) {
            return null;
        }
        Either $$6 = (Either)$$5.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
        if ($$6 == null) {
            return null;
        }
        ChunkAccess $$7 = (ChunkAccess)$$6.left().orElse(null);
        if ($$7 != null) {
            this.storeInCache($$2, $$7, ChunkStatus.FULL);
            if ($$7 instanceof LevelChunk) {
                return (LevelChunk)$$7;
            }
        }
        return null;
    }

    private void clearCache() {
        Arrays.fill((long[])this.lastChunkPos, (long)ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill((Object[])this.lastChunkStatus, null);
        Arrays.fill((Object[])this.lastChunk, null);
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int $$02, int $$1, ChunkStatus $$2, boolean $$3) {
        CompletableFuture $$6;
        boolean $$4;
        boolean bl = $$4 = Thread.currentThread() == this.mainThread;
        if ($$4) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> $$5 = this.getChunkFutureMainThread($$02, $$1, $$2, $$3);
            this.mainThreadProcessor.managedBlock(() -> $$5.isDone());
        } else {
            $$6 = CompletableFuture.supplyAsync(() -> this.getChunkFutureMainThread($$02, $$1, $$2, $$3), (Executor)this.mainThreadProcessor).thenCompose($$0 -> $$0);
        }
        return $$6;
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int $$0, int $$1, ChunkStatus $$2, boolean $$3) {
        ChunkPos $$4 = new ChunkPos($$0, $$1);
        long $$5 = $$4.toLong();
        int $$6 = 33 + ChunkStatus.getDistance($$2);
        ChunkHolder $$7 = this.getVisibleChunkIfPresent($$5);
        if ($$3) {
            this.distanceManager.addTicket(TicketType.UNKNOWN, $$4, $$6, $$4);
            if (this.chunkAbsent($$7, $$6)) {
                ProfilerFiller $$8 = this.level.getProfiler();
                $$8.push("chunkLoad");
                this.runDistanceManagerUpdates();
                $$7 = this.getVisibleChunkIfPresent($$5);
                $$8.pop();
                if (this.chunkAbsent($$7, $$6)) {
                    throw Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        if (this.chunkAbsent($$7, $$6)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return $$7.getOrScheduleFuture($$2, this.chunkMap);
    }

    private boolean chunkAbsent(@Nullable ChunkHolder $$0, int $$1) {
        return $$0 == null || $$0.getTicketLevel() > $$1;
    }

    @Override
    public boolean hasChunk(int $$0, int $$1) {
        int $$3;
        ChunkHolder $$2 = this.getVisibleChunkIfPresent(new ChunkPos($$0, $$1).toLong());
        return !this.chunkAbsent($$2, $$3 = 33 + ChunkStatus.getDistance(ChunkStatus.FULL));
    }

    @Override
    public BlockGetter getChunkForLighting(int $$0, int $$1) {
        long $$2 = ChunkPos.asLong($$0, $$1);
        ChunkHolder $$3 = this.getVisibleChunkIfPresent($$2);
        if ($$3 == null) {
            return null;
        }
        int $$4 = CHUNK_STATUSES.size() - 1;
        while (true) {
            ChunkStatus $$5;
            Optional $$6;
            if (($$6 = ((Either)$$3.getFutureIfPresentUnchecked($$5 = (ChunkStatus)CHUNK_STATUSES.get($$4)).getNow(ChunkHolder.UNLOADED_CHUNK)).left()).isPresent()) {
                return (BlockGetter)$$6.get();
            }
            if ($$5 == ChunkStatus.LIGHT.getParent()) break;
            --$$4;
        }
        return null;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }

    boolean runDistanceManagerUpdates() {
        boolean $$0 = this.distanceManager.runAllUpdates(this.chunkMap);
        boolean $$1 = this.chunkMap.promoteChunkMap();
        if ($$0 || $$1) {
            this.clearCache();
            return true;
        }
        return false;
    }

    public boolean isPositionTicking(long $$0) {
        ChunkHolder $$1 = this.getVisibleChunkIfPresent($$0);
        if ($$1 == null) {
            return false;
        }
        if (!this.level.shouldTickBlocksAt($$0)) {
            return false;
        }
        Either $$2 = (Either)$$1.getTickingChunkFuture().getNow(null);
        return $$2 != null && $$2.left().isPresent();
    }

    public void save(boolean $$0) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks($$0);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightEngine.close();
        this.chunkMap.close();
    }

    @Override
    public void tick(BooleanSupplier $$0, boolean $$1) {
        this.level.getProfiler().push("purge");
        this.distanceManager.purgeStaleTickets();
        this.runDistanceManagerUpdates();
        this.level.getProfiler().popPush("chunks");
        if ($$1) {
            this.tickChunks();
        }
        this.level.getProfiler().popPush("unload");
        this.chunkMap.tick($$0);
        this.level.getProfiler().pop();
        this.clearCache();
    }

    private void tickChunks() {
        NaturalSpawner.SpawnState $$8;
        long $$02 = this.level.getGameTime();
        long $$1 = $$02 - this.lastInhabitedUpdate;
        this.lastInhabitedUpdate = $$02;
        boolean $$2 = this.level.isDebug();
        if ($$2) {
            this.chunkMap.tick();
            return;
        }
        LevelData $$3 = this.level.getLevelData();
        ProfilerFiller $$4 = this.level.getProfiler();
        $$4.push("pollingChunks");
        int $$5 = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        boolean $$6 = $$3.getGameTime() % 400L == 0L;
        $$4.push("naturalSpawnCount");
        int $$7 = this.distanceManager.getNaturalSpawnChunkCount();
        this.lastSpawnState = $$8 = NaturalSpawner.createState($$7, this.level.getAllEntities(), this::getFullChunk, new LocalMobCapCalculator(this.chunkMap));
        $$4.popPush("filteringLoadedChunks");
        ArrayList $$9 = Lists.newArrayListWithCapacity((int)$$7);
        for (ChunkHolder $$10 : this.chunkMap.getChunks()) {
            LevelChunk $$11 = $$10.getTickingChunk();
            if ($$11 == null) continue;
            $$9.add((Object)new ChunkAndHolder($$11, $$10));
        }
        $$4.popPush("spawnAndTick");
        boolean $$12 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        Collections.shuffle((List)$$9);
        for (ChunkAndHolder $$13 : $$9) {
            LevelChunk $$14 = $$13.chunk;
            ChunkPos $$15 = $$14.getPos();
            if (!this.level.isNaturalSpawningAllowed($$15) || !this.chunkMap.anyPlayerCloseEnoughForSpawning($$15)) continue;
            $$14.incrementInhabitedTime($$1);
            if ($$12 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds($$15)) {
                NaturalSpawner.spawnForChunk(this.level, $$14, $$8, this.spawnFriendlies, this.spawnEnemies, $$6);
            }
            if (!this.level.shouldTickBlocksAt($$15.toLong())) continue;
            this.level.tickChunk($$14, $$5);
        }
        $$4.popPush("customSpawners");
        if ($$12) {
            this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
        }
        $$4.popPush("broadcast");
        $$9.forEach($$0 -> $$0.holder.broadcastChanges($$0.chunk));
        $$4.pop();
        $$4.pop();
        this.chunkMap.tick();
    }

    private void getFullChunk(long $$0, Consumer<LevelChunk> $$1) {
        ChunkHolder $$2 = this.getVisibleChunkIfPresent($$0);
        if ($$2 != null) {
            ((Either)$$2.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)).left().ifPresent($$1);
        }
    }

    @Override
    public String gatherStats() {
        return Integer.toString((int)this.getLoadedChunksCount());
    }

    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }

    public ChunkGenerator getGenerator() {
        return this.chunkMap.generator();
    }

    public ChunkGeneratorStructureState getGeneratorState() {
        return this.chunkMap.generatorState();
    }

    public RandomState randomState() {
        return this.chunkMap.randomState();
    }

    @Override
    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }

    public void blockChanged(BlockPos $$0) {
        int $$2;
        int $$1 = SectionPos.blockToSectionCoord($$0.getX());
        ChunkHolder $$3 = this.getVisibleChunkIfPresent(ChunkPos.asLong($$1, $$2 = SectionPos.blockToSectionCoord($$0.getZ())));
        if ($$3 != null) {
            $$3.blockChanged($$0);
        }
    }

    @Override
    public void onLightUpdate(LightLayer $$0, SectionPos $$1) {
        this.mainThreadProcessor.execute(() -> {
            ChunkHolder $$2 = this.getVisibleChunkIfPresent($$1.chunk().toLong());
            if ($$2 != null) {
                $$2.sectionLightChanged($$0, $$1.y());
            }
        });
    }

    public <T> void addRegionTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        this.distanceManager.addRegionTicket($$0, $$1, $$2, $$3);
    }

    public <T> void removeRegionTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        this.distanceManager.removeRegionTicket($$0, $$1, $$2, $$3);
    }

    @Override
    public void updateChunkForced(ChunkPos $$0, boolean $$1) {
        this.distanceManager.updateChunkForced($$0, $$1);
    }

    public void move(ServerPlayer $$0) {
        if (!$$0.isRemoved()) {
            this.chunkMap.move($$0);
        }
    }

    public void removeEntity(Entity $$0) {
        this.chunkMap.removeEntity($$0);
    }

    public void addEntity(Entity $$0) {
        this.chunkMap.addEntity($$0);
    }

    public void broadcastAndSend(Entity $$0, Packet<?> $$1) {
        this.chunkMap.broadcastAndSend($$0, $$1);
    }

    public void broadcast(Entity $$0, Packet<?> $$1) {
        this.chunkMap.broadcast($$0, $$1);
    }

    public void setViewDistance(int $$0) {
        this.chunkMap.setViewDistance($$0);
    }

    public void setSimulationDistance(int $$0) {
        this.distanceManager.updateSimulationDistance($$0);
    }

    @Override
    public void setSpawnSettings(boolean $$0, boolean $$1) {
        this.spawnEnemies = $$0;
        this.spawnFriendlies = $$1;
    }

    public String getChunkDebugData(ChunkPos $$0) {
        return this.chunkMap.getChunkDebugData($$0);
    }

    public DimensionDataStorage getDataStorage() {
        return this.dataStorage;
    }

    public PoiManager getPoiManager() {
        return this.chunkMap.getPoiManager();
    }

    public ChunkScanAccess chunkScanner() {
        return this.chunkMap.chunkScanner();
    }

    @Nullable
    @VisibleForDebug
    public NaturalSpawner.SpawnState getLastSpawnState() {
        return this.lastSpawnState;
    }

    public void removeTicketsOnClosing() {
        this.distanceManager.removeTicketsOnClosing();
    }

    final class MainThreadExecutor
    extends BlockableEventLoop<Runnable> {
        MainThreadExecutor(Level $$0) {
            super("Chunk source main thread executor for " + $$0.dimension().location());
        }

        @Override
        protected Runnable wrapRunnable(Runnable $$0) {
            return $$0;
        }

        @Override
        protected boolean shouldRun(Runnable $$0) {
            return true;
        }

        @Override
        protected boolean scheduleExecutables() {
            return true;
        }

        @Override
        protected Thread getRunningThread() {
            return ServerChunkCache.this.mainThread;
        }

        @Override
        protected void doRunTask(Runnable $$0) {
            ServerChunkCache.this.level.getProfiler().incrementCounter("runTask");
            super.doRunTask($$0);
        }

        @Override
        protected boolean pollTask() {
            if (ServerChunkCache.this.runDistanceManagerUpdates()) {
                return true;
            }
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
        }
    }

    record ChunkAndHolder(LevelChunk chunk, ChunkHolder holder) {
    }
}