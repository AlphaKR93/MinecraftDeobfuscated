/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.util.TaskChainer;
import org.slf4j.Logger;

public class FutureChain
implements TaskChainer,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private CompletableFuture<?> head = CompletableFuture.completedFuture(null);
    private final Executor checkedExecutor = $$1 -> {
        if (!this.closed) {
            $$0.execute($$1);
        }
    };
    private volatile boolean closed;

    public FutureChain(Executor $$0) {
    }

    @Override
    public void append(TaskChainer.DelayedTask $$02) {
        this.head = this.head.thenComposeAsync($$1 -> $$02.submit(this.checkedExecutor), this.checkedExecutor).exceptionally($$0 -> {
            if ($$0 instanceof CompletionException) {
                CompletionException $$1 = (CompletionException)$$0;
                $$0 = $$1.getCause();
            }
            if ($$0 instanceof CancellationException) {
                CancellationException $$2 = (CancellationException)$$0;
                throw $$2;
            }
            LOGGER.error("Chain link failed, continuing to next one", $$0);
            return null;
        });
    }

    public void close() {
        this.closed = true;
    }
}