/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.data.models.blockstates.VariantProperty;

public class Variant
implements Supplier<JsonElement> {
    private final Map<VariantProperty<?>, VariantProperty.Value> values = Maps.newLinkedHashMap();

    public <T> Variant with(VariantProperty<T> $$0, T $$1) {
        VariantProperty.Value $$2 = (VariantProperty.Value)this.values.put($$0, (Object)$$0.withValue($$1));
        if ($$2 != null) {
            throw new IllegalStateException("Replacing value of " + $$2 + " with " + $$1);
        }
        return this;
    }

    public static Variant variant() {
        return new Variant();
    }

    public static Variant merge(Variant $$0, Variant $$1) {
        Variant $$2 = new Variant();
        $$2.values.putAll($$0.values);
        $$2.values.putAll($$1.values);
        return $$2;
    }

    public JsonElement get() {
        JsonObject $$0 = new JsonObject();
        this.values.values().forEach($$1 -> $$1.addToVariant($$0));
        return $$0;
    }

    public static JsonElement convertList(List<Variant> $$0) {
        if ($$0.size() == 1) {
            return ((Variant)$$0.get(0)).get();
        }
        JsonArray $$12 = new JsonArray();
        $$0.forEach($$1 -> $$12.add($$1.get()));
        return $$12;
    }
}