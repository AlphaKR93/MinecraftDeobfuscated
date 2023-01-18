/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();

    SmeltItemFunction(LootItemCondition[] $$0) {
        super($$0);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FURNACE_SMELT;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ItemStack $$3;
        if ($$0.isEmpty()) {
            return $$0;
        }
        Optional<SmeltingRecipe> $$2 = $$1.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer($$0), $$1.getLevel());
        if ($$2.isPresent() && !($$3 = ((SmeltingRecipe)$$2.get()).getResultItem()).isEmpty()) {
            ItemStack $$4 = $$3.copy();
            $$4.setCount($$0.getCount());
            return $$4;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)$$0);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> smelted() {
        return SmeltItemFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)SmeltItemFunction::new));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SmeltItemFunction> {
        @Override
        public SmeltItemFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            return new SmeltItemFunction($$2);
        }
    }
}