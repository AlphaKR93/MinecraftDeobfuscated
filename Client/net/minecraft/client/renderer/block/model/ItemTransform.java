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
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Type;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ItemTransform {
    public static final ItemTransform NO_TRANSFORM = new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(1.0f, 1.0f, 1.0f));
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;

    public ItemTransform(Vector3f $$0, Vector3f $$1, Vector3f $$2) {
        this.rotation = new Vector3f((Vector3fc)$$0);
        this.translation = new Vector3f((Vector3fc)$$1);
        this.scale = new Vector3f((Vector3fc)$$2);
    }

    public void apply(boolean $$0, PoseStack $$1) {
        if (this == NO_TRANSFORM) {
            return;
        }
        float $$2 = this.rotation.x();
        float $$3 = this.rotation.y();
        float $$4 = this.rotation.z();
        if ($$0) {
            $$3 = -$$3;
            $$4 = -$$4;
        }
        int $$5 = $$0 ? -1 : 1;
        $$1.translate((float)$$5 * this.translation.x(), this.translation.y(), this.translation.z());
        $$1.mulPose(new Quaternionf().rotationXYZ($$2 * ((float)Math.PI / 180), $$3 * ((float)Math.PI / 180), $$4 * ((float)Math.PI / 180)));
        $$1.scale(this.scale.x(), this.scale.y(), this.scale.z());
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (this.getClass() == $$0.getClass()) {
            ItemTransform $$1 = (ItemTransform)$$0;
            return this.rotation.equals((Object)$$1.rotation) && this.scale.equals((Object)$$1.scale) && this.translation.equals((Object)$$1.translation);
        }
        return false;
    }

    public int hashCode() {
        int $$0 = this.rotation.hashCode();
        $$0 = 31 * $$0 + this.translation.hashCode();
        $$0 = 31 * $$0 + this.scale.hashCode();
        return $$0;
    }

    protected static class Deserializer
    implements JsonDeserializer<ItemTransform> {
        private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
        public static final float MAX_TRANSLATION = 5.0f;
        public static final float MAX_SCALE = 4.0f;

        protected Deserializer() {
        }

        public ItemTransform deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Vector3f $$4 = this.getVector3f($$3, "rotation", DEFAULT_ROTATION);
            Vector3f $$5 = this.getVector3f($$3, "translation", DEFAULT_TRANSLATION);
            $$5.mul(0.0625f);
            $$5.set(Mth.clamp($$5.x, -5.0f, 5.0f), Mth.clamp($$5.y, -5.0f, 5.0f), Mth.clamp($$5.z, -5.0f, 5.0f));
            Vector3f $$6 = this.getVector3f($$3, "scale", DEFAULT_SCALE);
            $$6.set(Mth.clamp($$6.x, -4.0f, 4.0f), Mth.clamp($$6.y, -4.0f, 4.0f), Mth.clamp($$6.z, -4.0f, 4.0f));
            return new ItemTransform($$4, $$5, $$6);
        }

        private Vector3f getVector3f(JsonObject $$0, String $$1, Vector3f $$2) {
            if (!$$0.has($$1)) {
                return $$2;
            }
            JsonArray $$3 = GsonHelper.getAsJsonArray($$0, $$1);
            if ($$3.size() != 3) {
                throw new JsonParseException("Expected 3 " + $$1 + " values, found: " + $$3.size());
            }
            float[] $$4 = new float[3];
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                $$4[$$5] = GsonHelper.convertToFloat($$3.get($$5), $$1 + "[" + $$5 + "]");
            }
            return new Vector3f($$4[0], $$4[1], $$4[2]);
        }
    }
}