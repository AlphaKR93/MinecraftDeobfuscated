/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.Shapes;

public final class DiscreteCubeMerger
implements IndexMerger {
    private final CubePointRange result;
    private final int firstDiv;
    private final int secondDiv;

    DiscreteCubeMerger(int $$0, int $$1) {
        this.result = new CubePointRange((int)Shapes.lcm($$0, $$1));
        int $$2 = IntMath.gcd((int)$$0, (int)$$1);
        this.firstDiv = $$0 / $$2;
        this.secondDiv = $$1 / $$2;
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer $$0) {
        int $$1 = this.result.size() - 1;
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            if ($$0.merge($$2 / this.secondDiv, $$2 / this.firstDiv, $$2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return this.result.size();
    }

    @Override
    public DoubleList getList() {
        return this.result;
    }
}