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

public class Subscription
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long startDate;
    public int daysLeft;
    public SubscriptionType type = SubscriptionType.NORMAL;

    public static Subscription parse(String $$0) {
        Subscription $$1 = new Subscription();
        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse($$0).getAsJsonObject();
            $$1.startDate = JsonUtils.getLongOr("startDate", $$3, 0L);
            $$1.daysLeft = JsonUtils.getIntOr("daysLeft", $$3, 0);
            $$1.type = Subscription.typeFrom(JsonUtils.getStringOr("subscriptionType", $$3, SubscriptionType.NORMAL.name()));
        }
        catch (Exception $$4) {
            LOGGER.error("Could not parse Subscription: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }

    private static SubscriptionType typeFrom(String $$0) {
        try {
            return SubscriptionType.valueOf($$0);
        }
        catch (Exception $$1) {
            return SubscriptionType.NORMAL;
        }
    }

    public static enum SubscriptionType {
        NORMAL,
        RECURRING;

    }
}