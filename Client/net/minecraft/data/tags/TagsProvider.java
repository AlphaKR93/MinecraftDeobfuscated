/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.SafeVarargs
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import org.slf4j.Logger;

public abstract class TagsProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    private final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();

    protected TagsProvider(PackOutput $$0, ResourceKey<? extends Registry<T>> $$1, CompletableFuture<HolderLookup.Provider> $$2) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir($$1));
        this.lookupProvider = $$2;
        this.registryKey = $$1;
    }

    @Override
    public final String getName() {
        return "Tags for " + this.registryKey.location();
    }

    protected abstract void addTags(HolderLookup.Provider var1);

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        return this.lookupProvider.thenCompose($$12 -> {
            this.builders.clear();
            this.addTags((HolderLookup.Provider)$$12);
            HolderLookup.RegistryLookup $$22 = $$12.lookupOrThrow(this.registryKey);
            Predicate $$3 = $$1 -> $$22.get(ResourceKey.create(this.registryKey, $$1)).isPresent();
            return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])this.builders.entrySet().stream().map($$2 -> {
                ResourceLocation $$3 = (ResourceLocation)$$2.getKey();
                TagBuilder $$4 = (TagBuilder)$$2.getValue();
                List<TagEntry> $$5 = $$4.build();
                List $$6 = $$5.stream().filter($$1 -> !$$1.verifyIfPresent((Predicate<ResourceLocation>)$$3, (Predicate<ResourceLocation>)((Predicate)arg_0 -> this.builders.containsKey(arg_0)))).toList();
                if (!$$6.isEmpty()) {
                    throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"Couldn't define tag %s as it is missing following references: %s", (Object[])new Object[]{$$3, $$6.stream().map(Objects::toString).collect(Collectors.joining((CharSequence)","))}));
                }
                JsonElement $$7 = (JsonElement)TagFile.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)new TagFile($$5, false)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0));
                Path $$8 = this.pathProvider.json($$3);
                return DataProvider.saveStable($$0, $$7, $$8);
            }).toArray(CompletableFuture[]::new)));
        });
    }

    protected TagAppender<T> tag(TagKey<T> $$0) {
        TagBuilder $$1 = this.getOrCreateRawBuilder($$0);
        return new TagAppender($$1);
    }

    protected TagBuilder getOrCreateRawBuilder(TagKey<T> $$02) {
        return (TagBuilder)this.builders.computeIfAbsent((Object)$$02.location(), $$0 -> TagBuilder.create());
    }

    protected static class TagAppender<T> {
        private final TagBuilder builder;

        protected TagAppender(TagBuilder $$0) {
            this.builder = $$0;
        }

        public final TagAppender<T> add(ResourceKey<T> $$0) {
            this.builder.addElement($$0.location());
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> add(ResourceKey<T> ... $$0) {
            for (ResourceKey<T> $$1 : $$0) {
                this.builder.addElement($$1.location());
            }
            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation $$0) {
            this.builder.addOptionalElement($$0);
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> $$0) {
            this.builder.addTag($$0.location());
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation $$0) {
            this.builder.addOptionalTag($$0);
            return this;
        }
    }
}