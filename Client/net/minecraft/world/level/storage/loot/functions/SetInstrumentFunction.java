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
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction
extends LootItemConditionalFunction {
    final TagKey<Instrument> options;

    SetInstrumentFunction(LootItemCondition[] $$0, TagKey<Instrument> $$1) {
        super($$0);
        this.options = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_INSTRUMENT;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        InstrumentItem.setRandom($$0, this.options, $$1.getRandom());
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(TagKey<Instrument> $$0) {
        return SetInstrumentFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetInstrumentFunction((LootItemCondition[])$$1, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetInstrumentFunction> {
        @Override
        public void serialize(JsonObject $$0, SetInstrumentFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("options", "#" + $$1.options.location());
        }

        @Override
        public SetInstrumentFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            String $$3 = GsonHelper.getAsString($$0, "options");
            if (!$$3.startsWith("#")) {
                throw new JsonSyntaxException("Inline tag value not supported: " + $$3);
            }
            return new SetInstrumentFunction($$2, TagKey.create(Registries.INSTRUMENT, new ResourceLocation($$3.substring(1))));
        }
    }
}