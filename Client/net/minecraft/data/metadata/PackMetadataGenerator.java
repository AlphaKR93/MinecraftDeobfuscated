/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Supplier
 */
package net.minecraft.data.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.DetectedVersion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackMetadataGenerator
implements DataProvider {
    private final PackOutput output;
    private final Map<String, Supplier<JsonElement>> elements = new HashMap();

    public PackMetadataGenerator(PackOutput $$0) {
        this.output = $$0;
    }

    public <T> PackMetadataGenerator add(MetadataSectionType<T> $$0, T $$1) {
        this.elements.put((Object)$$0.getMetadataSectionName(), () -> $$0.toJson($$1));
        return this;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        JsonObject $$12 = new JsonObject();
        this.elements.forEach(($$1, $$2) -> $$12.add($$1, (JsonElement)$$2.get()));
        return DataProvider.saveStable($$0, (JsonElement)$$12, this.output.getOutputFolder().resolve("pack.mcmeta"));
    }

    @Override
    public final String getName() {
        return "Pack Metadata";
    }

    public static PackMetadataGenerator forFeaturePack(PackOutput $$0, Component $$1) {
        return new PackMetadataGenerator($$0).add(PackMetadataSection.TYPE, new PackMetadataSection($$1, DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA)));
    }

    public static PackMetadataGenerator forFeaturePack(PackOutput $$0, Component $$1, FeatureFlagSet $$2) {
        return PackMetadataGenerator.forFeaturePack($$0, $$1).add(FeatureFlagsMetadataSection.TYPE, new FeatureFlagsMetadataSection($$2));
    }
}