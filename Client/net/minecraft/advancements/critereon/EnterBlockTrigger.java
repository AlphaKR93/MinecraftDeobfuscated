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
 *  net.minecraft.server.level.ServerPlayer
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("enter_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$12, DeserializationContext $$2) {
        Block $$3 = EnterBlockTrigger.deserializeBlock($$0);
        StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson($$0.get("state"));
        if ($$3 != null) {
            $$4.checkState($$3.getStateDefinition(), (Consumer<String>)((Consumer)$$1 -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + $$1);
            }));
        }
        return new TriggerInstance($$12, $$3, $$4);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject $$0) {
        if ($$0.has("block")) {
            ResourceLocation $$1 = new ResourceLocation(GsonHelper.getAsString($$0, "block"));
            return (Block)BuiltInRegistries.BLOCK.getOptional($$1).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + $$1 + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayer $$0, BlockState $$12) {
        this.trigger($$0, $$1 -> $$1.matches($$12));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Block block;
        private final StatePropertiesPredicate state;

        public TriggerInstance(EntityPredicate.Composite $$0, @Nullable Block $$1, StatePropertiesPredicate $$2) {
            super(ID, $$0);
            this.block = $$1;
            this.state = $$2;
        }

        public static TriggerInstance entersBlock(Block $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0, StatePropertiesPredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (this.block != null) {
                $$1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }
            $$1.add("state", this.state.serializeToJson());
            return $$1;
        }

        public boolean matches(BlockState $$0) {
            if (this.block != null && !$$0.is(this.block)) {
                return false;
            }
            return this.state.matches($$0);
        }
    }
}