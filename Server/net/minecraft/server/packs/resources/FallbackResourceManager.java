/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  java.io.FilterInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.PrintWriter
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.invoke.LambdaMetafactory
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.TreeMap
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.slf4j.Logger;

public class FallbackResourceManager
implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<PackEntry> fallbacks = Lists.newArrayList();
    private final PackType type;
    private final String namespace;

    public FallbackResourceManager(PackType $$0, String $$1) {
        this.type = $$0;
        this.namespace = $$1;
    }

    public void push(PackResources $$0) {
        this.pushInternal($$0.packId(), $$0, null);
    }

    public void push(PackResources $$0, Predicate<ResourceLocation> $$1) {
        this.pushInternal($$0.packId(), $$0, $$1);
    }

    public void pushFilterOnly(String $$0, Predicate<ResourceLocation> $$1) {
        this.pushInternal($$0, null, $$1);
    }

    private void pushInternal(String $$0, @Nullable PackResources $$1, @Nullable Predicate<ResourceLocation> $$2) {
        this.fallbacks.add((Object)new PackEntry($$0, $$1, $$2));
    }

    @Override
    public Set<String> getNamespaces() {
        return ImmutableSet.of((Object)this.namespace);
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation $$0) {
        for (int $$1 = this.fallbacks.size() - 1; $$1 >= 0; --$$1) {
            IoSupplier<InputStream> $$4;
            PackEntry $$2 = (PackEntry)((Object)this.fallbacks.get($$1));
            PackResources $$3 = $$2.resources;
            if ($$3 != null && ($$4 = $$3.getResource(this.type, $$0)) != null) {
                IoSupplier<ResourceMetadata> $$5 = this.createStackMetadataFinder($$0, $$1);
                return Optional.of((Object)FallbackResourceManager.createResource($$3, $$0, $$4, $$5));
            }
            if (!$$2.isFiltered($$0)) continue;
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", (Object)$$0, (Object)$$2.name);
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static Resource createResource(PackResources $$0, ResourceLocation $$1, IoSupplier<InputStream> $$2, IoSupplier<ResourceMetadata> $$3) {
        return new Resource($$0, FallbackResourceManager.wrapForDebug($$1, $$0, $$2), $$3);
    }

    private static IoSupplier<InputStream> wrapForDebug(ResourceLocation $$0, PackResources $$1, IoSupplier<InputStream> $$2) {
        if (LOGGER.isDebugEnabled()) {
            return () -> new LeakedResourceWarningInputStream((InputStream)$$2.get(), $$0, $$1.packId());
        }
        return $$2;
    }

    @Override
    public List<Resource> getResourceStack(ResourceLocation $$0) {
        ResourceLocation $$1 = FallbackResourceManager.getMetadataLocation($$0);
        ArrayList $$2 = new ArrayList();
        boolean $$3 = false;
        String $$4 = null;
        for (int $$5 = this.fallbacks.size() - 1; $$5 >= 0; --$$5) {
            IoSupplier<InputStream> $$8;
            PackEntry $$6 = (PackEntry)((Object)this.fallbacks.get($$5));
            PackResources $$7 = $$6.resources;
            if ($$7 != null && ($$8 = $$7.getResource(this.type, $$0)) != null) {
                IoSupplier<ResourceMetadata> $$10;
                if ($$3) {
                    IoSupplier<ResourceMetadata> $$9 = ResourceMetadata.EMPTY_SUPPLIER;
                } else {
                    $$10 = () -> {
                        IoSupplier<InputStream> $$2 = $$7.getResource(this.type, $$1);
                        return $$2 != null ? FallbackResourceManager.parseMetadata($$2) : ResourceMetadata.EMPTY;
                    };
                }
                $$2.add((Object)new Resource($$7, $$8, $$10));
            }
            if ($$6.isFiltered($$0)) {
                $$4 = $$6.name;
                break;
            }
            if (!$$6.isFiltered($$1)) continue;
            $$3 = true;
        }
        if ($$2.isEmpty() && $$4 != null) {
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", (Object)$$0, $$4);
        }
        return Lists.reverse((List)$$2);
    }

    private static boolean isMetadata(ResourceLocation $$0) {
        return $$0.getPath().endsWith(".mcmeta");
    }

    private static ResourceLocation getResourceLocationFromMetadata(ResourceLocation $$0) {
        String $$1 = $$0.getPath().substring(0, $$0.getPath().length() - ".mcmeta".length());
        return $$0.withPath($$1);
    }

    static ResourceLocation getMetadataLocation(ResourceLocation $$0) {
        return $$0.withPath($$0.getPath() + ".mcmeta");
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String $$0, Predicate<ResourceLocation> $$1) {
        HashMap $$2 = new HashMap();
        HashMap $$3 = new HashMap();
        int $$4 = this.fallbacks.size();
        for (int $$5 = 0; $$5 < $$4; ++$$5) {
            PackEntry $$6 = (PackEntry)((Object)this.fallbacks.get($$5));
            $$6.filterAll((Collection<ResourceLocation>)$$2.keySet());
            $$6.filterAll((Collection<ResourceLocation>)$$3.keySet());
            PackResources $$7 = $$6.resources;
            if ($$7 == null) continue;
            int $$8 = $$5;
            $$7.listResources(this.type, this.namespace, $$0, (arg_0, arg_1) -> FallbackResourceManager.lambda$listResources$2($$1, (Map)$$3, $$7, $$8, (Map)$$2, arg_0, arg_1));
        }
        TreeMap $$9 = Maps.newTreeMap();
        $$2.forEach((arg_0, arg_1) -> FallbackResourceManager.lambda$listResources$3((Map)$$3, (Map)$$9, arg_0, arg_1));
        return $$9;
    }

    private IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation $$0, int $$1) {
        return () -> {
            ResourceLocation $$2 = FallbackResourceManager.getMetadataLocation($$0);
            for (int $$3 = this.fallbacks.size() - 1; $$3 >= $$1; --$$3) {
                IoSupplier<InputStream> $$6;
                PackEntry $$4 = (PackEntry)((Object)((Object)this.fallbacks.get($$3)));
                PackResources $$5 = $$4.resources;
                if ($$5 != null && ($$6 = $$5.getResource(this.type, $$2)) != null) {
                    return FallbackResourceManager.parseMetadata($$6);
                }
                if ($$4.isFiltered($$2)) break;
            }
            return ResourceMetadata.EMPTY;
        };
    }

    private static IoSupplier<ResourceMetadata> convertToMetadata(IoSupplier<InputStream> $$0) {
        return () -> FallbackResourceManager.parseMetadata($$0);
    }

    private static ResourceMetadata parseMetadata(IoSupplier<InputStream> $$0) throws IOException {
        try (InputStream $$1 = $$0.get();){
            ResourceMetadata resourceMetadata = ResourceMetadata.fromJsonStream($$1);
            return resourceMetadata;
        }
    }

    private static void applyPackFiltersToExistingResources(PackEntry $$0, Map<ResourceLocation, EntryStack> $$1) {
        for (EntryStack $$2 : $$1.values()) {
            if ($$0.isFiltered($$2.fileLocation)) {
                $$2.fileSources.clear();
                continue;
            }
            if (!$$0.isFiltered($$2.metadataLocation())) continue;
            $$2.metaSources.clear();
        }
    }

    private void listPackResources(PackEntry $$0, String $$1, Predicate<ResourceLocation> $$2, Map<ResourceLocation, EntryStack> $$32) {
        PackResources $$42 = $$0.resources;
        if ($$42 == null) {
            return;
        }
        $$42.listResources(this.type, this.namespace, $$1, ($$3, $$4) -> {
            if (FallbackResourceManager.isMetadata($$3)) {
                ResourceLocation $$5 = FallbackResourceManager.getResourceLocationFromMetadata($$3);
                if (!$$2.test((Object)$$5)) {
                    return;
                }
                ((EntryStack)((Object)((Object)$$1.computeIfAbsent((Object)$$5, (Function)(Function)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, <init>(net.minecraft.resources.ResourceLocation ), (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/FallbackResourceManager$EntryStack;)())))).metaSources.put((Object)$$42, $$4);
            } else {
                if (!$$2.test($$3)) {
                    return;
                }
                ((EntryStack)((Object)((Object)$$1.computeIfAbsent((Object)$$3, (Function)(Function)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, <init>(net.minecraft.resources.ResourceLocation ), (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/FallbackResourceManager$EntryStack;)())))).fileSources.add((Object)new ResourceWithSource($$42, (IoSupplier<InputStream>)$$4));
            }
        });
    }

    @Override
    public Map<ResourceLocation, List<Resource>> listResourceStacks(String $$0, Predicate<ResourceLocation> $$1) {
        HashMap $$2 = Maps.newHashMap();
        for (PackEntry $$3 : this.fallbacks) {
            FallbackResourceManager.applyPackFiltersToExistingResources($$3, (Map<ResourceLocation, EntryStack>)$$2);
            this.listPackResources($$3, $$0, $$1, (Map<ResourceLocation, EntryStack>)$$2);
        }
        TreeMap $$4 = Maps.newTreeMap();
        for (EntryStack $$5 : $$2.values()) {
            if ($$5.fileSources.isEmpty()) continue;
            ArrayList $$6 = new ArrayList();
            for (ResourceWithSource $$7 : $$5.fileSources) {
                PackResources $$8 = $$7.source;
                IoSupplier $$9 = (IoSupplier)$$5.metaSources.get((Object)$$8);
                IoSupplier<ResourceMetadata> $$10 = $$9 != null ? FallbackResourceManager.convertToMetadata($$9) : ResourceMetadata.EMPTY_SUPPLIER;
                $$6.add((Object)FallbackResourceManager.createResource($$8, $$5.fileLocation, $$7.resource, $$10));
            }
            $$4.put((Object)$$5.fileLocation, (Object)$$6);
        }
        return $$4;
    }

    @Override
    public Stream<PackResources> listPacks() {
        return this.fallbacks.stream().map($$0 -> $$0.resources).filter(Objects::nonNull);
    }

    private static /* synthetic */ void lambda$listResources$3(Map $$0, Map $$1, ResourceLocation $$2, 1ResourceWithSourceAndIndex $$3) {
        IoSupplier<ResourceMetadata> $$7;
        ResourceLocation $$4 = FallbackResourceManager.getMetadataLocation($$2);
        record ResourceWithSourceAndIndex(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
        }
        ResourceWithSourceAndIndex $$5 = (ResourceWithSourceAndIndex)((Object)$$0.get((Object)$$4));
        if ($$5 != null && $$5.packIndex >= $$3.packIndex) {
            IoSupplier<ResourceMetadata> $$6 = FallbackResourceManager.convertToMetadata($$5.resource);
        } else {
            $$7 = ResourceMetadata.EMPTY_SUPPLIER;
        }
        $$1.put((Object)$$2, (Object)FallbackResourceManager.createResource($$3.packResources, $$2, $$3.resource, $$7));
    }

    private static /* synthetic */ void lambda$listResources$2(Predicate $$0, Map $$1, PackResources $$2, int $$3, Map $$4, ResourceLocation $$5, IoSupplier $$6) {
        record ResourceWithSourceAndIndex(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
        }
        if (FallbackResourceManager.isMetadata($$5)) {
            if ($$0.test((Object)FallbackResourceManager.getResourceLocationFromMetadata($$5))) {
                $$1.put((Object)$$5, (Object)new ResourceWithSourceAndIndex($$2, $$6, $$3));
            }
        } else if ($$0.test((Object)$$5)) {
            $$4.put((Object)$$5, (Object)new ResourceWithSourceAndIndex($$2, $$6, $$3));
        }
    }

    record PackEntry(String name, @Nullable PackResources resources, @Nullable Predicate<ResourceLocation> filter) {
        public void filterAll(Collection<ResourceLocation> $$0) {
            if (this.filter != null) {
                $$0.removeIf(this.filter);
            }
        }

        public boolean isFiltered(ResourceLocation $$0) {
            return this.filter != null && this.filter.test((Object)$$0);
        }
    }

    record EntryStack(ResourceLocation fileLocation, ResourceLocation metadataLocation, List<ResourceWithSource> fileSources, Map<PackResources, IoSupplier<InputStream>> metaSources) {
        EntryStack(ResourceLocation $$0) {
            this($$0, FallbackResourceManager.getMetadataLocation($$0), (List<ResourceWithSource>)new ArrayList(), (Map<PackResources, IoSupplier<InputStream>>)new Object2ObjectArrayMap());
        }
    }

    record ResourceWithSource(PackResources source, IoSupplier<InputStream> resource) {
    }

    static class LeakedResourceWarningInputStream
    extends FilterInputStream {
        private final Supplier<String> message;
        private boolean closed;

        public LeakedResourceWarningInputStream(InputStream $$0, ResourceLocation $$1, String $$2) {
            super($$0);
            Exception $$3 = new Exception("Stacktrace");
            this.message = () -> {
                StringWriter $$3 = new StringWriter();
                $$3.printStackTrace(new PrintWriter((Writer)$$3));
                return "Leaked resource: '" + $$1 + "' loaded from pack: '" + $$2 + "'\n" + $$3;
            };
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                LOGGER.warn("{}", this.message.get());
            }
            super.finalize();
        }
    }
}