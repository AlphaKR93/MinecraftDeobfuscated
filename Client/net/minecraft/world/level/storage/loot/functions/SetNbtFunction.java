/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction
extends LootItemConditionalFunction {
    final CompoundTag tag;

    SetNbtFunction(LootItemCondition[] $$0, CompoundTag $$1) {
        super($$0);
        this.tag = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_NBT;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.getOrCreateTag().merge(this.tag);
        return $$0;
    }

    @Deprecated
    public static LootItemConditionalFunction.Builder<?> setTag(CompoundTag $$0) {
        return SetNbtFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetNbtFunction((LootItemCondition[])$$1, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetNbtFunction> {
        @Override
        public void serialize(JsonObject $$0, SetNbtFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("tag", $$1.tag.toString());
        }

        @Override
        public SetNbtFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            try {
                CompoundTag $$3 = TagParser.parseTag(GsonHelper.getAsString($$0, "tag"));
                return new SetNbtFunction($$2, $$3);
            }
            catch (CommandSyntaxException $$4) {
                throw new JsonSyntaxException($$4.getMessage());
            }
        }
    }
}