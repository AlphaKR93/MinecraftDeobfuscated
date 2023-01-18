/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Boolean
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetEnchantmentsFunction
extends LootItemConditionalFunction {
    final Map<Enchantment, NumberProvider> enchantments;
    final boolean add;

    SetEnchantmentsFunction(LootItemCondition[] $$0, Map<Enchantment, NumberProvider> $$1, boolean $$2) {
        super($$0);
        this.enchantments = ImmutableMap.copyOf($$1);
        this.add = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ENCHANTMENTS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)this.enchantments.values().stream().flatMap($$0 -> $$0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$12) {
        Object2IntOpenHashMap $$22 = new Object2IntOpenHashMap();
        this.enchantments.forEach((arg_0, arg_1) -> SetEnchantmentsFunction.lambda$run$1((Object2IntMap)$$22, $$12, arg_0, arg_1));
        if ($$0.getItem() == Items.BOOK) {
            ItemStack $$3 = new ItemStack(Items.ENCHANTED_BOOK);
            $$22.forEach(($$1, $$2) -> EnchantedBookItem.addEnchantment($$3, new EnchantmentInstance((Enchantment)$$1, (int)$$2)));
            return $$3;
        }
        Map<Enchantment, Integer> $$4 = EnchantmentHelper.getEnchantments($$0);
        if (this.add) {
            $$22.forEach(($$1, $$2) -> SetEnchantmentsFunction.updateEnchantment($$4, $$1, Math.max((int)((Integer)$$4.getOrDefault($$1, (Object)0) + $$2), (int)0)));
        } else {
            $$22.forEach(($$1, $$2) -> SetEnchantmentsFunction.updateEnchantment($$4, $$1, Math.max((int)$$2, (int)0)));
        }
        EnchantmentHelper.setEnchantments($$4, $$0);
        return $$0;
    }

    private static void updateEnchantment(Map<Enchantment, Integer> $$0, Enchantment $$1, int $$2) {
        if ($$2 == 0) {
            $$0.remove((Object)$$1);
        } else {
            $$0.put((Object)$$1, (Object)$$2);
        }
    }

    private static /* synthetic */ void lambda$run$1(Object2IntMap $$0, LootContext $$1, Enchantment $$2, NumberProvider $$3) {
        $$0.put((Object)$$2, $$3.getInt($$1));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetEnchantmentsFunction> {
        @Override
        public void serialize(JsonObject $$0, SetEnchantmentsFunction $$1, JsonSerializationContext $$22) {
            super.serialize($$0, $$1, $$22);
            JsonObject $$32 = new JsonObject();
            $$1.enchantments.forEach(($$2, $$3) -> {
                ResourceLocation $$4 = BuiltInRegistries.ENCHANTMENT.getKey((Enchantment)$$2);
                if ($$4 == null) {
                    throw new IllegalArgumentException("Don't know how to serialize enchantment " + $$2);
                }
                $$32.add($$4.toString(), $$22.serialize($$3));
            });
            $$0.add("enchantments", (JsonElement)$$32);
            $$0.addProperty("add", Boolean.valueOf((boolean)$$1.add));
        }

        @Override
        public SetEnchantmentsFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            HashMap $$3 = Maps.newHashMap();
            if ($$0.has("enchantments")) {
                JsonObject $$4 = GsonHelper.getAsJsonObject($$0, "enchantments");
                for (Map.Entry $$5 : $$4.entrySet()) {
                    String $$6 = (String)$$5.getKey();
                    JsonElement $$7 = (JsonElement)$$5.getValue();
                    Enchantment $$8 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + $$6 + "'"));
                    NumberProvider $$9 = (NumberProvider)$$1.deserialize($$7, NumberProvider.class);
                    $$3.put((Object)$$8, (Object)$$9);
                }
            }
            boolean $$10 = GsonHelper.getAsBoolean($$0, "add", false);
            return new SetEnchantmentsFunction($$2, (Map<Enchantment, NumberProvider>)$$3, $$10);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Map<Enchantment, NumberProvider> enchantments = Maps.newHashMap();
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean $$0) {
            this.add = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Enchantment $$0, NumberProvider $$1) {
            this.enchantments.put((Object)$$0, (Object)$$1);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetEnchantmentsFunction(this.getConditions(), this.enchantments, this.add);
        }
    }
}