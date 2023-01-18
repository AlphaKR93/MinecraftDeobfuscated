/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.lang.System
 *  java.util.concurrent.atomic.AtomicLong
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    @VisibleForTesting
    public static long mixStafford13(long $$0) {
        $$0 = ($$0 ^ $$0 >>> 30) * -4658895280553007687L;
        $$0 = ($$0 ^ $$0 >>> 27) * -7723592293110705685L;
        return $$0 ^ $$0 >>> 31;
    }

    public static Seed128bit upgradeSeedTo128bit(long $$0) {
        long $$1 = $$0 ^ 0x6A09E667F3BCC909L;
        long $$2 = $$1 + -7046029254386353131L;
        return new Seed128bit(RandomSupport.mixStafford13($$1), RandomSupport.mixStafford13($$2));
    }

    public static long generateUniqueSeed() {
        return SEED_UNIQUIFIER.updateAndGet($$0 -> $$0 * 1181783497276652981L) ^ System.nanoTime();
    }

    public record Seed128bit(long seedLo, long seedHi) {
    }
}