/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.time.Instant
 *  java.time.ZonedDateTime
 *  java.util.Date
 *  java.util.UUID
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion
implements WorldVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WorldVersion BUILT_IN = new DetectedVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final DataVersion worldVersion;
    private final int protocolVersion;
    private final int resourcePackVersion;
    private final int dataPackVersion;
    private final Date buildTime;

    private DetectedVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "23w03a";
        this.stable = false;
        this.worldVersion = new DataVersion(3320, "main");
        this.protocolVersion = SharedConstants.getProtocolVersion();
        this.resourcePackVersion = 12;
        this.dataPackVersion = 11;
        this.buildTime = new Date();
    }

    private DetectedVersion(JsonObject $$0) {
        this.id = GsonHelper.getAsString($$0, "id");
        this.name = GsonHelper.getAsString($$0, "name");
        this.stable = GsonHelper.getAsBoolean($$0, "stable");
        this.worldVersion = new DataVersion(GsonHelper.getAsInt($$0, "world_version"), GsonHelper.getAsString($$0, "series_id", DataVersion.MAIN_SERIES));
        this.protocolVersion = GsonHelper.getAsInt($$0, "protocol_version");
        JsonObject $$1 = GsonHelper.getAsJsonObject($$0, "pack_version");
        this.resourcePackVersion = GsonHelper.getAsInt($$1, "resource");
        this.dataPackVersion = GsonHelper.getAsInt($$1, "data");
        this.buildTime = Date.from((Instant)ZonedDateTime.parse((CharSequence)GsonHelper.getAsString($$0, "build_time")).toInstant());
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static WorldVersion tryDetectVersion() {
        try (InputStream $$0 = DetectedVersion.class.getResourceAsStream("/version.json");){
            DetectedVersion detectedVersion;
            if ($$0 == null) {
                LOGGER.warn("Missing version information!");
                WorldVersion worldVersion = BUILT_IN;
                return worldVersion;
            }
            try (InputStreamReader $$1 = new InputStreamReader($$0);){
                detectedVersion = new DetectedVersion(GsonHelper.parse((Reader)$$1));
            }
            return detectedVersion;
        }
        catch (JsonParseException | IOException $$2) {
            throw new IllegalStateException("Game version information is corrupt", $$2);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataVersion getDataVersion() {
        return this.worldVersion;
    }

    @Override
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public int getPackVersion(PackType $$0) {
        return $$0 == PackType.SERVER_DATA ? this.dataPackVersion : this.resourcePackVersion;
    }

    @Override
    public Date getBuildTime() {
        return this.buildTime;
    }

    @Override
    public boolean isStable() {
        return this.stable;
    }
}