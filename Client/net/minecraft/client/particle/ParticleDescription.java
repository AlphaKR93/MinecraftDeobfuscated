/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
    @Nullable
    private final List<ResourceLocation> textures;

    private ParticleDescription(@Nullable List<ResourceLocation> $$0) {
        this.textures = $$0;
    }

    @Nullable
    public List<ResourceLocation> getTextures() {
        return this.textures;
    }

    public static ParticleDescription fromJson(JsonObject $$02) {
        List<ResourceLocation> $$3;
        JsonArray $$1 = GsonHelper.getAsJsonArray($$02, "textures", null);
        if ($$1 != null) {
            List $$2 = (List)Streams.stream((Iterable)$$1).map($$0 -> GsonHelper.convertToString($$0, "texture")).map(ResourceLocation::new).collect(ImmutableList.toImmutableList());
        } else {
            $$3 = null;
        }
        return new ParticleDescription($$3);
    }
}