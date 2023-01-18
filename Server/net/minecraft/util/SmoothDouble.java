/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.util;

import net.minecraft.util.Mth;

public class SmoothDouble {
    private double targetValue;
    private double remainingValue;
    private double lastAmount;

    public double getNewDeltaValue(double $$0, double $$1) {
        this.targetValue += $$0;
        double $$2 = this.targetValue - this.remainingValue;
        double $$3 = Mth.lerp(0.5, this.lastAmount, $$2);
        double $$4 = Math.signum((double)$$2);
        if ($$4 * $$2 > $$4 * this.lastAmount) {
            $$2 = $$3;
        }
        this.lastAmount = $$3;
        this.remainingValue += $$2 * $$1;
        return $$2 * $$1;
    }

    public void reset() {
        this.targetValue = 0.0;
        this.remainingValue = 0.0;
        this.lastAmount = 0.0;
    }
}