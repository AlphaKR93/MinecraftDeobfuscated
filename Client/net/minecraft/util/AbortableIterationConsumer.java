/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.function.Consumer
 */
package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer<T> {
    public Continuation accept(T var1);

    public static <T> AbortableIterationConsumer<T> forConsumer(Consumer<T> $$0) {
        return $$1 -> {
            $$0.accept($$1);
            return Continuation.CONTINUE;
        };
    }

    public static enum Continuation {
        CONTINUE,
        ABORT;


        public boolean shouldAbort() {
            return this == ABORT;
        }
    }
}