/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashSet
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState
extends LootItemConditionalFunction {
    final Block block;
    final Set<Property<?>> properties;

    CopyBlockState(LootItemCondition[] $$0, Block $$1, Set<Property<?>> $$2) {
        super($$0);
        this.block = $$1;
        this.properties = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_STATE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        BlockState $$22 = $$1.getParamOrNull(LootContextParams.BLOCK_STATE);
        if ($$22 != null) {
            CompoundTag $$5;
            CompoundTag $$3 = $$0.getOrCreateTag();
            if ($$3.contains("BlockStateTag", 10)) {
                CompoundTag $$4 = $$3.getCompound("BlockStateTag");
            } else {
                $$5 = new CompoundTag();
                $$3.put("BlockStateTag", $$5);
            }
            this.properties.stream().filter($$22::hasProperty).forEach($$2 -> $$5.putString($$2.getName(), CopyBlockState.serialize($$22, $$2)));
        }
        return $$0;
    }

    public static Builder copyState(Block $$0) {
        return new Builder($$0);
    }

    private static <T extends Comparable<T>> String serialize(BlockState $$0, Property<T> $$1) {
        T $$2 = $$0.getValue($$1);
        return $$1.getName($$2);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Block block;
        private final Set<Property<?>> properties = Sets.newHashSet();

        Builder(Block $$0) {
            this.block = $$0;
        }

        public Builder copy(Property<?> $$0) {
            if (!this.block.getStateDefinition().getProperties().contains($$0)) {
                throw new IllegalStateException("Property " + $$0 + " is not present on block " + this.block);
            }
            this.properties.add($$0);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyBlockState(this.getConditions(), this.block, this.properties);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyBlockState> {
        @Override
        public void serialize(JsonObject $$0, CopyBlockState $$12, JsonSerializationContext $$2) {
            super.serialize($$0, $$12, $$2);
            $$0.addProperty("block", BuiltInRegistries.BLOCK.getKey($$12.block).toString());
            JsonArray $$3 = new JsonArray();
            $$12.properties.forEach($$1 -> $$3.add($$1.getName()));
            $$0.add("properties", (JsonElement)$$3);
        }

        @Override
        public CopyBlockState deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$0, "block"));
            Block $$4 = (Block)BuiltInRegistries.BLOCK.getOptional($$3).orElseThrow(() -> new IllegalArgumentException("Can't find block " + $$3));
            StateDefinition<Block, BlockState> $$5 = $$4.getStateDefinition();
            HashSet $$6 = Sets.newHashSet();
            JsonArray $$7 = GsonHelper.getAsJsonArray($$0, "properties", null);
            if ($$7 != null) {
                $$7.forEach(arg_0 -> Serializer.lambda$deserialize$2((Set)$$6, $$5, arg_0));
            }
            return new CopyBlockState($$2, $$4, (Set<Property<?>>)$$6);
        }

        private static /* synthetic */ void lambda$deserialize$2(Set $$0, StateDefinition $$1, JsonElement $$2) {
            $$0.add($$1.getProperty(GsonHelper.convertToString($$2, "property")));
        }
    }
}