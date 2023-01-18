/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class RealmsPersistence {
    private static final String FILE_NAME = "realms_persistence.json";
    private static final GuardedSerializer GSON = new GuardedSerializer();
    private static final Logger LOGGER = LogUtils.getLogger();

    public RealmsPersistenceData read() {
        return RealmsPersistence.readFile();
    }

    public void save(RealmsPersistenceData $$0) {
        RealmsPersistence.writeFile($$0);
    }

    public static RealmsPersistenceData readFile() {
        File $$0 = RealmsPersistence.getPathToData();
        try {
            String $$1 = FileUtils.readFileToString((File)$$0, (Charset)StandardCharsets.UTF_8);
            RealmsPersistenceData $$2 = GSON.fromJson($$1, RealmsPersistenceData.class);
            if ($$2 != null) {
                return $$2;
            }
        }
        catch (FileNotFoundException $$1) {
        }
        catch (Exception $$3) {
            LOGGER.warn("Failed to read Realms storage {}", (Object)$$0, (Object)$$3);
        }
        return new RealmsPersistenceData();
    }

    public static void writeFile(RealmsPersistenceData $$0) {
        File $$1 = RealmsPersistence.getPathToData();
        try {
            FileUtils.writeStringToFile((File)$$1, (String)GSON.toJson($$0), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static File getPathToData() {
        return new File(Minecraft.getInstance().gameDirectory, FILE_NAME);
    }

    public static class RealmsPersistenceData
    implements ReflectionBasedSerialization {
        @SerializedName(value="newsLink")
        public String newsLink;
        @SerializedName(value="hasUnreadNews")
        public boolean hasUnreadNews;
    }
}