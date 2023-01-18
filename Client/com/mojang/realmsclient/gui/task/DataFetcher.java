/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.concurrent.Callable
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.TimeUnit
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.task;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.task.RepeatedDelayStrategy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.TimeSource;
import org.slf4j.Logger;

public class DataFetcher {
    static final Logger LOGGER = LogUtils.getLogger();
    final Executor executor;
    final TimeUnit resolution;
    final TimeSource timeSource;

    public DataFetcher(Executor $$0, TimeUnit $$1, TimeSource $$2) {
        this.executor = $$0;
        this.resolution = $$1;
        this.timeSource = $$2;
    }

    public <T> Task<T> createTask(String $$0, Callable<T> $$1, Duration $$2, RepeatedDelayStrategy $$3) {
        long $$4 = this.resolution.convert($$2);
        if ($$4 == 0L) {
            throw new IllegalArgumentException("Period of " + $$2 + " too short for selected resolution of " + this.resolution);
        }
        return new Task<T>($$0, $$1, $$4, $$3);
    }

    public Subscription createSubscription() {
        return new Subscription();
    }

    public class Task<T> {
        private final String id;
        private final Callable<T> updater;
        private final long period;
        private final RepeatedDelayStrategy repeatStrategy;
        @Nullable
        private CompletableFuture<ComputationResult<T>> pendingTask;
        @Nullable
        SuccessfulComputationResult<T> lastResult;
        private long nextUpdate = -1L;

        Task(String $$1, Callable<T> $$2, long $$3, RepeatedDelayStrategy $$4) {
            this.id = $$1;
            this.updater = $$2;
            this.period = $$3;
            this.repeatStrategy = $$4;
        }

        void updateIfNeeded(long $$0) {
            if (this.pendingTask != null) {
                ComputationResult $$12 = (ComputationResult)((Object)this.pendingTask.getNow(null));
                if ($$12 == null) {
                    return;
                }
                this.pendingTask = null;
                long $$2 = $$12.time;
                $$12.value().ifLeft($$1 -> {
                    this.lastResult = new SuccessfulComputationResult<Object>($$1, $$2);
                    this.nextUpdate = $$2 + this.period * this.repeatStrategy.delayCyclesAfterSuccess();
                }).ifRight($$1 -> {
                    long $$2 = this.repeatStrategy.delayCyclesAfterFailure();
                    LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.id, $$2, $$1});
                    this.nextUpdate = $$2 + this.period * $$2;
                });
            }
            if (this.nextUpdate <= $$0) {
                this.pendingTask = CompletableFuture.supplyAsync(() -> {
                    try {
                        Object $$0 = this.updater.call();
                        long $$1 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                        return new ComputationResult(Either.left((Object)$$0), $$1);
                    }
                    catch (Exception $$2) {
                        long $$3 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                        return new ComputationResult(Either.right((Object)((Object)$$2)), $$3);
                    }
                }, (Executor)DataFetcher.this.executor);
            }
        }

        void reset() {
            this.pendingTask = null;
            this.lastResult = null;
            this.nextUpdate = -1L;
        }
    }

    public class Subscription {
        private final List<SubscribedTask<?>> subscriptions = new ArrayList();

        public <T> void subscribe(Task<T> $$0, Consumer<T> $$1) {
            SubscribedTask<T> $$2 = new SubscribedTask<T>($$0, $$1);
            this.subscriptions.add($$2);
            $$2.runCallbackIfNeeded();
        }

        public void forceUpdate() {
            for (SubscribedTask $$0 : this.subscriptions) {
                $$0.runCallback();
            }
        }

        public void tick() {
            for (SubscribedTask $$0 : this.subscriptions) {
                $$0.update(DataFetcher.this.timeSource.get(DataFetcher.this.resolution));
            }
        }

        public void reset() {
            for (SubscribedTask $$0 : this.subscriptions) {
                $$0.reset();
            }
        }
    }

    class SubscribedTask<T> {
        private final Task<T> task;
        private final Consumer<T> output;
        private long lastCheckTime = -1L;

        SubscribedTask(Task<T> $$0, Consumer<T> $$1) {
            this.task = $$0;
            this.output = $$1;
        }

        void update(long $$0) {
            this.task.updateIfNeeded($$0);
            this.runCallbackIfNeeded();
        }

        void runCallbackIfNeeded() {
            SuccessfulComputationResult $$0 = this.task.lastResult;
            if ($$0 != null && this.lastCheckTime < $$0.time) {
                this.output.accept($$0.value);
                this.lastCheckTime = $$0.time;
            }
        }

        void runCallback() {
            SuccessfulComputationResult $$0 = this.task.lastResult;
            if ($$0 != null) {
                this.output.accept($$0.value);
                this.lastCheckTime = $$0.time;
            }
        }

        void reset() {
            this.task.reset();
            this.lastCheckTime = -1L;
        }
    }

    record SuccessfulComputationResult<T>(T value, long time) {
    }

    record ComputationResult<T>(Either<T, Exception> value, long time) {
    }
}