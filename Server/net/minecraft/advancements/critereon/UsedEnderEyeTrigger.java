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
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        MinMaxBounds.Doubles $$3 = MinMaxBounds.Doubles.fromJson($$0.get("distance"));
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, BlockPos $$12) {
        double $$2 = $$0.getX() - (double)$$12.getX();
        double $$3 = $$0.getZ() - (double)$$12.getZ();
        double $$4 = $$2 * $$2 + $$3 * $$3;
        this.trigger($$0, $$1 -> $$1.matches($$4));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Doubles level;

        public TriggerInstance(EntityPredicate.Composite $$0, MinMaxBounds.Doubles $$1) {
            super(ID, $$0);
            this.level = $$1;
        }

        public boolean matches(double $$0) {
            return this.level.matchesSqr($$0);
        }
    }
}