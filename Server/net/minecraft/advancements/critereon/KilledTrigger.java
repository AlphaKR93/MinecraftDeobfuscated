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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public KilledTrigger(ResourceLocation $$0) {
        this.id = $$0;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        return new TriggerInstance(this.id, $$1, EntityPredicate.Composite.fromJson($$0, "entity", $$2), DamageSourcePredicate.fromJson($$0.get("killing_blow")));
    }

    public void trigger(ServerPlayer $$0, Entity $$1, DamageSource $$2) {
        LootContext $$32 = EntityPredicate.createContext($$0, $$1);
        this.trigger($$0, $$3 -> $$3.matches($$0, $$32, $$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite entityPredicate;
        private final DamageSourcePredicate killingBlow;

        public TriggerInstance(ResourceLocation $$0, EntityPredicate.Composite $$1, EntityPredicate.Composite $$2, DamageSourcePredicate $$3) {
            super($$0, $$1);
            this.entityPredicate = $$2;
            this.killingBlow = $$3;
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate $$0) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate.Builder $$0) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance playerKilledEntity() {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate $$0, DamageSourcePredicate $$1) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), $$1);
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate.Builder $$0, DamageSourcePredicate $$1) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), $$1);
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate $$0, DamageSourcePredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), $$1.build());
        }

        public static TriggerInstance playerKilledEntity(EntityPredicate.Builder $$0, DamageSourcePredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), $$1.build());
        }

        public static TriggerInstance playerKilledEntityNearSculkCatalyst() {
            return new TriggerInstance(CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate $$0) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate.Builder $$0) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance entityKilledPlayer() {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate $$0, DamageSourcePredicate $$1) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), $$1);
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate.Builder $$0, DamageSourcePredicate $$1) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), $$1);
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate $$0, DamageSourcePredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), $$1.build());
        }

        public static TriggerInstance entityKilledPlayer(EntityPredicate.Builder $$0, DamageSourcePredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()), $$1.build());
        }

        public boolean matches(ServerPlayer $$0, LootContext $$1, DamageSource $$2) {
            if (!this.killingBlow.matches($$0, $$2)) {
                return false;
            }
            return this.entityPredicate.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("entity", this.entityPredicate.toJson($$0));
            $$1.add("killing_blow", this.killingBlow.serializeToJson());
            return $$1;
        }
    }
}