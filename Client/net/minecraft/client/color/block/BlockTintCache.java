/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.ThreadLocal
 *  java.util.Arrays
 *  java.util.concurrent.locks.ReentrantReadWriteLock
 *  java.util.function.ToIntFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public class BlockTintCache {
    private static final int MAX_CACHE_ENTRIES = 256;
    private final ThreadLocal<LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(LatestCacheInfo::new);
    private final Long2ObjectLinkedOpenHashMap<CacheData> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ToIntFunction<BlockPos> source;

    public BlockTintCache(ToIntFunction<BlockPos> $$0) {
        this.source = $$0;
    }

    public int getColor(BlockPos $$0) {
        int $$9;
        int $$1 = SectionPos.blockToSectionCoord($$0.getX());
        int $$2 = SectionPos.blockToSectionCoord($$0.getZ());
        LatestCacheInfo $$3 = (LatestCacheInfo)this.latestChunkOnThread.get();
        if ($$3.x != $$1 || $$3.z != $$2 || $$3.cache == null || $$3.cache.isInvalidated()) {
            $$3.x = $$1;
            $$3.z = $$2;
            $$3.cache = this.findOrCreateChunkCache($$1, $$2);
        }
        int[] $$4 = $$3.cache.getLayer($$0.getY());
        int $$5 = $$0.getX() & 0xF;
        int $$6 = $$0.getZ() & 0xF;
        int $$7 = $$6 << 4 | $$5;
        int $$8 = $$4[$$7];
        if ($$8 != -1) {
            return $$8;
        }
        $$4[$$7] = $$9 = this.source.applyAsInt((Object)$$0);
        return $$9;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidateForChunk(int $$0, int $$1) {
        try {
            this.lock.writeLock().lock();
            for (int $$2 = -1; $$2 <= 1; ++$$2) {
                for (int $$3 = -1; $$3 <= 1; ++$$3) {
                    long $$4 = ChunkPos.asLong($$0 + $$2, $$1 + $$3);
                    CacheData $$5 = (CacheData)this.cache.remove($$4);
                    if ($$5 == null) continue;
                    $$5.invalidate();
                }
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void invalidateAll() {
        try {
            this.lock.writeLock().lock();
            this.cache.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CacheData findOrCreateChunkCache(int $$0, int $$1) {
        long $$2 = ChunkPos.asLong($$0, $$1);
        this.lock.readLock().lock();
        try {
            CacheData $$3 = (CacheData)this.cache.get($$2);
            if ($$3 != null) {
                CacheData cacheData = $$3;
                return cacheData;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            CacheData $$4 = (CacheData)this.cache.get($$2);
            if ($$4 != null) {
                CacheData cacheData = $$4;
                return cacheData;
            }
            CacheData $$5 = new CacheData();
            if (this.cache.size() >= 256) {
                this.cache.removeFirst();
            }
            this.cache.put($$2, (Object)$$5);
            CacheData cacheData = $$5;
            return cacheData;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    static class LatestCacheInfo {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        @Nullable
        CacheData cache;

        private LatestCacheInfo() {
        }
    }

    static class CacheData {
        private final Int2ObjectArrayMap<int[]> cache = new Int2ObjectArrayMap(16);
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static final int BLOCKS_PER_LAYER = Mth.square(16);
        private volatile boolean invalidated;

        CacheData() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int[] getLayer(int $$02) {
            this.lock.readLock().lock();
            try {
                int[] $$1 = (int[])this.cache.get($$02);
                if ($$1 != null) {
                    int[] nArray = $$1;
                    return nArray;
                }
            }
            finally {
                this.lock.readLock().unlock();
            }
            this.lock.writeLock().lock();
            try {
                int[] nArray = (int[])this.cache.computeIfAbsent($$02, $$0 -> this.allocateLayer());
                return nArray;
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }

        private int[] allocateLayer() {
            int[] $$0 = new int[BLOCKS_PER_LAYER];
            Arrays.fill((int[])$$0, (int)-1);
            return $$0;
        }

        public boolean isInvalidated() {
            return this.invalidated;
        }

        public void invalidate() {
            this.invalidated = true;
        }
    }
}