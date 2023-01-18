/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleLists
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import net.minecraft.world.phys.shapes.IndexMerger;

public class IndirectMerger
implements IndexMerger {
    private static final DoubleList EMPTY = DoubleLists.unmodifiable((DoubleList)DoubleArrayList.wrap((double[])new double[]{0.0}));
    private final double[] result;
    private final int[] firstIndices;
    private final int[] secondIndices;
    private final int resultLength;

    public IndirectMerger(DoubleList $$0, DoubleList $$1, boolean $$2, boolean $$3) {
        double $$4 = Double.NaN;
        int $$5 = $$0.size();
        int $$6 = $$1.size();
        int $$7 = $$5 + $$6;
        this.result = new double[$$7];
        this.firstIndices = new int[$$7];
        this.secondIndices = new int[$$7];
        boolean $$8 = !$$2;
        boolean $$9 = !$$3;
        int $$10 = 0;
        int $$11 = 0;
        int $$12 = 0;
        while (true) {
            double $$18;
            boolean $$15;
            boolean $$14;
            boolean $$13 = $$11 >= $$5;
            boolean bl = $$14 = $$12 >= $$6;
            if ($$13 && $$14) break;
            boolean bl2 = $$15 = !$$13 && ($$14 || $$0.getDouble($$11) < $$1.getDouble($$12) + 1.0E-7);
            if ($$15) {
                ++$$11;
                if ($$8 && ($$12 == 0 || $$14)) {
                    continue;
                }
            } else {
                ++$$12;
                if ($$9 && ($$11 == 0 || $$13)) continue;
            }
            int $$16 = $$11 - 1;
            int $$17 = $$12 - 1;
            double d = $$18 = $$15 ? $$0.getDouble($$16) : $$1.getDouble($$17);
            if (!($$4 >= $$18 - 1.0E-7)) {
                this.firstIndices[$$10] = $$16;
                this.secondIndices[$$10] = $$17;
                this.result[$$10] = $$18;
                ++$$10;
                $$4 = $$18;
                continue;
            }
            this.firstIndices[$$10 - 1] = $$16;
            this.secondIndices[$$10 - 1] = $$17;
        }
        this.resultLength = Math.max((int)1, (int)$$10);
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer $$0) {
        int $$1 = this.resultLength - 1;
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            if ($$0.merge(this.firstIndices[$$2], this.secondIndices[$$2], $$2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return this.resultLength;
    }

    @Override
    public DoubleList getList() {
        return this.resultLength <= 1 ? EMPTY : DoubleArrayList.wrap((double[])this.result, (int)this.resultLength);
    }
}