/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 */
package net.minecraft.data.models.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModelTemplate {
    private final Optional<ResourceLocation> model;
    private final Set<TextureSlot> requiredSlots;
    private final Optional<String> suffix;

    public ModelTemplate(Optional<ResourceLocation> $$0, Optional<String> $$1, TextureSlot ... $$2) {
        this.model = $$0;
        this.suffix = $$1;
        this.requiredSlots = ImmutableSet.copyOf((Object[])$$2);
    }

    public ResourceLocation create(Block $$0, TextureMapping $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2) {
        return this.create(ModelLocationUtils.getModelLocation($$0, (String)this.suffix.orElse((Object)"")), $$1, $$2);
    }

    public ResourceLocation createWithSuffix(Block $$0, String $$1, TextureMapping $$2, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$3) {
        return this.create(ModelLocationUtils.getModelLocation($$0, $$1 + (String)this.suffix.orElse((Object)"")), $$2, $$3);
    }

    public ResourceLocation createWithOverride(Block $$0, String $$1, TextureMapping $$2, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$3) {
        return this.create(ModelLocationUtils.getModelLocation($$0, $$1), $$2, $$3);
    }

    public ResourceLocation create(ResourceLocation $$0, TextureMapping $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2) {
        return this.create($$0, $$1, $$2, this::createBaseTemplate);
    }

    public ResourceLocation create(ResourceLocation $$0, TextureMapping $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2, JsonFactory $$3) {
        Map<TextureSlot, ResourceLocation> $$4 = this.createMap($$1);
        $$2.accept((Object)$$0, () -> $$3.create($$0, $$4));
        return $$0;
    }

    public JsonObject createBaseTemplate(ResourceLocation $$0, Map<TextureSlot, ResourceLocation> $$12) {
        JsonObject $$22 = new JsonObject();
        this.model.ifPresent($$1 -> $$22.addProperty("parent", $$1.toString()));
        if (!$$12.isEmpty()) {
            JsonObject $$3 = new JsonObject();
            $$12.forEach(($$1, $$2) -> $$3.addProperty($$1.getId(), $$2.toString()));
            $$22.add("textures", (JsonElement)$$3);
        }
        return $$22;
    }

    private Map<TextureSlot, ResourceLocation> createMap(TextureMapping $$0) {
        return (Map)Streams.concat((Stream[])new Stream[]{this.requiredSlots.stream(), $$0.getForced()}).collect(ImmutableMap.toImmutableMap((Function)Function.identity(), $$0::get));
    }

    public static interface JsonFactory {
        public JsonObject create(ResourceLocation var1, Map<TextureSlot, ResourceLocation> var2);
    }
}