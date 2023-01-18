/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.util.random;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.slf4j.Logger;

public class Weight {
    public static final Codec<Weight> CODEC = Codec.INT.xmap(Weight::of, Weight::asInt);
    private static final Weight ONE = new Weight(1);
    private static final Logger LOGGER = LogUtils.getLogger();
    private final int value;

    private Weight(int $$0) {
        this.value = $$0;
    }

    public static Weight of(int $$0) {
        if ($$0 == 1) {
            return ONE;
        }
        Weight.validateWeight($$0);
        return new Weight($$0);
    }

    public int asInt() {
        return this.value;
    }

    private static void validateWeight(int $$0) {
        if ($$0 < 0) {
            throw Util.pauseInIde(new IllegalArgumentException("Weight should be >= 0"));
        }
        if ($$0 == 0 && SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.warn("Found 0 weight, make sure this is intentional!");
        }
    }

    public String toString() {
        return Integer.toString((int)this.value);
    }

    public int hashCode() {
        return Integer.hashCode((int)this.value);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        return $$0 instanceof Weight && this.value == ((Weight)$$0).value;
    }
}