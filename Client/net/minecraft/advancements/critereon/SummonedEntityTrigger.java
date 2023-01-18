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
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("summoned_entity");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite $$3 = EntityPredicate.Composite.fromJson($$0, "entity", $$2);
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, Entity $$12) {
        LootContext $$2 = EntityPredicate.createContext($$0, $$12);
        this.trigger($$0, $$1 -> $$1.matches($$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite entity;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite $$1) {
            super(ID, $$0);
            this.entity = $$1;
        }

        public static TriggerInstance summonedEntity(EntityPredicate.Builder $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()));
        }

        public boolean matches(LootContext $$0) {
            return this.entity.matches($$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("entity", this.entity.toJson($$0));
            return $$1;
        }
    }
}