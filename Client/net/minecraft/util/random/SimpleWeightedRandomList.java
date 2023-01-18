/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class SimpleWeightedRandomList<E>
extends WeightedRandomList<WeightedEntry.Wrapper<E>> {
    public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodecAllowingEmpty(Codec<E> $$0) {
        return WeightedEntry.Wrapper.codec($$0).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
    }

    public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodec(Codec<E> $$0) {
        return ExtraCodecs.nonEmptyList(WeightedEntry.Wrapper.codec($$0).listOf()).xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
    }

    SimpleWeightedRandomList(List<? extends WeightedEntry.Wrapper<E>> $$0) {
        super($$0);
    }

    public static <E> Builder<E> builder() {
        return new Builder();
    }

    public static <E> SimpleWeightedRandomList<E> empty() {
        return new SimpleWeightedRandomList<E>(List.of());
    }

    public static <E> SimpleWeightedRandomList<E> single(E $$0) {
        return new SimpleWeightedRandomList<E>(List.of(WeightedEntry.wrap($$0, 1)));
    }

    public Optional<E> getRandomValue(RandomSource $$0) {
        return this.getRandom($$0).map(WeightedEntry.Wrapper::getData);
    }

    public static class Builder<E> {
        private final ImmutableList.Builder<WeightedEntry.Wrapper<E>> result = ImmutableList.builder();

        public Builder<E> add(E $$0, int $$1) {
            this.result.add(WeightedEntry.wrap($$0, $$1));
            return this;
        }

        public SimpleWeightedRandomList<E> build() {
            return new SimpleWeightedRandomList(this.result.build());
        }
    }
}