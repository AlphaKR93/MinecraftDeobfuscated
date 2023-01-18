/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SimplexNoise {
    protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private static final double SQRT_3 = Math.sqrt((double)3.0);
    private static final double F2 = 0.5 * (SQRT_3 - 1.0);
    private static final double G2 = (3.0 - SQRT_3) / 6.0;
    private final int[] p = new int[512];
    public final double xo;
    public final double yo;
    public final double zo;

    public SimplexNoise(RandomSource $$0) {
        this.xo = $$0.nextDouble() * 256.0;
        this.yo = $$0.nextDouble() * 256.0;
        this.zo = $$0.nextDouble() * 256.0;
        for (int $$1 = 0; $$1 < 256; ++$$1) {
            this.p[$$1] = $$1;
        }
        for (int $$2 = 0; $$2 < 256; ++$$2) {
            int $$3 = $$0.nextInt(256 - $$2);
            int $$4 = this.p[$$2];
            this.p[$$2] = this.p[$$3 + $$2];
            this.p[$$3 + $$2] = $$4;
        }
    }

    private int p(int $$0) {
        return this.p[$$0 & 0xFF];
    }

    protected static double dot(int[] $$0, double $$1, double $$2, double $$3) {
        return (double)$$0[0] * $$1 + (double)$$0[1] * $$2 + (double)$$0[2] * $$3;
    }

    private double getCornerNoise3D(int $$0, double $$1, double $$2, double $$3, double $$4) {
        double $$7;
        double $$5 = $$4 - $$1 * $$1 - $$2 * $$2 - $$3 * $$3;
        if ($$5 < 0.0) {
            double $$6 = 0.0;
        } else {
            $$5 *= $$5;
            $$7 = $$5 * $$5 * SimplexNoise.dot(GRADIENT[$$0], $$1, $$2, $$3);
        }
        return $$7;
    }

    public double getValue(double $$0, double $$1) {
        int $$13;
        int $$12;
        double $$7;
        double $$9;
        int $$4;
        double $$5;
        double $$2 = ($$0 + $$1) * F2;
        int $$3 = Mth.floor($$0 + $$2);
        double $$6 = (double)$$3 - ($$5 = (double)($$3 + ($$4 = Mth.floor($$1 + $$2))) * G2);
        double $$8 = $$0 - $$6;
        if ($$8 > ($$9 = $$1 - ($$7 = (double)$$4 - $$5))) {
            boolean $$10 = true;
            boolean $$11 = false;
        } else {
            $$12 = 0;
            $$13 = 1;
        }
        double $$14 = $$8 - (double)$$12 + G2;
        double $$15 = $$9 - (double)$$13 + G2;
        double $$16 = $$8 - 1.0 + 2.0 * G2;
        double $$17 = $$9 - 1.0 + 2.0 * G2;
        int $$18 = $$3 & 0xFF;
        int $$19 = $$4 & 0xFF;
        int $$20 = this.p($$18 + this.p($$19)) % 12;
        int $$21 = this.p($$18 + $$12 + this.p($$19 + $$13)) % 12;
        int $$22 = this.p($$18 + 1 + this.p($$19 + 1)) % 12;
        double $$23 = this.getCornerNoise3D($$20, $$8, $$9, 0.0, 0.5);
        double $$24 = this.getCornerNoise3D($$21, $$14, $$15, 0.0, 0.5);
        double $$25 = this.getCornerNoise3D($$22, $$16, $$17, 0.0, 0.5);
        return 70.0 * ($$23 + $$24 + $$25);
    }

    public double getValue(double $$0, double $$1, double $$2) {
        int $$51;
        int $$50;
        int $$49;
        int $$48;
        int $$47;
        int $$46;
        double $$3 = 0.3333333333333333;
        double $$4 = ($$0 + $$1 + $$2) * 0.3333333333333333;
        int $$5 = Mth.floor($$0 + $$4);
        int $$6 = Mth.floor($$1 + $$4);
        int $$7 = Mth.floor($$2 + $$4);
        double $$8 = 0.16666666666666666;
        double $$9 = (double)($$5 + $$6 + $$7) * 0.16666666666666666;
        double $$10 = (double)$$5 - $$9;
        double $$11 = (double)$$6 - $$9;
        double $$12 = (double)$$7 - $$9;
        double $$13 = $$0 - $$10;
        double $$14 = $$1 - $$11;
        double $$15 = $$2 - $$12;
        if ($$13 >= $$14) {
            if ($$14 >= $$15) {
                boolean $$16 = true;
                boolean $$17 = false;
                boolean $$18 = false;
                boolean $$19 = true;
                boolean $$20 = true;
                boolean $$21 = false;
            } else if ($$13 >= $$15) {
                boolean $$22 = true;
                boolean $$23 = false;
                boolean $$24 = false;
                boolean $$25 = true;
                boolean $$26 = false;
                boolean $$27 = true;
            } else {
                boolean $$28 = false;
                boolean $$29 = false;
                boolean $$30 = true;
                boolean $$31 = true;
                boolean $$32 = false;
                boolean $$33 = true;
            }
        } else if ($$14 < $$15) {
            boolean $$34 = false;
            boolean $$35 = false;
            boolean $$36 = true;
            boolean $$37 = false;
            boolean $$38 = true;
            boolean $$39 = true;
        } else if ($$13 < $$15) {
            boolean $$40 = false;
            boolean $$41 = true;
            boolean $$42 = false;
            boolean $$43 = false;
            boolean $$44 = true;
            boolean $$45 = true;
        } else {
            $$46 = 0;
            $$47 = 1;
            $$48 = 0;
            $$49 = 1;
            $$50 = 1;
            $$51 = 0;
        }
        double $$52 = $$13 - (double)$$46 + 0.16666666666666666;
        double $$53 = $$14 - (double)$$47 + 0.16666666666666666;
        double $$54 = $$15 - (double)$$48 + 0.16666666666666666;
        double $$55 = $$13 - (double)$$49 + 0.3333333333333333;
        double $$56 = $$14 - (double)$$50 + 0.3333333333333333;
        double $$57 = $$15 - (double)$$51 + 0.3333333333333333;
        double $$58 = $$13 - 1.0 + 0.5;
        double $$59 = $$14 - 1.0 + 0.5;
        double $$60 = $$15 - 1.0 + 0.5;
        int $$61 = $$5 & 0xFF;
        int $$62 = $$6 & 0xFF;
        int $$63 = $$7 & 0xFF;
        int $$64 = this.p($$61 + this.p($$62 + this.p($$63))) % 12;
        int $$65 = this.p($$61 + $$46 + this.p($$62 + $$47 + this.p($$63 + $$48))) % 12;
        int $$66 = this.p($$61 + $$49 + this.p($$62 + $$50 + this.p($$63 + $$51))) % 12;
        int $$67 = this.p($$61 + 1 + this.p($$62 + 1 + this.p($$63 + 1))) % 12;
        double $$68 = this.getCornerNoise3D($$64, $$13, $$14, $$15, 0.6);
        double $$69 = this.getCornerNoise3D($$65, $$52, $$53, $$54, 0.6);
        double $$70 = this.getCornerNoise3D($$66, $$55, $$56, $$57, 0.6);
        double $$71 = this.getCornerNoise3D($$67, $$58, $$59, $$60, 0.6);
        return 32.0 * ($$68 + $$69 + $$70 + $$71);
    }
}