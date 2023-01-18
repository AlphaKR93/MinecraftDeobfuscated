/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 */
package net.minecraft.util.valueproviders;

import java.util.Arrays;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;

public class MultipliedFloats
implements SampledFloat {
    private final SampledFloat[] values;

    public MultipliedFloats(SampledFloat ... $$0) {
        this.values = $$0;
    }

    @Override
    public float sample(RandomSource $$0) {
        float $$1 = 1.0f;
        for (int $$2 = 0; $$2 < this.values.length; ++$$2) {
            $$1 *= this.values[$$2].sample($$0);
        }
        return $$1;
    }

    public String toString() {
        return "MultipliedFloats" + Arrays.toString((Object[])this.values);
    }
}