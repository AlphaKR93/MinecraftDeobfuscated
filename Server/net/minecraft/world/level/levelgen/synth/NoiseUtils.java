/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Locale
 */
package net.minecraft.world.level.levelgen.synth;

import java.util.Locale;

public class NoiseUtils {
    public static double biasTowardsExtreme(double $$0, double $$1) {
        return $$0 + Math.sin((double)(Math.PI * $$0)) * $$1 / Math.PI;
    }

    public static void parityNoiseOctaveConfigString(StringBuilder $$0, double $$1, double $$2, double $$3, byte[] $$4) {
        $$0.append(String.format((Locale)Locale.ROOT, (String)"xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (Object[])new Object[]{Float.valueOf((float)((float)$$1)), Float.valueOf((float)((float)$$2)), Float.valueOf((float)((float)$$3)), $$4[0], $$4[255]}));
    }

    public static void parityNoiseOctaveConfigString(StringBuilder $$0, double $$1, double $$2, double $$3, int[] $$4) {
        $$0.append(String.format((Locale)Locale.ROOT, (String)"xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (Object[])new Object[]{Float.valueOf((float)((float)$$1)), Float.valueOf((float)((float)$$2)), Float.valueOf((float)((float)$$3)), $$4[0], $$4[255]}));
    }
}