/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;

public class MultiVariant
implements UnbakedModel {
    private final List<Variant> variants;

    public MultiVariant(List<Variant> $$0) {
        this.variants = $$0;
    }

    public List<Variant> getVariants() {
        return this.variants;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof MultiVariant) {
            MultiVariant $$1 = (MultiVariant)$$0;
            return this.variants.equals($$1.variants);
        }
        return false;
    }

    public int hashCode() {
        return this.variants.hashCode();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return (Collection)this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> $$0) {
        this.getVariants().stream().map(Variant::getModelLocation).distinct().forEach($$1 -> ((UnbakedModel)$$0.apply($$1)).resolveParents($$0));
    }

    @Override
    @Nullable
    public BakedModel bake(ModelBaker $$0, Function<Material, TextureAtlasSprite> $$1, ModelState $$2, ResourceLocation $$3) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        WeightedBakedModel.Builder $$4 = new WeightedBakedModel.Builder();
        for (Variant $$5 : this.getVariants()) {
            BakedModel $$6 = $$0.bake($$5.getModelLocation(), $$5);
            $$4.add($$6, $$5.getWeight());
        }
        return $$4.build();
    }

    public static class Deserializer
    implements JsonDeserializer<MultiVariant> {
        public MultiVariant deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            ArrayList $$3 = Lists.newArrayList();
            if ($$0.isJsonArray()) {
                JsonArray $$4 = $$0.getAsJsonArray();
                if ($$4.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement $$5 : $$4) {
                    $$3.add((Object)((Variant)$$2.deserialize($$5, Variant.class)));
                }
            } else {
                $$3.add((Object)((Variant)$$2.deserialize($$0, Variant.class)));
            }
            return new MultiVariant((List<Variant>)$$3);
        }
    }
}