/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class PendingInvitesList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<PendingInvite> pendingInvites = Lists.newArrayList();

    public static PendingInvitesList parse(String $$0) {
        PendingInvitesList $$1 = new PendingInvitesList();
        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse($$0).getAsJsonObject();
            if ($$3.get("invites").isJsonArray()) {
                Iterator $$4 = $$3.get("invites").getAsJsonArray().iterator();
                while ($$4.hasNext()) {
                    $$1.pendingInvites.add((Object)PendingInvite.parse(((JsonElement)$$4.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception $$5) {
            LOGGER.error("Could not parse PendingInvitesList: {}", (Object)$$5.getMessage());
        }
        return $$1;
    }
}