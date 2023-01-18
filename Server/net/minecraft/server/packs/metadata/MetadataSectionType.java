/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.server.packs.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface MetadataSectionType<T>
extends MetadataSectionSerializer<T> {
    public JsonObject toJson(T var1);

    public static <T> MetadataSectionType<T> fromCodec(final String $$0, final Codec<T> $$1) {
        return new MetadataSectionType<T>(){

            @Override
            public String getMetadataSectionName() {
                return $$0;
            }

            @Override
            public T fromJson(JsonObject $$02) {
                return $$1.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$02).getOrThrow(false, $$0 -> {});
            }

            @Override
            public JsonObject toJson(T $$02) {
                return ((JsonElement)$$1.encodeStart((DynamicOps)JsonOps.INSTANCE, $$02).getOrThrow(false, $$0 -> {})).getAsJsonObject();
            }
        };
    }
}