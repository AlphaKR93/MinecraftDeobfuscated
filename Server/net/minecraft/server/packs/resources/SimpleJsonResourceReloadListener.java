/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener
extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Gson gson;
    private final String directory;

    public SimpleJsonResourceReloadListener(Gson $$0, String $$1) {
        this.gson = $$0;
        this.directory = $$1;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager $$0, ProfilerFiller $$1) {
        HashMap $$2 = Maps.newHashMap();
        FileToIdConverter $$3 = FileToIdConverter.json(this.directory);
        for (Map.Entry $$4 : $$3.listMatchingResources($$0).entrySet()) {
            ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
            ResourceLocation $$6 = $$3.fileToId($$5);
            try {
                BufferedReader $$7 = ((Resource)$$4.getValue()).openAsReader();
                try {
                    JsonElement $$8 = GsonHelper.fromJson(this.gson, (Reader)$$7, JsonElement.class);
                    JsonElement $$9 = (JsonElement)$$2.put((Object)$$6, (Object)$$8);
                    if ($$9 == null) continue;
                    throw new IllegalStateException("Duplicate data file ignored with ID " + $$6);
                }
                finally {
                    if ($$7 == null) continue;
                    $$7.close();
                }
            }
            catch (JsonParseException | IOException | IllegalArgumentException $$10) {
                LOGGER.error("Couldn't parse data file {} from {}", new Object[]{$$6, $$5, $$10});
            }
        }
        return $$2;
    }
}