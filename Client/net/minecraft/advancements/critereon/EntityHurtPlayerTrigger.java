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

public class EntityHurtPlayerTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        DamagePredicate $$3 = DamagePredicate.fromJson($$0.get("damage"));
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, DamageSource $$1, float $$2, float $$3, boolean $$4) {
        this.trigger($$0, $$5 -> $$5.matches($$0, $$1, $$2, $$3, $$4));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final DamagePredicate damage;

        public TriggerInstance(EntityPredicate.Composite $$0, DamagePredicate $$1) {
            super(ID, $$0);
            this.damage = $$1;
        }

        public static TriggerInstance entityHurtPlayer() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY);
        }

        public static TriggerInstance entityHurtPlayer(DamagePredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0);
        }

        public static TriggerInstance entityHurtPlayer(DamagePredicate.Builder $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0.build());
        }

        public boolean matches(ServerPlayer $$0, DamageSource $$1, float $$2, float $$3, boolean $$4) {
            return this.damage.matches($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("damage", this.damage.serializeToJson());
            return $$1;
        }
    }
}