/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class BonusLevelTableCondition
implements LootItemCondition {
    final Enchantment enchantment;
    final float[] values;

    BonusLevelTableCondition(Enchantment $$0, float[] $$1) {
        this.enchantment = $$0;
        this.values = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.TABLE_BONUS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext $$0) {
        ItemStack $$1 = $$0.getParamOrNull(LootContextParams.TOOL);
        int $$2 = $$1 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, $$1) : 0;
        float $$3 = this.values[Math.min((int)$$2, (int)(this.values.length - 1))];
        return $$0.getRandom().nextFloat() < $$3;
    }

    public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment $$0, float ... $$1) {
        return () -> new BonusLevelTableCondition($$0, $$1);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<BonusLevelTableCondition> {
        @Override
        public void serialize(JsonObject $$0, BonusLevelTableCondition $$1, JsonSerializationContext $$2) {
            $$0.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey($$1.enchantment).toString());
            $$0.add("chances", $$2.serialize((Object)$$1.values));
        }

        @Override
        public BonusLevelTableCondition deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString($$0, "enchantment"));
            Enchantment $$3 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$2).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + $$2));
            float[] $$4 = GsonHelper.getAsObject($$0, "chances", $$1, float[].class);
            return new BonusLevelTableCondition($$3, $$4);
        }
    }
}