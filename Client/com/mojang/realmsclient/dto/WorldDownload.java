/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.slf4j.Logger;

public class WorldDownload
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String downloadLink;
    public String resourcePackUrl;
    public String resourcePackHash;

    public static WorldDownload parse(String $$0) {
        JsonParser $$1 = new JsonParser();
        JsonObject $$2 = $$1.parse($$0).getAsJsonObject();
        WorldDownload $$3 = new WorldDownload();
        try {
            $$3.downloadLink = JsonUtils.getStringOr("downloadLink", $$2, "");
            $$3.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", $$2, "");
            $$3.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", $$2, "");
        }
        catch (Exception $$4) {
            LOGGER.error("Could not parse WorldDownload: {}", (Object)$$4.getMessage());
        }
        return $$3;
    }
}