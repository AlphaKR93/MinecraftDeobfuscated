/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class ConditionReference
implements LootItemCondition {
    private static final Logger LOGGER = LogUtils.getLogger();
    final ResourceLocation name;

    ConditionReference(ResourceLocation $$0) {
        this.name = $$0;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.REFERENCE;
    }

    @Override
    public void validate(ValidationContext $$0) {
        if ($$0.hasVisitedCondition(this.name)) {
            $$0.reportProblem("Condition " + this.name + " is recursively called");
            return;
        }
        LootItemCondition.super.validate($$0);
        LootItemCondition $$1 = $$0.resolveCondition(this.name);
        if ($$1 == null) {
            $$0.reportProblem("Unknown condition table called " + this.name);
        } else {
            $$1.validate($$0.enterTable(".{" + this.name + "}", this.name));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean test(LootContext $$0) {
        LootItemCondition $$1 = $$0.getCondition(this.name);
        if ($$1 == null) {
            LOGGER.warn("Tried using unknown condition table called {}", (Object)this.name);
            return false;
        }
        if ($$0.addVisitedCondition($$1)) {
            try {
                boolean bl = $$1.test($$0);
                return bl;
            }
            finally {
                $$0.removeVisitedCondition($$1);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return false;
    }

    public static LootItemCondition.Builder conditionReference(ResourceLocation $$0) {
        return () -> new ConditionReference($$0);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ConditionReference> {
        @Override
        public void serialize(JsonObject $$0, ConditionReference $$1, JsonSerializationContext $$2) {
            $$0.addProperty("name", $$1.name.toString());
        }

        @Override
        public ConditionReference deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString($$0, "name"));
            return new ConditionReference($$2);
        }
    }
}