/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2BooleanFunction
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.concurrent.ConcurrentLinkedQueue
 *  java.util.concurrent.Executor
 *  java.util.concurrent.RejectedExecutionException
 *  java.util.concurrent.atomic.AtomicInteger
 *  org.slf4j.Logger
 */
package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.StrictQueue;
import org.slf4j.Logger;

public class ProcessorMailbox<T>
implements ProfilerMeasured,
ProcessorHandle<T>,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CLOSED_BIT = 1;
    private static final int SCHEDULED_BIT = 2;
    private final AtomicInteger status = new AtomicInteger(0);
    private final StrictQueue<? super T, ? extends Runnable> queue;
    private final Executor dispatcher;
    private final String name;

    public static ProcessorMailbox<Runnable> create(Executor $$0, String $$1) {
        return new ProcessorMailbox<Runnable>(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue()), $$0, $$1);
    }

    public ProcessorMailbox(StrictQueue<? super T, ? extends Runnable> $$0, Executor $$1, String $$2) {
        this.dispatcher = $$1;
        this.queue = $$0;
        this.name = $$2;
        MetricsRegistry.INSTANCE.add(this);
    }

    private boolean setAsScheduled() {
        int $$0;
        do {
            if ((($$0 = this.status.get()) & 3) == 0) continue;
            return false;
        } while (!this.status.compareAndSet($$0, $$0 | 2));
        return true;
    }

    private void setAsIdle() {
        int $$0;
        while (!this.status.compareAndSet($$0 = this.status.get(), $$0 & 0xFFFFFFFD)) {
        }
    }

    private boolean canBeScheduled() {
        if ((this.status.get() & 1) != 0) {
            return false;
        }
        return !this.queue.isEmpty();
    }

    @Override
    public void close() {
        int $$0;
        while (!this.status.compareAndSet($$0 = this.status.get(), $$0 | 1)) {
        }
    }

    private boolean shouldProcess() {
        return (this.status.get() & 2) != 0;
    }

    private boolean pollTask() {
        if (!this.shouldProcess()) {
            return false;
        }
        Runnable $$0 = this.queue.pop();
        if ($$0 == null) {
            return false;
        }
        Util.wrapThreadWithTaskName(this.name, $$0).run();
        return true;
    }

    public void run() {
        try {
            this.pollUntil($$0 -> $$0 == 0);
        }
        finally {
            this.setAsIdle();
            this.registerForExecution();
        }
    }

    public void runAll() {
        try {
            this.pollUntil($$0 -> true);
        }
        finally {
            this.setAsIdle();
            this.registerForExecution();
        }
    }

    @Override
    public void tell(T $$0) {
        this.queue.push($$0);
        this.registerForExecution();
    }

    private void registerForExecution() {
        if (this.canBeScheduled() && this.setAsScheduled()) {
            try {
                this.dispatcher.execute((Runnable)this);
            }
            catch (RejectedExecutionException $$0) {
                try {
                    this.dispatcher.execute((Runnable)this);
                }
                catch (RejectedExecutionException $$1) {
                    LOGGER.error("Cound not schedule mailbox", (Throwable)$$1);
                }
            }
        }
    }

    private int pollUntil(Int2BooleanFunction $$0) {
        int $$1 = 0;
        while ($$0.get($$1) && this.pollTask()) {
            ++$$1;
        }
        return $$1;
    }

    public int size() {
        return this.queue.size();
    }

    public boolean hasWork() {
        return this.shouldProcess() && !this.queue.isEmpty();
    }

    public String toString() {
        return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<MetricSampler> profiledMetrics() {
        return ImmutableList.of((Object)MetricSampler.create(this.name + "-queue-size", MetricCategory.MAIL_BOXES, this::size));
    }
}