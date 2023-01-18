/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.io.IOException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.List
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import org.slf4j.Logger;

public class EntityStorage
implements EntityPersistentStorage<Entity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ENTITIES_TAG = "Entities";
    private static final String POSITION_TAG = "Position";
    private final ServerLevel level;
    private final IOWorker worker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final ProcessorMailbox<Runnable> entityDeserializerQueue;
    protected final DataFixer fixerUpper;

    public EntityStorage(ServerLevel $$0, Path $$1, DataFixer $$2, boolean $$3, Executor $$4) {
        this.level = $$0;
        this.fixerUpper = $$2;
        this.entityDeserializerQueue = ProcessorMailbox.create($$4, "entity-deserializer");
        this.worker = new IOWorker($$1, $$3, "entities");
    }

    @Override
    public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos $$0) {
        if (this.emptyChunks.contains($$0.toLong())) {
            return CompletableFuture.completedFuture(EntityStorage.emptyChunk($$0));
        }
        return this.worker.loadAsync($$0).thenApplyAsync($$1 -> {
            if ($$1.isEmpty()) {
                this.emptyChunks.add($$0.toLong());
                return EntityStorage.emptyChunk($$0);
            }
            try {
                ChunkPos $$2 = EntityStorage.readChunkPos((CompoundTag)$$1.get());
                if (!Objects.equals((Object)$$0, (Object)$$2)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", new Object[]{$$0, $$0, $$2});
                }
            }
            catch (Exception $$3) {
                LOGGER.warn("Failed to parse chunk {} position info", (Object)$$0, (Object)$$3);
            }
            CompoundTag $$4 = this.upgradeChunkTag((CompoundTag)$$1.get());
            ListTag $$5 = $$4.getList(ENTITIES_TAG, 10);
            List $$6 = (List)EntityType.loadEntitiesRecursive((List<? extends Tag>)$$5, this.level).collect(ImmutableList.toImmutableList());
            return new ChunkEntities($$0, $$6);
        }, this.entityDeserializerQueue::tell);
    }

    private static ChunkPos readChunkPos(CompoundTag $$0) {
        int[] $$1 = $$0.getIntArray(POSITION_TAG);
        return new ChunkPos($$1[0], $$1[1]);
    }

    private static void writeChunkPos(CompoundTag $$0, ChunkPos $$1) {
        $$0.put(POSITION_TAG, new IntArrayTag(new int[]{$$1.x, $$1.z}));
    }

    private static ChunkEntities<Entity> emptyChunk(ChunkPos $$0) {
        return new ChunkEntities<Entity>($$0, (List<Entity>)ImmutableList.of());
    }

    @Override
    public void storeEntities(ChunkEntities<Entity> $$0) {
        ChunkPos $$12 = $$0.getPos();
        if ($$0.isEmpty()) {
            if (this.emptyChunks.add($$12.toLong())) {
                this.worker.store($$12, null);
            }
            return;
        }
        ListTag $$2 = new ListTag();
        $$0.getEntities().forEach($$1 -> {
            CompoundTag $$2 = new CompoundTag();
            if ($$1.save($$2)) {
                $$2.add($$2);
            }
        });
        CompoundTag $$3 = NbtUtils.addCurrentDataVersion(new CompoundTag());
        $$3.put(ENTITIES_TAG, $$2);
        EntityStorage.writeChunkPos($$3, $$12);
        this.worker.store($$12, $$3).exceptionally($$1 -> {
            LOGGER.error("Failed to store chunk {}", (Object)$$12, $$1);
            return null;
        });
        this.emptyChunks.remove($$12.toLong());
    }

    @Override
    public void flush(boolean $$0) {
        this.worker.synchronize($$0).join();
        this.entityDeserializerQueue.runAll();
    }

    private CompoundTag upgradeChunkTag(CompoundTag $$0) {
        int $$1 = NbtUtils.getDataVersion($$0, -1);
        return DataFixTypes.ENTITY_CHUNK.updateToCurrentVersion(this.fixerUpper, $$0, $$1);
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}