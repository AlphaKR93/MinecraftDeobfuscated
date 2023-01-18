/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface Holder<T> {
    public T value();

    public boolean isBound();

    public boolean is(ResourceLocation var1);

    public boolean is(ResourceKey<T> var1);

    public boolean is(Predicate<ResourceKey<T>> var1);

    public boolean is(TagKey<T> var1);

    public Stream<TagKey<T>> tags();

    public Either<ResourceKey<T>, T> unwrap();

    public Optional<ResourceKey<T>> unwrapKey();

    public Kind kind();

    public boolean canSerializeIn(HolderOwner<T> var1);

    public static <T> Holder<T> direct(T $$0) {
        return new Direct<T>($$0);
    }

    public record Direct<T>(T value) implements Holder<T>
    {
        @Override
        public boolean isBound() {
            return true;
        }

        @Override
        public boolean is(ResourceLocation $$0) {
            return false;
        }

        @Override
        public boolean is(ResourceKey<T> $$0) {
            return false;
        }

        @Override
        public boolean is(TagKey<T> $$0) {
            return false;
        }

        @Override
        public boolean is(Predicate<ResourceKey<T>> $$0) {
            return false;
        }

        @Override
        public Either<ResourceKey<T>, T> unwrap() {
            return Either.right(this.value);
        }

        @Override
        public Optional<ResourceKey<T>> unwrapKey() {
            return Optional.empty();
        }

        @Override
        public Kind kind() {
            return Kind.DIRECT;
        }

        public String toString() {
            return "Direct{" + this.value + "}";
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return true;
        }

        @Override
        public Stream<TagKey<T>> tags() {
            return Stream.of((Object[])new TagKey[0]);
        }
    }

    public static class Reference<T>
    implements Holder<T> {
        private final HolderOwner<T> owner;
        private Set<TagKey<T>> tags = Set.of();
        private final Type type;
        @Nullable
        private ResourceKey<T> key;
        @Nullable
        private T value;

        private Reference(Type $$0, HolderOwner<T> $$1, @Nullable ResourceKey<T> $$2, @Nullable T $$3) {
            this.owner = $$1;
            this.type = $$0;
            this.key = $$2;
            this.value = $$3;
        }

        public static <T> Reference<T> createStandAlone(HolderOwner<T> $$0, ResourceKey<T> $$1) {
            return new Reference<Object>(Type.STAND_ALONE, $$0, $$1, null);
        }

        @Deprecated
        public static <T> Reference<T> createIntrusive(HolderOwner<T> $$0, @Nullable T $$1) {
            return new Reference<T>(Type.INTRUSIVE, $$0, null, $$1);
        }

        public ResourceKey<T> key() {
            if (this.key == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.owner);
            }
            return this.key;
        }

        @Override
        public T value() {
            if (this.value == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.owner);
            }
            return this.value;
        }

        @Override
        public boolean is(ResourceLocation $$0) {
            return this.key().location().equals($$0);
        }

        @Override
        public boolean is(ResourceKey<T> $$0) {
            return this.key() == $$0;
        }

        @Override
        public boolean is(TagKey<T> $$0) {
            return this.tags.contains($$0);
        }

        @Override
        public boolean is(Predicate<ResourceKey<T>> $$0) {
            return $$0.test(this.key());
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return this.owner.canSerializeIn($$0);
        }

        @Override
        public Either<ResourceKey<T>, T> unwrap() {
            return Either.left(this.key());
        }

        @Override
        public Optional<ResourceKey<T>> unwrapKey() {
            return Optional.of(this.key());
        }

        @Override
        public Kind kind() {
            return Kind.REFERENCE;
        }

        @Override
        public boolean isBound() {
            return this.key != null && this.value != null;
        }

        void bindKey(ResourceKey<T> $$0) {
            if (this.key != null && $$0 != this.key) {
                throw new IllegalStateException("Can't change holder key: existing=" + this.key + ", new=" + $$0);
            }
            this.key = $$0;
        }

        void bindValue(T $$0) {
            if (this.type == Type.INTRUSIVE && this.value != $$0) {
                throw new IllegalStateException("Can't change holder " + this.key + " value: existing=" + this.value + ", new=" + $$0);
            }
            this.value = $$0;
        }

        void bindTags(Collection<TagKey<T>> $$0) {
            this.tags = Set.copyOf($$0);
        }

        @Override
        public Stream<TagKey<T>> tags() {
            return this.tags.stream();
        }

        public String toString() {
            return "Reference{" + this.key + "=" + this.value + "}";
        }

        static enum Type {
            STAND_ALONE,
            INTRUSIVE;

        }
    }

    public static enum Kind {
        REFERENCE,
        DIRECT;

    }
}