/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.util;

public class FastColor {

    public static class ARGB32 {
        public static int alpha(int $$0) {
            return $$0 >>> 24;
        }

        public static int red(int $$0) {
            return $$0 >> 16 & 0xFF;
        }

        public static int green(int $$0) {
            return $$0 >> 8 & 0xFF;
        }

        public static int blue(int $$0) {
            return $$0 & 0xFF;
        }

        public static int color(int $$0, int $$1, int $$2, int $$3) {
            return $$0 << 24 | $$1 << 16 | $$2 << 8 | $$3;
        }

        public static int multiply(int $$0, int $$1) {
            return ARGB32.color(ARGB32.alpha($$0) * ARGB32.alpha($$1) / 255, ARGB32.red($$0) * ARGB32.red($$1) / 255, ARGB32.green($$0) * ARGB32.green($$1) / 255, ARGB32.blue($$0) * ARGB32.blue($$1) / 255);
        }
    }
}