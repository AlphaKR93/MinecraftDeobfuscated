/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static TaskChainer immediate(Executor $$0) {
        return $$1 -> $$1.submit($$0).exceptionally($$0 -> {
            LOGGER.error("Task failed", $$0);
            return null;
        });
    }

    public void append(DelayedTask var1);

    public static interface DelayedTask {
        public CompletableFuture<?> submit(Executor var1);
    }
}