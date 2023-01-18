/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class SetItemDamageFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final NumberProvider damage;
    final boolean add;

    SetItemDamageFunction(LootItemCondition[] $$0, NumberProvider $$1, boolean $$2) {
        super($$0);
        this.damage = $$1;
        this.add = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_DAMAGE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.damage.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isDamageableItem()) {
            int $$2 = $$0.getMaxDamage();
            float $$3 = this.add ? 1.0f - (float)$$0.getDamageValue() / (float)$$2 : 0.0f;
            float $$4 = 1.0f - Mth.clamp(this.damage.getFloat($$1) + $$3, 0.0f, 1.0f);
            $$0.setDamageValue(Mth.floor($$4 * (float)$$2));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)$$0);
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider $$0) {
        return SetItemDamageFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetItemDamageFunction((LootItemCondition[])$$1, $$0, false)));
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider $$0, boolean $$1) {
        return SetItemDamageFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$2 -> new SetItemDamageFunction((LootItemCondition[])$$2, $$0, $$1)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetItemDamageFunction> {
        @Override
        public void serialize(JsonObject $$0, SetItemDamageFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("damage", $$2.serialize((Object)$$1.damage));
            $$0.addProperty("add", Boolean.valueOf((boolean)$$1.add));
        }

        @Override
        public SetItemDamageFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            NumberProvider $$3 = GsonHelper.getAsObject($$0, "damage", $$1, NumberProvider.class);
            boolean $$4 = GsonHelper.getAsBoolean($$0, "add", false);
            return new SetItemDamageFunction($$2, $$3, $$4);
        }
    }
}