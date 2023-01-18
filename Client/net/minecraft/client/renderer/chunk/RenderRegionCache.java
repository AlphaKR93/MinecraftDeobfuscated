/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class RenderRegionCache {
    private final Long2ObjectMap<ChunkInfo> chunkInfoCache = new Long2ObjectOpenHashMap();

    @Nullable
    public RenderChunkRegion createRegion(Level $$0, BlockPos $$12, BlockPos $$2, int $$3) {
        int $$4 = SectionPos.blockToSectionCoord($$12.getX() - $$3);
        int $$5 = SectionPos.blockToSectionCoord($$12.getZ() - $$3);
        int $$6 = SectionPos.blockToSectionCoord($$2.getX() + $$3);
        int $$7 = SectionPos.blockToSectionCoord($$2.getZ() + $$3);
        ChunkInfo[][] $$8 = new ChunkInfo[$$6 - $$4 + 1][$$7 - $$5 + 1];
        for (int $$9 = $$4; $$9 <= $$6; ++$$9) {
            for (int $$10 = $$5; $$10 <= $$7; ++$$10) {
                $$8[$$9 - $$4][$$10 - $$5] = (ChunkInfo)this.chunkInfoCache.computeIfAbsent(ChunkPos.asLong($$9, $$10), $$1 -> new ChunkInfo($$0.getChunk(ChunkPos.getX($$1), ChunkPos.getZ($$1))));
            }
        }
        if (RenderRegionCache.isAllEmpty($$12, $$2, $$4, $$5, $$8)) {
            return null;
        }
        RenderChunk[][] $$11 = new RenderChunk[$$6 - $$4 + 1][$$7 - $$5 + 1];
        for (int $$122 = $$4; $$122 <= $$6; ++$$122) {
            for (int $$13 = $$5; $$13 <= $$7; ++$$13) {
                $$11[$$122 - $$4][$$13 - $$5] = $$8[$$122 - $$4][$$13 - $$5].renderChunk();
            }
        }
        return new RenderChunkRegion($$0, $$4, $$5, $$11);
    }

    private static boolean isAllEmpty(BlockPos $$0, BlockPos $$1, int $$2, int $$3, ChunkInfo[][] $$4) {
        int $$5 = SectionPos.blockToSectionCoord($$0.getX());
        int $$6 = SectionPos.blockToSectionCoord($$0.getZ());
        int $$7 = SectionPos.blockToSectionCoord($$1.getX());
        int $$8 = SectionPos.blockToSectionCoord($$1.getZ());
        for (int $$9 = $$5; $$9 <= $$7; ++$$9) {
            for (int $$10 = $$6; $$10 <= $$8; ++$$10) {
                LevelChunk $$11 = $$4[$$9 - $$2][$$10 - $$3].chunk();
                if ($$11.isYSpaceEmpty($$0.getY(), $$1.getY())) continue;
                return false;
            }
        }
        return true;
    }

    static final class ChunkInfo {
        private final LevelChunk chunk;
        @Nullable
        private RenderChunk renderChunk;

        ChunkInfo(LevelChunk $$0) {
            this.chunk = $$0;
        }

        public LevelChunk chunk() {
            return this.chunk;
        }

        public RenderChunk renderChunk() {
            if (this.renderChunk == null) {
                this.renderChunk = new RenderChunk(this.chunk);
            }
            return this.renderChunk;
        }
    }
}