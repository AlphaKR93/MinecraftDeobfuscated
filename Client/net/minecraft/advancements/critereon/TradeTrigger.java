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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("villager_trade");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite $$3 = EntityPredicate.Composite.fromJson($$0, "villager", $$2);
        ItemPredicate $$4 = ItemPredicate.fromJson($$0.get("item"));
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, AbstractVillager $$1, ItemStack $$22) {
        LootContext $$3 = EntityPredicate.createContext($$0, $$1);
        this.trigger($$0, $$2 -> $$2.matches($$3, $$22));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite villager;
        private final ItemPredicate item;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite $$1, ItemPredicate $$2) {
            super(ID, $$0);
            this.villager = $$1;
            this.item = $$2;
        }

        public static TriggerInstance tradedWithVillager() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, ItemPredicate.ANY);
        }

        public static TriggerInstance tradedWithVillager(EntityPredicate.Builder $$0) {
            return new TriggerInstance(EntityPredicate.Composite.wrap($$0.build()), EntityPredicate.Composite.ANY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext $$0, ItemStack $$1) {
            if (!this.villager.matches($$0)) {
                return false;
            }
            return this.item.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("villager", this.villager.toJson($$0));
            return $$1;
        }
    }
}