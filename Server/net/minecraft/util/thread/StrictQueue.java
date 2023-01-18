/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Queue
 *  java.util.concurrent.atomic.AtomicInteger
 *  javax.annotation.Nullable
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T, F> {
    @Nullable
    public F pop();

    public boolean push(T var1);

    public boolean isEmpty();

    public int size();

    public static final class FixedPriorityQueue
    implements StrictQueue<IntRunnable, Runnable> {
        private final Queue<Runnable>[] queues;
        private final AtomicInteger size = new AtomicInteger();

        public FixedPriorityQueue(int $$0) {
            this.queues = new Queue[$$0];
            for (int $$1 = 0; $$1 < $$0; ++$$1) {
                this.queues[$$1] = Queues.newConcurrentLinkedQueue();
            }
        }

        @Override
        @Nullable
        public Runnable pop() {
            for (Queue<Runnable> $$0 : this.queues) {
                Runnable $$1 = (Runnable)$$0.poll();
                if ($$1 == null) continue;
                this.size.decrementAndGet();
                return $$1;
            }
            return null;
        }

        @Override
        public boolean push(IntRunnable $$0) {
            int $$1 = $$0.priority;
            if ($$1 >= this.queues.length || $$1 < 0) {
                throw new IndexOutOfBoundsException(String.format((Locale)Locale.ROOT, (String)"Priority %d not supported. Expected range [0-%d]", (Object[])new Object[]{$$1, this.queues.length - 1}));
            }
            this.queues[$$1].add((Object)$$0);
            this.size.incrementAndGet();
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.size.get() == 0;
        }

        @Override
        public int size() {
            return this.size.get();
        }
    }

    public static final class IntRunnable
    implements Runnable {
        final int priority;
        private final Runnable task;

        public IntRunnable(int $$0, Runnable $$1) {
            this.priority = $$0;
            this.task = $$1;
        }

        public void run() {
            this.task.run();
        }

        public int getPriority() {
            return this.priority;
        }
    }

    public static final class QueueStrictQueue<T>
    implements StrictQueue<T, T> {
        private final Queue<T> queue;

        public QueueStrictQueue(Queue<T> $$0) {
            this.queue = $$0;
        }

        @Override
        @Nullable
        public T pop() {
            return (T)this.queue.poll();
        }

        @Override
        public boolean push(T $$0) {
            return this.queue.add($$0);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        @Override
        public int size() {
            return this.queue.size();
        }
    }
}