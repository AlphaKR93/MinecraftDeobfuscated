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
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public DistanceTrigger(ResourceLocation $$0) {
        this.id = $$0;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        LocationPredicate $$3 = LocationPredicate.fromJson($$0.get("start_position"));
        DistancePredicate $$4 = DistancePredicate.fromJson($$0.get("distance"));
        return new TriggerInstance(this.id, $$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, Vec3 $$1) {
        Vec3 $$2 = $$0.position();
        this.trigger($$0, $$3 -> $$3.matches($$0.getLevel(), $$1, $$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final LocationPredicate startPosition;
        private final DistancePredicate distance;

        public TriggerInstance(ResourceLocation $$0, EntityPredicate.Composite $$1, LocationPredicate $$2, DistancePredicate $$3) {
            super($$0, $$1);
            this.startPosition = $$2;
            this.distance = $$3;
        }

        public static TriggerInstance fallFromHeight(EntityPredicate.Builder $$0, DistancePredicate $$1, LocationPredicate $$2) {
            return new TriggerInstance(CriteriaTriggers.FALL_FROM_HEIGHT.id, EntityPredicate.Composite.wrap($$0.build()), $$2, $$1);
        }

        public static TriggerInstance rideEntityInLava(EntityPredicate.Builder $$0, DistancePredicate $$1) {
            return new TriggerInstance(CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.id, EntityPredicate.Composite.wrap($$0.build()), LocationPredicate.ANY, $$1);
        }

        public static TriggerInstance travelledThroughNether(DistancePredicate $$0) {
            return new TriggerInstance(CriteriaTriggers.NETHER_TRAVEL.id, EntityPredicate.Composite.ANY, LocationPredicate.ANY, $$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("start_position", this.startPosition.serializeToJson());
            $$1.add("distance", this.distance.serializeToJson());
            return $$1;
        }

        public boolean matches(ServerLevel $$0, Vec3 $$1, Vec3 $$2) {
            if (!this.startPosition.matches($$0, $$1.x, $$1.y, $$1.z)) {
                return false;
            }
            return this.distance.matches($$1.x, $$1.y, $$1.z, $$2.x, $$2.y, $$2.z);
        }
    }
}