/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.IndexMerger;

public class NonOverlappingMerger
extends AbstractDoubleList
implements IndexMerger {
    private final DoubleList lower;
    private final DoubleList upper;
    private final boolean swap;

    protected NonOverlappingMerger(DoubleList $$0, DoubleList $$1, boolean $$2) {
        this.lower = $$0;
        this.upper = $$1;
        this.swap = $$2;
    }

    @Override
    public int size() {
        return this.lower.size() + this.upper.size();
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer $$0) {
        if (this.swap) {
            return this.forNonSwappedIndexes(($$1, $$2, $$3) -> $$0.merge($$2, $$1, $$3));
        }
        return this.forNonSwappedIndexes($$0);
    }

    private boolean forNonSwappedIndexes(IndexMerger.IndexConsumer $$0) {
        int $$1 = this.lower.size();
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            if ($$0.merge($$2, -1, $$2)) continue;
            return false;
        }
        int $$3 = this.upper.size() - 1;
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            if ($$0.merge($$1 - 1, $$4, $$1 + $$4)) continue;
            return false;
        }
        return true;
    }

    public double getDouble(int $$0) {
        if ($$0 < this.lower.size()) {
            return this.lower.getDouble($$0);
        }
        return this.upper.getDouble($$0 - this.lower.size());
    }

    @Override
    public DoubleList getList() {
        return this;
    }
}