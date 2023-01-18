/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount
extends LootItemConditionalFunction {
    final IntRange limiter;

    LimitCount(LootItemCondition[] $$0, IntRange $$1) {
        super($$0);
        this.limiter = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.LIMIT_COUNT;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.limiter.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        int $$2 = this.limiter.clamp($$1, $$0.getCount());
        $$0.setCount($$2);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> limitCount(IntRange $$0) {
        return LimitCount.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new LimitCount((LootItemCondition[])$$1, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<LimitCount> {
        @Override
        public void serialize(JsonObject $$0, LimitCount $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("limit", $$2.serialize((Object)$$1.limiter));
        }

        @Override
        public LimitCount deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            IntRange $$3 = GsonHelper.getAsObject($$0, "limit", $$1, IntRange.class);
            return new LimitCount($$2, $$3);
        }
    }
}