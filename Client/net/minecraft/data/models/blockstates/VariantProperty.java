/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantProperty<T> {
    final String key;
    final Function<T, JsonElement> serializer;

    public VariantProperty(String $$0, Function<T, JsonElement> $$1) {
        this.key = $$0;
        this.serializer = $$1;
    }

    public Value withValue(T $$0) {
        return new Value($$0);
    }

    public String toString() {
        return this.key;
    }

    public class Value {
        private final T value;

        public Value(T $$1) {
            this.value = $$1;
        }

        public VariantProperty<T> getKey() {
            return VariantProperty.this;
        }

        public void addToVariant(JsonObject $$0) {
            $$0.add(VariantProperty.this.key, (JsonElement)VariantProperty.this.serializer.apply(this.value));
        }

        public String toString() {
            return VariantProperty.this.key + "=" + this.value;
        }
    }
}