/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.LinkedHashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectsPredicate {
    public static final MobEffectsPredicate ANY = new MobEffectsPredicate((Map<MobEffect, MobEffectInstancePredicate>)Collections.emptyMap());
    private final Map<MobEffect, MobEffectInstancePredicate> effects;

    public MobEffectsPredicate(Map<MobEffect, MobEffectInstancePredicate> $$0) {
        this.effects = $$0;
    }

    public static MobEffectsPredicate effects() {
        return new MobEffectsPredicate((Map<MobEffect, MobEffectInstancePredicate>)Maps.newLinkedHashMap());
    }

    public MobEffectsPredicate and(MobEffect $$0) {
        this.effects.put((Object)$$0, (Object)new MobEffectInstancePredicate());
        return this;
    }

    public MobEffectsPredicate and(MobEffect $$0, MobEffectInstancePredicate $$1) {
        this.effects.put((Object)$$0, (Object)$$1);
        return this;
    }

    public boolean matches(Entity $$0) {
        if (this == ANY) {
            return true;
        }
        if ($$0 instanceof LivingEntity) {
            return this.matches(((LivingEntity)$$0).getActiveEffectsMap());
        }
        return false;
    }

    public boolean matches(LivingEntity $$0) {
        if (this == ANY) {
            return true;
        }
        return this.matches($$0.getActiveEffectsMap());
    }

    public boolean matches(Map<MobEffect, MobEffectInstance> $$0) {
        if (this == ANY) {
            return true;
        }
        for (Map.Entry $$1 : this.effects.entrySet()) {
            MobEffectInstance $$2 = (MobEffectInstance)$$0.get($$1.getKey());
            if (((MobEffectInstancePredicate)$$1.getValue()).matches($$2)) continue;
            return false;
        }
        return true;
    }

    public static MobEffectsPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "effects");
        LinkedHashMap $$2 = Maps.newLinkedHashMap();
        for (Map.Entry $$3 : $$1.entrySet()) {
            ResourceLocation $$4 = new ResourceLocation((String)$$3.getKey());
            MobEffect $$5 = (MobEffect)BuiltInRegistries.MOB_EFFECT.getOptional($$4).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + $$4 + "'"));
            MobEffectInstancePredicate $$6 = MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject((JsonElement)$$3.getValue(), (String)$$3.getKey()));
            $$2.put((Object)$$5, (Object)$$6);
        }
        return new MobEffectsPredicate((Map<MobEffect, MobEffectInstancePredicate>)$$2);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        for (Map.Entry $$1 : this.effects.entrySet()) {
            $$0.add(BuiltInRegistries.MOB_EFFECT.getKey((MobEffect)$$1.getKey()).toString(), ((MobEffectInstancePredicate)$$1.getValue()).serializeToJson());
        }
        return $$0;
    }

    public static class MobEffectInstancePredicate {
        private final MinMaxBounds.Ints amplifier;
        private final MinMaxBounds.Ints duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public MobEffectInstancePredicate(MinMaxBounds.Ints $$0, MinMaxBounds.Ints $$1, @Nullable Boolean $$2, @Nullable Boolean $$3) {
            this.amplifier = $$0;
            this.duration = $$1;
            this.ambient = $$2;
            this.visible = $$3;
        }

        public MobEffectInstancePredicate() {
            this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, null, null);
        }

        public boolean matches(@Nullable MobEffectInstance $$0) {
            if ($$0 == null) {
                return false;
            }
            if (!this.amplifier.matches($$0.getAmplifier())) {
                return false;
            }
            if (!this.duration.matches($$0.getDuration())) {
                return false;
            }
            if (this.ambient != null && this.ambient.booleanValue() != $$0.isAmbient()) {
                return false;
            }
            return this.visible == null || this.visible.booleanValue() == $$0.isVisible();
        }

        public JsonElement serializeToJson() {
            JsonObject $$0 = new JsonObject();
            $$0.add("amplifier", this.amplifier.serializeToJson());
            $$0.add("duration", this.duration.serializeToJson());
            $$0.addProperty("ambient", this.ambient);
            $$0.addProperty("visible", this.visible);
            return $$0;
        }

        public static MobEffectInstancePredicate fromJson(JsonObject $$0) {
            MinMaxBounds.Ints $$1 = MinMaxBounds.Ints.fromJson($$0.get("amplifier"));
            MinMaxBounds.Ints $$2 = MinMaxBounds.Ints.fromJson($$0.get("duration"));
            Boolean $$3 = $$0.has("ambient") ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, "ambient")) : null;
            Boolean $$4 = $$0.has("visible") ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, "visible")) : null;
            return new MobEffectInstancePredicate($$1, $$2, $$3, $$4);
        }
    }
}