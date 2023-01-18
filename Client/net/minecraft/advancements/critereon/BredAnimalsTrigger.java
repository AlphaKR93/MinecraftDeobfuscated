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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("bred_animals");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite $$3 = EntityPredicate.Composite.fromJson($$0, "parent", $$2);
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "partner", $$2);
        EntityPredicate.Composite $$5 = EntityPredicate.Composite.fromJson($$0, "child", $$2);
        return new TriggerInstance($$1, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer $$0, Animal $$1, Animal $$2, @Nullable AgeableMob $$32) {
        LootContext $$4 = EntityPredicate.createContext($$0, $$1);
        LootContext $$5 = EntityPredicate.createContext($$0, $$2);
        LootContext $$6 = $$32 != null ? EntityPredicate.createContext($$0, $$32) : null;
        this.trigger($$0, $$3 -> $$3.matches($$4, $$5, $$6));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite parent;
        private final EntityPredicate.Composite partner;
        private final EntityPredicate.Composite child;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite $$1, EntityPredicate.Composite $$2, EntityPredicate.Composite $$3) {
            super(ID, $$0);
            this.parent = $$1;
            this.partner = $$2;
            this.child = $$3;
        }

        public static TriggerInstance bredAnimals() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance bredAnimals(EntityPredicate.Builder $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0.build()));
        }

        public static TriggerInstance bredAnimals(EntityPredicate $$0, EntityPredicate $$1, EntityPredicate $$2) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap($$0), EntityPredicate.Composite.wrap($$1), EntityPredicate.Composite.wrap($$2));
        }

        public boolean matches(LootContext $$0, LootContext $$1, @Nullable LootContext $$2) {
            if (!(this.child == EntityPredicate.Composite.ANY || $$2 != null && this.child.matches($$2))) {
                return false;
            }
            return this.parent.matches($$0) && this.partner.matches($$1) || this.parent.matches($$1) && this.partner.matches($$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("parent", this.parent.toJson($$0));
            $$1.add("partner", this.partner.toJson($$0));
            $$1.add("child", this.child.toJson($$0));
            return $$1;
        }
    }
}