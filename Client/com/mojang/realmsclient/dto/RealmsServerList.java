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
 *  java.util.Iterator
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
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<RealmsServer> servers;

    public static RealmsServerList parse(String $$0) {
        RealmsServerList $$1 = new RealmsServerList();
        $$1.servers = Lists.newArrayList();
        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse($$0).getAsJsonObject();
            if ($$3.get("servers").isJsonArray()) {
                JsonArray $$4 = $$3.get("servers").getAsJsonArray();
                Iterator $$5 = $$4.iterator();
                while ($$5.hasNext()) {
                    $$1.servers.add((Object)RealmsServer.parse(((JsonElement)$$5.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception $$6) {
            LOGGER.error("Could not parse McoServerList: {}", (Object)$$6.getMessage());
        }
        return $$1;
    }
}