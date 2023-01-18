/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.SafeVarargs
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

public class WeightedRandomList<E extends WeightedEntry> {
    private final int totalWeight;
    private final ImmutableList<E> items;

    WeightedRandomList(List<? extends E> $$0) {
        this.items = ImmutableList.copyOf($$0);
        this.totalWeight = WeightedRandom.getTotalWeight($$0);
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> create() {
        return new WeightedRandomList<E>(ImmutableList.of());
    }

    @SafeVarargs
    public static <E extends WeightedEntry> WeightedRandomList<E> create(E ... $$0) {
        return new WeightedRandomList<E>(ImmutableList.copyOf((Object[])$$0));
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> create(List<E> $$0) {
        return new WeightedRandomList<E>($$0);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public Optional<E> getRandom(RandomSource $$0) {
        if (this.totalWeight == 0) {
            return Optional.empty();
        }
        int $$1 = $$0.nextInt(this.totalWeight);
        return WeightedRandom.getWeightedItem(this.items, $$1);
    }

    public List<E> unwrap() {
        return this.items;
    }

    public static <E extends WeightedEntry> Codec<WeightedRandomList<E>> codec(Codec<E> $$0) {
        return $$0.listOf().xmap(WeightedRandomList::create, WeightedRandomList::unwrap);
    }
}