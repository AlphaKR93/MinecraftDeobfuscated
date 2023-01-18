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
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger
implements CriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("impossible");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<TriggerInstance> $$1) {
    }

    @Override
    public void removePlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<TriggerInstance> $$1) {
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements $$0) {
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, DeserializationContext $$1) {
        return new TriggerInstance();
    }

    public static class TriggerInstance
    implements CriterionTriggerInstance {
        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            return new JsonObject();
        }
    }
}