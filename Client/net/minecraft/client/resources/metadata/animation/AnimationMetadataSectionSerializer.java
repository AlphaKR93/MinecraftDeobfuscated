/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.ClassCastException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
implements MetadataSectionSerializer<AnimationMetadataSection> {
    @Override
    public AnimationMetadataSection fromJson(JsonObject $$0) {
        ImmutableList.Builder $$1 = ImmutableList.builder();
        int $$2 = GsonHelper.getAsInt($$0, "frametime", 1);
        if ($$2 != 1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)$$2, (String)"Invalid default frame time");
        }
        if ($$0.has("frames")) {
            try {
                JsonArray $$3 = GsonHelper.getAsJsonArray($$0, "frames");
                for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                    JsonElement $$5 = $$3.get($$4);
                    AnimationFrame $$6 = this.getFrame($$4, $$5);
                    if ($$6 == null) continue;
                    $$1.add((Object)$$6);
                }
            }
            catch (ClassCastException $$7) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + $$0.get("frames"), (Throwable)$$7);
            }
        }
        int $$8 = GsonHelper.getAsInt($$0, "width", -1);
        int $$9 = GsonHelper.getAsInt($$0, "height", -1);
        if ($$8 != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)$$8, (String)"Invalid width");
        }
        if ($$9 != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)$$9, (String)"Invalid height");
        }
        boolean $$10 = GsonHelper.getAsBoolean($$0, "interpolate", false);
        return new AnimationMetadataSection((List<AnimationFrame>)$$1.build(), $$8, $$9, $$2, $$10);
    }

    @Nullable
    private AnimationFrame getFrame(int $$0, JsonElement $$1) {
        if ($$1.isJsonPrimitive()) {
            return new AnimationFrame(GsonHelper.convertToInt($$1, "frames[" + $$0 + "]"));
        }
        if ($$1.isJsonObject()) {
            JsonObject $$2 = GsonHelper.convertToJsonObject($$1, "frames[" + $$0 + "]");
            int $$3 = GsonHelper.getAsInt($$2, "time", -1);
            if ($$2.has("time")) {
                Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)$$3, (String)"Invalid frame time");
            }
            int $$4 = GsonHelper.getAsInt($$2, "index");
            Validate.inclusiveBetween((long)0L, (long)Integer.MAX_VALUE, (long)$$4, (String)"Invalid frame index");
            return new AnimationFrame($$4, $$3);
        }
        return null;
    }

    @Override
    public String getMetadataSectionName() {
        return "animation";
    }
}