/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Exception
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;

public class ServerActivity
extends ValueObject {
    public String profileUuid;
    public long joinTime;
    public long leaveTime;

    public static ServerActivity parse(JsonObject $$0) {
        ServerActivity $$1 = new ServerActivity();
        try {
            $$1.profileUuid = JsonUtils.getStringOr("profileUuid", $$0, null);
            $$1.joinTime = JsonUtils.getLongOr("joinTime", $$0, Long.MIN_VALUE);
            $$1.leaveTime = JsonUtils.getLongOr("leaveTime", $$0, Long.MIN_VALUE);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}