/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicInteger
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.InactiveProfiler;

public class SimpleReloadInstance<S>
implements ReloadInstance {
    private static final int PREPARATION_PROGRESS_WEIGHT = 2;
    private static final int EXTRA_RELOAD_PROGRESS_WEIGHT = 2;
    private static final int LISTENER_PROGRESS_WEIGHT = 1;
    protected final CompletableFuture<Unit> allPreparations = new CompletableFuture();
    protected CompletableFuture<List<S>> allDone;
    final Set<PreparableReloadListener> preparingListeners;
    private final int listenerCount;
    private int startedReloads;
    private int finishedReloads;
    private final AtomicInteger startedTaskCounter = new AtomicInteger();
    private final AtomicInteger doneTaskCounter = new AtomicInteger();

    public static SimpleReloadInstance<Void> of(ResourceManager $$0, List<PreparableReloadListener> $$12, Executor $$22, Executor $$32, CompletableFuture<Unit> $$42) {
        return new SimpleReloadInstance<Void>($$22, $$32, $$0, $$12, ($$1, $$2, $$3, $$4, $$5) -> $$3.reload($$1, $$2, InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, $$22, $$5), $$42);
    }

    protected SimpleReloadInstance(Executor $$0, final Executor $$12, ResourceManager $$2, List<PreparableReloadListener> $$3, StateFactory<S> $$4, CompletableFuture<Unit> $$5) {
        this.listenerCount = $$3.size();
        this.startedTaskCounter.incrementAndGet();
        $$5.thenRun(() -> ((AtomicInteger)this.doneTaskCounter).incrementAndGet());
        ArrayList $$6 = Lists.newArrayList();
        Object $$7 = $$5;
        this.preparingListeners = Sets.newHashSet($$3);
        for (final PreparableReloadListener $$8 : $$3) {
            final CompletableFuture<Unit> $$9 = $$7;
            CompletableFuture<S> $$10 = $$4.create(new PreparableReloadListener.PreparationBarrier(){

                @Override
                public <T> CompletableFuture<T> wait(T $$0) {
                    $$12.execute(() -> {
                        SimpleReloadInstance.this.preparingListeners.remove((Object)$$8);
                        if (SimpleReloadInstance.this.preparingListeners.isEmpty()) {
                            SimpleReloadInstance.this.allPreparations.complete((Object)Unit.INSTANCE);
                        }
                    });
                    return SimpleReloadInstance.this.allPreparations.thenCombine((CompletionStage)$$9, ($$1, $$2) -> $$0);
                }
            }, $$2, $$8, $$1 -> {
                this.startedTaskCounter.incrementAndGet();
                $$0.execute(() -> {
                    $$1.run();
                    this.doneTaskCounter.incrementAndGet();
                });
            }, $$1 -> {
                ++this.startedReloads;
                $$12.execute(() -> {
                    $$1.run();
                    ++this.finishedReloads;
                });
            });
            $$6.add($$10);
            $$7 = $$10;
        }
        this.allDone = Util.sequenceFailFast($$6);
    }

    @Override
    public CompletableFuture<?> done() {
        return this.allDone;
    }

    @Override
    public float getActualProgress() {
        int $$0 = this.listenerCount - this.preparingListeners.size();
        float $$1 = this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + $$0 * 1;
        float $$2 = this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1;
        return $$1 / $$2;
    }

    public static ReloadInstance create(ResourceManager $$0, List<PreparableReloadListener> $$1, Executor $$2, Executor $$3, CompletableFuture<Unit> $$4, boolean $$5) {
        if ($$5) {
            return new ProfiledReloadInstance($$0, $$1, $$2, $$3, $$4);
        }
        return SimpleReloadInstance.of($$0, $$1, $$2, $$3, $$4);
    }

    protected static interface StateFactory<S> {
        public CompletableFuture<S> create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
    }
}