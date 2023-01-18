/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
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

public class PickedUpItemTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private final ResourceLocation id;

    public PickedUpItemTrigger(ResourceLocation $$0) {
        this.id = $$0;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ItemPredicate $$3 = ItemPredicate.fromJson($$0.get("item"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "entity", $$2);
        return new TriggerInstance(this.id, $$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, ItemStack $$1, @Nullable Entity $$2) {
        LootContext $$32 = EntityPredicate.createContext($$0, $$2);
        this.trigger($$0, $$3 -> $$3.matches($$0, $$1, $$32));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final EntityPredicate.Composite entity;

        public TriggerInstance(ResourceLocation $$0, EntityPredicate.Composite $$1, ItemPredicate $$2, EntityPredicate.Composite $$3) {
            super($$0, $$1);
            this.item = $$2;
            this.entity = $$3;
        }

        public static TriggerInstance thrownItemPickedUpByEntity(EntityPredicate.Composite $$0, ItemPredicate $$1, EntityPredicate.Composite $$2) {
            return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.getId(), $$0, $$1, $$2);
        }

        public static TriggerInstance thrownItemPickedUpByPlayer(EntityPredicate.Composite $$0, ItemPredicate $$1, EntityPredicate.Composite $$2) {
            return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.getId(), $$0, $$1, $$2);
        }

        public boolean matches(ServerPlayer $$0, ItemStack $$1, LootContext $$2) {
            if (!this.item.matches($$1)) {
                return false;
            }
            return this.entity.matches($$2);
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