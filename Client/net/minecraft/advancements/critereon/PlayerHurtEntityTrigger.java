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
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerHurtEntityTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        DamagePredicate $$3 = DamagePredicate.fromJson($$0.get("damage"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "entity", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, Entity $$1, DamageSource $$2, float $$3, float $$4, boolean $$5) {
        LootContext $$62 = EntityPredicate.createContext($$0, $$1);
        this.trigger($$0, $$6 -> $$6.matches($$0, $$62, $$2, $$3, $$4, $$5));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final DamagePredicate damage;
        private final EntityPredicate.Composite entity;

        public TriggerInstance(EntityPredicate.Composite $$0, DamagePredicate $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.damage = $$1;
            this.entity = $$2;
        }

        public static TriggerInstance playerHurtEntity() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance playerHurtEntity(DamagePredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance playerHurtEntity(DamagePredicate.Builder $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0.build(), EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance playerHurtEntity(EntityPredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.wrap($$0));
        }

        public static TriggerInstance playerHurtEntity(DamagePredicate $$0, EntityPredicate $$1) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, EntityPredicate.Composite.wrap($$1));
        }

        public static TriggerInstance playerHurtEntity(DamagePredicate.Builder $$0, EntityPredicate $$1) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0.build(), EntityPredicate.Composite.wrap($$1));
        }

        public boolean matches(ServerPlayer $$0, LootContext $$1, DamageSource $$2, float $$3, float $$4, boolean $$5) {
            if (!this.damage.matches($$0, $$2, $$3, $$4, $$5)) {
                return false;
            }
            return this.entity.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("damage", this.damage.serializeToJson());
            $$1.add("entity", this.entity.toJson($$0));
            return $$1;
        }
    }
}