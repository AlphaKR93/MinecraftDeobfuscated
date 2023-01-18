/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class StoringChunkProgressListener
implements ChunkProgressListener {
    private final LoggerChunkProgressListener delegate;
    private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
    private ChunkPos spawnPos = new ChunkPos(0, 0);
    private final int fullDiameter;
    private final int radius;
    private final int diameter;
    private boolean started;

    public StoringChunkProgressListener(int $$0) {
        this.delegate = new LoggerChunkProgressListener($$0);
        this.fullDiameter = $$0 * 2 + 1;
        this.radius = $$0 + ChunkStatus.maxDistance();
        this.diameter = this.radius * 2 + 1;
        this.statuses = new Long2ObjectOpenHashMap();
    }

    @Override
    public void updateSpawnPos(ChunkPos $$0) {
        if (!this.started) {
            return;
        }
        this.delegate.updateSpawnPos($$0);
        this.spawnPos = $$0;
    }

    @Override
    public void onStatusChange(ChunkPos $$0, @Nullable ChunkStatus $$1) {
        if (!this.started) {
            return;
        }
        this.delegate.onStatusChange($$0, $$1);
        if ($$1 == null) {
            this.statuses.remove($$0.toLong());
        } else {
            this.statuses.put($$0.toLong(), (Object)$$1);
        }
    }

    @Override
    public void start() {
        this.started = true;
        this.statuses.clear();
        this.delegate.start();
    }

    @Override
    public void stop() {
        this.started = false;
        this.delegate.stop();
    }

    public int getFullDiameter() {
        return this.fullDiameter;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getProgress() {
        return this.delegate.getProgress();
    }

    @Nullable
    public ChunkStatus getStatus(int $$0, int $$1) {
        return (ChunkStatus)this.statuses.get(ChunkPos.asLong($$0 + this.spawnPos.x - this.radius, $$1 + this.spawnPos.z - this.radius));
    }
}