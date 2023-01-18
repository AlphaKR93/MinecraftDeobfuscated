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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference
extends LootPoolSingletonContainer {
    final ResourceLocation name;

    LootTableReference(ResourceLocation $$0, int $$1, int $$2, LootItemCondition[] $$3, LootItemFunction[] $$4) {
        super($$1, $$2, $$3, $$4);
        this.name = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.REFERENCE;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
        LootTable $$2 = $$1.getLootTable(this.name);
        $$2.getRandomItemsRaw($$1, $$0);
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

    public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation $$0) {
        return LootTableReference.simpleBuilder(($$1, $$2, $$3, $$4) -> new LootTableReference($$0, $$1, $$2, $$3, $$4));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<LootTableReference> {
        @Override
        public void serialize(JsonObject $$0, LootTableReference $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("name", $$1.name.toString());
        }

        @Override
        protected LootTableReference deserialize(JsonObject $$0, JsonDeserializationContext $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
            ResourceLocation $$6 = new ResourceLocation(GsonHelper.getAsString($$0, "name"));
            return new LootTableReference($$6, $$2, $$3, $$4, $$5);
        }
    }
}