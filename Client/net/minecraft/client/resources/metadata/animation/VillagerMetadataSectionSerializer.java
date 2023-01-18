/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class VillagerMetadataSectionSerializer
implements MetadataSectionSerializer<VillagerMetaDataSection> {
    @Override
    public VillagerMetaDataSection fromJson(JsonObject $$0) {
        return new VillagerMetaDataSection(VillagerMetaDataSection.Hat.getByName(GsonHelper.getAsString($$0, "hat", "none")));
    }

    @Override
    public String getMetadataSectionName() {
        return "villager";
    }
}