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
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("changed_dimension");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ResourceKey<Level> $$3 = $$0.has("from") ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(GsonHelper.getAsString($$0, "from"))) : null;
        ResourceKey<Level> $$4 = $$0.has("to") ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(GsonHelper.getAsString($$0, "to"))) : null;
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, ResourceKey<Level> $$1, ResourceKey<Level> $$22) {
        this.trigger($$0, $$2 -> $$2.matches($$1, $$22));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        @Nullable
        private final ResourceKey<Level> from;
        @Nullable
        private final ResourceKey<Level> to;

        public TriggerInstance(EntityPredicate.Composite $$0, @Nullable ResourceKey<Level> $$1, @Nullable ResourceKey<Level> $$2) {
            super(ID, $$0);
            this.from = $$1;
            this.to = $$2;
        }

        public static TriggerInstance changedDimension() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, null, null);
        }

        public static TriggerInstance changedDimension(ResourceKey<Level> $$0, ResourceKey<Level> $$1) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, $$1);
        }

        public static TriggerInstance changedDimensionTo(ResourceKey<Level> $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, null, $$0);
        }

        public static TriggerInstance changedDimensionFrom(ResourceKey<Level> $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, null);
        }

        public boolean matches(ResourceKey<Level> $$0, ResourceKey<Level> $$1) {
            if (this.from != null && this.from != $$0) {
                return false;
            }
            return this.to == null || this.to == $$1;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (this.from != null) {
                $$1.addProperty("from", this.from.location().toString());
            }
            if (this.to != null) {
                $$1.addProperty("to", this.to.location().toString());
            }
            return $$1;
        }
    }
}