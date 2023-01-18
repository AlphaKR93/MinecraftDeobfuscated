/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry
extends LootPoolSingletonContainer {
    final TagKey<Item> tag;
    final boolean expand;

    TagEntry(TagKey<Item> $$0, boolean $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
        super($$2, $$3, $$4, $$5);
        this.tag = $$0;
        this.expand = $$1;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.TAG;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$12) {
        BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).forEach($$1 -> $$0.accept((Object)new ItemStack((Holder<Item>)$$1)));
    }

    private boolean expandTag(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.canRun($$0)) {
            for (final Holder $$2 : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
                $$1.accept((Object)new LootPoolSingletonContainer.EntryBase(){

                    @Override
                    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
                        $$0.accept((Object)new ItemStack($$2));
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.expand) {
            return this.expandTag($$0, $$1);
        }
        return super.expand($$0, $$1);
    }

    public static LootPoolSingletonContainer.Builder<?> tagContents(TagKey<Item> $$0) {
        return TagEntry.simpleBuilder(($$1, $$2, $$3, $$4) -> new TagEntry($$0, false, $$1, $$2, $$3, $$4));
    }

    public static LootPoolSingletonContainer.Builder<?> expandTag(TagKey<Item> $$0) {
        return TagEntry.simpleBuilder(($$1, $$2, $$3, $$4) -> new TagEntry($$0, true, $$1, $$2, $$3, $$4));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<TagEntry> {
        @Override
        public void serialize(JsonObject $$0, TagEntry $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.addProperty("name", $$1.tag.location().toString());
            $$0.addProperty("expand", Boolean.valueOf((boolean)$$1.expand));
        }

        @Override
        protected TagEntry deserialize(JsonObject $$0, JsonDeserializationContext $$1, int $$2, int $$3, LootItemCondition[] $$4, LootItemFunction[] $$5) {
            ResourceLocation $$6 = new ResourceLocation(GsonHelper.getAsString($$0, "name"));
            TagKey<Item> $$7 = TagKey.create(Registries.ITEM, $$6);
            boolean $$8 = GsonHelper.getAsBoolean($$0, "expand");
            return new TagEntry($$7, $$8, $$2, $$3, $$4, $$5);
        }
    }
}