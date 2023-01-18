/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Long
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.levelgen.RandomSupport;

public class Xoroshiro128PlusPlus {
    private long seedLo;
    private long seedHi;

    public Xoroshiro128PlusPlus(RandomSupport.Seed128bit $$0) {
        this($$0.seedLo(), $$0.seedHi());
    }

    public Xoroshiro128PlusPlus(long $$0, long $$1) {
        this.seedLo = $$0;
        this.seedHi = $$1;
        if ((this.seedLo | this.seedHi) == 0L) {
            this.seedLo = -7046029254386353131L;
            this.seedHi = 7640891576956012809L;
        }
    }

    public long nextLong() {
        long $$0 = this.seedLo;
        long $$1 = this.seedHi;
        long $$2 = Long.rotateLeft((long)($$0 + $$1), (int)17) + $$0;
        this.seedLo = Long.rotateLeft((long)$$0, (int)49) ^ ($$1 ^= $$0) ^ $$1 << 21;
        this.seedHi = Long.rotateLeft((long)$$1, (int)28);
        return $$2;
    }
}