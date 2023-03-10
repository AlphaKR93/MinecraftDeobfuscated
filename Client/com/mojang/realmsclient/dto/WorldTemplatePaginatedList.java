/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class WorldTemplatePaginatedList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<WorldTemplate> templates;
    public int page;
    public int size;
    public int total;

    public WorldTemplatePaginatedList() {
    }

    public WorldTemplatePaginatedList(int $$0) {
        this.templates = Collections.emptyList();
        this.page = 0;
        this.size = $$0;
        this.total = -1;
    }

    public boolean isLastPage() {
        return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
    }

    public static WorldTemplatePaginatedList parse(String $$0) {
        WorldTemplatePaginatedList $$1 = new WorldTemplatePaginatedList();
        $$1.templates = Lists.newArrayList();
        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse($$0).getAsJsonObject();
            if ($$3.get("templates").isJsonArray()) {
                Iterator $$4 = $$3.get("templates").getAsJsonArray().iterator();
                while ($$4.hasNext()) {
                    $$1.templates.add((Object)WorldTemplate.parse(((JsonElement)$$4.next()).getAsJsonObject()));
                }
            }
            $$1.page = JsonUtils.getIntOr("page", $$3, 0);
            $$1.size = JsonUtils.getIntOr("size", $$3, 0);
            $$1.total = JsonUtils.getIntOr("total", $$3, 0);
        }
        catch (Exception $$5) {
            LOGGER.error("Could not parse WorldTemplatePaginatedList: {}", (Object)$$5.getMessage());
        }
        return $$1;
    }
}