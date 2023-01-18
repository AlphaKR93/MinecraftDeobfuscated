/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Thread
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ViewArea {
    protected final LevelRenderer levelRenderer;
    protected final Level level;
    protected int chunkGridSizeY;
    protected int chunkGridSizeX;
    protected int chunkGridSizeZ;
    public ChunkRenderDispatcher.RenderChunk[] chunks;

    public ViewArea(ChunkRenderDispatcher $$0, Level $$1, int $$2, LevelRenderer $$3) {
        this.levelRenderer = $$3;
        this.level = $$1;
        this.setViewDistance($$2);
        this.createChunks($$0);
    }

    protected void createChunks(ChunkRenderDispatcher $$0) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("createChunks called from wrong thread: " + Thread.currentThread().getName());
        }
        int $$1 = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
        this.chunks = new ChunkRenderDispatcher.RenderChunk[$$1];
        for (int $$2 = 0; $$2 < this.chunkGridSizeX; ++$$2) {
            for (int $$3 = 0; $$3 < this.chunkGridSizeY; ++$$3) {
                for (int $$4 = 0; $$4 < this.chunkGridSizeZ; ++$$4) {
                    int $$5 = this.getChunkIndex($$2, $$3, $$4);
                    ChunkRenderDispatcher chunkRenderDispatcher = $$0;
                    Objects.requireNonNull((Object)chunkRenderDispatcher);
                    this.chunks[$$5] = new ChunkRenderDispatcher.RenderChunk(chunkRenderDispatcher, $$5, $$2 * 16, $$3 * 16, $$4 * 16);
                }
            }
        }
    }

    public void releaseAllBuffers() {
        for (ChunkRenderDispatcher.RenderChunk $$0 : this.chunks) {
            $$0.releaseBuffers();
        }
    }

    private int getChunkIndex(int $$0, int $$1, int $$2) {
        return ($$2 * this.chunkGridSizeY + $$1) * this.chunkGridSizeX + $$0;
    }

    protected void setViewDistance(int $$0) {
        int $$1;
        this.chunkGridSizeX = $$1 = $$0 * 2 + 1;
        this.chunkGridSizeY = this.level.getSectionsCount();
        this.chunkGridSizeZ = $$1;
    }

    public void repositionCamera(double $$0, double $$1) {
        int $$2 = Mth.ceil($$0);
        int $$3 = Mth.ceil($$1);
        for (int $$4 = 0; $$4 < this.chunkGridSizeX; ++$$4) {
            int $$5 = this.chunkGridSizeX * 16;
            int $$6 = $$2 - 8 - $$5 / 2;
            int $$7 = $$6 + Math.floorMod((int)($$4 * 16 - $$6), (int)$$5);
            for (int $$8 = 0; $$8 < this.chunkGridSizeZ; ++$$8) {
                int $$9 = this.chunkGridSizeZ * 16;
                int $$10 = $$3 - 8 - $$9 / 2;
                int $$11 = $$10 + Math.floorMod((int)($$8 * 16 - $$10), (int)$$9);
                for (int $$12 = 0; $$12 < this.chunkGridSizeY; ++$$12) {
                    int $$13 = this.level.getMinBuildHeight() + $$12 * 16;
                    ChunkRenderDispatcher.RenderChunk $$14 = this.chunks[this.getChunkIndex($$4, $$12, $$8)];
                    BlockPos $$15 = $$14.getOrigin();
                    if ($$7 == $$15.getX() && $$13 == $$15.getY() && $$11 == $$15.getZ()) continue;
                    $$14.setOrigin($$7, $$13, $$11);
                }
            }
        }
    }

    public void setDirty(int $$0, int $$1, int $$2, boolean $$3) {
        int $$4 = Math.floorMod((int)$$0, (int)this.chunkGridSizeX);
        int $$5 = Math.floorMod((int)($$1 - this.level.getMinSection()), (int)this.chunkGridSizeY);
        int $$6 = Math.floorMod((int)$$2, (int)this.chunkGridSizeZ);
        ChunkRenderDispatcher.RenderChunk $$7 = this.chunks[this.getChunkIndex($$4, $$5, $$6)];
        $$7.setDirty($$3);
    }

    @Nullable
    protected ChunkRenderDispatcher.RenderChunk getRenderChunkAt(BlockPos $$0) {
        int $$1 = Mth.floorDiv($$0.getX(), 16);
        int $$2 = Mth.floorDiv($$0.getY() - this.level.getMinBuildHeight(), 16);
        int $$3 = Mth.floorDiv($$0.getZ(), 16);
        if ($$2 < 0 || $$2 >= this.chunkGridSizeY) {
            return null;
        }
        $$1 = Mth.positiveModulo($$1, this.chunkGridSizeX);
        $$3 = Mth.positiveModulo($$3, this.chunkGridSizeZ);
        return this.chunks[this.getChunkIndex($$1, $$2, $$3)];
    }
}