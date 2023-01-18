/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount
extends LootItemConditionalFunction {
    static final Map<ResourceLocation, FormulaDeserializer> FORMULAS = Maps.newHashMap();
    final Enchantment enchantment;
    final Formula formula;

    ApplyBonusCount(LootItemCondition[] $$0, Enchantment $$1, Formula $$2) {
        super($$0);
        this.enchantment = $$1;
        this.formula = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ItemStack $$2 = $$1.getParamOrNull(LootContextParams.TOOL);
        if ($$2 != null) {
            int $$3 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, $$2);
            int $$4 = this.formula.calculateNewCount($$1.getRandom(), $$0.getCount(), $$3);
            $$0.setCount($$4);
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment $$0, float $$1, int $$2) {
        return ApplyBonusCount.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$3 -> new ApplyBonusCount((LootItemCondition[])$$3, $$0, new BinomialWithBonusCount($$2, $$1))));
    }

    public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment $$0) {
        return ApplyBonusCount.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new ApplyBonusCount((LootItemCondition[])$$1, $$0, new OreDrops())));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment $$0) {
        return ApplyBonusCount.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new ApplyBonusCount((LootItemCondition[])$$1, $$0, new UniformBonusCount(1))));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment $$0, int $$1) {
        return ApplyBonusCount.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$2 -> new ApplyBonusCount((LootItemCondition[])$$2, $$0, new UniformBonusCount($$1))));
    }

    static {
        FORMULAS.put((Object)BinomialWithBonusCount.TYPE, BinomialWithBonusCount::deserialize);
        FORMULAS.put((Object)OreDrops.TYPE, OreDrops::deserialize);
        FORMULAS.put((Object)UniformBonusCount.TYPE, UniformBonusCount::deserialize);
    }

    static interface Formula {
        public int calculateNewCount(RandomSource var1, int var2, int var3);

        public void serializeParams(JsonObject var1, JsonSerializationContext var2);

        public ResourceLocation getType();
    }

    static final class UniformBonusCount
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int $$0) {
            this.bonusMultiplier = $$0;
        }

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            return $$1 + $$0.nextInt(this.bonusMultiplier * $$2 + 1);
        }

        @Override
        public void serializeParams(JsonObject $$0, JsonSerializationContext $$1) {
            $$0.addProperty("bonusMultiplier", (Number)Integer.valueOf((int)this.bonusMultiplier));
        }

        public static Formula deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            int $$2 = GsonHelper.getAsInt($$0, "bonusMultiplier");
            return new UniformBonusCount($$2);
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static final class OreDrops
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

        OreDrops() {
        }

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            if ($$2 > 0) {
                int $$3 = $$0.nextInt($$2 + 2) - 1;
                if ($$3 < 0) {
                    $$3 = 0;
                }
                return $$1 * ($$3 + 1);
            }
            return $$1;
        }

        @Override
        public void serializeParams(JsonObject $$0, JsonSerializationContext $$1) {
        }

        public static Formula deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            return new OreDrops();
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static final class BinomialWithBonusCount
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
        private final int extraRounds;
        private final float probability;

        public BinomialWithBonusCount(int $$0, float $$1) {
            this.extraRounds = $$0;
            this.probability = $$1;
        }

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            for (int $$3 = 0; $$3 < $$2 + this.extraRounds; ++$$3) {
                if (!($$0.nextFloat() < this.probability)) continue;
                ++$$1;
            }
            return $$1;
        }

        @Override
        public void serializeParams(JsonObject $$0, JsonSerializationContext $$1) {
            $$0.addProperty("extra", (Number)Integer.valueOf((int)this.extraRounds));
            $$0.addProperty("probability", (Number)Float.valueOf((float)this.probability));
        }

        public static Formula deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            int $$2 = GsonHelper.getAsInt($$0, "extra");
            float $$3 = GsonHelper.getAsFloat($$0, "probability");
            return new BinomialWithBonusCount($$2, $$3);
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static interface FormulaDeserializer {
        public Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ApplyBonusCount> {
        @Override
        public void serialize(JsonObject $$0, ApplyBonusCount $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey($$1.enchantment).toString());
            $$0.addProperty("formula", $$1.formula.getType().toString());
            JsonObject $$3 = new JsonObject();
            $$1.formula.serializeParams($$3, $$2);
            if ($$3.size() > 0) {
                $$0.add("parameters", (JsonElement)$$3);
            }
        }

        @Override
        public ApplyBonusCount deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            Formula $$8;
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$0, "enchantment"));
            Enchantment $$4 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$3).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + $$3));
            ResourceLocation $$5 = new ResourceLocation(GsonHelper.getAsString($$0, "formula"));
            FormulaDeserializer $$6 = (FormulaDeserializer)FORMULAS.get((Object)$$5);
            if ($$6 == null) {
                throw new JsonParseException("Invalid formula id: " + $$5);
            }
            if ($$0.has("parameters")) {
                Formula $$7 = $$6.deserialize(GsonHelper.getAsJsonObject($$0, "parameters"), $$1);
            } else {
                $$8 = $$6.deserialize(new JsonObject(), $$1);
            }
            return new ApplyBonusCount($$2, $$4, $$8);
        }
    }
}