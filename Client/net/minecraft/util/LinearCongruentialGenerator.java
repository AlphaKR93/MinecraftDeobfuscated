/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.util;

public class LinearCongruentialGenerator {
    private static final long MULTIPLIER = 6364136223846793005L;
    private static final long INCREMENT = 1442695040888963407L;

    public static long next(long $$0, long $$1) {
        $$0 *= $$0 * 6364136223846793005L + 1442695040888963407L;
        return $$0 += $$1;
    }
}