/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.common.primitives.Doubles
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ReferenceArraySet
 *  java.lang.Comparable
 *  java.lang.InterruptedException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Queue
 *  java.util.Set
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.PriorityBlockingQueue
 *  java.util.concurrent.atomic.AtomicBoolean
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ChunkRenderDispatcher {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_WORKERS_32_BIT = 4;
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;
    private static final int MAX_HIGH_PRIORITY_QUOTA = 2;
    private final PriorityBlockingQueue<RenderChunk.ChunkCompileTask> toBatchHighPriority = Queues.newPriorityBlockingQueue();
    private final Queue<RenderChunk.ChunkCompileTask> toBatchLowPriority = Queues.newLinkedBlockingDeque();
    private int highPriorityQuota = 2;
    private final Queue<ChunkBufferBuilderPack> freeBuffers;
    private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
    private volatile int toBatchCount;
    private volatile int freeBufferCount;
    final ChunkBufferBuilderPack fixedBuffers;
    private final ProcessorMailbox<Runnable> mailbox;
    private final Executor executor;
    ClientLevel level;
    final LevelRenderer renderer;
    private Vec3 camera = Vec3.ZERO;

    public ChunkRenderDispatcher(ClientLevel $$0, LevelRenderer $$1, Executor $$2, boolean $$3, ChunkBufferBuilderPack $$4) {
        this.level = $$0;
        this.renderer = $$1;
        int $$5 = Math.max((int)1, (int)((int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1));
        int $$6 = Runtime.getRuntime().availableProcessors();
        int $$7 = $$3 ? $$6 : Math.min((int)$$6, (int)4);
        int $$8 = Math.max((int)1, (int)Math.min((int)$$7, (int)$$5));
        this.fixedBuffers = $$4;
        ArrayList $$9 = Lists.newArrayListWithExpectedSize((int)$$8);
        try {
            for (int $$10 = 0; $$10 < $$8; ++$$10) {
                $$9.add((Object)new ChunkBufferBuilderPack());
            }
        }
        catch (OutOfMemoryError $$11) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)$$9.size(), (Object)$$8);
            int $$12 = Math.min((int)($$9.size() * 2 / 3), (int)($$9.size() - 1));
            for (int $$13 = 0; $$13 < $$12; ++$$13) {
                $$9.remove($$9.size() - 1);
            }
            System.gc();
        }
        this.freeBuffers = Queues.newArrayDeque((Iterable)$$9);
        this.freeBufferCount = this.freeBuffers.size();
        this.executor = $$2;
        this.mailbox = ProcessorMailbox.create($$2, "Chunk Renderer");
        this.mailbox.tell(this::runTask);
    }

    public void setLevel(ClientLevel $$0) {
        this.level = $$0;
    }

    private void runTask() {
        if (this.freeBuffers.isEmpty()) {
            return;
        }
        RenderChunk.ChunkCompileTask $$02 = this.pollTask();
        if ($$02 == null) {
            return;
        }
        ChunkBufferBuilderPack $$12 = (ChunkBufferBuilderPack)this.freeBuffers.poll();
        this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
        this.freeBufferCount = this.freeBuffers.size();
        CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName($$02.name(), () -> $$02.doTask($$12)), (Executor)this.executor).thenCompose($$0 -> $$0).whenComplete(($$1, $$2) -> {
            if ($$2 != null) {
                Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$2, "Batching chunks"));
                return;
            }
            this.mailbox.tell(() -> {
                if ($$1 == ChunkTaskResult.SUCCESSFUL) {
                    $$12.clearAll();
                } else {
                    $$12.discardAll();
                }
                this.freeBuffers.add((Object)$$12);
                this.freeBufferCount = this.freeBuffers.size();
                this.runTask();
            });
        });
    }

    @Nullable
    private RenderChunk.ChunkCompileTask pollTask() {
        RenderChunk.ChunkCompileTask $$0;
        if (this.highPriorityQuota <= 0 && ($$0 = (RenderChunk.ChunkCompileTask)this.toBatchLowPriority.poll()) != null) {
            this.highPriorityQuota = 2;
            return $$0;
        }
        RenderChunk.ChunkCompileTask $$1 = (RenderChunk.ChunkCompileTask)this.toBatchHighPriority.poll();
        if ($$1 != null) {
            --this.highPriorityQuota;
            return $$1;
        }
        this.highPriorityQuota = 2;
        return (RenderChunk.ChunkCompileTask)this.toBatchLowPriority.poll();
    }

    public String getStats() {
        return String.format((Locale)Locale.ROOT, (String)"pC: %03d, pU: %02d, aB: %02d", (Object[])new Object[]{this.toBatchCount, this.toUpload.size(), this.freeBufferCount});
    }

    public int getToBatchCount() {
        return this.toBatchCount;
    }

    public int getToUpload() {
        return this.toUpload.size();
    }

    public int getFreeBufferCount() {
        return this.freeBufferCount;
    }

    public void setCamera(Vec3 $$0) {
        this.camera = $$0;
    }

    public Vec3 getCameraPosition() {
        return this.camera;
    }

    public void uploadAllPendingUploads() {
        Runnable $$0;
        while (($$0 = (Runnable)this.toUpload.poll()) != null) {
            $$0.run();
        }
    }

    public void rebuildChunkSync(RenderChunk $$0, RenderRegionCache $$1) {
        $$0.compileSync($$1);
    }

    public void blockUntilClear() {
        this.clearBatchQueue();
    }

    public void schedule(RenderChunk.ChunkCompileTask $$0) {
        this.mailbox.tell(() -> {
            if ($$0.isHighPriority) {
                this.toBatchHighPriority.offer((Object)$$0);
            } else {
                this.toBatchLowPriority.offer((Object)$$0);
            }
            this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
            this.runTask();
        });
    }

    public CompletableFuture<Void> uploadChunkLayer(BufferBuilder.RenderedBuffer $$0, VertexBuffer $$1) {
        return CompletableFuture.runAsync(() -> {
            if ($$1.isInvalid()) {
                return;
            }
            $$1.bind();
            $$1.upload($$0);
            VertexBuffer.unbind();
        }, arg_0 -> this.toUpload.add(arg_0));
    }

    private void clearBatchQueue() {
        while (!this.toBatchHighPriority.isEmpty()) {
            RenderChunk.ChunkCompileTask $$0 = (RenderChunk.ChunkCompileTask)this.toBatchHighPriority.poll();
            if ($$0 == null) continue;
            $$0.cancel();
        }
        while (!this.toBatchLowPriority.isEmpty()) {
            RenderChunk.ChunkCompileTask $$1 = (RenderChunk.ChunkCompileTask)this.toBatchLowPriority.poll();
            if ($$1 == null) continue;
            $$1.cancel();
        }
        this.toBatchCount = 0;
    }

    public boolean isQueueEmpty() {
        return this.toBatchCount == 0 && this.toUpload.isEmpty();
    }

    public void dispose() {
        this.clearBatchQueue();
        this.mailbox.close();
        this.freeBuffers.clear();
    }

    public class RenderChunk {
        public static final int SIZE = 16;
        public final int index;
        public final AtomicReference<CompiledChunk> compiled = new AtomicReference((Object)CompiledChunk.UNCOMPILED);
        final AtomicInteger initialCompilationCancelCount = new AtomicInteger(0);
        @Nullable
        private RebuildTask lastRebuildTask;
        @Nullable
        private ResortTransparencyTask lastResortTransparencyTask;
        private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
        private final Map<RenderType, VertexBuffer> buffers = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap($$0 -> $$0, $$0 -> new VertexBuffer()));
        private AABB bb;
        private boolean dirty = true;
        final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
        private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6], $$0 -> {
            for (int $$1 = 0; $$1 < ((BlockPos.MutableBlockPos[])$$0).length; ++$$1) {
                $$0[$$1] = new BlockPos.MutableBlockPos();
            }
        });
        private boolean playerChanged;

        public RenderChunk(int $$1, int $$2, int $$3, int $$4) {
            this.index = $$1;
            this.setOrigin($$2, $$3, $$4);
        }

        private boolean doesChunkExistAt(BlockPos $$0) {
            return ChunkRenderDispatcher.this.level.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()), ChunkStatus.FULL, false) != null;
        }

        public boolean hasAllNeighbors() {
            int $$0 = 24;
            if (this.getDistToPlayerSqr() > 576.0) {
                return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
            }
            return true;
        }

        public AABB getBoundingBox() {
            return this.bb;
        }

        public VertexBuffer getBuffer(RenderType $$0) {
            return (VertexBuffer)this.buffers.get((Object)$$0);
        }

        public void setOrigin(int $$0, int $$1, int $$2) {
            this.reset();
            this.origin.set($$0, $$1, $$2);
            this.bb = new AABB($$0, $$1, $$2, $$0 + 16, $$1 + 16, $$2 + 16);
            for (Direction $$3 : Direction.values()) {
                this.relativeOrigins[$$3.ordinal()].set(this.origin).move($$3, 16);
            }
        }

        protected double getDistToPlayerSqr() {
            Camera $$0 = Minecraft.getInstance().gameRenderer.getMainCamera();
            double $$1 = this.bb.minX + 8.0 - $$0.getPosition().x;
            double $$2 = this.bb.minY + 8.0 - $$0.getPosition().y;
            double $$3 = this.bb.minZ + 8.0 - $$0.getPosition().z;
            return $$1 * $$1 + $$2 * $$2 + $$3 * $$3;
        }

        void beginLayer(BufferBuilder $$0) {
            $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        }

        public CompiledChunk getCompiledChunk() {
            return (CompiledChunk)this.compiled.get();
        }

        private void reset() {
            this.cancelTasks();
            this.compiled.set((Object)CompiledChunk.UNCOMPILED);
            this.dirty = true;
        }

        public void releaseBuffers() {
            this.reset();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public void setDirty(boolean $$0) {
            boolean $$1 = this.dirty;
            this.dirty = true;
            this.playerChanged = $$0 | ($$1 && this.playerChanged);
        }

        public void setNotDirty() {
            this.dirty = false;
            this.playerChanged = false;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public boolean isDirtyFromPlayer() {
            return this.dirty && this.playerChanged;
        }

        public BlockPos getRelativeOrigin(Direction $$0) {
            return this.relativeOrigins[$$0.ordinal()];
        }

        public boolean resortTransparency(RenderType $$0, ChunkRenderDispatcher $$1) {
            CompiledChunk $$2 = this.getCompiledChunk();
            if (this.lastResortTransparencyTask != null) {
                this.lastResortTransparencyTask.cancel();
            }
            if (!$$2.hasBlocks.contains((Object)$$0)) {
                return false;
            }
            this.lastResortTransparencyTask = new ResortTransparencyTask(this.getDistToPlayerSqr(), $$2);
            $$1.schedule(this.lastResortTransparencyTask);
            return true;
        }

        protected boolean cancelTasks() {
            boolean $$0 = false;
            if (this.lastRebuildTask != null) {
                this.lastRebuildTask.cancel();
                this.lastRebuildTask = null;
                $$0 = true;
            }
            if (this.lastResortTransparencyTask != null) {
                this.lastResortTransparencyTask.cancel();
                this.lastResortTransparencyTask = null;
            }
            return $$0;
        }

        public ChunkCompileTask createCompileTask(RenderRegionCache $$0) {
            boolean $$5;
            boolean $$1 = this.cancelTasks();
            BlockPos $$2 = this.origin.immutable();
            boolean $$3 = true;
            RenderChunkRegion $$4 = $$0.createRegion(ChunkRenderDispatcher.this.level, $$2.offset(-1, -1, -1), $$2.offset(16, 16, 16), 1);
            boolean bl = $$5 = this.compiled.get() == CompiledChunk.UNCOMPILED;
            if ($$5 && $$1) {
                this.initialCompilationCancelCount.incrementAndGet();
            }
            this.lastRebuildTask = new RebuildTask(this.getDistToPlayerSqr(), $$4, !$$5 || this.initialCompilationCancelCount.get() > 2);
            return this.lastRebuildTask;
        }

        public void rebuildChunkAsync(ChunkRenderDispatcher $$0, RenderRegionCache $$1) {
            ChunkCompileTask $$2 = this.createCompileTask($$1);
            $$0.schedule($$2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * WARNING - void declaration
         */
        void updateGlobalBlockEntities(Collection<BlockEntity> $$0) {
            void $$3;
            HashSet $$1 = Sets.newHashSet($$0);
            Set<BlockEntity> set = this.globalBlockEntities;
            synchronized (set) {
                HashSet $$2 = Sets.newHashSet(this.globalBlockEntities);
                $$1.removeAll(this.globalBlockEntities);
                $$2.removeAll($$0);
                this.globalBlockEntities.clear();
                this.globalBlockEntities.addAll($$0);
            }
            ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities((Collection<BlockEntity>)$$3, (Collection<BlockEntity>)$$1);
        }

        public void compileSync(RenderRegionCache $$0) {
            ChunkCompileTask $$1 = this.createCompileTask($$0);
            $$1.doTask(ChunkRenderDispatcher.this.fixedBuffers);
        }

        class ResortTransparencyTask
        extends ChunkCompileTask {
            private final CompiledChunk compiledChunk;

            public ResortTransparencyTask(double $$0, CompiledChunk $$1) {
                super($$0, true);
                this.compiledChunk = $$1;
            }

            @Override
            protected String name() {
                return "rend_chk_sort";
            }

            @Override
            public CompletableFuture<ChunkTaskResult> doTask(ChunkBufferBuilderPack $$02) {
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                if (!RenderChunk.this.hasAllNeighbors()) {
                    this.isCancelled.set(true);
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                Vec3 $$12 = ChunkRenderDispatcher.this.getCameraPosition();
                float $$2 = (float)$$12.x;
                float $$3 = (float)$$12.y;
                float $$4 = (float)$$12.z;
                BufferBuilder.SortState $$5 = this.compiledChunk.transparencyState;
                if ($$5 == null || this.compiledChunk.isEmpty(RenderType.translucent())) {
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                BufferBuilder $$6 = $$02.builder(RenderType.translucent());
                RenderChunk.this.beginLayer($$6);
                $$6.restoreSortState($$5);
                $$6.setQuadSortOrigin($$2 - (float)RenderChunk.this.origin.getX(), $$3 - (float)RenderChunk.this.origin.getY(), $$4 - (float)RenderChunk.this.origin.getZ());
                this.compiledChunk.transparencyState = $$6.getSortState();
                BufferBuilder.RenderedBuffer $$7 = $$6.end();
                if (this.isCancelled.get()) {
                    $$7.release();
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                CompletableFuture $$8 = ChunkRenderDispatcher.this.uploadChunkLayer($$7, RenderChunk.this.getBuffer(RenderType.translucent())).thenApply($$0 -> ChunkTaskResult.CANCELLED);
                return $$8.handle(($$0, $$1) -> {
                    if ($$1 != null && !($$1 instanceof CancellationException) && !($$1 instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$1, "Rendering chunk"));
                    }
                    return this.isCancelled.get() ? ChunkTaskResult.CANCELLED : ChunkTaskResult.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.isCancelled.set(true);
            }
        }

        abstract class ChunkCompileTask
        implements Comparable<ChunkCompileTask> {
            protected final double distAtCreation;
            protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
            protected final boolean isHighPriority;

            public ChunkCompileTask(double $$0, boolean $$1) {
                this.distAtCreation = $$0;
                this.isHighPriority = $$1;
            }

            public abstract CompletableFuture<ChunkTaskResult> doTask(ChunkBufferBuilderPack var1);

            public abstract void cancel();

            protected abstract String name();

            public int compareTo(ChunkCompileTask $$0) {
                return Doubles.compare((double)this.distAtCreation, (double)$$0.distAtCreation);
            }
        }

        class RebuildTask
        extends ChunkCompileTask {
            @Nullable
            protected RenderChunkRegion region;

            public RebuildTask(@Nullable double $$0, RenderChunkRegion $$1, boolean $$2) {
                super($$0, $$2);
                this.region = $$1;
            }

            @Override
            protected String name() {
                return "rend_chk_rebuild";
            }

            @Override
            public CompletableFuture<ChunkTaskResult> doTask(ChunkBufferBuilderPack $$0) {
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                if (!RenderChunk.this.hasAllNeighbors()) {
                    this.region = null;
                    RenderChunk.this.setDirty(false);
                    this.isCancelled.set(true);
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                Vec3 $$12 = ChunkRenderDispatcher.this.getCameraPosition();
                float $$22 = (float)$$12.x;
                float $$3 = (float)$$12.y;
                float $$4 = (float)$$12.z;
                CompileResults $$5 = this.compile($$22, $$3, $$4, $$0);
                RenderChunk.this.updateGlobalBlockEntities((Collection<BlockEntity>)$$5.globalBlockEntities);
                if (this.isCancelled.get()) {
                    $$5.renderedLayers.values().forEach(BufferBuilder.RenderedBuffer::release);
                    return CompletableFuture.completedFuture((Object)((Object)ChunkTaskResult.CANCELLED));
                }
                CompiledChunk $$6 = new CompiledChunk();
                $$6.visibilitySet = $$5.visibilitySet;
                $$6.renderableBlockEntities.addAll($$5.blockEntities);
                $$6.transparencyState = $$5.transparencyState;
                ArrayList $$7 = Lists.newArrayList();
                $$5.renderedLayers.forEach((arg_0, arg_1) -> this.lambda$doTask$0((List)$$7, $$6, arg_0, arg_1));
                return Util.sequenceFailFast($$7).handle(($$1, $$2) -> {
                    if ($$2 != null && !($$2 instanceof CancellationException) && !($$2 instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$2, "Rendering chunk"));
                    }
                    if (this.isCancelled.get()) {
                        return ChunkTaskResult.CANCELLED;
                    }
                    RenderChunk.this.compiled.set((Object)$$6);
                    RenderChunk.this.initialCompilationCancelCount.set(0);
                    ChunkRenderDispatcher.this.renderer.addRecentlyCompiledChunk(RenderChunk.this);
                    return ChunkTaskResult.SUCCESSFUL;
                });
            }

            private CompileResults compile(float $$0, float $$1, float $$2, ChunkBufferBuilderPack $$3) {
                CompileResults $$4 = new CompileResults();
                boolean $$5 = true;
                BlockPos $$6 = RenderChunk.this.origin.immutable();
                BlockPos $$7 = $$6.offset(15, 15, 15);
                VisGraph $$8 = new VisGraph();
                RenderChunkRegion $$9 = this.region;
                this.region = null;
                PoseStack $$10 = new PoseStack();
                if ($$9 != null) {
                    BufferBuilder $$23;
                    ModelBlockRenderer.enableCaching();
                    ReferenceArraySet $$11 = new ReferenceArraySet(RenderType.chunkBufferLayers().size());
                    RandomSource $$12 = RandomSource.create();
                    BlockRenderDispatcher $$13 = Minecraft.getInstance().getBlockRenderer();
                    for (BlockPos $$14 : BlockPos.betweenClosed($$6, $$7)) {
                        BlockState $$17;
                        FluidState $$18;
                        BlockEntity $$16;
                        BlockState $$15 = $$9.getBlockState($$14);
                        if ($$15.isSolidRender($$9, $$14)) {
                            $$8.setOpaque($$14);
                        }
                        if ($$15.hasBlockEntity() && ($$16 = $$9.getBlockEntity($$14)) != null) {
                            this.handleBlockEntity($$4, $$16);
                        }
                        if (!($$18 = ($$17 = $$9.getBlockState($$14)).getFluidState()).isEmpty()) {
                            RenderType $$19 = ItemBlockRenderTypes.getRenderLayer($$18);
                            BufferBuilder $$20 = $$3.builder($$19);
                            if ($$11.add((Object)$$19)) {
                                RenderChunk.this.beginLayer($$20);
                            }
                            $$13.renderLiquid($$14, $$9, $$20, $$17, $$18);
                        }
                        if ($$15.getRenderShape() == RenderShape.INVISIBLE) continue;
                        RenderType $$21 = ItemBlockRenderTypes.getChunkRenderType($$15);
                        BufferBuilder $$22 = $$3.builder($$21);
                        if ($$11.add((Object)$$21)) {
                            RenderChunk.this.beginLayer($$22);
                        }
                        $$10.pushPose();
                        $$10.translate($$14.getX() & 0xF, $$14.getY() & 0xF, $$14.getZ() & 0xF);
                        $$13.renderBatched($$15, $$14, $$9, $$10, $$22, true, $$12);
                        $$10.popPose();
                    }
                    if ($$11.contains((Object)RenderType.translucent()) && !($$23 = $$3.builder(RenderType.translucent())).isCurrentBatchEmpty()) {
                        $$23.setQuadSortOrigin($$0 - (float)$$6.getX(), $$1 - (float)$$6.getY(), $$2 - (float)$$6.getZ());
                        $$4.transparencyState = $$23.getSortState();
                    }
                    for (RenderType $$24 : $$11) {
                        BufferBuilder.RenderedBuffer $$25 = $$3.builder($$24).endOrDiscardIfEmpty();
                        if ($$25 == null) continue;
                        $$4.renderedLayers.put((Object)$$24, (Object)$$25);
                    }
                    ModelBlockRenderer.clearCache();
                }
                $$4.visibilitySet = $$8.resolve();
                return $$4;
            }

            private <E extends BlockEntity> void handleBlockEntity(CompileResults $$0, E $$1) {
                BlockEntityRenderer<E> $$2 = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer($$1);
                if ($$2 != null) {
                    $$0.blockEntities.add($$1);
                    if ($$2.shouldRenderOffScreen($$1)) {
                        $$0.globalBlockEntities.add($$1);
                    }
                }
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.isCancelled.compareAndSet(false, true)) {
                    RenderChunk.this.setDirty(false);
                }
            }

            private /* synthetic */ void lambda$doTask$0(List $$0, CompiledChunk $$1, RenderType $$2, BufferBuilder.RenderedBuffer $$3) {
                $$0.add(ChunkRenderDispatcher.this.uploadChunkLayer($$3, RenderChunk.this.getBuffer($$2)));
                $$1.hasBlocks.add((Object)$$2);
            }

            static final class CompileResults {
                public final List<BlockEntity> globalBlockEntities = new ArrayList();
                public final List<BlockEntity> blockEntities = new ArrayList();
                public final Map<RenderType, BufferBuilder.RenderedBuffer> renderedLayers = new Reference2ObjectArrayMap();
                public VisibilitySet visibilitySet = new VisibilitySet();
                @Nullable
                public BufferBuilder.SortState transparencyState;

                CompileResults() {
                }
            }
        }
    }

    static enum ChunkTaskResult {
        SUCCESSFUL,
        CANCELLED;

    }

    public static class CompiledChunk {
        public static final CompiledChunk UNCOMPILED = new CompiledChunk(){

            @Override
            public boolean facesCanSeeEachother(Direction $$0, Direction $$1) {
                return false;
            }
        };
        final Set<RenderType> hasBlocks = new ObjectArraySet(RenderType.chunkBufferLayers().size());
        final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
        VisibilitySet visibilitySet = new VisibilitySet();
        @Nullable
        BufferBuilder.SortState transparencyState;

        public boolean hasNoRenderableLayers() {
            return this.hasBlocks.isEmpty();
        }

        public boolean isEmpty(RenderType $$0) {
            return !this.hasBlocks.contains((Object)$$0);
        }

        public List<BlockEntity> getRenderableBlockEntities() {
            return this.renderableBlockEntities;
        }

        public boolean facesCanSeeEachother(Direction $$0, Direction $$1) {
            return this.visibilitySet.visibilityBetween($$0, $$1);
        }
    }
}