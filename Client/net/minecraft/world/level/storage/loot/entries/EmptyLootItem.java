/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem
extends LootPoolSingletonContainer {
    EmptyLootItem(int $$0, int $$1, LootItemCondition[] $$2, LootItemFunction[] $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.EMPTY;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
    }

    public static LootPoolSingletonContainer.Builder<?> emptyItem() {
        return EmptyLootItem.simpleBuilder(EmptyLootItem::new);
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<EmptyLootItem> {
        @Override
        public EmptyLootItem deserialize(JsonObject $$0, JsonDeserializationContext $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
            return new EmptyLootItem($$2, $$3, $$4, $$5);
        }
    }
}