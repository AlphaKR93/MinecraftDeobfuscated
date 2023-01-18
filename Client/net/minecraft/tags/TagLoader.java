/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import org.slf4j.Logger;

public class TagLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Function<ResourceLocation, Optional<? extends T>> idToValue;
    private final String directory;

    public TagLoader(Function<ResourceLocation, Optional<? extends T>> $$0, String $$1) {
        this.idToValue = $$0;
        this.directory = $$1;
    }

    public Map<ResourceLocation, List<EntryWithSource>> load(ResourceManager $$02) {
        HashMap $$1 = Maps.newHashMap();
        FileToIdConverter $$22 = FileToIdConverter.json(this.directory);
        for (Map.Entry $$3 : $$22.listMatchingResourceStacks($$02).entrySet()) {
            ResourceLocation $$4 = (ResourceLocation)$$3.getKey();
            ResourceLocation $$5 = $$22.fileToId($$4);
            for (Resource $$6 : (List)$$3.getValue()) {
                try {
                    BufferedReader $$7 = $$6.openAsReader();
                    try {
                        JsonElement $$8 = JsonParser.parseReader((Reader)$$7);
                        List $$9 = (List)$$1.computeIfAbsent((Object)$$5, $$0 -> new ArrayList());
                        TagFile $$10 = (TagFile)((Object)TagFile.CODEC.parse(new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)$$8)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0)));
                        if ($$10.replace()) {
                            $$9.clear();
                        }
                        String $$11 = $$6.sourcePackId();
                        $$10.entries().forEach($$2 -> $$9.add((Object)new EntryWithSource((TagEntry)$$2, $$11)));
                    }
                    finally {
                        if ($$7 == null) continue;
                        $$7.close();
                    }
                }
                catch (Exception $$12) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{$$5, $$4, $$6.sourcePackId(), $$12});
                }
            }
        }
        return $$1;
    }

    private static void visitDependenciesAndElement(Map<ResourceLocation, List<EntryWithSource>> $$0, Multimap<ResourceLocation, ResourceLocation> $$1, Set<ResourceLocation> $$2, ResourceLocation $$3, BiConsumer<ResourceLocation, List<EntryWithSource>> $$42) {
        if (!$$2.add((Object)$$3)) {
            return;
        }
        $$1.get((Object)$$3).forEach($$4 -> TagLoader.visitDependenciesAndElement($$0, $$1, $$2, $$4, $$42));
        List $$5 = (List)$$0.get((Object)$$3);
        if ($$5 != null) {
            $$42.accept((Object)$$3, (Object)$$5);
        }
    }

    private static boolean isCyclic(Multimap<ResourceLocation, ResourceLocation> $$0, ResourceLocation $$1, ResourceLocation $$22) {
        Collection $$3 = $$0.get((Object)$$22);
        if ($$3.contains((Object)$$1)) {
            return true;
        }
        return $$3.stream().anyMatch($$2 -> TagLoader.isCyclic($$0, $$1, $$2));
    }

    private static void addDependencyIfNotCyclic(Multimap<ResourceLocation, ResourceLocation> $$0, ResourceLocation $$1, ResourceLocation $$2) {
        if (!TagLoader.isCyclic($$0, $$1, $$2)) {
            $$0.put((Object)$$1, (Object)$$2);
        }
    }

    private Either<Collection<EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> $$0, List<EntryWithSource> $$1) {
        ImmutableSet.Builder $$2 = ImmutableSet.builder();
        ArrayList $$3 = new ArrayList();
        for (EntryWithSource $$4 : $$1) {
            if ($$4.entry().build($$0, arg_0 -> ((ImmutableSet.Builder)$$2).add(arg_0))) continue;
            $$3.add((Object)$$4);
        }
        return $$3.isEmpty() ? Either.right((Object)$$2.build()) : Either.left((Object)$$3);
    }

    public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<EntryWithSource>> $$0) {
        HashMap $$1 = Maps.newHashMap();
        TagEntry.Lookup $$2 = new TagEntry.Lookup<T>((Map)$$1){
            final /* synthetic */ Map val$newTags;
            {
                this.val$newTags = map;
            }

            @Override
            @Nullable
            public T element(ResourceLocation $$0) {
                return ((Optional)TagLoader.this.idToValue.apply((Object)$$0)).orElse(null);
            }

            @Override
            @Nullable
            public Collection<T> tag(ResourceLocation $$0) {
                return (Collection)this.val$newTags.get((Object)$$0);
            }
        };
        HashMultimap $$3 = HashMultimap.create();
        $$0.forEach((arg_0, arg_1) -> TagLoader.lambda$build$6((Multimap)$$3, arg_0, arg_1));
        $$0.forEach((arg_0, arg_1) -> TagLoader.lambda$build$9((Multimap)$$3, arg_0, arg_1));
        HashSet $$4 = Sets.newHashSet();
        $$0.keySet().forEach(arg_0 -> this.lambda$build$13($$0, (Multimap)$$3, (Set)$$4, $$2, (Map)$$1, arg_0));
        return $$1;
    }

    public Map<ResourceLocation, Collection<T>> loadAndBuild(ResourceManager $$0) {
        return this.build(this.load($$0));
    }

    private /* synthetic */ void lambda$build$13(Map $$0, Multimap $$1, Set $$2, TagEntry.Lookup $$32, Map $$4, ResourceLocation $$5) {
        TagLoader.visitDependenciesAndElement((Map<ResourceLocation, List<EntryWithSource>>)$$0, (Multimap<ResourceLocation, ResourceLocation>)$$1, (Set<ResourceLocation>)$$2, $$5, (BiConsumer<ResourceLocation, List<EntryWithSource>>)((BiConsumer)($$22, $$3) -> this.build($$32, (List<EntryWithSource>)$$3).ifLeft($$1 -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", $$22, $$1.stream().map(Objects::toString).collect(Collectors.joining((CharSequence)", ")))).ifRight($$2 -> $$4.put($$22, $$2))));
    }

    private static /* synthetic */ void lambda$build$9(Multimap $$0, ResourceLocation $$1, List $$2) {
        $$2.forEach($$22 -> $$22.entry.visitOptionalDependencies((Consumer<ResourceLocation>)((Consumer)$$2 -> TagLoader.addDependencyIfNotCyclic((Multimap<ResourceLocation, ResourceLocation>)$$0, $$1, $$2))));
    }

    private static /* synthetic */ void lambda$build$6(Multimap $$0, ResourceLocation $$1, List $$2) {
        $$2.forEach($$22 -> $$22.entry.visitRequiredDependencies((Consumer<ResourceLocation>)((Consumer)$$2 -> TagLoader.addDependencyIfNotCyclic((Multimap<ResourceLocation, ResourceLocation>)$$0, $$1, $$2))));
    }

    public record EntryWithSource(TagEntry entry, String source) {
        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }
    }
}