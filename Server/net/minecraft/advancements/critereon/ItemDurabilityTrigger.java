/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ItemPredicate $$3 = ItemPredicate.fromJson($$0.get("item"));
        MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson($$0.get("durability"));
        MinMaxBounds.Ints $$5 = MinMaxBounds.Ints.fromJson($$0.get("delta"));
        return new TriggerInstance($$1, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer $$0, ItemStack $$1, int $$22) {
        this.trigger($$0, $$2 -> $$2.matches($$1, $$22));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final MinMaxBounds.Ints durability;
        private final MinMaxBounds.Ints delta;

        public TriggerInstance(EntityPredicate.Composite $$0, ItemPredicate $$1, MinMaxBounds.Ints $$2, MinMaxBounds.Ints $$3) {
            super(ID, $$0);
            this.item = $$1;
            this.durability = $$2;
            this.delta = $$3;
        }

        public static TriggerInstance changedDurability(ItemPredicate $$0, MinMaxBounds.Ints $$1) {
            return TriggerInstance.changedDurability(EntityPredicate.Composite.ANY, $$0, $$1);
        }

        public static TriggerInstance changedDurability(EntityPredicate.Composite $$0, ItemPredicate $$1, MinMaxBounds.Ints $$2) {
            return new TriggerInstance($$0, $$1, $$2, MinMaxBounds.Ints.ANY);
        }

        public boolean matches(ItemStack $$0, int $$1) {
            if (!this.item.matches($$0)) {
                return false;
            }
            if (!this.durability.matches($$0.getMaxDamage() - $$1)) {
                return false;
            }
            return this.delta.matches($$0.getDamageValue() - $$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("durability", this.durability.serializeToJson());
            $$1.add("delta", this.delta.serializeToJson());
            return $$1;
        }
    }
}