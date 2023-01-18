/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
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
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class BackupList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<Backup> backups;

    public static BackupList parse(String $$0) {
        JsonParser $$1 = new JsonParser();
        BackupList $$2 = new BackupList();
        $$2.backups = Lists.newArrayList();
        try {
            JsonElement $$3 = $$1.parse($$0).getAsJsonObject().get("backups");
            if ($$3.isJsonArray()) {
                Iterator $$4 = $$3.getAsJsonArray().iterator();
                while ($$4.hasNext()) {
                    $$2.backups.add((Object)Backup.parse((JsonElement)$$4.next()));
                }
            }
        }
        catch (Exception $$5) {
            LOGGER.error("Could not parse BackupList: {}", (Object)$$5.getMessage());
        }
        return $$2;
    }
}