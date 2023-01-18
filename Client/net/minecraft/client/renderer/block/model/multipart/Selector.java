/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.lang.reflect.Type
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class Selector {
    private final Condition condition;
    private final MultiVariant variant;

    public Selector(Condition $$0, MultiVariant $$1) {
        if ($$0 == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if ($$1 == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.condition = $$0;
        this.variant = $$1;
    }

    public MultiVariant getVariant() {
        return this.variant;
    }

    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> $$0) {
        return this.condition.getPredicate($$0);
    }

    public boolean equals(Object $$0) {
        return this == $$0;
    }

    public int hashCode() {
        return System.identityHashCode((Object)this);
    }

    public static class Deserializer
    implements JsonDeserializer<Selector> {
        public Selector deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            return new Selector(this.getSelector($$3), (MultiVariant)$$2.deserialize($$3.get("apply"), MultiVariant.class));
        }

        private Condition getSelector(JsonObject $$0) {
            if ($$0.has("when")) {
                return Deserializer.getCondition(GsonHelper.getAsJsonObject($$0, "when"));
            }
            return Condition.TRUE;
        }

        @VisibleForTesting
        static Condition getCondition(JsonObject $$02) {
            Set $$1 = $$02.entrySet();
            if ($$1.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if ($$1.size() == 1) {
                if ($$02.has("OR")) {
                    List $$2 = (List)Streams.stream((Iterable)GsonHelper.getAsJsonArray($$02, "OR")).map($$0 -> Deserializer.getCondition($$0.getAsJsonObject())).collect(Collectors.toList());
                    return new OrCondition((Iterable<? extends Condition>)$$2);
                }
                if ($$02.has("AND")) {
                    List $$3 = (List)Streams.stream((Iterable)GsonHelper.getAsJsonArray($$02, "AND")).map($$0 -> Deserializer.getCondition($$0.getAsJsonObject())).collect(Collectors.toList());
                    return new AndCondition((Iterable<? extends Condition>)$$3);
                }
                return Deserializer.getKeyValueCondition((Map.Entry<String, JsonElement>)((Map.Entry)$$1.iterator().next()));
            }
            return new AndCondition((Iterable<? extends Condition>)((Iterable)$$1.stream().map(Deserializer::getKeyValueCondition).collect(Collectors.toList())));
        }

        private static Condition getKeyValueCondition(Map.Entry<String, JsonElement> $$0) {
            return new KeyValueCondition((String)$$0.getKey(), ((JsonElement)$$0.getValue()).getAsString());
        }
    }
}