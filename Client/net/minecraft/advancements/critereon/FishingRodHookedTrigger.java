/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ItemPredicate $$3 = ItemPredicate.fromJson($$0.get("rod"));
        EntityPredicate.Composite $$4 = EntityPredicate.Composite.fromJson($$0, "entity", $$2);
        ItemPredicate $$5 = ItemPredicate.fromJson($$0.get("item"));
        return new TriggerInstance($$1, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer $$0, ItemStack $$1, FishingHook $$2, Collection<ItemStack> $$32) {
        LootContext $$4 = EntityPredicate.createContext($$0, $$2.getHookedIn() != null ? $$2.getHookedIn() : $$2);
        this.trigger($$0, $$3 -> $$3.matches($$1, $$4, $$32));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate rod;
        private final EntityPredicate.Composite entity;
        private final ItemPredicate item;

        public TriggerInstance(EntityPredicate.Composite $$0, ItemPredicate $$1, EntityPredicate.Composite $$2, ItemPredicate $$3) {
            super(ID, $$0);
            this.rod = $$1;
            this.entity = $$2;
            this.item = $$3;
        }

        public static TriggerInstance fishedItem(ItemPredicate $$0, EntityPredicate $$1, ItemPredicate $$2) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, EntityPredicate.Composite.wrap($$1), $$2);
        }

        public boolean matches(ItemStack $$0, LootContext $$1, Collection<ItemStack> $$2) {
            if (!this.rod.matches($$0)) {
                return false;
            }
            if (!this.entity.matches($$1)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                ItemEntity $$5;
                boolean $$3 = false;
                Entity $$4 = $$1.getParamOrNull(LootContextParams.THIS_ENTITY);
                if ($$4 instanceof ItemEntity && this.item.matches(($$5 = (ItemEntity)$$4).getItem())) {
                    $$3 = true;
                }
                for (ItemStack $$6 : $$2) {
                    if (!this.item.matches($$6)) continue;
                    $$3 = true;
                    break;
                }
                if (!$$3) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("rod", this.rod.serializeToJson());
            $$1.add("entity", this.entity.toJson($$0));
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}