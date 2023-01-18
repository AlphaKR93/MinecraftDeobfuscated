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
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite $$3 = EntityPredicate.Composite.fromJson($$0, "zombie", $$2);
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "villager", $$2);
        return new TriggerInstance($$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, Zombie $$1, Villager $$22) {
        LootContext $$3 = EntityPredicate.createContext($$0, $$1);
        LootContext $$4 = EntityPredicate.createContext($$0, $$22);
        this.trigger($$0, $$2 -> $$2.matches($$3, $$4));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite zombie;
        private final EntityPredicate.Composite villager;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite $$1, EntityPredicate.Composite $$2) {
            super(ID, $$0);
            this.zombie = $$1;
            this.villager = $$2;
        }

        public static TriggerInstance curedZombieVillager() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
        }

        public boolean matches(LootContext $$0, LootContext $$1) {
            if (!this.zombie.matches($$0)) {
                return false;
            }
            return this.villager.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("zombie", this.zombie.toJson($$0));
            $$1.add("villager", this.villager.toJson($$0));
            return $$1;
        }
    }
}