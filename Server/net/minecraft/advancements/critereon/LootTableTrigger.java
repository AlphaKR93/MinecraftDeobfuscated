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
import net.minecraft.util.GsonHelper;

public class LootTableTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$0, "loot_table"));
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, ResourceLocation $$12) {
        this.trigger($$0, $$1 -> $$1.matches($$12));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ResourceLocation lootTable;

        public TriggerInstance(EntityPredicate.Composite $$0, ResourceLocation $$1) {
            super(ID, $$0);
            this.lootTable = $$1;
        }

        public static TriggerInstance lootTableUsed(ResourceLocation $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$0);
        }

        public boolean matches(ResourceLocation $$0) {
            return this.lootTable.equals($$0);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.addProperty("loot_table", this.lootTable.toString());
            return $$1;
        }
    }
}