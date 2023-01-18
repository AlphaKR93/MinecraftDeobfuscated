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
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("levitation");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        DistancePredicate $$3 = DistancePredicate.fromJson($$0.get("distance"));
        MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson($$0.get("duration"));
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, Vec3 $$1, int $$2) {
        this.trigger($$0, $$3 -> $$3.matches($$0, $$1, $$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final DistancePredicate distance;
        private final MinMaxBounds.Ints duration;

        public TriggerInstance(EntityPredicate.Composite $$0, DistancePredicate $$1, MinMaxBounds.Ints $$2) {
            super(ID, $$0);
            this.distance = $$1;
            this.duration = $$2;
        }

        public static TriggerInstance levitated(DistancePredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, MinMaxBounds.Ints.ANY);
        }

        public boolean matches(ServerPlayer $$0, Vec3 $$1, int $$2) {
            if (!this.distance.matches($$1.x, $$1.y, $$1.z, $$0.getX(), $$0.getY(), $$0.getZ())) {
                return false;
            }
            return this.duration.matches($$2);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("distance", this.distance.serializeToJson());
            $$1.add("duration", this.duration.serializeToJson());
            return $$1;
        }
    }
}