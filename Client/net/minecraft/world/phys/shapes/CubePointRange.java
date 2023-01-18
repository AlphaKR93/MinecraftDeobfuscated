/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange
extends AbstractDoubleList {
    private final int parts;

    CubePointRange(int $$0) {
        if ($$0 <= 0) {
            throw new IllegalArgumentException("Need at least 1 part");
        }
        this.parts = $$0;
    }

    public double getDouble(int $$0) {
        return (double)$$0 / (double)this.parts;
    }

    public int size() {
        return this.parts + 1;
    }
}