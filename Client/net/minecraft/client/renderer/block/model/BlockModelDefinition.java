/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.io.Reader
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockModelDefinition {
    private final Map<String, MultiVariant> variants = Maps.newLinkedHashMap();
    private MultiPart multiPart;

    public static BlockModelDefinition fromStream(Context $$0, Reader $$1) {
        return GsonHelper.fromJson($$0.gson, $$1, BlockModelDefinition.class);
    }

    public static BlockModelDefinition fromJsonElement(Context $$0, JsonElement $$1) {
        return (BlockModelDefinition)$$0.gson.fromJson($$1, BlockModelDefinition.class);
    }

    public BlockModelDefinition(Map<String, MultiVariant> $$0, MultiPart $$1) {
        this.multiPart = $$1;
        this.variants.putAll($$0);
    }

    public BlockModelDefinition(List<BlockModelDefinition> $$0) {
        BlockModelDefinition $$1 = null;
        for (BlockModelDefinition $$2 : $$0) {
            if ($$2.isMultiPart()) {
                this.variants.clear();
                $$1 = $$2;
            }
            this.variants.putAll($$2.variants);
        }
        if ($$1 != null) {
            this.multiPart = $$1.multiPart;
        }
    }

    @VisibleForTesting
    public boolean hasVariant(String $$0) {
        return this.variants.get((Object)$$0) != null;
    }

    @VisibleForTesting
    public MultiVariant getVariant(String $$0) {
        MultiVariant $$1 = (MultiVariant)this.variants.get((Object)$$0);
        if ($$1 == null) {
            throw new MissingVariantException();
        }
        return $$1;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof BlockModelDefinition) {
            BlockModelDefinition $$1 = (BlockModelDefinition)$$0;
            if (this.variants.equals($$1.variants)) {
                return this.isMultiPart() ? this.multiPart.equals($$1.multiPart) : !$$1.isMultiPart();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
    }

    public Map<String, MultiVariant> getVariants() {
        return this.variants;
    }

    @VisibleForTesting
    public Set<MultiVariant> getMultiVariants() {
        HashSet $$0 = Sets.newHashSet((Iterable)this.variants.values());
        if (this.isMultiPart()) {
            $$0.addAll(this.multiPart.getMultiVariants());
        }
        return $$0;
    }

    public boolean isMultiPart() {
        return this.multiPart != null;
    }

    public MultiPart getMultiPart() {
        return this.multiPart;
    }

    public static final class Context {
        protected final Gson gson = new GsonBuilder().registerTypeAdapter(BlockModelDefinition.class, (Object)new Deserializer()).registerTypeAdapter(Variant.class, (Object)new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, (Object)new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, (Object)new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, (Object)new Selector.Deserializer()).create();
        private StateDefinition<Block, BlockState> definition;

        public StateDefinition<Block, BlockState> getDefinition() {
            return this.definition;
        }

        public void setDefinition(StateDefinition<Block, BlockState> $$0) {
            this.definition = $$0;
        }
    }

    protected class MissingVariantException
    extends RuntimeException {
        protected MissingVariantException() {
        }
    }

    public static class Deserializer
    implements JsonDeserializer<BlockModelDefinition> {
        public BlockModelDefinition deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Map<String, MultiVariant> $$4 = this.getVariants($$2, $$3);
            MultiPart $$5 = this.getMultiPart($$2, $$3);
            if ($$4.isEmpty() && ($$5 == null || $$5.getMultiVariants().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new BlockModelDefinition($$4, $$5);
        }

        protected Map<String, MultiVariant> getVariants(JsonDeserializationContext $$0, JsonObject $$1) {
            HashMap $$2 = Maps.newHashMap();
            if ($$1.has("variants")) {
                JsonObject $$3 = GsonHelper.getAsJsonObject($$1, "variants");
                for (Map.Entry $$4 : $$3.entrySet()) {
                    $$2.put((Object)((String)$$4.getKey()), (Object)((MultiVariant)$$0.deserialize((JsonElement)$$4.getValue(), MultiVariant.class)));
                }
            }
            return $$2;
        }

        @Nullable
        protected MultiPart getMultiPart(JsonDeserializationContext $$0, JsonObject $$1) {
            if (!$$1.has("multipart")) {
                return null;
            }
            JsonArray $$2 = GsonHelper.getAsJsonArray($$1, "multipart");
            return (MultiPart)$$0.deserialize((JsonElement)$$2, MultiPart.class);
        }
    }
}