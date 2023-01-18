/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("placed_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$12, DeserializationContext $$2) {
        Block $$3 = PlacedBlockTrigger.deserializeBlock($$0);
        StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson($$0.get("state"));
        if ($$3 != null) {
            $$4.checkState($$3.getStateDefinition(), (Consumer<String>)((Consumer)$$1 -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + $$1 + ":");
            }));
        }
        LocationPredicate $$5 = LocationPredicate.fromJson($$0.get("location"));
        ItemPredicate $$6 = ItemPredicate.fromJson($$0.get("item"));
        return new TriggerInstance($$12, $$3, $$4, $$5, $$6);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject $$0) {
        if ($$0.has("block")) {
            ResourceLocation $$1 = new ResourceLocation(GsonHelper.getAsString($$0, "block"));
            return (Block)BuiltInRegistries.BLOCK.getOptional($$1).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + $$1 + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayer $$0, BlockPos $$1, ItemStack $$2) {
        BlockState $$3 = $$0.getLevel().getBlockState($$1);
        this.trigger($$0, $$4 -> $$4.matches($$3, $$1, $$0.getLevel(), $$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Block block;
        private final StatePropertiesPredicate state;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public TriggerInstance(EntityPredicate.Composite $$0, @Nullable Block $$1, StatePropertiesPredicate $$2, LocationPredicate $$3, ItemPredicate $$4) {
            super(ID, $$0);
            this.block = $$1;
            this.state = $$2;
            this.location = $$3;
            this.item = $$4;
        }

        public static TriggerInstance placedBlock(Block $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(BlockState $$0, BlockPos $$1, ServerLevel $$2, ItemStack $$3) {
            if (this.block != null && !$$0.is(this.block)) {
                return false;
            }
            if (!this.state.matches($$0)) {
                return false;
            }
            if (!this.location.matches($$2, $$1.getX(), $$1.getY(), $$1.getZ())) {
                return false;
            }
            return this.item.matches($$3);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (this.block != null) {
                $$1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }
            $$1.add("state", this.state.serializeToJson());
            $$1.add("location", this.location.serializeToJson());
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}