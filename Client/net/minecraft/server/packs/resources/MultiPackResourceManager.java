/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.TreeMap
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceFilterSection;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class MultiPackResourceManager
implements CloseableResourceManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, FallbackResourceManager> namespacedManagers;
    private final List<PackResources> packs;

    public MultiPackResourceManager(PackType $$0, List<PackResources> $$12) {
        this.packs = List.copyOf($$12);
        HashMap $$2 = new HashMap();
        List $$3 = $$12.stream().flatMap($$1 -> $$1.getNamespaces($$0).stream()).distinct().toList();
        for (PackResources $$4 : $$12) {
            ResourceFilterSection $$5 = this.getPackFilterSection($$4);
            Set<String> $$6 = $$4.getNamespaces($$0);
            Predicate $$7 = $$5 != null ? $$1 -> $$5.isPathFiltered($$1.getPath()) : null;
            for (String $$8 : $$3) {
                boolean $$10;
                boolean $$9 = $$6.contains((Object)$$8);
                boolean bl = $$10 = $$5 != null && $$5.isNamespaceFiltered($$8);
                if (!$$9 && !$$10) continue;
                FallbackResourceManager $$11 = (FallbackResourceManager)$$2.get((Object)$$8);
                if ($$11 == null) {
                    $$11 = new FallbackResourceManager($$0, $$8);
                    $$2.put((Object)$$8, (Object)$$11);
                }
                if ($$9 && $$10) {
                    $$11.push($$4, (Predicate<ResourceLocation>)$$7);
                    continue;
                }
                if ($$9) {
                    $$11.push($$4);
                    continue;
                }
                $$11.pushFilterOnly($$4.packId(), (Predicate<ResourceLocation>)$$7);
            }
        }
        this.namespacedManagers = $$2;
    }

    @Nullable
    private ResourceFilterSection getPackFilterSection(PackResources $$0) {
        try {
            return $$0.getMetadataSection(ResourceFilterSection.TYPE);
        }
        catch (IOException $$1) {
            LOGGER.error("Failed to get filter section from pack {}", (Object)$$0.packId());
            return null;
        }
    }

    @Override
    public Set<String> getNamespaces() {
        return this.namespacedManagers.keySet();
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation $$0) {
        ResourceManager $$1 = (ResourceManager)this.namespacedManagers.get((Object)$$0.getNamespace());
        if ($$1 != null) {
            return $$1.getResource($$0);
        }
        return Optional.empty();
    }

    @Override
    public List<Resource> getResourceStack(ResourceLocation $$0) {
        ResourceManager $$1 = (ResourceManager)this.namespacedManagers.get((Object)$$0.getNamespace());
        if ($$1 != null) {
            return $$1.getResourceStack($$0);
        }
        return List.of();
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String $$0, Predicate<ResourceLocation> $$1) {
        MultiPackResourceManager.checkTrailingDirectoryPath($$0);
        TreeMap $$2 = new TreeMap();
        for (FallbackResourceManager $$3 : this.namespacedManagers.values()) {
            $$2.putAll($$3.listResources($$0, $$1));
        }
        return $$2;
    }

    @Override
    public Map<ResourceLocation, List<Resource>> listResourceStacks(String $$0, Predicate<ResourceLocation> $$1) {
        MultiPackResourceManager.checkTrailingDirectoryPath($$0);
        TreeMap $$2 = new TreeMap();
        for (FallbackResourceManager $$3 : this.namespacedManagers.values()) {
            $$2.putAll($$3.listResourceStacks($$0, $$1));
        }
        return $$2;
    }

    private static void checkTrailingDirectoryPath(String $$0) {
        if ($$0.endsWith("/")) {
            throw new IllegalArgumentException("Trailing slash in path " + $$0);
        }
    }

    @Override
    public Stream<PackResources> listPacks() {
        return this.packs.stream();
    }

    @Override
    public void close() {
        this.packs.forEach(PackResources::close);
    }
}