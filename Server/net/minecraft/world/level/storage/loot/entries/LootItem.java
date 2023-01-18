/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem
extends LootPoolSingletonContainer {
    final Item item;

    LootItem(Item $$0, int $$1, int $$2, LootItemCondition[] $$3, LootItemFunction[] $$4) {
        super($$1, $$2, $$3, $$4);
        this.item = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
        $$0.accept((Object)new ItemStack(this.item));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike $$0) {
        return LootItem.simpleBuilder(($$1, $$2, $$3, $$4) -> new LootItem($$0.asItem(), $$1, $$2, $$3, $$4));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<LootItem> {
        @Override
        public void serialize(JsonObject $$0, LootItem $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            ResourceLocation $$3 = BuiltInRegistries.ITEM.getKey($$1.item);
            if ($$3 == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + $$1.item);
            }
            $$0.addProperty("name", $$3.toString());
        }

        @Override
        protected LootItem deserialize(JsonObject $$0, JsonDeserializationContext $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
            Item $$6 = GsonHelper.getAsItem($$0, "name");
            return new LootItem($$6, $$2, $$3, $$4, $$5);
        }
    }
}