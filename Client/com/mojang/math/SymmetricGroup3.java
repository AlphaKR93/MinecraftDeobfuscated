/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Arrays
 *  org.joml.Matrix3f
 */
package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;

public enum SymmetricGroup3 {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] permutation;
    private final Matrix3f transformation;
    private static final int ORDER = 3;
    private static final SymmetricGroup3[][] cayleyTable;

    private SymmetricGroup3(int $$0, int $$1, int $$2) {
        this.permutation = new int[]{$$0, $$1, $$2};
        this.transformation = new Matrix3f();
        this.transformation.set(this.permutation(0), 0, 1.0f);
        this.transformation.set(this.permutation(1), 1, 1.0f);
        this.transformation.set(this.permutation(2), 2, 1.0f);
    }

    public SymmetricGroup3 compose(SymmetricGroup3 $$0) {
        return cayleyTable[this.ordinal()][$$0.ordinal()];
    }

    public int permutation(int $$0) {
        return this.permutation[$$0];
    }

    public Matrix3f transformation() {
        return this.transformation;
    }

    static {
        cayleyTable = Util.make(new SymmetricGroup3[SymmetricGroup3.values().length][SymmetricGroup3.values().length], $$0 -> {
            for (SymmetricGroup3 $$12 : SymmetricGroup3.values()) {
                for (SymmetricGroup3 $$2 : SymmetricGroup3.values()) {
                    SymmetricGroup3 $$5;
                    int[] $$3 = new int[3];
                    for (int $$4 = 0; $$4 < 3; ++$$4) {
                        $$3[$$4] = $$12.permutation[$$2.permutation[$$4]];
                    }
                    $$0[$$12.ordinal()][$$2.ordinal()] = $$5 = (SymmetricGroup3)((Object)((Object)Arrays.stream((Object[])SymmetricGroup3.values()).filter($$1 -> Arrays.equals((int[])$$1.permutation, (int[])$$3)).findFirst().get()));
                }
            }
        });
    }
}