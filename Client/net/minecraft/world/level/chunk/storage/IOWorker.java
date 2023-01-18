/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.nio.file.Path
 *  java.util.BitSet
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicBoolean
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.slf4j.Logger;

public class IOWorker
implements ChunkScanAccess,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;
    private final RegionFileStorage storage;
    private final Map<ChunkPos, PendingStore> pendingWrites = Maps.newLinkedHashMap();
    private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
    private static final int REGION_CACHE_SIZE = 1024;

    protected IOWorker(Path $$0, boolean $$1, String $$2) {
        this.storage = new RegionFileStorage($$0, $$1);
        this.mailbox = new ProcessorMailbox<StrictQueue.IntRunnable>(new StrictQueue.FixedPriorityQueue(Priority.values().length), (Executor)Util.ioPool(), "IOWorker-" + $$2);
    }

    public boolean isOldChunkAround(ChunkPos $$0, int $$1) {
        ChunkPos $$2 = new ChunkPos($$0.x - $$1, $$0.z - $$1);
        ChunkPos $$3 = new ChunkPos($$0.x + $$1, $$0.z + $$1);
        for (int $$4 = $$2.getRegionX(); $$4 <= $$3.getRegionX(); ++$$4) {
            for (int $$5 = $$2.getRegionZ(); $$5 <= $$3.getRegionZ(); ++$$5) {
                BitSet $$6 = (BitSet)this.getOrCreateOldDataForRegion($$4, $$5).join();
                if ($$6.isEmpty()) continue;
                ChunkPos $$7 = ChunkPos.minFromRegion($$4, $$5);
                int $$8 = Math.max((int)($$2.x - $$7.x), (int)0);
                int $$9 = Math.max((int)($$2.z - $$7.z), (int)0);
                int $$10 = Math.min((int)($$3.x - $$7.x), (int)31);
                int $$11 = Math.min((int)($$3.z - $$7.z), (int)31);
                for (int $$12 = $$8; $$12 <= $$10; ++$$12) {
                    for (int $$13 = $$9; $$13 <= $$11; ++$$13) {
                        int $$14 = $$13 * 32 + $$12;
                        if (!$$6.get($$14)) continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int $$0, int $$1) {
        long $$2 = ChunkPos.asLong($$0, $$1);
        Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> long2ObjectLinkedOpenHashMap = this.regionCacheForBlender;
        synchronized (long2ObjectLinkedOpenHashMap) {
            CompletableFuture<BitSet> $$3 = (CompletableFuture<BitSet>)this.regionCacheForBlender.getAndMoveToFirst($$2);
            if ($$3 == null) {
                $$3 = this.createOldDataForRegion($$0, $$1);
                this.regionCacheForBlender.putAndMoveToFirst($$2, $$3);
                if (this.regionCacheForBlender.size() > 1024) {
                    this.regionCacheForBlender.removeLast();
                }
            }
            return $$3;
        }
    }

    private CompletableFuture<BitSet> createOldDataForRegion(int $$0, int $$1) {
        return CompletableFuture.supplyAsync(() -> {
            ChunkPos $$2 = ChunkPos.minFromRegion($$0, $$1);
            ChunkPos $$3 = ChunkPos.maxFromRegion($$0, $$1);
            BitSet $$4 = new BitSet();
            ChunkPos.rangeClosed($$2, $$3).forEach($$1 -> {
                CompoundTag $$5;
                CollectFields $$2 = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data"));
                try {
                    this.scanChunk((ChunkPos)$$1, $$2).join();
                }
                catch (Exception $$3) {
                    LOGGER.warn("Failed to scan chunk {}", $$1, (Object)$$3);
                    return;
                }
                Tag $$4 = $$2.getResult();
                if ($$4 instanceof CompoundTag && this.isOldChunk($$5 = (CompoundTag)$$4)) {
                    int $$6 = $$1.getRegionLocalZ() * 32 + $$1.getRegionLocalX();
                    $$4.set($$6);
                }
            });
            return $$4;
        }, (Executor)Util.backgroundExecutor());
    }

    private boolean isOldChunk(CompoundTag $$0) {
        if (!$$0.contains("DataVersion", 99) || $$0.getInt("DataVersion") < 3088) {
            return true;
        }
        return $$0.contains("blending_data", 10);
    }

    public CompletableFuture<Void> store(ChunkPos $$0, @Nullable CompoundTag $$1) {
        return this.submitTask(() -> {
            PendingStore $$2 = (PendingStore)this.pendingWrites.computeIfAbsent((Object)$$0, $$1 -> new PendingStore($$1));
            $$2.data = $$1;
            return Either.left($$2.result);
        }).thenCompose(Function.identity());
    }

    public CompletableFuture<Optional<CompoundTag>> loadAsync(ChunkPos $$0) {
        return this.submitTask(() -> {
            PendingStore $$1 = (PendingStore)this.pendingWrites.get((Object)$$0);
            if ($$1 != null) {
                return Either.left((Object)Optional.ofNullable((Object)$$1.data));
            }
            try {
                CompoundTag $$2 = this.storage.read($$0);
                return Either.left((Object)Optional.ofNullable((Object)$$2));
            }
            catch (Exception $$3) {
                LOGGER.warn("Failed to read chunk {}", (Object)$$0, (Object)$$3);
                return Either.right((Object)((Object)$$3));
            }
        });
    }

    public CompletableFuture<Void> synchronize(boolean $$02) {
        CompletableFuture $$1 = this.submitTask(() -> Either.left((Object)CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])this.pendingWrites.values().stream().map($$0 -> $$0.result).toArray(CompletableFuture[]::new))))).thenCompose(Function.identity());
        if ($$02) {
            return $$1.thenCompose($$0 -> this.submitTask(() -> {
                try {
                    this.storage.flush();
                    return Either.left(null);
                }
                catch (Exception $$0) {
                    LOGGER.warn("Failed to synchronize chunks", (Throwable)$$0);
                    return Either.right((Object)$$0);
                }
            }));
        }
        return $$1.thenCompose($$0 -> this.submitTask(() -> Either.left(null)));
    }

    @Override
    public CompletableFuture<Void> scanChunk(ChunkPos $$0, StreamTagVisitor $$1) {
        return this.submitTask(() -> {
            try {
                PendingStore $$2 = (PendingStore)this.pendingWrites.get((Object)$$0);
                if ($$2 != null) {
                    if ($$2.data != null) {
                        $$2.data.acceptAsRoot($$1);
                    }
                } else {
                    this.storage.scanChunk($$0, $$1);
                }
                return Either.left(null);
            }
            catch (Exception $$3) {
                LOGGER.warn("Failed to bulk scan chunk {}", (Object)$$0, (Object)$$3);
                return Either.right((Object)((Object)$$3));
            }
        });
    }

    private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> $$0) {
        return this.mailbox.askEither($$1 -> new StrictQueue.IntRunnable(Priority.FOREGROUND.ordinal(), () -> this.lambda$submitTask$13($$1, (Supplier)$$0)));
    }

    private void storePendingChunk() {
        if (this.pendingWrites.isEmpty()) {
            return;
        }
        Iterator $$0 = this.pendingWrites.entrySet().iterator();
        Map.Entry $$1 = (Map.Entry)$$0.next();
        $$0.remove();
        this.runStore((ChunkPos)$$1.getKey(), (PendingStore)$$1.getValue());
        this.tellStorePending();
    }

    private void tellStorePending() {
        this.mailbox.tell(new StrictQueue.IntRunnable(Priority.BACKGROUND.ordinal(), this::storePendingChunk));
    }

    private void runStore(ChunkPos $$0, PendingStore $$1) {
        try {
            this.storage.write($$0, $$1.data);
            $$1.result.complete(null);
        }
        catch (Exception $$2) {
            LOGGER.error("Failed to store chunk {}", (Object)$$0, (Object)$$2);
            $$1.result.completeExceptionally((Throwable)$$2);
        }
    }

    public void close() throws IOException {
        if (!this.shutdownRequested.compareAndSet(false, true)) {
            return;
        }
        this.mailbox.ask($$0 -> new StrictQueue.IntRunnable(Priority.SHUTDOWN.ordinal(), () -> $$0.tell(Unit.INSTANCE))).join();
        this.mailbox.close();
        try {
            this.storage.close();
        }
        catch (Exception $$02) {
            LOGGER.error("Failed to close storage", (Throwable)$$02);
        }
    }

    private /* synthetic */ void lambda$submitTask$13(ProcessorHandle $$0, Supplier $$1) {
        if (!this.shutdownRequested.get()) {
            $$0.tell((Either)$$1.get());
        }
        this.tellStorePending();
    }

    static enum Priority {
        FOREGROUND,
        BACKGROUND,
        SHUTDOWN;

    }

    static class PendingStore {
        @Nullable
        CompoundTag data;
        final CompletableFuture<Void> result = new CompletableFuture();

        public PendingStore(@Nullable CompoundTag $$0) {
            this.data = $$0;
        }
    }
}