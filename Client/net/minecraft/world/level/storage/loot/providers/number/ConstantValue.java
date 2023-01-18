/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public final class ConstantValue
implements NumberProvider {
    final float value;

    ConstantValue(float $$0) {
        this.value = $$0;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.CONSTANT;
    }

    @Override
    public float getFloat(LootContext $$0) {
        return this.value;
    }

    public static ConstantValue exactly(float $$0) {
        return new ConstantValue($$0);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        return Float.compare((float)((ConstantValue)$$0).value, (float)this.value) == 0;
    }

    public int hashCode() {
        return this.value != 0.0f ? Float.floatToIntBits((float)this.value) : 0;
    }

    public static class InlineSerializer
    implements GsonAdapterFactory.InlineSerializer<ConstantValue> {
        @Override
        public JsonElement serialize(ConstantValue $$0, JsonSerializationContext $$1) {
            return new JsonPrimitive((Number)Float.valueOf((float)$$0.value));
        }

        @Override
        public ConstantValue deserialize(JsonElement $$0, JsonDeserializationContext $$1) {
            return new ConstantValue(GsonHelper.convertToFloat($$0, "value"));
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ConstantValue> {
        @Override
        public void serialize(JsonObject $$0, ConstantValue $$1, JsonSerializationContext $$2) {
            $$0.addProperty("value", (Number)Float.valueOf((float)$$1.value));
        }

        @Override
        public ConstantValue deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            float $$2 = GsonHelper.getAsFloat($$0, "value");
            return new ConstantValue($$2);
        }
    }
}