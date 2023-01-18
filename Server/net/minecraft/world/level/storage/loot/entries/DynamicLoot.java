/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DynamicLoot
extends LootPoolSingletonContainer {
    final ResourceLocation name;

    DynamicLoot(ResourceLocation $$0, int $$1, int $$2, LootItemCondition[] $$3, LootItemFunction[] $$4) {
        super($$1, $$2, $$3, $$4);
        this.name = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.DYNAMIC;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
        $$1.addDynamicDrops(this.name, $$0);
    }

    public static LootPoolSingletonContainer.Builder<?> dynamicEntry(ResourceLocation $$0) {
        return DynamicLoot.simpleBuilder(($$1, $$2, $$3, $$4) -> new DynamicLoot($$0, $$1, $$2, $$3, $$4));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<DynamicLoot> {
        @Override
        public void serialize(JsonObject $$0, DynamicLoot $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("name", $$1.name.toString());
        }

        @Override
        protected DynamicLoot deserialize(JsonObject $$0, JsonDeserializationContext $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
            ResourceLocation $$6 = new ResourceLocation(GsonHelper.getAsString($$0, "name"));
            return new DynamicLoot($$6, $$2, $$3, $$4, $$5);
        }
    }
}