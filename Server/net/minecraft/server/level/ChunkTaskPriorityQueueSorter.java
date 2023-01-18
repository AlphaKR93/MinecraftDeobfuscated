/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.AutoCloseable
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  java.util.function.IntConsumer
 *  java.util.function.IntSupplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class ChunkTaskPriorityQueueSorter
implements ChunkHolder.LevelChangeListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ProcessorHandle<?>, ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<Unit>, ?>>> queues;
    private final Set<ProcessorHandle<?>> sleeping;
    private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;

    public ChunkTaskPriorityQueueSorter(List<ProcessorHandle<?>> $$0, Executor $$12, int $$2) {
        this.queues = (Map)$$0.stream().collect(Collectors.toMap((Function)Function.identity(), $$1 -> new ChunkTaskPriorityQueue($$1.name() + "_queue", $$2)));
        this.sleeping = Sets.newHashSet($$0);
        this.mailbox = new ProcessorMailbox<StrictQueue.IntRunnable>(new StrictQueue.FixedPriorityQueue(4), $$12, "sorter");
    }

    public boolean hasWork() {
        return this.mailbox.hasWork() || this.queues.values().stream().anyMatch(ChunkTaskPriorityQueue::hasWork);
    }

    public static <T> Message<T> message(Function<ProcessorHandle<Unit>, T> $$0, long $$1, IntSupplier $$2) {
        return new Message<T>($$0, $$1, $$2);
    }

    public static Message<Runnable> message(Runnable $$0, long $$12, IntSupplier $$2) {
        return new Message<Runnable>($$1 -> () -> {
            $$0.run();
            $$1.tell(Unit.INSTANCE);
        }, $$12, $$2);
    }

    public static Message<Runnable> message(ChunkHolder $$0, Runnable $$1) {
        return ChunkTaskPriorityQueueSorter.message($$1, $$0.getPos().toLong(), $$0::getQueueLevel);
    }

    public static <T> Message<T> message(ChunkHolder $$0, Function<ProcessorHandle<Unit>, T> $$1) {
        return ChunkTaskPriorityQueueSorter.message($$1, $$0.getPos().toLong(), $$0::getQueueLevel);
    }

    public static Release release(Runnable $$0, long $$1, boolean $$2) {
        return new Release($$0, $$1, $$2);
    }

    public <T> ProcessorHandle<Message<T>> getProcessor(ProcessorHandle<T> $$0, boolean $$1) {
        return (ProcessorHandle)this.mailbox.ask($$2 -> new StrictQueue.IntRunnable(0, () -> {
            this.getQueue($$0);
            $$2.tell(ProcessorHandle.of("chunk priority sorter around " + $$0.name(), $$2 -> this.submit($$0, $$2.task, $$2.pos, $$2.level, $$1)));
        })).join();
    }

    public ProcessorHandle<Release> getReleaseProcessor(ProcessorHandle<Runnable> $$0) {
        return (ProcessorHandle)this.mailbox.ask($$1 -> new StrictQueue.IntRunnable(0, () -> $$1.tell(ProcessorHandle.of("chunk priority sorter around " + $$0.name(), $$1 -> this.release($$0, $$1.pos, $$1.task, $$1.clearQueue))))).join();
    }

    @Override
    public void onLevelChange(ChunkPos $$0, IntSupplier $$1, int $$2, IntConsumer $$3) {
        this.mailbox.tell(new StrictQueue.IntRunnable(0, () -> {
            int $$4 = $$1.getAsInt();
            this.queues.values().forEach($$3 -> $$3.resortChunkTasks($$4, $$0, $$2));
            $$3.accept($$2);
        }));
    }

    private <T> void release(ProcessorHandle<T> $$0, long $$1, Runnable $$2, boolean $$3) {
        this.mailbox.tell(new StrictQueue.IntRunnable(1, () -> {
            ChunkTaskPriorityQueue $$4 = this.getQueue($$0);
            $$4.release($$1, $$3);
            if (this.sleeping.remove((Object)$$0)) {
                this.pollTask($$4, $$0);
            }
            $$2.run();
        }));
    }

    private <T> void submit(ProcessorHandle<T> $$0, Function<ProcessorHandle<Unit>, T> $$1, long $$2, IntSupplier $$3, boolean $$4) {
        this.mailbox.tell(new StrictQueue.IntRunnable(2, () -> {
            ChunkTaskPriorityQueue $$5 = this.getQueue($$0);
            int $$6 = $$3.getAsInt();
            $$5.submit(Optional.of((Object)$$1), $$2, $$6);
            if ($$4) {
                $$5.submit(Optional.empty(), $$2, $$6);
            }
            if (this.sleeping.remove((Object)$$0)) {
                this.pollTask($$5, $$0);
            }
        }));
    }

    private <T> void pollTask(ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> $$0, ProcessorHandle<T> $$1) {
        this.mailbox.tell(new StrictQueue.IntRunnable(3, () -> {
            Stream $$22 = $$0.pop();
            if ($$22 == null) {
                this.sleeping.add((Object)$$1);
            } else {
                CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$22.map($$1 -> (CompletableFuture)$$1.map($$1::ask, $$0 -> {
                    $$0.run();
                    return CompletableFuture.completedFuture((Object)((Object)Unit.INSTANCE));
                })).toArray(CompletableFuture[]::new))).thenAccept($$2 -> this.pollTask($$0, $$1));
            }
        }));
    }

    private <T> ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> getQueue(ProcessorHandle<T> $$0) {
        ChunkTaskPriorityQueue $$1 = (ChunkTaskPriorityQueue)this.queues.get($$0);
        if ($$1 == null) {
            throw Util.pauseInIde(new IllegalArgumentException("No queue for: " + $$0));
        }
        return $$1;
    }

    @VisibleForTesting
    public String getDebugStatus() {
        return (String)this.queues.entrySet().stream().map($$02 -> ((ProcessorHandle)$$02.getKey()).name() + "=[" + (String)((ChunkTaskPriorityQueue)$$02.getValue()).getAcquired().stream().map($$0 -> $$0 + ":" + new ChunkPos((long)$$0)).collect(Collectors.joining((CharSequence)",")) + "]").collect(Collectors.joining((CharSequence)",")) + ", s=" + this.sleeping.size();
    }

    public void close() {
        this.queues.keySet().forEach(ProcessorHandle::close);
    }

    public static final class Message<T> {
        final Function<ProcessorHandle<Unit>, T> task;
        final long pos;
        final IntSupplier level;

        Message(Function<ProcessorHandle<Unit>, T> $$0, long $$1, IntSupplier $$2) {
            this.task = $$0;
            this.pos = $$1;
            this.level = $$2;
        }
    }

    public static final class Release {
        final Runnable task;
        final long pos;
        final boolean clearQueue;

        Release(Runnable $$0, long $$1, boolean $$2) {
            this.task = $$0;
            this.pos = $$1;
            this.clearQueue = $$2;
        }
    }
}