/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.IntStream
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;

public class ChunkTaskPriorityQueue<T> {
    public static final int PRIORITY_LEVEL_COUNT = ChunkMap.MAX_CHUNK_DISTANCE + 2;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> taskQueue = (List)IntStream.range((int)0, (int)PRIORITY_LEVEL_COUNT).mapToObj($$0 -> new Long2ObjectLinkedOpenHashMap()).collect(Collectors.toList());
    private volatile int firstQueue = PRIORITY_LEVEL_COUNT;
    private final String name;
    private final LongSet acquired = new LongOpenHashSet();
    private final int maxTasks;

    public ChunkTaskPriorityQueue(String $$02, int $$1) {
        this.name = $$02;
        this.maxTasks = $$1;
    }

    protected void resortChunkTasks(int $$02, ChunkPos $$1, int $$2) {
        if ($$02 >= PRIORITY_LEVEL_COUNT) {
            return;
        }
        Long2ObjectLinkedOpenHashMap $$3 = (Long2ObjectLinkedOpenHashMap)this.taskQueue.get($$02);
        List $$4 = (List)$$3.remove($$1.toLong());
        if ($$02 == this.firstQueue) {
            while (this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty()) {
                ++this.firstQueue;
            }
        }
        if ($$4 != null && !$$4.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.taskQueue.get($$2)).computeIfAbsent($$1.toLong(), $$0 -> Lists.newArrayList())).addAll((Collection)$$4);
            this.firstQueue = Math.min((int)this.firstQueue, (int)$$2);
        }
    }

    protected void submit(Optional<T> $$02, long $$1, int $$2) {
        ((List)((Long2ObjectLinkedOpenHashMap)this.taskQueue.get($$2)).computeIfAbsent($$1, $$0 -> Lists.newArrayList())).add($$02);
        this.firstQueue = Math.min((int)this.firstQueue, (int)$$2);
    }

    protected void release(long $$02, boolean $$1) {
        for (Long2ObjectLinkedOpenHashMap $$2 : this.taskQueue) {
            List $$3 = (List)$$2.get($$02);
            if ($$3 == null) continue;
            if ($$1) {
                $$3.clear();
            } else {
                $$3.removeIf($$0 -> !$$0.isPresent());
            }
            if (!$$3.isEmpty()) continue;
            $$2.remove($$02);
        }
        while (this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty()) {
            ++this.firstQueue;
        }
        this.acquired.remove($$02);
    }

    private Runnable acquire(long $$0) {
        return () -> this.acquired.add($$0);
    }

    @Nullable
    public Stream<Either<T, Runnable>> pop() {
        if (this.acquired.size() >= this.maxTasks) {
            return null;
        }
        if (this.hasWork()) {
            int $$0 = this.firstQueue;
            Long2ObjectLinkedOpenHashMap $$12 = (Long2ObjectLinkedOpenHashMap)this.taskQueue.get($$0);
            long $$2 = $$12.firstLongKey();
            List $$3 = (List)$$12.removeFirst();
            while (this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty()) {
                ++this.firstQueue;
            }
            return $$3.stream().map($$1 -> (Either)$$1.map(Either::left).orElseGet(() -> Either.right((Object)this.acquire($$2))));
        }
        return null;
    }

    public boolean hasWork() {
        return this.firstQueue < PRIORITY_LEVEL_COUNT;
    }

    public String toString() {
        return this.name + " " + this.firstQueue + "...";
    }

    @VisibleForTesting
    LongSet getAcquired() {
        return new LongOpenHashSet((LongCollection)this.acquired);
    }
}