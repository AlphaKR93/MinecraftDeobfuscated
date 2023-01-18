/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Object
 *  java.util.List
 *  java.util.Map
 */
package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public class LocalMobCapCalculator {
    private final Long2ObjectMap<List<ServerPlayer>> playersNearChunk = new Long2ObjectOpenHashMap();
    private final Map<ServerPlayer, MobCounts> playerMobCounts = Maps.newHashMap();
    private final ChunkMap chunkMap;

    public LocalMobCapCalculator(ChunkMap $$0) {
        this.chunkMap = $$0;
    }

    private List<ServerPlayer> getPlayersNear(ChunkPos $$0) {
        return (List)this.playersNearChunk.computeIfAbsent($$0.toLong(), $$1 -> this.chunkMap.getPlayersCloseForSpawning($$0));
    }

    public void addMob(ChunkPos $$02, MobCategory $$1) {
        for (ServerPlayer $$2 : this.getPlayersNear($$02)) {
            ((MobCounts)this.playerMobCounts.computeIfAbsent((Object)$$2, $$0 -> new MobCounts())).add($$1);
        }
    }

    public boolean canSpawn(MobCategory $$0, ChunkPos $$1) {
        for (ServerPlayer $$2 : this.getPlayersNear($$1)) {
            MobCounts $$3 = (MobCounts)this.playerMobCounts.get((Object)$$2);
            if ($$3 != null && !$$3.canSpawn($$0)) continue;
            return true;
        }
        return false;
    }

    static class MobCounts {
        private final Object2IntMap<MobCategory> counts = new Object2IntOpenHashMap(MobCategory.values().length);

        MobCounts() {
        }

        public void add(MobCategory $$02) {
            this.counts.computeInt((Object)$$02, ($$0, $$1) -> $$1 == null ? 1 : $$1 + 1);
        }

        public boolean canSpawn(MobCategory $$0) {
            return this.counts.getOrDefault((Object)$$0, 0) < $$0.getMaxInstancesPerChunk();
        }
    }
}