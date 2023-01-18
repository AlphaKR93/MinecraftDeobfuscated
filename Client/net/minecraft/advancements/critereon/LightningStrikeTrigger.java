/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.stream.Collectors
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("lightning_strike");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite $$3 = EntityPredicate.Composite.fromJson($$0, "lightning", $$2);
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "bystander", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, LightningBolt $$12, List<Entity> $$22) {
        List $$3 = (List)$$22.stream().map($$1 -> EntityPredicate.createContext($$0, $$1)).collect(Collectors.toList());
        LootContext $$4 = EntityPredicate.createContext($$0, $$12);
        this.trigger($$0, $$2 -> $$2.matches($$4, (List<LootContext>)$$3));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite lightning;
        private final EntityPredicate.Composite bystander;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.lightning = $$1;
            this.bystander = $$2;
        }

        public static TriggerInstance lighthingStrike(EntityPredicate $$0, EntityPredicate $$1) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), EntityPredicate.Composite.wrap($$1));
        }

        public boolean matches(LootContext $$0, List<LootContext> $$1) {
            if (!this.lightning.matches($$0)) {
                return false;
            }
            if (this.bystander != EntityPredicate.Composite.ANY) {
                if ($$1.stream().noneMatch(this.bystander::matches)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("lightning", this.lightning.toJson($$0));
            $$1.add("bystander", this.bystander.toJson($$0));
            return $$1;
        }
    }
}