/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 */
package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

public interface ResourceManager
extends ResourceProvider {
    public Set<String> getNamespaces();

    public List<Resource> getResourceStack(ResourceLocation var1);

    public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2);

    public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2);

    public Stream<PackResources> listPacks();

    public static enum Empty implements ResourceManager
    {
        INSTANCE;


        @Override
        public Set<String> getNamespaces() {
            return Set.of();
        }

        @Override
        public Optional<Resource> getResource(ResourceLocation $$0) {
            return Optional.empty();
        }

        @Override
        public List<Resource> getResourceStack(ResourceLocation $$0) {
            return List.of();
        }

        @Override
        public Map<ResourceLocation, Resource> listResources(String $$0, Predicate<ResourceLocation> $$1) {
            return Map.of();
        }

        @Override
        public Map<ResourceLocation, List<Resource>> listResourceStacks(String $$0, Predicate<ResourceLocation> $$1) {
            return Map.of();
        }

        @Override
        public Stream<PackResources> listPacks() {
            return Stream.of((Object[])new PackResources[0]);
        }
    }
}