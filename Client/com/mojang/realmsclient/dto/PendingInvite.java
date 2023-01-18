/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import org.slf4j.Logger;

public class PendingInvite
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String invitationId;
    public String worldName;
    public String worldOwnerName;
    public String worldOwnerUuid;
    public Date date;

    public static PendingInvite parse(JsonObject $$0) {
        PendingInvite $$1 = new PendingInvite();
        try {
            $$1.invitationId = JsonUtils.getStringOr("invitationId", $$0, "");
            $$1.worldName = JsonUtils.getStringOr("worldName", $$0, "");
            $$1.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", $$0, "");
            $$1.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", $$0, "");
            $$1.date = JsonUtils.getDateOr("date", $$0);
        }
        catch (Exception $$2) {
            LOGGER.error("Could not parse PendingInvite: {}", (Object)$$2.getMessage());
        }
        return $$1;
    }
}