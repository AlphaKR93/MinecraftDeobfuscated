/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootItemConditionalFunction
implements LootItemFunction {
    protected final LootItemCondition[] predicates;
    private final Predicate<LootContext> compositePredicates;

    protected LootItemConditionalFunction(LootItemCondition[] $$0) {
        this.predicates = $$0;
        this.compositePredicates = LootItemConditions.andConditions($$0);
    }

    public final ItemStack apply(ItemStack $$0, LootContext $$1) {
        return this.compositePredicates.test((Object)$$1) ? this.run($$0, $$1) : $$0;
    }

    protected abstract ItemStack run(ItemStack var1, LootContext var2);

    @Override
    public void validate(ValidationContext $$0) {
        LootItemFunction.super.validate($$0);
        for (int $$1 = 0; $$1 < this.predicates.length; ++$$1) {
            this.predicates[$$1].validate($$0.forChild(".conditions[" + $$1 + "]"));
        }
    }

    protected static Builder<?> simpleBuilder(Function<LootItemCondition[], LootItemFunction> $$0) {
        return new DummyBuilder($$0);
    }

    static final class DummyBuilder
    extends Builder<DummyBuilder> {
        private final Function<LootItemCondition[], LootItemFunction> constructor;

        public DummyBuilder(Function<LootItemCondition[], LootItemFunction> $$0) {
            this.constructor = $$0;
        }

        @Override
        protected DummyBuilder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return (LootItemFunction)this.constructor.apply((Object)this.getConditions());
        }
    }

    public static abstract class Serializer<T extends LootItemConditionalFunction>
    implements net.minecraft.world.level.storage.loot.Serializer<T> {
        @Override
        public void serialize(JsonObject $$0, T $$1, JsonSerializationContext $$2) {
            if (!ArrayUtils.isEmpty((Object[])((LootItemConditionalFunction)$$1).predicates)) {
                $$0.add("conditions", $$2.serialize((Object)((LootItemConditionalFunction)$$1).predicates));
            }
        }

        @Override
        public final T deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LootItemCondition[] $$2 = GsonHelper.getAsObject($$0, "conditions", new LootItemCondition[0], $$1, LootItemCondition[].class);
            return this.deserialize($$0, $$1, $$2);
        }

        public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootItemFunction.Builder,
    ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions = Lists.newArrayList();

        @Override
        public T when(LootItemCondition.Builder $$0) {
            this.conditions.add((Object)$$0.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected abstract T getThis();

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[])this.conditions.toArray((Object[])new LootItemCondition[0]);
        }
    }
}