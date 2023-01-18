/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootItemBlockStatePropertyCondition
implements LootItemCondition {
    final Block block;
    final StatePropertiesPredicate properties;

    LootItemBlockStatePropertyCondition(Block $$0, StatePropertiesPredicate $$1) {
        this.block = $$0;
        this.properties = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.BLOCK_STATE_PROPERTY;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    public boolean test(LootContext $$0) {
        BlockState $$1 = $$0.getParamOrNull(LootContextParams.BLOCK_STATE);
        return $$1 != null && $$1.is(this.block) && this.properties.matches($$1);
    }

    public static Builder hasBlockStateProperties(Block $$0) {
        return new Builder($$0);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final Block block;
        private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;

        public Builder(Block $$0) {
            this.block = $$0;
        }

        public Builder setProperties(StatePropertiesPredicate.Builder $$0) {
            this.properties = $$0.build();
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemBlockStatePropertyCondition(this.block, this.properties);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemBlockStatePropertyCondition> {
        @Override
        public void serialize(JsonObject $$0, LootItemBlockStatePropertyCondition $$1, JsonSerializationContext $$2) {
            $$0.addProperty("block", BuiltInRegistries.BLOCK.getKey($$1.block).toString());
            $$0.add("properties", $$1.properties.serializeToJson());
        }

        @Override
        public LootItemBlockStatePropertyCondition deserialize(JsonObject $$0, JsonDeserializationContext $$12) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString($$0, "block"));
            Block $$3 = (Block)BuiltInRegistries.BLOCK.getOptional($$2).orElseThrow(() -> new IllegalArgumentException("Can't find block " + $$2));
            StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson($$0.get("properties"));
            $$4.checkState($$3.getStateDefinition(), (Consumer<String>)((Consumer)$$1 -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + $$1);
            }));
            return new LootItemBlockStatePropertyCondition($$3, $$4);
        }
    }
}