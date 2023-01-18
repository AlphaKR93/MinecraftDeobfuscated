/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Void
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.atomic.AtomicLong
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class ProfiledReloadInstance
extends SimpleReloadInstance<State> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Stopwatch total = Stopwatch.createUnstarted();

    public ProfiledReloadInstance(ResourceManager $$0, List<PreparableReloadListener> $$12, Executor $$2, Executor $$32, CompletableFuture<Unit> $$42) {
        super($$2, $$32, $$0, $$12, ($$1, $$22, $$3, $$4, $$52) -> {
            AtomicLong $$6 = new AtomicLong();
            AtomicLong $$7 = new AtomicLong();
            ActiveProfiler $$8 = new ActiveProfiler(Util.timeSource, () -> 0, false);
            ActiveProfiler $$9 = new ActiveProfiler(Util.timeSource, () -> 0, false);
            CompletableFuture<Void> $$10 = $$3.reload($$1, $$22, $$8, $$9, $$2 -> $$4.execute(() -> {
                Runnable $$2 = Util.getNanos();
                $$2.run();
                $$6.addAndGet(Util.getNanos() - $$2);
            }), $$2 -> $$52.execute(() -> {
                Runnable $$2 = Util.getNanos();
                $$2.run();
                $$7.addAndGet(Util.getNanos() - $$2);
            }));
            return $$10.thenApplyAsync($$5 -> {
                LOGGER.debug("Finished reloading " + $$3.getName());
                return new State($$3.getName(), $$8.getResults(), $$9.getResults(), $$6, $$7);
            }, $$32);
        }, $$42);
        this.total.start();
        this.allDone = this.allDone.thenApplyAsync(this::finish, $$32);
    }

    private List<State> finish(List<State> $$0) {
        this.total.stop();
        long $$1 = 0L;
        LOGGER.info("Resource reload finished after {} ms", (Object)this.total.elapsed(TimeUnit.MILLISECONDS));
        for (State $$2 : $$0) {
            ProfileResults $$3 = $$2.preparationResult;
            ProfileResults $$4 = $$2.reloadResult;
            long $$5 = TimeUnit.NANOSECONDS.toMillis($$2.preparationNanos.get());
            long $$6 = TimeUnit.NANOSECONDS.toMillis($$2.reloadNanos.get());
            long $$7 = $$5 + $$6;
            String $$8 = $$2.name;
            LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", new Object[]{$$8, $$7, $$5, $$6});
            $$1 += $$6;
        }
        LOGGER.info("Total blocking time: {} ms", (Object)$$1);
        return $$0;
    }

    public static class State {
        final String name;
        final ProfileResults preparationResult;
        final ProfileResults reloadResult;
        final AtomicLong preparationNanos;
        final AtomicLong reloadNanos;

        State(String $$0, ProfileResults $$1, ProfileResults $$2, AtomicLong $$3, AtomicLong $$4) {
            this.name = $$0;
            this.preparationResult = $$1;
            this.reloadResult = $$2;
            this.preparationNanos = $$3;
            this.reloadNanos = $$4;
        }
    }
}