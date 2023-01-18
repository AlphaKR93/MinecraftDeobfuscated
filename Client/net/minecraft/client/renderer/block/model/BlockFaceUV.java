/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.reflect.Type
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

public class BlockFaceUV {
    public float[] uvs;
    public final int rotation;

    public BlockFaceUV(@Nullable float[] $$0, int $$1) {
        this.uvs = $$0;
        this.rotation = $$1;
    }

    public float getU(int $$0) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int $$1 = this.getShiftedIndex($$0);
        return this.uvs[$$1 == 0 || $$1 == 1 ? 0 : 2];
    }

    public float getV(int $$0) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int $$1 = this.getShiftedIndex($$0);
        return this.uvs[$$1 == 0 || $$1 == 3 ? 1 : 3];
    }

    private int getShiftedIndex(int $$0) {
        return ($$0 + this.rotation / 90) % 4;
    }

    public int getReverseIndex(int $$0) {
        return ($$0 + 4 - this.rotation / 90) % 4;
    }

    public void setMissingUv(float[] $$0) {
        if (this.uvs == null) {
            this.uvs = $$0;
        }
    }

    protected static class Deserializer
    implements JsonDeserializer<BlockFaceUV> {
        private static final int DEFAULT_ROTATION = 0;

        protected Deserializer() {
        }

        public BlockFaceUV deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            float[] $$4 = this.getUVs($$3);
            int $$5 = this.getRotation($$3);
            return new BlockFaceUV($$4, $$5);
        }

        protected int getRotation(JsonObject $$0) {
            int $$1 = GsonHelper.getAsInt($$0, "rotation", 0);
            if ($$1 < 0 || $$1 % 90 != 0 || $$1 / 90 > 3) {
                throw new JsonParseException("Invalid rotation " + $$1 + " found, only 0/90/180/270 allowed");
            }
            return $$1;
        }

        @Nullable
        private float[] getUVs(JsonObject $$0) {
            if (!$$0.has("uv")) {
                return null;
            }
            JsonArray $$1 = GsonHelper.getAsJsonArray($$0, "uv");
            if ($$1.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + $$1.size());
            }
            float[] $$2 = new float[4];
            for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
                $$2[$$3] = GsonHelper.convertToFloat($$1.get($$3), "uv[" + $$3 + "]");
            }
            return $$2;
        }
    }
}