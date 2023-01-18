/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetItemCountFunction
extends LootItemConditionalFunction {
    final NumberProvider value;
    final boolean add;

    SetItemCountFunction(LootItemCondition[] $$0, NumberProvider $$1, boolean $$2) {
        super($$0);
        this.value = $$1;
        this.add = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_COUNT;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.value.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        int $$2 = this.add ? $$0.getCount() : 0;
        $$0.setCount(Mth.clamp($$2 + this.value.getInt($$1), 0, $$0.getMaxStackSize()));
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider $$0) {
        return SetItemCountFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetItemCountFunction((LootItemCondition[])$$1, $$0, false)));
    }

    public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider $$0, boolean $$1) {
        return SetItemCountFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$2 -> new SetItemCountFunction((LootItemCondition[])$$2, $$0, $$1)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
        @Override
        public void serialize(JsonObject $$0, SetItemCountFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("count", $$2.serialize((Object)$$1.value));
            $$0.addProperty("add", Boolean.valueOf((boolean)$$1.add));
        }

        @Override
        public SetItemCountFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            NumberProvider $$3 = GsonHelper.getAsObject($$0, "count", $$1, NumberProvider.class);
            boolean $$4 = GsonHelper.getAsBoolean($$0, "add", false);
            return new SetItemCountFunction($$2, $$3, $$4);
        }
    }
}