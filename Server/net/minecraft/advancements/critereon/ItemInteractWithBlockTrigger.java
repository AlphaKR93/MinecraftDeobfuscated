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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ItemInteractWithBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public ItemInteractWithBlockTrigger(ResourceLocation $$0) {
        this.id = $$0;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        LocationPredicate $$3 = LocationPredicate.fromJson($$0.get("location"));
        ItemPredicate $$4 = ItemPredicate.fromJson($$0.get("item"));
        return new TriggerInstance(this.id, $$1, $$3, $$4);
    }

    public void trigger(ServerPlayer $$0, BlockPos $$1, ItemStack $$2) {
        BlockState $$3 = $$0.getLevel().getBlockState($$1);
        this.trigger($$0, $$4 -> $$4.matches($$3, $$0.getLevel(), $$1, $$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final LocationPredicate location;
        private final ItemPredicate item;

        public TriggerInstance(ResourceLocation $$0, EntityPredicate.Composite $$1, LocationPredicate $$2, ItemPredicate $$3) {
            super($$0, $$1);
            this.location = $$2;
            this.item = $$3;
        }

        public static TriggerInstance itemUsedOnBlock(LocationPredicate.Builder $$0, ItemPredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.id, EntityPredicate.Composite.ANY, $$0.build(), $$1.build());
        }

        public static TriggerInstance allayDropItemOnBlock(LocationPredicate.Builder $$0, ItemPredicate.Builder $$1) {
            return new TriggerInstance(CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.id, EntityPredicate.Composite.ANY, $$0.build(), $$1.build());
        }

        public boolean matches(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3) {
            if (!this.location.matches($$1, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5)) {
                return false;
            }
            return this.item.matches($$3);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("location", this.location.serializeToJson());
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}