/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.util.random;

import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;

public class WeightedRandom {
    private WeightedRandom() {
    }

    public static int getTotalWeight(List<? extends WeightedEntry> $$0) {
        long $$1 = 0L;
        for (WeightedEntry $$2 : $$0) {
            $$1 += (long)$$2.getWeight().asInt();
        }
        if ($$1 > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        }
        return (int)$$1;
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource $$0, List<T> $$1, int $$2) {
        if ($$2 < 0) {
            throw Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
        }
        if ($$2 == 0) {
            return Optional.empty();
        }
        int $$3 = $$0.nextInt($$2);
        return WeightedRandom.getWeightedItem($$1, $$3);
    }

    public static <T extends WeightedEntry> Optional<T> getWeightedItem(List<T> $$0, int $$1) {
        for (WeightedEntry $$2 : $$0) {
            if (($$1 -= $$2.getWeight().asInt()) >= 0) continue;
            return Optional.of((Object)$$2);
        }
        return Optional.empty();
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource $$0, List<T> $$1) {
        return WeightedRandom.getRandomItem($$0, $$1, WeightedRandom.getTotalWeight($$1));
    }
}