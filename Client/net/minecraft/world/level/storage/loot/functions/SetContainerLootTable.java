/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable
extends LootItemConditionalFunction {
    final ResourceLocation name;
    final long seed;
    final BlockEntityType<?> type;

    SetContainerLootTable(LootItemCondition[] $$0, ResourceLocation $$1, long $$2, BlockEntityType<?> $$3) {
        super($$0);
        this.name = $$1;
        this.seed = $$2;
        this.type = $$3;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isEmpty()) {
            return $$0;
        }
        CompoundTag $$2 = BlockItem.getBlockEntityData($$0);
        if ($$2 == null) {
            $$2 = new CompoundTag();
        }
        $$2.putString("LootTable", this.name.toString());
        if (this.seed != 0L) {
            $$2.putLong("LootTableSeed", this.seed);
        }
        BlockItem.setBlockEntityData($$0, this.type, $$2);
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        if ($$0.hasVisitedTable(this.name)) {
            $$0.reportProblem("Table " + this.name + " is recursively called");
            return;
        }
        super.validate($$0);
        LootTable $$1 = $$0.resolveLootTable(this.name);
        if ($$1 == null) {
            $$0.reportProblem("Unknown loot table called " + this.name);
        } else {
            $$1.validate($$0.enterTable("->{" + this.name + "}", this.name));
        }
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> $$0, ResourceLocation $$1) {
        return SetContainerLootTable.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$2 -> new SetContainerLootTable((LootItemCondition[])$$2, $$1, 0L, $$0)));
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> $$0, ResourceLocation $$1, long $$2) {
        return SetContainerLootTable.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$3 -> new SetContainerLootTable((LootItemCondition[])$$3, $$1, $$2, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
        @Override
        public void serialize(JsonObject $$0, SetContainerLootTable $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("name", $$1.name.toString());
            $$0.addProperty("type", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey($$1.type).toString());
            if ($$1.seed != 0L) {
                $$0.addProperty("seed", (Number)Long.valueOf((long)$$1.seed));
            }
        }

        @Override
        public SetContainerLootTable deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$0, "name"));
            long $$4 = GsonHelper.getAsLong($$0, "seed", 0L);
            ResourceLocation $$5 = new ResourceLocation(GsonHelper.getAsString($$0, "type"));
            BlockEntityType $$6 = (BlockEntityType)BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional($$5).orElseThrow(() -> new JsonSyntaxException("Unknown block entity type id '" + $$5 + "'"));
            return new SetContainerLootTable($$2, $$3, $$4, $$6);
        }
    }
}