/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class WorldTemplate
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type = WorldTemplateType.WORLD_TEMPLATE;

    public static WorldTemplate parse(JsonObject $$0) {
        WorldTemplate $$1 = new WorldTemplate();
        try {
            $$1.id = JsonUtils.getStringOr("id", $$0, "");
            $$1.name = JsonUtils.getStringOr("name", $$0, "");
            $$1.version = JsonUtils.getStringOr("version", $$0, "");
            $$1.author = JsonUtils.getStringOr("author", $$0, "");
            $$1.link = JsonUtils.getStringOr("link", $$0, "");
            $$1.image = JsonUtils.getStringOr("image", $$0, null);
            $$1.trailer = JsonUtils.getStringOr("trailer", $$0, "");
            $$1.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", $$0, "");
            $$1.type = WorldTemplateType.valueOf(JsonUtils.getStringOr("type", $$0, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception $$2) {
            LOGGER.error("Could not parse WorldTemplate: {}", (Object)$$2.getMessage());
        }
        return $$1;
    }

    public static enum WorldTemplateType {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }
}