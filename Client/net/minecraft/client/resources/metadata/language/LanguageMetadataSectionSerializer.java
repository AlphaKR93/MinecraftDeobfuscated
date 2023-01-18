/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Map$Entry
 */
package net.minecraft.client.resources.metadata.language;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class LanguageMetadataSectionSerializer
implements MetadataSectionSerializer<LanguageMetadataSection> {
    private static final int MAX_LANGUAGE_LENGTH = 16;

    @Override
    public LanguageMetadataSection fromJson(JsonObject $$0) {
        HashSet $$1 = Sets.newHashSet();
        for (Map.Entry $$2 : $$0.entrySet()) {
            String $$3 = (String)$$2.getKey();
            if ($$3.length() > 16) {
                throw new JsonParseException("Invalid language->'" + $$3 + "': language code must not be more than 16 characters long");
            }
            JsonObject $$4 = GsonHelper.convertToJsonObject((JsonElement)$$2.getValue(), "language");
            String $$5 = GsonHelper.getAsString($$4, "region");
            String $$6 = GsonHelper.getAsString($$4, "name");
            boolean $$7 = GsonHelper.getAsBoolean($$4, "bidirectional", false);
            if ($$5.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + $$3 + "'->region: empty value");
            }
            if ($$6.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + $$3 + "'->name: empty value");
            }
            if ($$1.add((Object)new LanguageInfo($$3, $$5, $$6, $$7))) continue;
            throw new JsonParseException("Duplicate language->'" + $$3 + "' defined");
        }
        return new LanguageMetadataSection((Collection<LanguageInfo>)$$1);
    }

    @Override
    public String getMetadataSectionName() {
        return "language";
    }
}