/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.EnumMap
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class BlockElement {
    private static final boolean DEFAULT_RESCALE = false;
    private static final float MIN_EXTENT = -16.0f;
    private static final float MAX_EXTENT = 32.0f;
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, BlockElementFace> faces;
    public final BlockElementRotation rotation;
    public final boolean shade;

    public BlockElement(Vector3f $$0, Vector3f $$1, Map<Direction, BlockElementFace> $$2, @Nullable BlockElementRotation $$3, boolean $$4) {
        this.from = $$0;
        this.to = $$1;
        this.faces = $$2;
        this.rotation = $$3;
        this.shade = $$4;
        this.fillUvs();
    }

    private void fillUvs() {
        for (Map.Entry $$0 : this.faces.entrySet()) {
            float[] $$1 = this.uvsByFace((Direction)$$0.getKey());
            ((BlockElementFace)$$0.getValue()).uv.setMissingUv($$1);
        }
    }

    private float[] uvsByFace(Direction $$0) {
        switch ($$0) {
            case DOWN: {
                return new float[]{this.from.x(), 16.0f - this.to.z(), this.to.x(), 16.0f - this.from.z()};
            }
            case UP: {
                return new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
            }
            default: {
                return new float[]{16.0f - this.to.x(), 16.0f - this.to.y(), 16.0f - this.from.x(), 16.0f - this.from.y()};
            }
            case SOUTH: {
                return new float[]{this.from.x(), 16.0f - this.to.y(), this.to.x(), 16.0f - this.from.y()};
            }
            case WEST: {
                return new float[]{this.from.z(), 16.0f - this.to.y(), this.to.z(), 16.0f - this.from.y()};
            }
            case EAST: 
        }
        return new float[]{16.0f - this.to.z(), 16.0f - this.to.y(), 16.0f - this.from.z(), 16.0f - this.from.y()};
    }

    protected static class Deserializer
    implements JsonDeserializer<BlockElement> {
        private static final boolean DEFAULT_SHADE = true;

        protected Deserializer() {
        }

        public BlockElement deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Vector3f $$4 = this.getFrom($$3);
            Vector3f $$5 = this.getTo($$3);
            BlockElementRotation $$6 = this.getRotation($$3);
            Map<Direction, BlockElementFace> $$7 = this.getFaces($$2, $$3);
            if ($$3.has("shade") && !GsonHelper.isBooleanValue($$3, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean $$8 = GsonHelper.getAsBoolean($$3, "shade", true);
            return new BlockElement($$4, $$5, $$7, $$6, $$8);
        }

        @Nullable
        private BlockElementRotation getRotation(JsonObject $$0) {
            BlockElementRotation $$1 = null;
            if ($$0.has("rotation")) {
                JsonObject $$2 = GsonHelper.getAsJsonObject($$0, "rotation");
                Vector3f $$3 = this.getVector3f($$2, "origin");
                $$3.mul(0.0625f);
                Direction.Axis $$4 = this.getAxis($$2);
                float $$5 = this.getAngle($$2);
                boolean $$6 = GsonHelper.getAsBoolean($$2, "rescale", false);
                $$1 = new BlockElementRotation($$3, $$4, $$5, $$6);
            }
            return $$1;
        }

        private float getAngle(JsonObject $$0) {
            float $$1 = GsonHelper.getAsFloat($$0, "angle");
            if ($$1 != 0.0f && Mth.abs($$1) != 22.5f && Mth.abs($$1) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + $$1 + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return $$1;
        }

        private Direction.Axis getAxis(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "axis");
            Direction.Axis $$2 = Direction.Axis.byName($$1.toLowerCase(Locale.ROOT));
            if ($$2 == null) {
                throw new JsonParseException("Invalid rotation axis: " + $$1);
            }
            return $$2;
        }

        private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext $$0, JsonObject $$1) {
            Map<Direction, BlockElementFace> $$2 = this.filterNullFromFaces($$0, $$1);
            if ($$2.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return $$2;
        }

        private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext $$0, JsonObject $$1) {
            EnumMap $$2 = Maps.newEnumMap(Direction.class);
            JsonObject $$3 = GsonHelper.getAsJsonObject($$1, "faces");
            for (Map.Entry $$4 : $$3.entrySet()) {
                Direction $$5 = this.getFacing((String)$$4.getKey());
                $$2.put((Object)$$5, (Object)((BlockElementFace)$$0.deserialize((JsonElement)$$4.getValue(), BlockElementFace.class)));
            }
            return $$2;
        }

        private Direction getFacing(String $$0) {
            Direction $$1 = Direction.byName($$0);
            if ($$1 == null) {
                throw new JsonParseException("Unknown facing: " + $$0);
            }
            return $$1;
        }

        private Vector3f getTo(JsonObject $$0) {
            Vector3f $$1 = this.getVector3f($$0, "to");
            if ($$1.x() < -16.0f || $$1.y() < -16.0f || $$1.z() < -16.0f || $$1.x() > 32.0f || $$1.y() > 32.0f || $$1.z() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + $$1);
            }
            return $$1;
        }

        private Vector3f getFrom(JsonObject $$0) {
            Vector3f $$1 = this.getVector3f($$0, "from");
            if ($$1.x() < -16.0f || $$1.y() < -16.0f || $$1.z() < -16.0f || $$1.x() > 32.0f || $$1.y() > 32.0f || $$1.z() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + $$1);
            }
            return $$1;
        }

        private Vector3f getVector3f(JsonObject $$0, String $$1) {
            JsonArray $$2 = GsonHelper.getAsJsonArray($$0, $$1);
            if ($$2.size() != 3) {
                throw new JsonParseException("Expected 3 " + $$1 + " values, found: " + $$2.size());
            }
            float[] $$3 = new float[3];
            for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
                $$3[$$4] = GsonHelper.convertToFloat($$2.get($$4), $$1 + "[" + $$4 + "]");
            }
            return new Vector3f($$3[0], $$3[1], $$3[2]);
        }
    }
}