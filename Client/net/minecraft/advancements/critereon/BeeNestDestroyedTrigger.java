/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        Block $$3 = BeeNestDestroyedTrigger.deserializeBlock($$0);
        ItemPredicate $$4 = ItemPredicate.fromJson($$0.get("item"));
        MinMaxBounds.Ints $$5 = MinMaxBounds.Ints.fromJson($$0.get("num_bees_inside"));
        return new TriggerInstance($$1, $$3, $$4, $$5);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject $$0) {
        if ($$0.has("block")) {
            ResourceLocation $$1 = new ResourceLocation(GsonHelper.getAsString($$0, "block"));
            return (Block)BuiltInRegistries.BLOCK.getOptional($$1).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + $$1 + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayer $$0, BlockState $$1, ItemStack $$2, int $$32) {
        this.trigger($$0, $$3 -> $$3.matches($$1, $$2, $$32));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Block block;
        private final ItemPredicate item;
        private final MinMaxBounds.Ints numBees;

        public TriggerInstance(EntityPredicate.Composite $$0, @Nullable Block $$1, ItemPredicate $$2, MinMaxBounds.Ints $$3) {
            super(ID, $$0);
            this.block = $$1;
            this.item = $$2;
            this.numBees = $$3;
        }

        public static TriggerInstance destroyedBeeNest(Block $$0, ItemPredicate.Builder $$1, MinMaxBounds.Ints $$2) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, $$1.build(), $$2);
        }

        public boolean matches(BlockState $$0, ItemStack $$1, int $$2) {
            if (this.block != null && !$$0.is(this.block)) {
                return false;
            }
            if (!this.item.matches($$1)) {
                return false;
            }
            return this.numBees.matches($$2);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (this.block != null) {
                $$1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }
            $$1.add("item", this.item.serializeToJson());
            $$1.add("num_bees_inside", this.numBees.serializeToJson());
            return $$1;
        }
    }
}