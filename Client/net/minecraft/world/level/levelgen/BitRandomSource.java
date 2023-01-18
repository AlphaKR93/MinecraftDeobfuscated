/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.util.RandomSource;

public interface BitRandomSource
extends RandomSource {
    public static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
    public static final double DOUBLE_MULTIPLIER = (double)1.110223E-16f;

    public int next(int var1);

    @Override
    default public int nextInt() {
        return this.next(32);
    }

    @Override
    default public int nextInt(int $$0) {
        int $$2;
        int $$1;
        if ($$0 <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        if (($$0 & $$0 - 1) == 0) {
            return (int)((long)$$0 * (long)this.next(31) >> 31);
        }
        while (($$1 = this.next(31)) - ($$2 = $$1 % $$0) + ($$0 - 1) < 0) {
        }
        return $$2;
    }

    @Override
    default public long nextLong() {
        int $$0 = this.next(32);
        int $$1 = this.next(32);
        long $$2 = (long)$$0 << 32;
        return $$2 + (long)$$1;
    }

    @Override
    default public boolean nextBoolean() {
        return this.next(1) != 0;
    }

    @Override
    default public float nextFloat() {
        return (float)this.next(24) * 5.9604645E-8f;
    }

    @Override
    default public double nextDouble() {
        int $$0 = this.next(26);
        int $$1 = this.next(27);
        long $$2 = ((long)$$0 << 27) + (long)$$1;
        return (double)$$2 * (double)1.110223E-16f;
    }
}