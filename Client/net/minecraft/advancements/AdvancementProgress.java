/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Date
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress
implements Comparable<AdvancementProgress> {
    final Map<String, CriterionProgress> criteria;
    private String[][] requirements = new String[0][];

    private AdvancementProgress(Map<String, CriterionProgress> $$0) {
        this.criteria = $$0;
    }

    public AdvancementProgress() {
        this.criteria = Maps.newHashMap();
    }

    public void update(Map<String, Criterion> $$0, String[][] $$12) {
        Set $$2 = $$0.keySet();
        this.criteria.entrySet().removeIf($$1 -> !$$2.contains($$1.getKey()));
        for (String $$3 : $$2) {
            if (this.criteria.containsKey((Object)$$3)) continue;
            this.criteria.put((Object)$$3, (Object)new CriterionProgress());
        }
        this.requirements = $$12;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        }
        for (String[] $$0 : this.requirements) {
            boolean $$1 = false;
            for (String $$2 : $$0) {
                CriterionProgress $$3 = this.getCriterion($$2);
                if ($$3 == null || !$$3.isDone()) continue;
                $$1 = true;
                break;
            }
            if ($$1) continue;
            return false;
        }
        return true;
    }

    public boolean hasProgress() {
        for (CriterionProgress $$0 : this.criteria.values()) {
            if (!$$0.isDone()) continue;
            return true;
        }
        return false;
    }

    public boolean grantProgress(String $$0) {
        CriterionProgress $$1 = (CriterionProgress)this.criteria.get((Object)$$0);
        if ($$1 != null && !$$1.isDone()) {
            $$1.grant();
            return true;
        }
        return false;
    }

    public boolean revokeProgress(String $$0) {
        CriterionProgress $$1 = (CriterionProgress)this.criteria.get((Object)$$0);
        if ($$1 != null && $$1.isDone()) {
            $$1.revoke();
            return true;
        }
        return false;
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + "}";
    }

    public void serializeToNetwork(FriendlyByteBuf $$02) {
        $$02.writeMap(this.criteria, FriendlyByteBuf::writeUtf, ($$0, $$1) -> $$1.serializeToNetwork((FriendlyByteBuf)((Object)$$0)));
    }

    public static AdvancementProgress fromNetwork(FriendlyByteBuf $$0) {
        Map $$1 = $$0.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
        return new AdvancementProgress($$1);
    }

    @Nullable
    public CriterionProgress getCriterion(String $$0) {
        return (CriterionProgress)this.criteria.get((Object)$$0);
    }

    public float getPercent() {
        if (this.criteria.isEmpty()) {
            return 0.0f;
        }
        float $$0 = this.requirements.length;
        float $$1 = this.countCompletedRequirements();
        return $$1 / $$0;
    }

    @Nullable
    public String getProgressText() {
        if (this.criteria.isEmpty()) {
            return null;
        }
        int $$0 = this.requirements.length;
        if ($$0 <= 1) {
            return null;
        }
        int $$1 = this.countCompletedRequirements();
        return $$1 + "/" + $$0;
    }

    private int countCompletedRequirements() {
        int $$0 = 0;
        for (String[] $$1 : this.requirements) {
            boolean $$2 = false;
            for (String $$3 : $$1) {
                CriterionProgress $$4 = this.getCriterion($$3);
                if ($$4 == null || !$$4.isDone()) continue;
                $$2 = true;
                break;
            }
            if (!$$2) continue;
            ++$$0;
        }
        return $$0;
    }

    public Iterable<String> getRemainingCriteria() {
        ArrayList $$0 = Lists.newArrayList();
        for (Map.Entry $$1 : this.criteria.entrySet()) {
            if (((CriterionProgress)$$1.getValue()).isDone()) continue;
            $$0.add((Object)((String)$$1.getKey()));
        }
        return $$0;
    }

    public Iterable<String> getCompletedCriteria() {
        ArrayList $$0 = Lists.newArrayList();
        for (Map.Entry $$1 : this.criteria.entrySet()) {
            if (!((CriterionProgress)$$1.getValue()).isDone()) continue;
            $$0.add((Object)((String)$$1.getKey()));
        }
        return $$0;
    }

    @Nullable
    public Date getFirstProgressDate() {
        Date $$0 = null;
        for (CriterionProgress $$1 : this.criteria.values()) {
            if (!$$1.isDone() || $$0 != null && !$$1.getObtained().before($$0)) continue;
            $$0 = $$1.getObtained();
        }
        return $$0;
    }

    public int compareTo(AdvancementProgress $$0) {
        Date $$1 = this.getFirstProgressDate();
        Date $$2 = $$0.getFirstProgressDate();
        if ($$1 == null && $$2 != null) {
            return 1;
        }
        if ($$1 != null && $$2 == null) {
            return -1;
        }
        if ($$1 == null && $$2 == null) {
            return 0;
        }
        return $$1.compareTo($$2);
    }

    public static class Serializer
    implements JsonDeserializer<AdvancementProgress>,
    JsonSerializer<AdvancementProgress> {
        public JsonElement serialize(AdvancementProgress $$0, Type $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            JsonObject $$4 = new JsonObject();
            for (Map.Entry $$5 : $$0.criteria.entrySet()) {
                CriterionProgress $$6 = (CriterionProgress)$$5.getValue();
                if (!$$6.isDone()) continue;
                $$4.add((String)$$5.getKey(), $$6.serializeToJson());
            }
            if (!$$4.entrySet().isEmpty()) {
                $$3.add("criteria", (JsonElement)$$4);
            }
            $$3.addProperty("done", Boolean.valueOf((boolean)$$0.isDone()));
            return $$3;
        }

        public AdvancementProgress deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "advancement");
            JsonObject $$4 = GsonHelper.getAsJsonObject($$3, "criteria", new JsonObject());
            AdvancementProgress $$5 = new AdvancementProgress();
            for (Map.Entry $$6 : $$4.entrySet()) {
                String $$7 = (String)$$6.getKey();
                $$5.criteria.put((Object)$$7, (Object)CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)$$6.getValue(), $$7)));
            }
            return $$5;
        }
    }
}