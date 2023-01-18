/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay
extends LootItemConditionalFunction {
    ApplyExplosionDecay(LootItemCondition[] $$0) {
        super($$0);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Float $$2 = $$1.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if ($$2 != null) {
            RandomSource $$3 = $$1.getRandom();
            float $$4 = 1.0f / $$2.floatValue();
            int $$5 = $$0.getCount();
            int $$6 = 0;
            for (int $$7 = 0; $$7 < $$5; ++$$7) {
                if (!($$3.nextFloat() <= $$4)) continue;
                ++$$6;
            }
            $$0.setCount($$6);
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> explosionDecay() {
        return ApplyExplosionDecay.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)ApplyExplosionDecay::new));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
        @Override
        public ApplyExplosionDecay deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            return new ApplyExplosionDecay($$2);
        }
    }
}