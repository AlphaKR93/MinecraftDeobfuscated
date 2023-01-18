/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.Object
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IndexMerger {
    public DoubleList getList();

    public boolean forMergedIndexes(IndexConsumer var1);

    public int size();

    public static interface IndexConsumer {
        public boolean merge(int var1, int var2, int var3);
    }
}