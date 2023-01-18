/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Void
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 */
package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public interface PreparableReloadListener {
    public CompletableFuture<Void> reload(PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6);

    default public String getName() {
        return this.getClass().getSimpleName();
    }

    public static interface PreparationBarrier {
        public <T> CompletableFuture<T> wait(T var1);
    }
}