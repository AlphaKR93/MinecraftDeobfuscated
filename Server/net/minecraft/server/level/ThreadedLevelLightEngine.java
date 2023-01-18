/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.lang.AutoCloseable
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.atomic.AtomicBoolean
 *  java.util.function.IntSupplier
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;

public class ThreadedLevelLightEngine
extends LevelLightEngine
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ProcessorMailbox<Runnable> taskMailbox;
    private final ObjectList<Pair<TaskType, Runnable>> lightTasks = new ObjectArrayList();
    private final ChunkMap chunkMap;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorterMailbox;
    private volatile int taskPerBatch = 5;
    private final AtomicBoolean scheduled = new AtomicBoolean();

    public ThreadedLevelLightEngine(LightChunkGetter $$0, ChunkMap $$1, boolean $$2, ProcessorMailbox<Runnable> $$3, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> $$4) {
        super($$0, true, $$2);
        this.chunkMap = $$1;
        this.sorterMailbox = $$4;
        this.taskMailbox = $$3;
    }

    public void close() {
    }

    @Override
    public int runUpdates(int $$0, boolean $$1, boolean $$2) {
        throw Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos $$0, int $$1) {
        throw Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPos $$0) {
        BlockPos $$1 = $$0.immutable();
        this.addTask(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()), TaskType.POST_UPDATE, Util.name(() -> super.checkBlock($$1), (Supplier<String>)((Supplier)() -> "checkBlock " + $$1)));
    }

    protected void updateChunkStatus(ChunkPos $$0) {
        this.addTask($$0.x, $$0.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> {
            super.retainData($$0, false);
            super.enableLightSources($$0, false);
            for (int $$1 = this.getMinLightSection(); $$1 < this.getMaxLightSection(); ++$$1) {
                super.queueSectionData(LightLayer.BLOCK, SectionPos.of($$0, $$1), null, true);
                super.queueSectionData(LightLayer.SKY, SectionPos.of($$0, $$1), null, true);
            }
            for (int $$2 = this.levelHeightAccessor.getMinSection(); $$2 < this.levelHeightAccessor.getMaxSection(); ++$$2) {
                super.updateSectionStatus(SectionPos.of($$0, $$2), true);
            }
        }, (Supplier<String>)((Supplier)() -> "updateChunkStatus " + $$0 + " true")));
    }

    @Override
    public void updateSectionStatus(SectionPos $$0, boolean $$1) {
        this.addTask($$0.x(), $$0.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.updateSectionStatus($$0, $$1), (Supplier<String>)((Supplier)() -> "updateSectionStatus " + $$0 + " " + $$1)));
    }

    @Override
    public void enableLightSources(ChunkPos $$0, boolean $$1) {
        this.addTask($$0.x, $$0.z, TaskType.PRE_UPDATE, Util.name(() -> super.enableLightSources($$0, $$1), (Supplier<String>)((Supplier)() -> "enableLight " + $$0 + " " + $$1)));
    }

    @Override
    public void queueSectionData(LightLayer $$0, SectionPos $$1, @Nullable DataLayer $$2, boolean $$3) {
        this.addTask($$1.x(), $$1.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.queueSectionData($$0, $$1, $$2, $$3), (Supplier<String>)((Supplier)() -> "queueData " + $$1)));
    }

    private void addTask(int $$0, int $$1, TaskType $$2, Runnable $$3) {
        this.addTask($$0, $$1, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong($$0, $$1)), $$2, $$3);
    }

    private void addTask(int $$0, int $$1, IntSupplier $$2, TaskType $$3, Runnable $$4) {
        this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
            this.lightTasks.add((Object)Pair.of((Object)((Object)$$3), (Object)$$4));
            if (this.lightTasks.size() >= this.taskPerBatch) {
                this.runUpdate();
            }
        }, ChunkPos.asLong($$0, $$1), $$2));
    }

    @Override
    public void retainData(ChunkPos $$0, boolean $$1) {
        this.addTask($$0.x, $$0.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.retainData($$0, $$1), (Supplier<String>)((Supplier)() -> "retainData " + $$0)));
    }

    public CompletableFuture<ChunkAccess> retainData(ChunkAccess $$0) {
        ChunkPos $$12 = $$0.getPos();
        return CompletableFuture.supplyAsync(Util.name(() -> {
            super.retainData($$12, true);
            return $$0;
        }, (Supplier<String>)((Supplier)() -> "retainData: " + $$12)), $$1 -> this.addTask($$0.x, $$0.z, TaskType.PRE_UPDATE, $$1));
    }

    public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess $$0, boolean $$12) {
        ChunkPos $$2 = $$0.getPos();
        $$0.setLightCorrect(false);
        this.addTask($$2.x, $$2.z, TaskType.PRE_UPDATE, Util.name(() -> {
            LevelChunkSection[] $$3 = $$0.getSections();
            for (int $$4 = 0; $$4 < $$0.getSectionsCount(); ++$$4) {
                LevelChunkSection $$5 = $$3[$$4];
                if ($$5.hasOnlyAir()) continue;
                int $$6 = this.levelHeightAccessor.getSectionYFromSectionIndex($$4);
                super.updateSectionStatus(SectionPos.of($$2, $$6), false);
            }
            super.enableLightSources($$2, true);
            if (!$$12) {
                $$0.getLights().forEach($$1 -> super.onBlockEmissionIncrease((BlockPos)$$1, $$0.getLightEmission((BlockPos)$$1)));
            }
        }, (Supplier<String>)((Supplier)() -> "lightChunk " + $$2 + " " + $$12)));
        return CompletableFuture.supplyAsync(() -> {
            $$0.setLightCorrect(true);
            super.retainData($$2, false);
            this.chunkMap.releaseLightTicket($$2);
            return $$0;
        }, $$1 -> this.addTask($$0.x, $$0.z, TaskType.POST_UPDATE, $$1));
    }

    public void tryScheduleUpdate() {
        if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
            this.taskMailbox.tell(() -> {
                this.runUpdate();
                this.scheduled.set(false);
            });
        }
    }

    private void runUpdate() {
        int $$2;
        int $$0 = Math.min((int)this.lightTasks.size(), (int)this.taskPerBatch);
        ObjectListIterator $$1 = this.lightTasks.iterator();
        for ($$2 = 0; $$1.hasNext() && $$2 < $$0; ++$$2) {
            Pair $$3 = (Pair)$$1.next();
            if ($$3.getFirst() != TaskType.PRE_UPDATE) continue;
            ((Runnable)$$3.getSecond()).run();
        }
        $$1.back($$2);
        super.runUpdates(Integer.MAX_VALUE, true, true);
        for ($$2 = 0; $$1.hasNext() && $$2 < $$0; ++$$2) {
            Pair $$4 = (Pair)$$1.next();
            if ($$4.getFirst() == TaskType.POST_UPDATE) {
                ((Runnable)$$4.getSecond()).run();
            }
            $$1.remove();
        }
    }

    public void setTaskPerBatch(int $$0) {
        this.taskPerBatch = $$0;
    }

    static enum TaskType {
        PRE_UPDATE,
        POST_UPDATE;

    }
}