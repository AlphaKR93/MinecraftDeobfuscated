/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.AllMissingGlyphProvider;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class FontManager
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_PATH = "fonts.json";
    public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
    static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
    private final FontSet missingFontSet;
    final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
    final TextureManager textureManager;
    private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
    private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager $$02, ProfilerFiller $$12) {
            $$12.startTick();
            Gson $$2 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            HashMap $$3 = Maps.newHashMap();
            for (Map.Entry $$4 : FONT_DEFINITIONS.listMatchingResourceStacks($$02).entrySet()) {
                ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
                ResourceLocation $$6 = FONT_DEFINITIONS.fileToId($$5);
                List $$7 = (List)$$3.computeIfAbsent((Object)$$6, $$0 -> Lists.newArrayList((Object[])new GlyphProvider[]{new AllMissingGlyphProvider()}));
                $$12.push((Supplier<String>)((Supplier)$$6::toString));
                for (Resource $$8 : (List)$$4.getValue()) {
                    $$12.push($$8.sourcePackId());
                    try (BufferedReader $$9 = $$8.openAsReader();){
                        try {
                            $$12.push("reading");
                            JsonArray $$10 = GsonHelper.getAsJsonArray(GsonHelper.fromJson($$2, (Reader)$$9, JsonObject.class), "providers");
                            $$12.popPush("parsing");
                            for (int $$11 = $$10.size() - 1; $$11 >= 0; --$$11) {
                                JsonObject $$122 = GsonHelper.convertToJsonObject($$10.get($$11), "providers[" + $$11 + "]");
                                String $$13 = GsonHelper.getAsString($$122, "type");
                                GlyphProviderBuilderType $$14 = GlyphProviderBuilderType.byName($$13);
                                try {
                                    $$12.push($$13);
                                    GlyphProvider $$15 = $$14.create($$122).create($$02);
                                    if ($$15 == null) continue;
                                    $$7.add((Object)$$15);
                                    continue;
                                }
                                finally {
                                    $$12.pop();
                                }
                            }
                        }
                        finally {
                            $$12.pop();
                        }
                    }
                    catch (Exception $$16) {
                        LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{$$6, FontManager.FONTS_PATH, $$8.sourcePackId(), $$16});
                    }
                    $$12.pop();
                }
                $$12.push("caching");
                IntOpenHashSet $$17 = new IntOpenHashSet();
                for (GlyphProvider $$18 : $$7) {
                    $$17.addAll((IntCollection)$$18.getSupportedGlyphs());
                }
                $$17.forEach($$1 -> {
                    GlyphProvider $$2;
                    if ($$1 == 32) {
                        return;
                    }
                    Iterator iterator = Lists.reverse((List)$$7).iterator();
                    while (iterator.hasNext() && ($$2 = (GlyphProvider)iterator.next()).getGlyph($$1) == null) {
                    }
                });
                $$12.pop();
                $$12.pop();
            }
            $$12.endTick();
            return $$3;
        }

        @Override
        protected void apply(Map<ResourceLocation, List<GlyphProvider>> $$02, ResourceManager $$12, ProfilerFiller $$2) {
            $$2.startTick();
            $$2.push("closing");
            FontManager.this.fontSets.values().forEach(FontSet::close);
            FontManager.this.fontSets.clear();
            $$2.popPush("reloading");
            $$02.forEach(($$0, $$1) -> {
                FontSet $$2 = new FontSet(FontManager.this.textureManager, (ResourceLocation)$$0);
                $$2.reload((List<GlyphProvider>)Lists.reverse((List)$$1));
                FontManager.this.fontSets.put($$0, (Object)$$2);
            });
            $$2.pop();
            $$2.endTick();
        }

        @Override
        public String getName() {
            return "FontManager";
        }
    };

    public FontManager(TextureManager $$02) {
        this.textureManager = $$02;
        this.missingFontSet = Util.make(new FontSet($$02, MISSING_FONT), $$0 -> $$0.reload((List<GlyphProvider>)Lists.newArrayList((Object[])new GlyphProvider[]{new AllMissingGlyphProvider()})));
    }

    public void setRenames(Map<ResourceLocation, ResourceLocation> $$0) {
        this.renames = $$0;
    }

    public Font createFont() {
        return new Font((Function<ResourceLocation, FontSet>)((Function)$$0 -> (FontSet)this.fontSets.getOrDefault(this.renames.getOrDefault($$0, $$0), (Object)this.missingFontSet)), false);
    }

    public Font createFontFilterFishy() {
        return new Font((Function<ResourceLocation, FontSet>)((Function)$$0 -> (FontSet)this.fontSets.getOrDefault(this.renames.getOrDefault($$0, $$0), (Object)this.missingFontSet)), true);
    }

    public PreparableReloadListener getReloadListener() {
        return this.reloadListener;
    }

    public void close() {
        this.fontSets.values().forEach(FontSet::close);
        this.missingFontSet.close();
    }
}