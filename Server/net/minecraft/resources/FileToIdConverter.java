/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Predicate
 */
package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class FileToIdConverter {
    private final String prefix;
    private final String extension;

    public FileToIdConverter(String $$0, String $$1) {
        this.prefix = $$0;
        this.extension = $$1;
    }

    public static FileToIdConverter json(String $$0) {
        return new FileToIdConverter($$0, ".json");
    }

    public ResourceLocation idToFile(ResourceLocation $$0) {
        return $$0.withPath(this.prefix + "/" + $$0.getPath() + this.extension);
    }

    public ResourceLocation fileToId(ResourceLocation $$0) {
        String $$1 = $$0.getPath();
        return $$0.withPath($$1.substring(this.prefix.length() + 1, $$1.length() - this.extension.length()));
    }

    public Map<ResourceLocation, Resource> listMatchingResources(ResourceManager $$02) {
        return $$02.listResources(this.prefix, (Predicate<ResourceLocation>)((Predicate)$$0 -> $$0.getPath().endsWith(this.extension)));
    }

    public Map<ResourceLocation, List<Resource>> listMatchingResourceStacks(ResourceManager $$02) {
        return $$02.listResourceStacks(this.prefix, (Predicate<ResourceLocation>)((Predicate)$$0 -> $$0.getPath().endsWith(this.extension)));
    }
}