/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.LinkedHashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetStewEffectFunction
extends LootItemConditionalFunction {
    final Map<MobEffect, NumberProvider> effectDurationMap;

    SetStewEffectFunction(LootItemCondition[] $$0, Map<MobEffect, NumberProvider> $$1) {
        super($$0);
        this.effectDurationMap = ImmutableMap.copyOf($$1);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_STEW_EFFECT;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)this.effectDurationMap.values().stream().flatMap($$0 -> $$0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if (!$$0.is(Items.SUSPICIOUS_STEW) || this.effectDurationMap.isEmpty()) {
            return $$0;
        }
        RandomSource $$2 = $$1.getRandom();
        int $$3 = $$2.nextInt(this.effectDurationMap.size());
        Map.Entry $$4 = (Map.Entry)Iterables.get((Iterable)this.effectDurationMap.entrySet(), (int)$$3);
        MobEffect $$5 = (MobEffect)$$4.getKey();
        int $$6 = ((NumberProvider)$$4.getValue()).getInt($$1);
        if (!$$5.isInstantenous()) {
            $$6 *= 20;
        }
        SuspiciousStewItem.saveMobEffect($$0, $$5, $$6);
        return $$0;
    }

    public static Builder stewEffect() {
        return new Builder();
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Map<MobEffect, NumberProvider> effectDurationMap = Maps.newLinkedHashMap();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEffect(MobEffect $$0, NumberProvider $$1) {
            this.effectDurationMap.put((Object)$$0, (Object)$$1);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetStewEffectFunction> {
        @Override
        public void serialize(JsonObject $$0, SetStewEffectFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            if (!$$1.effectDurationMap.isEmpty()) {
                JsonArray $$3 = new JsonArray();
                for (MobEffect $$4 : $$1.effectDurationMap.keySet()) {
                    JsonObject $$5 = new JsonObject();
                    ResourceLocation $$6 = BuiltInRegistries.MOB_EFFECT.getKey($$4);
                    if ($$6 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize mob effect " + $$4);
                    }
                    $$5.add("type", (JsonElement)new JsonPrimitive($$6.toString()));
                    $$5.add("duration", $$2.serialize($$1.effectDurationMap.get((Object)$$4)));
                    $$3.add((JsonElement)$$5);
                }
                $$0.add("effects", (JsonElement)$$3);
            }
        }

        @Override
        public SetStewEffectFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            LinkedHashMap $$3 = Maps.newLinkedHashMap();
            if ($$0.has("effects")) {
                JsonArray $$4 = GsonHelper.getAsJsonArray($$0, "effects");
                for (JsonElement $$5 : $$4) {
                    String $$6 = GsonHelper.getAsString($$5.getAsJsonObject(), "type");
                    MobEffect $$7 = (MobEffect)BuiltInRegistries.MOB_EFFECT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + $$6 + "'"));
                    NumberProvider $$8 = GsonHelper.getAsObject($$5.getAsJsonObject(), "duration", $$1, NumberProvider.class);
                    $$3.put((Object)$$7, (Object)$$8);
                }
            }
            return new SetStewEffectFunction($$2, (Map<MobEffect, NumberProvider>)$$3);
        }
    }
}