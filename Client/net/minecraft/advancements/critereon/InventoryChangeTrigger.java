/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Set;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("inventory_changed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        JsonObject $$3 = GsonHelper.getAsJsonObject($$0, "slots", new JsonObject());
        MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson($$3.get("occupied"));
        MinMaxBounds.Ints $$5 = MinMaxBounds.Ints.fromJson($$3.get("full"));
        MinMaxBounds.Ints $$6 = MinMaxBounds.Ints.fromJson($$3.get("empty"));
        ItemPredicate[] $$7 = ItemPredicate.fromJsonArray($$0.get("items"));
        return new TriggerInstance($$1, $$4, $$5, $$6, $$7);
    }

    public void trigger(ServerPlayer $$0, Inventory $$1, ItemStack $$2) {
        int $$3 = 0;
        int $$4 = 0;
        int $$5 = 0;
        for (int $$6 = 0; $$6 < $$1.getContainerSize(); ++$$6) {
            ItemStack $$7 = $$1.getItem($$6);
            if ($$7.isEmpty()) {
                ++$$4;
                continue;
            }
            ++$$5;
            if ($$7.getCount() < $$7.getMaxStackSize()) continue;
            ++$$3;
        }
        this.trigger($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private void trigger(ServerPlayer $$0, Inventory $$1, ItemStack $$2, int $$3, int $$4, int $$52) {
        this.trigger($$0, $$5 -> $$5.matches($$1, $$2, $$3, $$4, $$52));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints slotsOccupied;
        private final MinMaxBounds.Ints slotsFull;
        private final MinMaxBounds.Ints slotsEmpty;
        private final ItemPredicate[] predicates;

        public TriggerInstance(EntityPredicate.Composite $$0, MinMaxBounds.Ints $$1, MinMaxBounds.Ints $$2, MinMaxBounds.Ints $$3, ItemPredicate[] $$4) {
            super(ID, $$0);
            this.slotsOccupied = $$1;
            this.slotsFull = $$2;
            this.slotsEmpty = $$3;
            this.predicates = $$4;
        }

        public static TriggerInstance hasItems(ItemPredicate ... $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, $$0);
        }

        public static TriggerInstance hasItems(ItemLike ... $$0) {
            ItemPredicate[] $$1 = new ItemPredicate[$$0.length];
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                $$1[$$2] = new ItemPredicate(null, (Set<Item>)ImmutableSet.of((Object)$$0[$$2].asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY);
            }
            return TriggerInstance.hasItems($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (!(this.slotsOccupied.isAny() && this.slotsFull.isAny() && this.slotsEmpty.isAny())) {
                JsonObject $$2 = new JsonObject();
                $$2.add("occupied", this.slotsOccupied.serializeToJson());
                $$2.add("full", this.slotsFull.serializeToJson());
                $$2.add("empty", this.slotsEmpty.serializeToJson());
                $$1.add("slots", (JsonElement)$$2);
            }
            if (this.predicates.length > 0) {
                JsonArray $$3 = new JsonArray();
                for (ItemPredicate $$4 : this.predicates) {
                    $$3.add($$4.serializeToJson());
                }
                $$1.add("items", (JsonElement)$$3);
            }
            return $$1;
        }

        public boolean matches(Inventory $$0, ItemStack $$12, int $$2, int $$3, int $$4) {
            if (!this.slotsFull.matches($$2)) {
                return false;
            }
            if (!this.slotsEmpty.matches($$3)) {
                return false;
            }
            if (!this.slotsOccupied.matches($$4)) {
                return false;
            }
            int $$5 = this.predicates.length;
            if ($$5 == 0) {
                return true;
            }
            if ($$5 == 1) {
                return !$$12.isEmpty() && this.predicates[0].matches($$12);
            }
            ObjectArrayList $$6 = new ObjectArrayList((Object[])this.predicates);
            int $$7 = $$0.getContainerSize();
            for (int $$8 = 0; $$8 < $$7; ++$$8) {
                if ($$6.isEmpty()) {
                    return true;
                }
                ItemStack $$9 = $$0.getItem($$8);
                if ($$9.isEmpty()) continue;
                $$6.removeIf($$1 -> $$1.matches($$9));
            }
            return $$6.isEmpty();
        }
    }
}