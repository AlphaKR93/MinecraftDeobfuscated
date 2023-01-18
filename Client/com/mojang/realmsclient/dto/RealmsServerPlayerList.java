/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerPlayerList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final JsonParser JSON_PARSER = new JsonParser();
    public long serverId;
    public List<String> players;

    public static RealmsServerPlayerList parse(JsonObject $$0) {
        RealmsServerPlayerList $$1 = new RealmsServerPlayerList();
        try {
            JsonElement $$3;
            $$1.serverId = JsonUtils.getLongOr("serverId", $$0, -1L);
            String $$2 = JsonUtils.getStringOr("playerList", $$0, null);
            $$1.players = $$2 != null ? (($$3 = JSON_PARSER.parse($$2)).isJsonArray() ? RealmsServerPlayerList.parsePlayers($$3.getAsJsonArray()) : Lists.newArrayList()) : Lists.newArrayList();
        }
        catch (Exception $$4) {
            LOGGER.error("Could not parse RealmsServerPlayerList: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }

    private static List<String> parsePlayers(JsonArray $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (JsonElement $$2 : $$0) {
            try {
                $$1.add((Object)$$2.getAsString());
            }
            catch (Exception exception) {}
        }
        return $$1;
    }
}