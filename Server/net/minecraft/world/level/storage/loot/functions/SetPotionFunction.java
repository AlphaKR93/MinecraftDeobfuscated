/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Function
 *  net.minecraft.core.registries.BuiltInRegistries
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction
extends LootItemConditionalFunction {
    final Potion potion;

    SetPotionFunction(LootItemCondition[] $$0, Potion $$1) {
        super($$0);
        this.potion = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_POTION;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        PotionUtils.setPotion($$0, this.potion);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setPotion(Potion $$0) {
        return SetPotionFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetPotionFunction((LootItemCondition[])$$1, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetPotionFunction> {
        @Override
        public void serialize(JsonObject $$0, SetPotionFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("id", BuiltInRegistries.POTION.getKey($$1.potion).toString());
        }

        @Override
        public SetPotionFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            String $$3 = GsonHelper.getAsString($$0, "id");
            Potion $$4 = (Potion)BuiltInRegistries.POTION.getOptional(ResourceLocation.tryParse($$3)).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + $$3 + "'"));
            return new SetPotionFunction($$2, $$4);
        }
    }
}