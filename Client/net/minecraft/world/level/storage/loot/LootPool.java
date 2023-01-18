/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    final LootPoolEntryContainer[] entries;
    final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;
    final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    final NumberProvider rolls;
    final NumberProvider bonusRolls;

    LootPool(LootPoolEntryContainer[] $$0, LootItemCondition[] $$1, LootItemFunction[] $$2, NumberProvider $$3, NumberProvider $$4) {
        this.entries = $$0;
        this.conditions = $$1;
        this.compositeCondition = LootItemConditions.andConditions($$1);
        this.functions = $$2;
        this.compositeFunction = LootItemFunctions.compose($$2);
        this.rolls = $$3;
        this.bonusRolls = $$4;
    }

    private void addRandomItem(Consumer<ItemStack> $$0, LootContext $$1) {
        RandomSource $$2 = $$1.getRandom();
        ArrayList $$3 = Lists.newArrayList();
        MutableInt $$4 = new MutableInt();
        for (LootPoolEntryContainer $$5 : this.entries) {
            $$5.expand($$1, arg_0 -> LootPool.lambda$addRandomItem$0($$1, (List)$$3, $$4, arg_0));
        }
        int $$6 = $$3.size();
        if ($$4.intValue() == 0 || $$6 == 0) {
            return;
        }
        if ($$6 == 1) {
            ((LootPoolEntry)$$3.get(0)).createItemStack($$0, $$1);
            return;
        }
        int $$7 = $$2.nextInt($$4.intValue());
        for (LootPoolEntry $$8 : $$3) {
            if (($$7 -= $$8.getWeight($$1.getLuck())) >= 0) continue;
            $$8.createItemStack($$0, $$1);
            return;
        }
    }

    public void addRandomItems(Consumer<ItemStack> $$0, LootContext $$1) {
        if (!this.compositeCondition.test((Object)$$1)) {
            return;
        }
        Consumer<ItemStack> $$2 = LootItemFunction.decorate(this.compositeFunction, $$0, $$1);
        int $$3 = this.rolls.getInt($$1) + Mth.floor(this.bonusRolls.getFloat($$1) * $$1.getLuck());
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            this.addRandomItem($$2, $$1);
        }
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.conditions.length; ++$$1) {
            this.conditions[$$1].validate($$0.forChild(".condition[" + $$1 + "]"));
        }
        for (int $$2 = 0; $$2 < this.functions.length; ++$$2) {
            this.functions[$$2].validate($$0.forChild(".functions[" + $$2 + "]"));
        }
        for (int $$3 = 0; $$3 < this.entries.length; ++$$3) {
            this.entries[$$3].validate($$0.forChild(".entries[" + $$3 + "]"));
        }
        this.rolls.validate($$0.forChild(".rolls"));
        this.bonusRolls.validate($$0.forChild(".bonusRolls"));
    }

    public static Builder lootPool() {
        return new Builder();
    }

    private static /* synthetic */ void lambda$addRandomItem$0(LootContext $$0, List $$1, MutableInt $$2, LootPoolEntry $$3) {
        int $$4 = $$3.getWeight($$0.getLuck());
        if ($$4 > 0) {
            $$1.add((Object)$$3);
            $$2.add($$4);
        }
    }

    public static class Builder
    implements FunctionUserBuilder<Builder>,
    ConditionUserBuilder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
        private final List<LootItemCondition> conditions = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private NumberProvider rolls = ConstantValue.exactly(1.0f);
        private NumberProvider bonusRolls = ConstantValue.exactly(0.0f);

        public Builder setRolls(NumberProvider $$0) {
            this.rolls = $$0;
            return this;
        }

        @Override
        public Builder unwrap() {
            return this;
        }

        public Builder setBonusRolls(NumberProvider $$0) {
            this.bonusRolls = $$0;
            return this;
        }

        public Builder add(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder when(LootItemCondition.Builder $$0) {
            this.conditions.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return this;
        }

        public LootPool build() {
            if (this.rolls == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool((LootPoolEntryContainer[])this.entries.toArray((Object[])new LootPoolEntryContainer[0]), (LootItemCondition[])this.conditions.toArray((Object[])new LootItemCondition[0]), (LootItemFunction[])this.functions.toArray((Object[])new LootItemFunction[0]), this.rolls, this.bonusRolls);
        }
    }

    public static class Serializer
    implements JsonDeserializer<LootPool>,
    JsonSerializer<LootPool> {
        public LootPool deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "loot pool");
            LootPoolEntryContainer[] $$4 = GsonHelper.getAsObject($$3, "entries", $$2, LootPoolEntryContainer[].class);
            LootItemCondition[] $$5 = GsonHelper.getAsObject($$3, "conditions", new LootItemCondition[0], $$2, LootItemCondition[].class);
            LootItemFunction[] $$6 = GsonHelper.getAsObject($$3, "functions", new LootItemFunction[0], $$2, LootItemFunction[].class);
            NumberProvider $$7 = GsonHelper.getAsObject($$3, "rolls", $$2, NumberProvider.class);
            NumberProvider $$8 = GsonHelper.getAsObject($$3, "bonus_rolls", ConstantValue.exactly(0.0f), $$2, NumberProvider.class);
            return new LootPool($$4, $$5, $$6, $$7, $$8);
        }

        public JsonElement serialize(LootPool $$0, Type $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            $$3.add("rolls", $$2.serialize((Object)$$0.rolls));
            $$3.add("bonus_rolls", $$2.serialize((Object)$$0.bonusRolls));
            $$3.add("entries", $$2.serialize((Object)$$0.entries));
            if (!ArrayUtils.isEmpty((Object[])$$0.conditions)) {
                $$3.add("conditions", $$2.serialize((Object)$$0.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])$$0.functions)) {
                $$3.add("functions", $$2.serialize((Object)$$0.functions));
            }
            return $$3;
        }
    }
}