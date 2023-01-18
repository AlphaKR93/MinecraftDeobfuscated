/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final List<Enchantment> enchantments;

    EnchantRandomlyFunction(LootItemCondition[] $$0, Collection<Enchantment> $$1) {
        super($$0);
        this.enchantments = ImmutableList.copyOf($$1);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Enchantment $$6;
        RandomSource $$22 = $$1.getRandom();
        if (this.enchantments.isEmpty()) {
            boolean $$3 = $$0.is(Items.BOOK);
            List $$4 = (List)BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter($$2 -> $$3 || $$2.canEnchant($$0)).collect(Collectors.toList());
            if ($$4.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)$$0);
                return $$0;
            }
            Enchantment $$5 = (Enchantment)$$4.get($$22.nextInt($$4.size()));
        } else {
            $$6 = (Enchantment)this.enchantments.get($$22.nextInt(this.enchantments.size()));
        }
        return EnchantRandomlyFunction.enchantItem($$0, $$6, $$22);
    }

    private static ItemStack enchantItem(ItemStack $$0, Enchantment $$1, RandomSource $$2) {
        int $$3 = Mth.nextInt($$2, $$1.getMinLevel(), $$1.getMaxLevel());
        if ($$0.is(Items.BOOK)) {
            $$0 = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment($$0, new EnchantmentInstance($$1, $$3));
        } else {
            $$0.enchant($$1, $$3);
        }
        return $$0;
    }

    public static Builder randomEnchantment() {
        return new Builder();
    }

    public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
        return EnchantRandomlyFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$0 -> new EnchantRandomlyFunction((LootItemCondition[])$$0, (Collection<Enchantment>)ImmutableList.of())));
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Set<Enchantment> enchantments = Sets.newHashSet();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Enchantment $$0) {
            this.enchantments.add((Object)$$0);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantRandomlyFunction(this.getConditions(), (Collection<Enchantment>)this.enchantments);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
        @Override
        public void serialize(JsonObject $$0, EnchantRandomlyFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            if (!$$1.enchantments.isEmpty()) {
                JsonArray $$3 = new JsonArray();
                for (Enchantment $$4 : $$1.enchantments) {
                    ResourceLocation $$5 = BuiltInRegistries.ENCHANTMENT.getKey($$4);
                    if ($$5 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + $$4);
                    }
                    $$3.add((JsonElement)new JsonPrimitive($$5.toString()));
                }
                $$0.add("enchantments", (JsonElement)$$3);
            }
        }

        @Override
        public EnchantRandomlyFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            ArrayList $$3 = Lists.newArrayList();
            if ($$0.has("enchantments")) {
                JsonArray $$4 = GsonHelper.getAsJsonArray($$0, "enchantments");
                for (JsonElement $$5 : $$4) {
                    String $$6 = GsonHelper.convertToString($$5, "enchantment");
                    Enchantment $$7 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + $$6 + "'"));
                    $$3.add((Object)$$7);
                }
            }
            return new EnchantRandomlyFunction($$2, (Collection<Enchantment>)$$3);
        }
    }
}