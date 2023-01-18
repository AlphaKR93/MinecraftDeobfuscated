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

public class RealmsNews
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String newsLink;

    public static RealmsNews parse(String $$0) {
        RealmsNews $$1 = new RealmsNews();
        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse($$0).getAsJsonObject();
            $$1.newsLink = JsonUtils.getStringOr("newsLink", $$3, null);
        }
        catch (Exception $$4) {
            LOGGER.error("Could not parse RealmsNews: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }
}