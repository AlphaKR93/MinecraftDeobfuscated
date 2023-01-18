/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  java.lang.Deprecated
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.SafeVarargs
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.Spliterator
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public interface HolderSet<T>
extends Iterable<Holder<T>> {
    public Stream<Holder<T>> stream();

    public int size();

    public Either<TagKey<T>, List<Holder<T>>> unwrap();

    public Optional<Holder<T>> getRandomElement(RandomSource var1);

    public Holder<T> get(int var1);

    public boolean contains(Holder<T> var1);

    public boolean canSerializeIn(HolderOwner<T> var1);

    public Optional<TagKey<T>> unwrapKey();

    @Deprecated
    @VisibleForTesting
    public static <T> Named<T> emptyNamed(HolderOwner<T> $$0, TagKey<T> $$1) {
        return new Named<T>($$0, $$1);
    }

    @SafeVarargs
    public static <T> Direct<T> direct(Holder<T> ... $$0) {
        return new Direct(List.of((Object[])$$0));
    }

    public static <T> Direct<T> direct(List<? extends Holder<T>> $$0) {
        return new Direct(List.copyOf($$0));
    }

    @SafeVarargs
    public static <E, T> Direct<T> direct(Function<E, Holder<T>> $$0, E ... $$1) {
        return HolderSet.direct(Stream.of((Object[])$$1).map($$0).toList());
    }

    public static <E, T> Direct<T> direct(Function<E, Holder<T>> $$0, List<E> $$1) {
        return HolderSet.direct($$1.stream().map($$0).toList());
    }

    public static class Named<T>
    extends ListBacked<T> {
        private final HolderOwner<T> owner;
        private final TagKey<T> key;
        private List<Holder<T>> contents = List.of();

        Named(HolderOwner<T> $$0, TagKey<T> $$1) {
            this.owner = $$0;
            this.key = $$1;
        }

        void bind(List<Holder<T>> $$0) {
            this.contents = List.copyOf($$0);
        }

        public TagKey<T> key() {
            return this.key;
        }

        @Override
        protected List<Holder<T>> contents() {
            return this.contents;
        }

        @Override
        public Either<TagKey<T>, List<Holder<T>>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public Optional<TagKey<T>> unwrapKey() {
            return Optional.of(this.key);
        }

        @Override
        public boolean contains(Holder<T> $$0) {
            return $$0.is(this.key);
        }

        public String toString() {
            return "NamedSet(" + this.key + ")[" + this.contents + "]";
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return this.owner.canSerializeIn($$0);
        }
    }

    public static class Direct<T>
    extends ListBacked<T> {
        private final List<Holder<T>> contents;
        @Nullable
        @Nullable
        private Set<Holder<T>> contentsSet;

        Direct(List<Holder<T>> $$0) {
            this.contents = $$0;
        }

        @Override
        protected List<Holder<T>> contents() {
            return this.contents;
        }

        @Override
        public Either<TagKey<T>, List<Holder<T>>> unwrap() {
            return Either.right(this.contents);
        }

        @Override
        public Optional<TagKey<T>> unwrapKey() {
            return Optional.empty();
        }

        @Override
        public boolean contains(Holder<T> $$0) {
            if (this.contentsSet == null) {
                this.contentsSet = Set.copyOf(this.contents);
            }
            return this.contentsSet.contains($$0);
        }

        public String toString() {
            return "DirectSet[" + this.contents + "]";
        }
    }

    public static abstract class ListBacked<T>
    implements HolderSet<T> {
        protected abstract List<Holder<T>> contents();

        @Override
        public int size() {
            return this.contents().size();
        }

        public Spliterator<Holder<T>> spliterator() {
            return this.contents().spliterator();
        }

        public Iterator<Holder<T>> iterator() {
            return this.contents().iterator();
        }

        @Override
        public Stream<Holder<T>> stream() {
            return this.contents().stream();
        }

        @Override
        public Optional<Holder<T>> getRandomElement(RandomSource $$0) {
            return Util.getRandomSafe(this.contents(), $$0);
        }

        @Override
        public Holder<T> get(int $$0) {
            return (Holder)this.contents().get($$0);
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return true;
        }
    }
}