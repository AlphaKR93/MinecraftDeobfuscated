/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.Objects
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Variant
implements ModelState {
    private final ResourceLocation modelLocation;
    private final Transformation rotation;
    private final boolean uvLock;
    private final int weight;

    public Variant(ResourceLocation $$0, Transformation $$1, boolean $$2, int $$3) {
        this.modelLocation = $$0;
        this.rotation = $$1;
        this.uvLock = $$2;
        this.weight = $$3;
    }

    public ResourceLocation getModelLocation() {
        return this.modelLocation;
    }

    @Override
    public Transformation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isUvLocked() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + "}";
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof Variant) {
            Variant $$1 = (Variant)$$0;
            return this.modelLocation.equals($$1.modelLocation) && Objects.equals((Object)this.rotation, (Object)$$1.rotation) && this.uvLock == $$1.uvLock && this.weight == $$1.weight;
        }
        return false;
    }

    public int hashCode() {
        int $$0 = this.modelLocation.hashCode();
        $$0 = 31 * $$0 + this.rotation.hashCode();
        $$0 = 31 * $$0 + Boolean.valueOf((boolean)this.uvLock).hashCode();
        $$0 = 31 * $$0 + this.weight;
        return $$0;
    }

    public static class Deserializer
    implements JsonDeserializer<Variant> {
        @VisibleForTesting
        static final boolean DEFAULT_UVLOCK = false;
        @VisibleForTesting
        static final int DEFAULT_WEIGHT = 1;
        @VisibleForTesting
        static final int DEFAULT_X_ROTATION = 0;
        @VisibleForTesting
        static final int DEFAULT_Y_ROTATION = 0;

        public Variant deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            ResourceLocation $$4 = this.getModel($$3);
            BlockModelRotation $$5 = this.getBlockRotation($$3);
            boolean $$6 = this.getUvLock($$3);
            int $$7 = this.getWeight($$3);
            return new Variant($$4, $$5.getRotation(), $$6, $$7);
        }

        private boolean getUvLock(JsonObject $$0) {
            return GsonHelper.getAsBoolean($$0, "uvlock", false);
        }

        protected BlockModelRotation getBlockRotation(JsonObject $$0) {
            int $$2;
            int $$1 = GsonHelper.getAsInt($$0, "x", 0);
            BlockModelRotation $$3 = BlockModelRotation.by($$1, $$2 = GsonHelper.getAsInt($$0, "y", 0));
            if ($$3 == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + $$1 + ", y: " + $$2);
            }
            return $$3;
        }

        protected ResourceLocation getModel(JsonObject $$0) {
            return new ResourceLocation(GsonHelper.getAsString($$0, "model"));
        }

        protected int getWeight(JsonObject $$0) {
            int $$1 = GsonHelper.getAsInt($$0, "weight", 1);
            if ($$1 < 1) {
                throw new JsonParseException("Invalid weight " + $$1 + " found, expected integer >= 1");
            }
            return $$1;
        }
    }
}