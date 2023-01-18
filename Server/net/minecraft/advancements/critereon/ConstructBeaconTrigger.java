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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("construct_beacon");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        MinMaxBounds.Ints $$3 = MinMaxBounds.Ints.fromJson($$0.get("level"));
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, int $$12) {
        this.trigger($$0, $$1 -> $$1.matches($$12));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints level;

        public TriggerInstance(EntityPredicate.Composite $$0, MinMaxBounds.Ints $$1) {
            super(ID, $$0);
            this.level = $$1;
        }

        public static TriggerInstance constructedBeacon() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY);
        }

        public static TriggerInstance constructedBeacon(MinMaxBounds.Ints $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0);
        }

        public boolean matches(int $$0) {
            return this.level.matches($$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("level", this.level.serializeToJson());
            return $$1;
        }
    }
}