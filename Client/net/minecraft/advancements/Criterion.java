/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
    @Nullable
    private final CriterionTriggerInstance trigger;

    public Criterion(CriterionTriggerInstance $$0) {
        this.trigger = $$0;
    }

    public Criterion() {
        this.trigger = null;
    }

    public void serializeToNetwork(FriendlyByteBuf $$0) {
    }

    public static Criterion criterionFromJson(JsonObject $$0, DeserializationContext $$1) {
        ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString($$0, "trigger"));
        CriterionTrigger $$3 = CriteriaTriggers.getCriterion($$2);
        if ($$3 == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + $$2);
        }
        Object $$4 = $$3.createInstance(GsonHelper.getAsJsonObject($$0, "conditions", new JsonObject()), $$1);
        return new Criterion((CriterionTriggerInstance)$$4);
    }

    public static Criterion criterionFromNetwork(FriendlyByteBuf $$0) {
        return new Criterion();
    }

    public static Map<String, Criterion> criteriaFromJson(JsonObject $$0, DeserializationContext $$1) {
        HashMap $$2 = Maps.newHashMap();
        for (Map.Entry $$3 : $$0.entrySet()) {
            $$2.put((Object)((String)$$3.getKey()), (Object)Criterion.criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)$$3.getValue(), "criterion"), $$1));
        }
        return $$2;
    }

    public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf $$0) {
        return $$0.readMap(FriendlyByteBuf::readUtf, Criterion::criterionFromNetwork);
    }

    public static void serializeToNetwork(Map<String, Criterion> $$02, FriendlyByteBuf $$12) {
        $$12.writeMap($$02, FriendlyByteBuf::writeUtf, ($$0, $$1) -> $$1.serializeToNetwork((FriendlyByteBuf)((Object)$$0)));
    }

    @Nullable
    public CriterionTriggerInstance getTrigger() {
        return this.trigger;
    }

    public JsonElement serializeToJson() {
        if (this.trigger == null) {
            throw new JsonSyntaxException("Missing trigger");
        }
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("trigger", this.trigger.getCriterion().toString());
        JsonObject $$1 = this.trigger.serializeToJson(SerializationContext.INSTANCE);
        if ($$1.size() != 0) {
            $$0.add("conditions", (JsonElement)$$1);
        }
        return $$0;
    }
}