/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.concurrent.atomic.AtomicLong
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.world.level.levelgen.BitRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class LegacyRandomSource
implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public LegacyRandomSource(long $$0) {
        this.setSeed($$0);
    }

    @Override
    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new LegacyPositionalRandomFactory(this.nextLong());
    }

    @Override
    public void setSeed(long $$0) {
        if (!this.seed.compareAndSet(this.seed.get(), ($$0 ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
        }
        this.gaussianSource.reset();
    }

    @Override
    public int next(int $$0) {
        long $$2;
        long $$1 = this.seed.get();
        if (!this.seed.compareAndSet($$1, $$2 = $$1 * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
        }
        return (int)($$2 >> 48 - $$0);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    public static class LegacyPositionalRandomFactory
    implements PositionalRandomFactory {
        private final long seed;

        public LegacyPositionalRandomFactory(long $$0) {
            this.seed = $$0;
        }

        @Override
        public RandomSource at(int $$0, int $$1, int $$2) {
            long $$3 = Mth.getSeed($$0, $$1, $$2);
            long $$4 = $$3 ^ this.seed;
            return new LegacyRandomSource($$4);
        }

        @Override
        public RandomSource fromHashOf(String $$0) {
            int $$1 = $$0.hashCode();
            return new LegacyRandomSource((long)$$1 ^ this.seed);
        }

        @Override
        @VisibleForTesting
        public void parityConfigString(StringBuilder $$0) {
            $$0.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}