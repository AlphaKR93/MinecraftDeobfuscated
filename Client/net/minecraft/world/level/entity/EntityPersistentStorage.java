/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.world.level.entity;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;

public interface EntityPersistentStorage<T>
extends AutoCloseable {
    public CompletableFuture<ChunkEntities<T>> loadEntities(ChunkPos var1);

    public void storeEntities(ChunkEntities<T> var1);

    public void flush(boolean var1);

    default public void close() throws IOException {
    }
}