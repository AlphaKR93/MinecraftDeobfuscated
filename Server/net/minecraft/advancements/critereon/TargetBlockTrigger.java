/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("target_hit");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        MinMaxBounds.Ints $$3 = MinMaxBounds.Ints.fromJson($$0.get("signal_strength"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "projectile", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, Entity $$1, Vec3 $$2, int $$32) {
        LootContext $$4 = EntityPredicate.createContext($$0, $$1);
        this.trigger($$0, $$3 -> $$3.matches($$4, $$2, $$32));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints signalStrength;
        private final EntityPredicate.Composite projectile;

        public TriggerInstance(EntityPredicate.Composite $$0, MinMaxBounds.Ints $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.signalStrength = $$1;
            this.projectile = $$2;
        }

        public static TriggerInstance targetHit(MinMaxBounds.Ints $$0, EntityPredicate.Composite $$1) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, $$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("signal_strength", this.signalStrength.serializeToJson());
            $$1.add("projectile", this.projectile.toJson($$0));
            return $$1;
        }

        public boolean matches(LootContext $$0, Vec3 $$1, int $$2) {
            if (!this.signalStrength.matches($$2)) {
                return false;
            }
            return this.projectile.matches($$0);
        }
    }
}