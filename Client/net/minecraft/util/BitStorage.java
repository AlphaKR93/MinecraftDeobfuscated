/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.IntConsumer
 */
package net.minecraft.util;

import java.util.function.IntConsumer;

public interface BitStorage {
    public int getAndSet(int var1, int var2);

    public void set(int var1, int var2);

    public int get(int var1);

    public long[] getRaw();

    public int getSize();

    public int getBits();

    public void getAll(IntConsumer var1);

    public void unpack(int[] var1);

    public BitStorage copy();
}