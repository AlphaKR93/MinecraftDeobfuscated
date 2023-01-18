/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Void
 *  java.util.List
 *  java.util.Queue
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.locks.LockSupport
 *  java.util.function.BooleanSupplier
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import net.minecraft.util.thread.ProcessorHandle;
import org.slf4j.Logger;

public abstract class BlockableEventLoop<R extends Runnable>
implements ProfilerMeasured,
ProcessorHandle<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
    private int blockingCount;

    protected BlockableEventLoop(String $$0) {
        this.name = $$0;
        MetricsRegistry.INSTANCE.add(this);
    }

    protected abstract R wrapRunnable(Runnable var1);

    protected abstract boolean shouldRun(R var1);

    public boolean isSameThread() {
        return Thread.currentThread() == this.getRunningThread();
    }

    protected abstract Thread getRunningThread();

    protected boolean scheduleExecutables() {
        return !this.isSameThread();
    }

    public int getPendingTasksCount() {
        return this.pendingRunnables.size();
    }

    @Override
    public String name() {
        return this.name;
    }

    public <V> CompletableFuture<V> submit(Supplier<V> $$0) {
        if (this.scheduleExecutables()) {
            return CompletableFuture.supplyAsync($$0, (Executor)this);
        }
        return CompletableFuture.completedFuture((Object)$$0.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable $$0) {
        return CompletableFuture.supplyAsync(() -> {
            $$0.run();
            return null;
        }, (Executor)this);
    }

    public CompletableFuture<Void> submit(Runnable $$0) {
        if (this.scheduleExecutables()) {
            return this.submitAsync($$0);
        }
        $$0.run();
        return CompletableFuture.completedFuture(null);
    }

    public void executeBlocking(Runnable $$0) {
        if (!this.isSameThread()) {
            this.submitAsync($$0).join();
        } else {
            $$0.run();
        }
    }

    @Override
    public void tell(R $$0) {
        this.pendingRunnables.add($$0);
        LockSupport.unpark((Thread)this.getRunningThread());
    }

    public void execute(Runnable $$0) {
        if (this.scheduleExecutables()) {
            this.tell(this.wrapRunnable($$0));
        } else {
            $$0.run();
        }
    }

    public void executeIfPossible(Runnable $$0) {
        this.execute($$0);
    }

    protected void dropAllTasks() {
        this.pendingRunnables.clear();
    }

    protected void runAllTasks() {
        while (this.pollTask()) {
        }
    }

    public boolean pollTask() {
        Runnable $$0 = (Runnable)this.pendingRunnables.peek();
        if ($$0 == null) {
            return false;
        }
        if (this.blockingCount == 0 && !this.shouldRun($$0)) {
            return false;
        }
        this.doRunTask((Runnable)this.pendingRunnables.remove());
        return true;
    }

    public void managedBlock(BooleanSupplier $$0) {
        ++this.blockingCount;
        try {
            while (!$$0.getAsBoolean()) {
                if (this.pollTask()) continue;
                this.waitForTasks();
            }
        }
        finally {
            --this.blockingCount;
        }
    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos((Object)"waiting for tasks", (long)100000L);
    }

    protected void doRunTask(R $$0) {
        try {
            $$0.run();
        }
        catch (Exception $$1) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", (Object)this.name(), (Object)$$1);
            throw $$1;
        }
    }

    @Override
    public List<MetricSampler> profiledMetrics() {
        return ImmutableList.of((Object)MetricSampler.create(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::getPendingTasksCount));
    }
}