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
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ItemPredicate $$3 = ItemPredicate.fromJson($$0.get("item"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "entity", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, ItemStack $$1, Entity $$22) {
        LootContext $$3 = EntityPredicate.createContext($$0, $$22);
        this.trigger($$0, $$2 -> $$2.matches($$1, $$3));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final EntityPredicate.Composite entity;

        public TriggerInstance(EntityPredicate.Composite $$0, ItemPredicate $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.item = $$1;
            this.entity = $$2;
        }

        public static TriggerInstance itemUsedOnEntity(EntityPredicate.Composite $$0, ItemPredicate.Builder $$1, EntityPredicate.Composite $$2) {
            return new TriggerInstance($$0, $$1.build(), $$2);
        }

        public static TriggerInstance itemUsedOnEntity(ItemPredicate.Builder $$0, EntityPredicate.Composite $$1) {
            return TriggerInstance.itemUsedOnEntity(EntityPredicate.Composite.ANY, $$0, $$1);
        }

        public boolean matches(ItemStack $$0, LootContext $$1) {
            if (!this.item.matches($$0)) {
                return false;
            }
            return this.entity.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("entity", this.entity.toJson($$0));
            return $$1;
        }
    }
}