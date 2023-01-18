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
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("effects_changed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        MobEffectsPredicate $$3 = MobEffectsPredicate.fromJson($$0.get("effects"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "source", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, @Nullable Entity $$1) {
        LootContext $$22 = $$1 != null ? EntityPredicate.createContext($$0, $$1) : null;
        this.trigger($$0, $$2 -> $$2.matches($$0, $$22));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MobEffectsPredicate effects;
        private final EntityPredicate.Composite source;

        public TriggerInstance(EntityPredicate.Composite $$0, MobEffectsPredicate $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.effects = $$1;
            this.source = $$2;
        }

        public static TriggerInstance hasEffects(MobEffectsPredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance gotEffectsFrom(EntityPredicate $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MobEffectsPredicate.ANY, EntityPredicate.Composite.wrap($$0));
        }

        public boolean matches(ServerPlayer $$0, @Nullable LootContext $$1) {
            if (!this.effects.matches($$0)) {
                return false;
            }
            return this.source == EntityPredicate.Composite.ANY || $$1 != null && this.source.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("effects", this.effects.serializeToJson());
            $$1.add("source", this.source.toJson($$0));
            return $$1;
        }
    }
}