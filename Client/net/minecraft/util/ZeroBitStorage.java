/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.function.IntConsumer
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.util;

import java.util.Arrays;
import java.util.function.IntConsumer;
import net.minecraft.util.BitStorage;
import org.apache.commons.lang3.Validate;

public class ZeroBitStorage
implements BitStorage {
    public static final long[] RAW = new long[0];
    private final int size;

    public ZeroBitStorage(int $$0) {
        this.size = $$0;
    }

    @Override
    public int getAndSet(int $$0, int $$1) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)$$0);
        Validate.inclusiveBetween((long)0L, (long)0L, (long)$$1);
        return 0;
    }

    @Override
    public void set(int $$0, int $$1) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)$$0);
        Validate.inclusiveBetween((long)0L, (long)0L, (long)$$1);
    }

    @Override
    public int get(int $$0) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)$$0);
        return 0;
    }

    @Override
    public long[] getRaw() {
        return RAW;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getBits() {
        return 0;
    }

    @Override
    public void getAll(IntConsumer $$0) {
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            $$0.accept(0);
        }
    }

    @Override
    public void unpack(int[] $$0) {
        Arrays.fill((int[])$$0, (int)0, (int)this.size, (int)0);
    }

    @Override
    public BitStorage copy() {
        return this;
    }
}