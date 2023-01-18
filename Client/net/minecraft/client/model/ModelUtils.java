/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.client.model;

public class ModelUtils {
    public static float rotlerpRad(float $$0, float $$1, float $$2) {
        float $$3;
        for ($$3 = $$1 - $$0; $$3 < (float)(-Math.PI); $$3 += (float)Math.PI * 2) {
        }
        while ($$3 >= (float)Math.PI) {
            $$3 -= (float)Math.PI * 2;
        }
        return $$0 + $$2 * $$3;
    }
}