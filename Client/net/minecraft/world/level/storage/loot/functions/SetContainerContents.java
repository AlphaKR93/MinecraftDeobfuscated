/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents
extends LootItemConditionalFunction {
    final List<LootPoolEntryContainer> entries;
    final BlockEntityType<?> type;

    SetContainerContents(LootItemCondition[] $$0, BlockEntityType<?> $$1, List<LootPoolEntryContainer> $$2) {
        super($$0);
        this.type = $$1;
        this.entries = ImmutableList.copyOf($$2);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isEmpty()) {
            return $$0;
        }
        NonNullList<ItemStack> $$2 = NonNullList.create();
        this.entries.forEach($$22 -> $$22.expand($$1, $$2 -> $$2.createItemStack(LootTable.createStackSplitter($$1, (Consumer<ItemStack>)((Consumer)arg_0 -> ((NonNullList)$$2).add(arg_0))), $$1)));
        CompoundTag $$3 = new CompoundTag();
        ContainerHelper.saveAllItems($$3, $$2);
        CompoundTag $$4 = BlockItem.getBlockEntityData($$0);
        if ($$4 == null) {
            $$4 = $$3;
        } else {
            $$4.merge($$3);
        }
        BlockItem.setBlockEntityData($$0, this.type, $$4);
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.entries.size(); ++$$1) {
            ((LootPoolEntryContainer)this.entries.get($$1)).validate($$0.forChild(".entry[" + $$1 + "]"));
        }
    }

    public static Builder setContents(BlockEntityType<?> $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
        private final BlockEntityType<?> type;

        public Builder(BlockEntityType<?> $$0) {
            this.type = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEntry(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.type, this.entries);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetContainerContents> {
        @Override
        public void serialize(JsonObject $$0, SetContainerContents $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("type", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey($$1.type).toString());
            $$0.add("entries", $$2.serialize($$1.entries));
        }

        @Override
        public SetContainerContents deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            Object[] $$3 = GsonHelper.getAsObject($$0, "entries", $$1, LootPoolEntryContainer[].class);
            ResourceLocation $$4 = new ResourceLocation(GsonHelper.getAsString($$0, "type"));
            BlockEntityType $$5 = (BlockEntityType)BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional($$4).orElseThrow(() -> new JsonSyntaxException("Unknown block entity type id '" + $$4 + "'"));
            return new SetContainerContents($$2, $$5, (List<LootPoolEntryContainer>)Arrays.asList((Object[])$$3));
        }
    }
}