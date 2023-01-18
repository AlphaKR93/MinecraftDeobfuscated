/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.GsonHelper;

public class PackMetadataSectionSerializer
implements MetadataSectionType<PackMetadataSection> {
    @Override
    public PackMetadataSection fromJson(JsonObject $$0) {
        MutableComponent $$1 = Component.Serializer.fromJson($$0.get("description"));
        if ($$1 == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int $$2 = GsonHelper.getAsInt($$0, "pack_format");
        return new PackMetadataSection($$1, $$2);
    }

    @Override
    public JsonObject toJson(PackMetadataSection $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.add("description", Component.Serializer.toJsonTree($$0.getDescription()));
        $$1.addProperty("pack_format", (Number)Integer.valueOf((int)$$0.getPackFormat()));
        return $$1;
    }

    @Override
    public String getMetadataSectionName() {
        return "pack";
    }
}