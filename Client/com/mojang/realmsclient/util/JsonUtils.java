/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 */
package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;

public class JsonUtils {
    public static String getStringOr(String $$0, JsonObject $$1, String $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsString();
        }
        return $$2;
    }

    public static int getIntOr(String $$0, JsonObject $$1, int $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsInt();
        }
        return $$2;
    }

    public static long getLongOr(String $$0, JsonObject $$1, long $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsLong();
        }
        return $$2;
    }

    public static boolean getBooleanOr(String $$0, JsonObject $$1, boolean $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsBoolean();
        }
        return $$2;
    }

    public static Date getDateOr(String $$0, JsonObject $$1) {
        JsonElement $$2 = $$1.get($$0);
        if ($$2 != null) {
            return new Date(Long.parseLong((String)$$2.getAsString()));
        }
        return new Date();
    }
}