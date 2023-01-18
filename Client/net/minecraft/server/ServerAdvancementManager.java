/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.slf4j.Logger;

public class ServerAdvancementManager
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private AdvancementList advancements = new AdvancementList();
    private final PredicateManager predicateManager;

    public ServerAdvancementManager(PredicateManager $$0) {
        super(GSON, "advancements");
        this.predicateManager = $$0;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> $$0, ResourceManager $$1, ProfilerFiller $$2) {
        HashMap $$3 = Maps.newHashMap();
        $$0.forEach((arg_0, arg_1) -> this.lambda$apply$0((Map)$$3, arg_0, arg_1));
        AdvancementList $$4 = new AdvancementList();
        $$4.add((Map<ResourceLocation, Advancement.Builder>)$$3);
        for (Advancement $$5 : $$4.getRoots()) {
            if ($$5.getDisplay() == null) continue;
            TreeNodePosition.run($$5);
        }
        this.advancements = $$4;
    }

    @Nullable
    public Advancement getAdvancement(ResourceLocation $$0) {
        return this.advancements.get($$0);
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.getAllAdvancements();
    }

    private /* synthetic */ void lambda$apply$0(Map $$0, ResourceLocation $$1, JsonElement $$2) {
        try {
            JsonObject $$3 = GsonHelper.convertToJsonObject($$2, "advancement");
            Advancement.Builder $$4 = Advancement.Builder.fromJson($$3, new DeserializationContext($$1, this.predicateManager));
            $$0.put((Object)$$1, (Object)$$4);
        }
        catch (Exception $$5) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", (Object)$$1, (Object)$$5.getMessage());
        }
    }
}