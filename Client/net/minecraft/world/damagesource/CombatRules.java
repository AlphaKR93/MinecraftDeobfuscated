/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.damagesource;

import net.minecraft.util.Mth;

public class CombatRules {
    public static final float MAX_ARMOR = 20.0f;
    public static final float ARMOR_PROTECTION_DIVIDER = 25.0f;
    public static final float BASE_ARMOR_TOUGHNESS = 2.0f;
    public static final float MIN_ARMOR_RATIO = 0.2f;
    private static final int NUM_ARMOR_ITEMS = 4;

    public static float getDamageAfterAbsorb(float $$0, float $$1, float $$2) {
        float $$3 = 2.0f + $$2 / 4.0f;
        float $$4 = Mth.clamp($$1 - $$0 / $$3, $$1 * 0.2f, 20.0f);
        return $$0 * (1.0f - $$4 / 25.0f);
    }

    public static float getDamageAfterMagicAbsorb(float $$0, float $$1) {
        float $$2 = Mth.clamp($$1, 0.0f, 20.0f);
        return $$0 * (1.0f - $$2 / 25.0f);
    }
}