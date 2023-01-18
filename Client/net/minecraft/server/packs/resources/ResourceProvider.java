/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.BufferedReader
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

@FunctionalInterface
public interface ResourceProvider {
    public Optional<Resource> getResource(ResourceLocation var1);

    default public Resource getResourceOrThrow(ResourceLocation $$0) throws FileNotFoundException {
        return (Resource)this.getResource($$0).orElseThrow(() -> new FileNotFoundException($$0.toString()));
    }

    default public InputStream open(ResourceLocation $$0) throws IOException {
        return this.getResourceOrThrow($$0).open();
    }

    default public BufferedReader openAsReader(ResourceLocation $$0) throws IOException {
        return this.getResourceOrThrow($$0).openAsReader();
    }

    public static ResourceProvider fromMap(Map<ResourceLocation, Resource> $$0) {
        return $$1 -> Optional.ofNullable((Object)((Resource)$$0.get((Object)$$1)));
    }
}