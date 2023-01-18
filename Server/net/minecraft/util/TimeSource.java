/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.concurrent.TimeUnit
 *  java.util.function.LongSupplier
 */
package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface TimeSource {
    public long get(TimeUnit var1);

    public static interface NanoTimeSource
    extends TimeSource,
    LongSupplier {
        @Override
        default public long get(TimeUnit $$0) {
            return $$0.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
        }
    }
}