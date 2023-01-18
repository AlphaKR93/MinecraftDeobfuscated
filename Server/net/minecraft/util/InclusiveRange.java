/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public record InclusiveRange<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
    public static final Codec<InclusiveRange<Integer>> INT = InclusiveRange.codec(Codec.INT);

    public InclusiveRange {
        if ($$0.compareTo($$1) > 0) {
            throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
        }
    }

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> $$0) {
        return ExtraCodecs.intervalCodec($$0, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
    }

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> $$0, T $$1, T $$22) {
        Function $$3 = $$2 -> {
            if ($$2.minInclusive().compareTo((Object)$$1) < 0) {
                return DataResult.error((String)("Range limit too low, expected at least " + $$1 + " [" + $$2.minInclusive() + "-" + $$2.maxInclusive() + "]"));
            }
            if ($$2.maxInclusive().compareTo((Object)$$22) > 0) {
                return DataResult.error((String)("Range limit too high, expected at most " + $$22 + " [" + $$2.minInclusive() + "-" + $$2.maxInclusive() + "]"));
            }
            return DataResult.success((Object)$$2);
        };
        return InclusiveRange.codec($$0).flatXmap($$3, $$3);
    }

    public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(T $$0, T $$1) {
        if ($$0.compareTo($$1) <= 0) {
            return DataResult.success(new InclusiveRange<T>($$0, $$1));
        }
        return DataResult.error((String)"min_inclusive must be less than or equal to max_inclusive");
    }

    public boolean isValueInRange(T $$0) {
        return $$0.compareTo(this.minInclusive) >= 0 && $$0.compareTo(this.maxInclusive) <= 0;
    }

    public boolean contains(InclusiveRange<T> $$0) {
        return $$0.minInclusive().compareTo(this.minInclusive) >= 0 && $$0.maxInclusive.compareTo(this.maxInclusive) <= 0;
    }

    public String toString() {
        return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
    }
}