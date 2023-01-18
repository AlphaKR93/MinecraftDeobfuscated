/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.List;

public class ServerActivityList
extends ValueObject {
    public long periodInMillis;
    public List<ServerActivity> serverActivities = Lists.newArrayList();

    public static ServerActivityList parse(String $$0) {
        ServerActivityList $$1 = new ServerActivityList();
        JsonParser $$2 = new JsonParser();
        try {
            JsonElement $$3 = $$2.parse($$0);
            JsonObject $$4 = $$3.getAsJsonObject();
            $$1.periodInMillis = JsonUtils.getLongOr("periodInMillis", $$4, -1L);
            JsonElement $$5 = $$4.get("playerActivityDto");
            if ($$5 != null && $$5.isJsonArray()) {
                JsonArray $$6 = $$5.getAsJsonArray();
                for (JsonElement $$7 : $$6) {
                    ServerActivity $$8 = ServerActivity.parse($$7.getAsJsonObject());
                    $$1.serverActivities.add((Object)$$8);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}