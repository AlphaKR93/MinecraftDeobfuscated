/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RealmsError {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String errorMessage;
    private final int errorCode;

    private RealmsError(String $$0, int $$1) {
        this.errorMessage = $$0;
        this.errorCode = $$1;
    }

    @Nullable
    public static RealmsError parse(String $$0) {
        if (Strings.isNullOrEmpty((String)$$0)) {
            return null;
        }
        try {
            JsonObject $$1 = JsonParser.parseString((String)$$0).getAsJsonObject();
            String $$2 = JsonUtils.getStringOr("errorMsg", $$1, "");
            int $$3 = JsonUtils.getIntOr("errorCode", $$1, -1);
            return new RealmsError($$2, $$3);
        }
        catch (Exception $$4) {
            LOGGER.error("Could not parse RealmsError: {}", (Object)$$4.getMessage());
            LOGGER.error("The error was: {}", (Object)$$0);
            return null;
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}