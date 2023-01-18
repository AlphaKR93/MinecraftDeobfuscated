/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction
extends LootItemConditionalFunction {
    final NameSource source;

    CopyNameFunction(LootItemCondition[] $$0, NameSource $$1) {
        super($$0);
        this.source = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Nameable $$3;
        Object $$2 = $$1.getParamOrNull(this.source.param);
        if ($$2 instanceof Nameable && ($$3 = (Nameable)$$2).hasCustomName()) {
            $$0.setHoverName($$3.getDisplayName());
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> copyName(NameSource $$0) {
        return CopyNameFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new CopyNameFunction((LootItemCondition[])$$1, $$0)));
    }

    public static enum NameSource {
        THIS("this", LootContextParams.THIS_ENTITY),
        KILLER("killer", LootContextParams.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

        public final String name;
        public final LootContextParam<?> param;

        private NameSource(String $$0, LootContextParam<?> $$1) {
            this.name = $$0;
            this.param = $$1;
        }

        public static NameSource getByName(String $$0) {
            for (NameSource $$1 : NameSource.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            throw new IllegalArgumentException("Invalid name source " + $$0);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
        @Override
        public void serialize(JsonObject $$0, CopyNameFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("source", $$1.source.name);
        }

        @Override
        public CopyNameFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            NameSource $$3 = NameSource.getByName(GsonHelper.getAsString($$0, "source"));
            return new CopyNameFunction($$2, $$3);
        }
    }
}