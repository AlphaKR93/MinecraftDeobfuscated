/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.SafeVarargs
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Function
 *  java.util.stream.Stream
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class IntrinsicHolderTagsProvider<T>
extends TagsProvider<T> {
    private final Function<T, ResourceKey<T>> keyExtractor;

    public IntrinsicHolderTagsProvider(PackOutput $$0, ResourceKey<? extends Registry<T>> $$1, CompletableFuture<HolderLookup.Provider> $$2, Function<T, ResourceKey<T>> $$3) {
        super($$0, $$1, $$2);
        this.keyExtractor = $$3;
    }

    @Override
    protected IntrinsicTagAppender<T> tag(TagKey<T> $$0) {
        TagBuilder $$1 = this.getOrCreateRawBuilder($$0);
        return new IntrinsicTagAppender<T>($$1, this.keyExtractor);
    }

    protected static class IntrinsicTagAppender<T>
    extends TagsProvider.TagAppender<T> {
        private final Function<T, ResourceKey<T>> keyExtractor;

        IntrinsicTagAppender(TagBuilder $$0, Function<T, ResourceKey<T>> $$1) {
            super($$0);
            this.keyExtractor = $$1;
        }

        @Override
        public IntrinsicTagAppender<T> addTag(TagKey<T> $$0) {
            super.addTag($$0);
            return this;
        }

        public final IntrinsicTagAppender<T> add(T $$0) {
            ((TagsProvider.TagAppender)this).add((ResourceKey)this.keyExtractor.apply($$0));
            return this;
        }

        @SafeVarargs
        public final IntrinsicTagAppender<T> add(T ... $$0) {
            Stream.of((Object[])$$0).map(this.keyExtractor).forEach(this::add);
            return this;
        }
    }
}