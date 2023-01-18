/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.lang.reflect.Type
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.SerializerType;

public class GsonAdapterFactory {
    public static <E, T extends SerializerType<E>> Builder<E, T> builder(Registry<T> $$0, String $$1, String $$2, Function<E, T> $$3) {
        return new Builder<E, T>($$0, $$1, $$2, $$3);
    }

    public static class Builder<E, T extends SerializerType<E>> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<T, InlineSerializer<? extends E>> inlineType;
        @Nullable
        private T defaultType;

        Builder(Registry<T> $$0, String $$1, String $$2, Function<E, T> $$3) {
            this.registry = $$0;
            this.elementName = $$1;
            this.typeKey = $$2;
            this.typeGetter = $$3;
        }

        public Builder<E, T> withInlineSerializer(T $$0, InlineSerializer<? extends E> $$1) {
            this.inlineType = Pair.of($$0, $$1);
            return this;
        }

        public Builder<E, T> withDefaultType(T $$0) {
            this.defaultType = $$0;
            return this;
        }

        public Object build() {
            return new JsonAdapter<E, T>(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType, this.inlineType);
        }
    }

    public static interface InlineSerializer<T> {
        public JsonElement serialize(T var1, JsonSerializationContext var2);

        public T deserialize(JsonElement var1, JsonDeserializationContext var2);
    }

    static class JsonAdapter<E, T extends SerializerType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private final T defaultType;
        @Nullable
        private final Pair<T, InlineSerializer<? extends E>> inlineType;

        JsonAdapter(Registry<T> $$0, String $$1, String $$2, Function<E, T> $$3, @Nullable T $$4, @Nullable Pair<T, InlineSerializer<? extends E>> $$5) {
            this.registry = $$0;
            this.elementName = $$1;
            this.typeKey = $$2;
            this.typeGetter = $$3;
            this.defaultType = $$4;
            this.inlineType = $$5;
        }

        public E deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            if ($$0.isJsonObject()) {
                SerializerType $$7;
                JsonObject $$3 = GsonHelper.convertToJsonObject($$0, this.elementName);
                String $$4 = GsonHelper.getAsString($$3, this.typeKey, "");
                if ($$4.isEmpty()) {
                    T $$5 = this.defaultType;
                } else {
                    ResourceLocation $$6 = new ResourceLocation($$4);
                    $$7 = (SerializerType)this.registry.get($$6);
                }
                if ($$7 == null) {
                    throw new JsonSyntaxException("Unknown type '" + $$4 + "'");
                }
                return (E)$$7.getSerializer().deserialize($$3, $$2);
            }
            if (this.inlineType == null) {
                throw new UnsupportedOperationException("Object " + $$0 + " can't be deserialized");
            }
            return (E)((InlineSerializer)this.inlineType.getSecond()).deserialize($$0, $$2);
        }

        public JsonElement serialize(E $$0, Type $$1, JsonSerializationContext $$2) {
            SerializerType $$3 = (SerializerType)this.typeGetter.apply($$0);
            if (this.inlineType != null && this.inlineType.getFirst() == $$3) {
                return ((InlineSerializer)this.inlineType.getSecond()).serialize($$0, $$2);
            }
            if ($$3 == null) {
                throw new JsonSyntaxException("Unknown type: " + $$0);
            }
            JsonObject $$4 = new JsonObject();
            $$4.addProperty(this.typeKey, this.registry.getKey($$3).toString());
            $$3.getSerializer().serialize($$4, $$0, $$2);
            return $$4;
        }
    }
}