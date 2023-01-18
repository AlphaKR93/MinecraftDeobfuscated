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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("using_item");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ItemPredicate $$3 = ItemPredicate.fromJson($$0.get("item"));
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, ItemStack $$12) {
        this.trigger($$0, $$1 -> $$1.matches($$12));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(EntityPredicate.Composite $$0, ItemPredicate $$1) {
            super(ID, $$0);
            this.item = $$1;
        }

        public static TriggerInstance lookingAt(EntityPredicate.Builder $$0, ItemPredicate.Builder $$1) {
            return new TriggerInstance(EntityPredicate.Composite.wrap($$0.build()), $$1.build());
        }

        public boolean matches(ItemStack $$0) {
            return this.item.matches($$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}