/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.stream.Stream
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ItemOverride {
    private final ResourceLocation model;
    private final List<Predicate> predicates;

    public ItemOverride(ResourceLocation $$0, List<Predicate> $$1) {
        this.model = $$0;
        this.predicates = ImmutableList.copyOf($$1);
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public Stream<Predicate> getPredicates() {
        return this.predicates.stream();
    }

    public static class Predicate {
        private final ResourceLocation property;
        private final float value;

        public Predicate(ResourceLocation $$0, float $$1) {
            this.property = $$0;
            this.value = $$1;
        }

        public ResourceLocation getProperty() {
            return this.property;
        }

        public float getValue() {
            return this.value;
        }
    }

    protected static class Deserializer
    implements JsonDeserializer<ItemOverride> {
        protected Deserializer() {
        }

        public ItemOverride deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            ResourceLocation $$4 = new ResourceLocation(GsonHelper.getAsString($$3, "model"));
            List<Predicate> $$5 = this.getPredicates($$3);
            return new ItemOverride($$4, $$5);
        }

        protected List<Predicate> getPredicates(JsonObject $$02) {
            LinkedHashMap $$1 = Maps.newLinkedHashMap();
            JsonObject $$2 = GsonHelper.getAsJsonObject($$02, "predicate");
            for (Map.Entry $$3 : $$2.entrySet()) {
                $$1.put((Object)new ResourceLocation((String)$$3.getKey()), (Object)Float.valueOf((float)GsonHelper.convertToFloat((JsonElement)$$3.getValue(), (String)$$3.getKey())));
            }
            return (List)$$1.entrySet().stream().map($$0 -> new Predicate((ResourceLocation)$$0.getKey(), ((Float)$$0.getValue()).floatValue())).collect(ImmutableList.toImmutableList());
        }
    }
}