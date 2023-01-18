/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.resources.metadata.texture;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class TextureMetadataSectionSerializer
implements MetadataSectionSerializer<TextureMetadataSection> {
    @Override
    public TextureMetadataSection fromJson(JsonObject $$0) {
        boolean $$1 = GsonHelper.getAsBoolean($$0, "blur", false);
        boolean $$2 = GsonHelper.getAsBoolean($$0, "clamp", false);
        return new TextureMetadataSection($$1, $$2);
    }

    @Override
    public String getMetadataSectionName() {
        return "texture";
    }
}