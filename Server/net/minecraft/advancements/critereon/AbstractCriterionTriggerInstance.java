/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCriterionTriggerInstance
implements CriterionTriggerInstance {
    private final ResourceLocation criterion;
    private final EntityPredicate.Composite player;

    public AbstractCriterionTriggerInstance(ResourceLocation $$0, EntityPredicate.Composite $$1) {
        this.criterion = $$0;
        this.player = $$1;
    }

    @Override
    public ResourceLocation getCriterion() {
        return this.criterion;
    }

    protected EntityPredicate.Composite getPlayerPredicate() {
        return this.player;
    }

    @Override
    public JsonObject serializeToJson(SerializationContext $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.add("player", this.player.toJson($$0));
        return $$1;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.criterion + "}";
    }
}