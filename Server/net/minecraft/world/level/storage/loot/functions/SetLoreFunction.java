/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Boolean
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Set
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction
extends LootItemConditionalFunction {
    final boolean replace;
    final List<Component> lore;
    @Nullable
    final LootContext.EntityTarget resolutionContext;

    public SetLoreFunction(LootItemCondition[] $$0, boolean $$1, List<Component> $$2, @Nullable LootContext.EntityTarget $$3) {
        super($$0);
        this.replace = $$1;
        this.lore = ImmutableList.copyOf($$2);
        this.resolutionContext = $$3;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_LORE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ListTag $$2 = this.getLoreTag($$0, !this.lore.isEmpty());
        if ($$2 != null) {
            if (this.replace) {
                $$2.clear();
            }
            UnaryOperator<Component> $$3 = SetNameFunction.createResolver($$1, this.resolutionContext);
            this.lore.stream().map($$3).map(Component.Serializer::toJson).map(StringTag::valueOf).forEach(arg_0 -> ((ListTag)$$2).add(arg_0));
        }
        return $$0;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private ListTag getLoreTag(ItemStack $$0, boolean $$1) {
        void $$7;
        void $$4;
        if ($$0.hasTag()) {
            CompoundTag $$2 = $$0.getTag();
        } else if ($$1) {
            CompoundTag $$3 = new CompoundTag();
            $$0.setTag($$3);
        } else {
            return null;
        }
        if ($$4.contains("display", 10)) {
            CompoundTag $$5 = $$4.getCompound("display");
        } else if ($$1) {
            CompoundTag $$6 = new CompoundTag();
            $$4.put("display", $$6);
        } else {
            return null;
        }
        if ($$7.contains("Lore", 9)) {
            return $$7.getList("Lore", 8);
        }
        if ($$1) {
            ListTag $$8 = new ListTag();
            $$7.put("Lore", $$8);
            return $$8;
        }
        return null;
    }

    public static Builder setLore() {
        return new Builder();
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private boolean replace;
        private LootContext.EntityTarget resolutionContext;
        private final List<Component> lore = Lists.newArrayList();

        public Builder setReplace(boolean $$0) {
            this.replace = $$0;
            return this;
        }

        public Builder setResolutionContext(LootContext.EntityTarget $$0) {
            this.resolutionContext = $$0;
            return this;
        }

        public Builder addLine(Component $$0) {
            this.lore.add((Object)$$0);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetLoreFunction(this.getConditions(), this.replace, this.lore, this.resolutionContext);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetLoreFunction> {
        @Override
        public void serialize(JsonObject $$0, SetLoreFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("replace", Boolean.valueOf((boolean)$$1.replace));
            JsonArray $$3 = new JsonArray();
            for (Component $$4 : $$1.lore) {
                $$3.add(Component.Serializer.toJsonTree($$4));
            }
            $$0.add("lore", (JsonElement)$$3);
            if ($$1.resolutionContext != null) {
                $$0.add("entity", $$2.serialize((Object)$$1.resolutionContext));
            }
        }

        @Override
        public SetLoreFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            boolean $$3 = GsonHelper.getAsBoolean($$0, "replace", false);
            List $$4 = (List)Streams.stream((Iterable)GsonHelper.getAsJsonArray($$0, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            LootContext.EntityTarget $$5 = GsonHelper.getAsObject($$0, "entity", null, $$1, LootContext.EntityTarget.class);
            return new SetLoreFunction($$2, $$3, (List<Component>)$$4, $$5);
        }
    }
}