/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.util.random;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.Weight;

public interface WeightedEntry {
    public Weight getWeight();

    public static <T> Wrapper<T> wrap(T $$0, int $$1) {
        return new Wrapper<T>($$0, Weight.of($$1));
    }

    public static class Wrapper<T>
    implements WeightedEntry {
        private final T data;
        private final Weight weight;

        Wrapper(T $$0, Weight $$1) {
            this.data = $$0;
            this.weight = $$1;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public Weight getWeight() {
            return this.weight;
        }

        public static <E> Codec<Wrapper<E>> codec(Codec<E> $$0) {
            return RecordCodecBuilder.create($$1 -> $$1.group((App)$$0.fieldOf("data").forGetter(Wrapper::getData), (App)Weight.CODEC.fieldOf("weight").forGetter(Wrapper::getWeight)).apply((Applicative)$$1, Wrapper::new));
        }
    }

    public static class IntrusiveBase
    implements WeightedEntry {
        private final Weight weight;

        public IntrusiveBase(int $$0) {
            this.weight = Weight.of($$0);
        }

        public IntrusiveBase(Weight $$0) {
            this.weight = $$0;
        }

        @Override
        public Weight getWeight() {
            return this.weight;
        }
    }
}