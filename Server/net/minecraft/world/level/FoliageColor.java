/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level;

public class FoliageColor {
    private static int[] pixels = new int[65536];

    public static void init(int[] $$0) {
        pixels = $$0;
    }

    public static int get(double $$0, double $$1) {
        int $$3 = (int)((1.0 - ($$1 *= $$0)) * 255.0);
        int $$2 = (int)((1.0 - $$0) * 255.0);
        int $$4 = $$3 << 8 | $$2;
        if ($$4 >= pixels.length) {
            return FoliageColor.getDefaultColor();
        }
        return pixels[$$4];
    }

    public static int getEvergreenColor() {
        return 0x619961;
    }

    public static int getBirchColor() {
        return 8431445;
    }

    public static int getDefaultColor() {
        return 4764952;
    }

    public static int getMangroveColor() {
        return 9619016;
    }
}