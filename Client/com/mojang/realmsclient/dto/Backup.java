/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Character
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Date
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

public class Backup
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String backupId;
    public Date lastModifiedDate;
    public long size;
    private boolean uploadedVersion;
    public Map<String, String> metadata = Maps.newHashMap();
    public Map<String, String> changeList = Maps.newHashMap();

    public static Backup parse(JsonElement $$0) {
        JsonObject $$1 = $$0.getAsJsonObject();
        Backup $$2 = new Backup();
        try {
            $$2.backupId = JsonUtils.getStringOr("backupId", $$1, "");
            $$2.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", $$1);
            $$2.size = JsonUtils.getLongOr("size", $$1, 0L);
            if ($$1.has("metadata")) {
                JsonObject $$3 = $$1.getAsJsonObject("metadata");
                Set $$4 = $$3.entrySet();
                for (Map.Entry $$5 : $$4) {
                    if (((JsonElement)$$5.getValue()).isJsonNull()) continue;
                    $$2.metadata.put((Object)Backup.format((String)$$5.getKey()), (Object)((JsonElement)$$5.getValue()).getAsString());
                }
            }
        }
        catch (Exception $$6) {
            LOGGER.error("Could not parse Backup: {}", (Object)$$6.getMessage());
        }
        return $$2;
    }

    private static String format(String $$0) {
        String[] $$1 = $$0.split("_");
        StringBuilder $$2 = new StringBuilder();
        for (String $$3 : $$1) {
            if ($$3 == null || $$3.length() < 1) continue;
            if ("of".equals((Object)$$3)) {
                $$2.append($$3).append(" ");
                continue;
            }
            char $$4 = Character.toUpperCase((char)$$3.charAt(0));
            $$2.append($$4).append($$3.substring(1)).append(" ");
        }
        return $$2.toString();
    }

    public boolean isUploadedVersion() {
        return this.uploadedVersion;
    }

    public void setUploadedVersion(boolean $$0) {
        this.uploadedVersion = $$0;
    }
}