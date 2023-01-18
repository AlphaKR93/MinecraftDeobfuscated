/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class UploadInfo
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DEFAULT_SCHEMA = "http://";
    private static final int DEFAULT_PORT = 8080;
    private static final Pattern URI_SCHEMA_PATTERN = Pattern.compile((String)"^[a-zA-Z][-a-zA-Z0-9+.]+:");
    private final boolean worldClosed;
    @Nullable
    private final String token;
    private final URI uploadEndpoint;

    private UploadInfo(boolean $$0, @Nullable String $$1, URI $$2) {
        this.worldClosed = $$0;
        this.token = $$1;
        this.uploadEndpoint = $$2;
    }

    @Nullable
    public static UploadInfo parse(String $$0) {
        try {
            int $$4;
            URI $$5;
            JsonParser $$1 = new JsonParser();
            JsonObject $$2 = $$1.parse($$0).getAsJsonObject();
            String $$3 = JsonUtils.getStringOr("uploadEndpoint", $$2, null);
            if ($$3 != null && ($$5 = UploadInfo.assembleUri($$3, $$4 = JsonUtils.getIntOr("port", $$2, -1))) != null) {
                boolean $$6 = JsonUtils.getBooleanOr("worldClosed", $$2, false);
                String $$7 = JsonUtils.getStringOr("token", $$2, null);
                return new UploadInfo($$6, $$7, $$5);
            }
        }
        catch (Exception $$8) {
            LOGGER.error("Could not parse UploadInfo: {}", (Object)$$8.getMessage());
        }
        return null;
    }

    @Nullable
    @VisibleForTesting
    public static URI assembleUri(String $$0, int $$1) {
        Matcher $$2 = URI_SCHEMA_PATTERN.matcher((CharSequence)$$0);
        String $$3 = UploadInfo.ensureEndpointSchema($$0, $$2);
        try {
            URI $$4 = new URI($$3);
            int $$5 = UploadInfo.selectPortOrDefault($$1, $$4.getPort());
            if ($$5 != $$4.getPort()) {
                return new URI($$4.getScheme(), $$4.getUserInfo(), $$4.getHost(), $$5, $$4.getPath(), $$4.getQuery(), $$4.getFragment());
            }
            return $$4;
        }
        catch (URISyntaxException $$6) {
            LOGGER.warn("Failed to parse URI {}", (Object)$$3, (Object)$$6);
            return null;
        }
    }

    private static int selectPortOrDefault(int $$0, int $$1) {
        if ($$0 != -1) {
            return $$0;
        }
        if ($$1 != -1) {
            return $$1;
        }
        return 8080;
    }

    private static String ensureEndpointSchema(String $$0, Matcher $$1) {
        if ($$1.find()) {
            return $$0;
        }
        return DEFAULT_SCHEMA + $$0;
    }

    public static String createRequest(@Nullable String $$0) {
        JsonObject $$1 = new JsonObject();
        if ($$0 != null) {
            $$1.addProperty("token", $$0);
        }
        return $$1.toString();
    }

    @Nullable
    public String getToken() {
        return this.token;
    }

    public URI getUploadEndpoint() {
        return this.uploadEndpoint;
    }

    public boolean isWorldClosed() {
        return this.worldClosed;
    }
}