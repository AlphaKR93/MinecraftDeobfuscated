/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMaps
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.lang.Byte
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ChunkTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.TickingTracker;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.slf4j.Logger;

public abstract class DistanceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int ENTITY_TICKING_RANGE = 2;
    static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    private static final int ENTITY_TICKING_LEVEL_THRESHOLD = 32;
    private static final int BLOCK_TICKING_LEVEL_THRESHOLD = 33;
    final Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
    final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
    private final ChunkTicketTracker ticketTracker = new ChunkTicketTracker();
    private final FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new FixedPlayerDistanceChunkTracker(8);
    private final TickingTracker tickingTicketsTracker = new TickingTracker();
    private final PlayerTicketTracker playerTicketManager = new PlayerTicketTracker(33);
    final Set<ChunkHolder> chunksToUpdateFutures = Sets.newHashSet();
    final ChunkTaskPriorityQueueSorter ticketThrottler;
    final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> ticketThrottlerInput;
    final ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> ticketThrottlerReleaser;
    final LongSet ticketsToRelease = new LongOpenHashSet();
    final Executor mainThreadExecutor;
    private long ticketTickCounter;
    private int simulationDistance = 10;

    protected DistanceManager(Executor $$0, Executor $$1) {
        ChunkTaskPriorityQueueSorter $$3;
        ProcessorHandle<Runnable> $$2 = ProcessorHandle.of("player ticket throttler", arg_0 -> ((Executor)$$1).execute(arg_0));
        this.ticketThrottler = $$3 = new ChunkTaskPriorityQueueSorter((List<ProcessorHandle<?>>)ImmutableList.of($$2), $$0, 4);
        this.ticketThrottlerInput = $$3.getProcessor($$2, true);
        this.ticketThrottlerReleaser = $$3.getReleaseProcessor($$2);
        this.mainThreadExecutor = $$1;
    }

    protected void purgeStaleTickets() {
        ++this.ticketTickCounter;
        ObjectIterator $$0 = this.tickets.long2ObjectEntrySet().fastIterator();
        while ($$0.hasNext()) {
            Long2ObjectMap.Entry $$1 = (Long2ObjectMap.Entry)$$0.next();
            Iterator $$2 = ((SortedArraySet)((Object)$$1.getValue())).iterator();
            boolean $$3 = false;
            while ($$2.hasNext()) {
                Ticket $$4 = (Ticket)$$2.next();
                if (!$$4.timedOut(this.ticketTickCounter)) continue;
                $$2.remove();
                $$3 = true;
                this.tickingTicketsTracker.removeTicket($$1.getLongKey(), $$4);
            }
            if ($$3) {
                this.ticketTracker.update($$1.getLongKey(), DistanceManager.getTicketLevelAt((SortedArraySet)((Object)$$1.getValue())), false);
            }
            if (!((SortedArraySet)((Object)$$1.getValue())).isEmpty()) continue;
            $$0.remove();
        }
    }

    private static int getTicketLevelAt(SortedArraySet<Ticket<?>> $$0) {
        return !$$0.isEmpty() ? $$0.first().getTicketLevel() : ChunkMap.MAX_CHUNK_DISTANCE + 1;
    }

    protected abstract boolean isChunkToRemove(long var1);

    @Nullable
    protected abstract ChunkHolder getChunk(long var1);

    @Nullable
    protected abstract ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean runAllUpdates(ChunkMap $$02) {
        boolean $$2;
        this.naturalSpawnChunkCounter.runAllUpdates();
        this.tickingTicketsTracker.runAllUpdates();
        this.playerTicketManager.runAllUpdates();
        int $$12 = Integer.MAX_VALUE - this.ticketTracker.runDistanceUpdates(Integer.MAX_VALUE);
        boolean bl = $$2 = $$12 != 0;
        if ($$2) {
            // empty if block
        }
        if (!this.chunksToUpdateFutures.isEmpty()) {
            this.chunksToUpdateFutures.forEach($$1 -> $$1.updateFutures($$02, this.mainThreadExecutor));
            this.chunksToUpdateFutures.clear();
            return true;
        }
        if (!this.ticketsToRelease.isEmpty()) {
            LongIterator $$3 = this.ticketsToRelease.iterator();
            while ($$3.hasNext()) {
                long $$4 = $$3.nextLong();
                if (!this.getTickets($$4).stream().anyMatch($$0 -> $$0.getType() == TicketType.PLAYER)) continue;
                ChunkHolder $$5 = $$02.getUpdatingChunkIfPresent($$4);
                if ($$5 == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> $$6 = $$5.getEntityTickingChunkFuture();
                $$6.thenAccept($$1 -> this.mainThreadExecutor.execute(() -> this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {}, $$4, false))));
            }
            this.ticketsToRelease.clear();
        }
        return $$2;
    }

    void addTicket(long $$0, Ticket<?> $$1) {
        SortedArraySet<Ticket<?>> $$2 = this.getTickets($$0);
        int $$3 = DistanceManager.getTicketLevelAt($$2);
        Ticket<?> $$4 = $$2.addOrGet($$1);
        $$4.setCreatedTick(this.ticketTickCounter);
        if ($$1.getTicketLevel() < $$3) {
            this.ticketTracker.update($$0, $$1.getTicketLevel(), true);
        }
    }

    void removeTicket(long $$0, Ticket<?> $$1) {
        SortedArraySet<Ticket<?>> $$2 = this.getTickets($$0);
        if ($$2.remove($$1)) {
            // empty if block
        }
        if ($$2.isEmpty()) {
            this.tickets.remove($$0);
        }
        this.ticketTracker.update($$0, DistanceManager.getTicketLevelAt($$2), false);
    }

    public <T> void addTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        this.addTicket($$1.toLong(), new Ticket<T>($$0, $$2, $$3));
    }

    public <T> void removeTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        Ticket<T> $$4 = new Ticket<T>($$0, $$2, $$3);
        this.removeTicket($$1.toLong(), $$4);
    }

    public <T> void addRegionTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        Ticket<T> $$4 = new Ticket<T>($$0, 33 - $$2, $$3);
        long $$5 = $$1.toLong();
        this.addTicket($$5, $$4);
        this.tickingTicketsTracker.addTicket($$5, $$4);
    }

    public <T> void removeRegionTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        Ticket<T> $$4 = new Ticket<T>($$0, 33 - $$2, $$3);
        long $$5 = $$1.toLong();
        this.removeTicket($$5, $$4);
        this.tickingTicketsTracker.removeTicket($$5, $$4);
    }

    private SortedArraySet<Ticket<?>> getTickets(long $$02) {
        return (SortedArraySet)((Object)this.tickets.computeIfAbsent($$02, $$0 -> SortedArraySet.create(4)));
    }

    protected void updateChunkForced(ChunkPos $$0, boolean $$1) {
        Ticket<ChunkPos> $$2 = new Ticket<ChunkPos>(TicketType.FORCED, 31, $$0);
        long $$3 = $$0.toLong();
        if ($$1) {
            this.addTicket($$3, $$2);
            this.tickingTicketsTracker.addTicket($$3, $$2);
        } else {
            this.removeTicket($$3, $$2);
            this.tickingTicketsTracker.removeTicket($$3, $$2);
        }
    }

    public void addPlayer(SectionPos $$02, ServerPlayer $$1) {
        ChunkPos $$2 = $$02.chunk();
        long $$3 = $$2.toLong();
        ((ObjectSet)this.playersPerChunk.computeIfAbsent($$3, $$0 -> new ObjectOpenHashSet())).add((Object)$$1);
        this.naturalSpawnChunkCounter.update($$3, 0, true);
        this.playerTicketManager.update($$3, 0, true);
        this.tickingTicketsTracker.addTicket(TicketType.PLAYER, $$2, this.getPlayerTicketLevel(), $$2);
    }

    public void removePlayer(SectionPos $$0, ServerPlayer $$1) {
        ChunkPos $$2 = $$0.chunk();
        long $$3 = $$2.toLong();
        ObjectSet $$4 = (ObjectSet)this.playersPerChunk.get($$3);
        $$4.remove((Object)$$1);
        if ($$4.isEmpty()) {
            this.playersPerChunk.remove($$3);
            this.naturalSpawnChunkCounter.update($$3, Integer.MAX_VALUE, false);
            this.playerTicketManager.update($$3, Integer.MAX_VALUE, false);
            this.tickingTicketsTracker.removeTicket(TicketType.PLAYER, $$2, this.getPlayerTicketLevel(), $$2);
        }
    }

    private int getPlayerTicketLevel() {
        return Math.max((int)0, (int)(31 - this.simulationDistance));
    }

    public boolean inEntityTickingRange(long $$0) {
        return this.tickingTicketsTracker.getLevel($$0) < 32;
    }

    public boolean inBlockTickingRange(long $$0) {
        return this.tickingTicketsTracker.getLevel($$0) < 33;
    }

    protected String getTicketDebugString(long $$0) {
        SortedArraySet $$1 = (SortedArraySet)((Object)this.tickets.get($$0));
        if ($$1 == null || $$1.isEmpty()) {
            return "no_ticket";
        }
        return ((Ticket)$$1.first()).toString();
    }

    protected void updatePlayerTickets(int $$0) {
        this.playerTicketManager.updateViewDistance($$0);
    }

    public void updateSimulationDistance(int $$0) {
        if ($$0 != this.simulationDistance) {
            this.simulationDistance = $$0;
            this.tickingTicketsTracker.replacePlayerTicketsLevel(this.getPlayerTicketLevel());
        }
    }

    public int getNaturalSpawnChunkCount() {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.size();
    }

    public boolean hasPlayersNearby(long $$0) {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.containsKey($$0);
    }

    public String getDebugStatus() {
        return this.ticketThrottler.getDebugStatus();
    }

    private void dumpTickets(String $$0) {
        try (FileOutputStream $$1 = new FileOutputStream(new File($$0));){
            for (Long2ObjectMap.Entry $$2 : this.tickets.long2ObjectEntrySet()) {
                ChunkPos $$3 = new ChunkPos($$2.getLongKey());
                Iterator iterator = ((SortedArraySet)((Object)$$2.getValue())).iterator();
                while (iterator.hasNext()) {
                    Ticket $$4 = (Ticket)iterator.next();
                    $$1.write(($$3.x + "\t" + $$3.z + "\t" + $$4.getType() + "\t" + $$4.getTicketLevel() + "\t\n").getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        catch (IOException $$5) {
            LOGGER.error("Failed to dump tickets to {}", (Object)$$0, (Object)$$5);
        }
    }

    @VisibleForTesting
    TickingTracker tickingTracker() {
        return this.tickingTicketsTracker;
    }

    public void removeTicketsOnClosing() {
        ImmutableSet $$0 = ImmutableSet.of(TicketType.UNKNOWN, TicketType.POST_TELEPORT, TicketType.LIGHT);
        ObjectIterator $$1 = this.tickets.long2ObjectEntrySet().fastIterator();
        while ($$1.hasNext()) {
            Long2ObjectMap.Entry $$2 = (Long2ObjectMap.Entry)$$1.next();
            Iterator $$3 = ((SortedArraySet)((Object)$$2.getValue())).iterator();
            boolean $$4 = false;
            while ($$3.hasNext()) {
                Ticket $$5 = (Ticket)$$3.next();
                if ($$0.contains($$5.getType())) continue;
                $$3.remove();
                $$4 = true;
                this.tickingTicketsTracker.removeTicket($$2.getLongKey(), $$5);
            }
            if ($$4) {
                this.ticketTracker.update($$2.getLongKey(), DistanceManager.getTicketLevelAt((SortedArraySet)((Object)$$2.getValue())), false);
            }
            if (!((SortedArraySet)((Object)$$2.getValue())).isEmpty()) continue;
            $$1.remove();
        }
    }

    public boolean hasTickets() {
        return !this.tickets.isEmpty();
    }

    class ChunkTicketTracker
    extends ChunkTracker {
        public ChunkTicketTracker() {
            super(ChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
        }

        @Override
        protected int getLevelFromSource(long $$0) {
            SortedArraySet $$1 = (SortedArraySet)((Object)DistanceManager.this.tickets.get($$0));
            if ($$1 == null) {
                return Integer.MAX_VALUE;
            }
            if ($$1.isEmpty()) {
                return Integer.MAX_VALUE;
            }
            return ((Ticket)$$1.first()).getTicketLevel();
        }

        @Override
        protected int getLevel(long $$0) {
            ChunkHolder $$1;
            if (!DistanceManager.this.isChunkToRemove($$0) && ($$1 = DistanceManager.this.getChunk($$0)) != null) {
                return $$1.getTicketLevel();
            }
            return ChunkMap.MAX_CHUNK_DISTANCE + 1;
        }

        @Override
        protected void setLevel(long $$0, int $$1) {
            int $$3;
            ChunkHolder $$2 = DistanceManager.this.getChunk($$0);
            int n = $$3 = $$2 == null ? ChunkMap.MAX_CHUNK_DISTANCE + 1 : $$2.getTicketLevel();
            if ($$3 == $$1) {
                return;
            }
            if (($$2 = DistanceManager.this.updateChunkScheduling($$0, $$1, $$2, $$3)) != null) {
                DistanceManager.this.chunksToUpdateFutures.add((Object)$$2);
            }
        }

        public int runDistanceUpdates(int $$0) {
            return this.runUpdates($$0);
        }
    }

    class FixedPlayerDistanceChunkTracker
    extends ChunkTracker {
        protected final Long2ByteMap chunks;
        protected final int maxDistance;

        protected FixedPlayerDistanceChunkTracker(int $$0) {
            super($$0 + 2, 16, 256);
            this.chunks = new Long2ByteOpenHashMap();
            this.maxDistance = $$0;
            this.chunks.defaultReturnValue((byte)($$0 + 2));
        }

        @Override
        protected int getLevel(long $$0) {
            return this.chunks.get($$0);
        }

        @Override
        protected void setLevel(long $$0, int $$1) {
            byte $$3;
            if ($$1 > this.maxDistance) {
                byte $$2 = this.chunks.remove($$0);
            } else {
                $$3 = this.chunks.put($$0, (byte)$$1);
            }
            this.onLevelChange($$0, $$3, $$1);
        }

        protected void onLevelChange(long $$0, int $$1, int $$2) {
        }

        @Override
        protected int getLevelFromSource(long $$0) {
            return this.havePlayer($$0) ? 0 : Integer.MAX_VALUE;
        }

        private boolean havePlayer(long $$0) {
            ObjectSet $$1 = (ObjectSet)DistanceManager.this.playersPerChunk.get($$0);
            return $$1 != null && !$$1.isEmpty();
        }

        public void runAllUpdates() {
            this.runUpdates(Integer.MAX_VALUE);
        }

        private void dumpChunks(String $$0) {
            try (FileOutputStream $$1 = new FileOutputStream(new File($$0));){
                for (Long2ByteMap.Entry $$2 : this.chunks.long2ByteEntrySet()) {
                    ChunkPos $$3 = new ChunkPos($$2.getLongKey());
                    String $$4 = Byte.toString((byte)$$2.getByteValue());
                    $$1.write(($$3.x + "\t" + $$3.z + "\t" + $$4 + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            catch (IOException $$5) {
                LOGGER.error("Failed to dump chunks to {}", (Object)$$0, (Object)$$5);
            }
        }
    }

    class PlayerTicketTracker
    extends FixedPlayerDistanceChunkTracker {
        private int viewDistance;
        private final Long2IntMap queueLevels;
        private final LongSet toUpdate;

        protected PlayerTicketTracker(int $$0) {
            super($$0);
            this.queueLevels = Long2IntMaps.synchronize((Long2IntMap)new Long2IntOpenHashMap());
            this.toUpdate = new LongOpenHashSet();
            this.viewDistance = 0;
            this.queueLevels.defaultReturnValue($$0 + 2);
        }

        @Override
        protected void onLevelChange(long $$0, int $$1, int $$2) {
            this.toUpdate.add($$0);
        }

        public void updateViewDistance(int $$0) {
            for (Long2ByteMap.Entry $$1 : this.chunks.long2ByteEntrySet()) {
                byte $$2 = $$1.getByteValue();
                long $$3 = $$1.getLongKey();
                this.onLevelChange($$3, $$2, this.haveTicketFor($$2), $$2 <= $$0 - 2);
            }
            this.viewDistance = $$0;
        }

        private void onLevelChange(long $$0, int $$1, boolean $$2, boolean $$3) {
            if ($$2 != $$3) {
                Ticket<ChunkPos> $$4 = new Ticket<ChunkPos>(TicketType.PLAYER, PLAYER_TICKET_LEVEL, new ChunkPos($$0));
                if ($$3) {
                    DistanceManager.this.ticketThrottlerInput.tell(ChunkTaskPriorityQueueSorter.message(() -> DistanceManager.this.mainThreadExecutor.execute(() -> {
                        if (this.haveTicketFor(this.getLevel($$0))) {
                            DistanceManager.this.addTicket($$0, $$4);
                            DistanceManager.this.ticketsToRelease.add($$0);
                        } else {
                            DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {}, $$0, false));
                        }
                    }), $$0, () -> $$1));
                } else {
                    DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> DistanceManager.this.mainThreadExecutor.execute(() -> DistanceManager.this.removeTicket($$0, $$4)), $$0, true));
                }
            }
        }

        @Override
        public void runAllUpdates() {
            super.runAllUpdates();
            if (!this.toUpdate.isEmpty()) {
                LongIterator $$0 = this.toUpdate.iterator();
                while ($$0.hasNext()) {
                    int $$3;
                    long $$12 = $$0.nextLong();
                    int $$2 = this.queueLevels.get($$12);
                    if ($$2 == ($$3 = this.getLevel($$12))) continue;
                    DistanceManager.this.ticketThrottler.onLevelChange(new ChunkPos($$12), () -> this.queueLevels.get($$12), $$3, $$1 -> {
                        if ($$1 >= this.queueLevels.defaultReturnValue()) {
                            this.queueLevels.remove($$12);
                        } else {
                            this.queueLevels.put($$12, $$1);
                        }
                    });
                    this.onLevelChange($$12, $$3, this.haveTicketFor($$2), this.haveTicketFor($$3));
                }
                this.toUpdate.clear();
            }
        }

        private boolean haveTicketFor(int $$0) {
            return $$0 <= this.viewDistance - 2;
        }
    }
}