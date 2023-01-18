/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  it.unimi.dsi.fastutil.ints.Int2FloatMap
 *  it.unimi.dsi.fastutil.ints.Int2FloatMaps
 *  it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.font;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public class SpaceProvider
implements GlyphProvider {
    private final Int2ObjectMap<GlyphInfo.SpaceGlyphInfo> glyphs;

    public SpaceProvider(Int2FloatMap $$02) {
        this.glyphs = new Int2ObjectOpenHashMap($$02.size());
        Int2FloatMaps.fastForEach((Int2FloatMap)$$02, $$0 -> {
            float $$1 = $$0.getFloatValue();
            this.glyphs.put($$0.getIntKey(), () -> $$1);
        });
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        return (GlyphInfo)this.glyphs.get($$0);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.keySet());
    }

    public static GlyphProviderBuilder builderFromJson(JsonObject $$0) {
        Int2FloatOpenHashMap $$1 = new Int2FloatOpenHashMap();
        JsonObject $$2 = GsonHelper.getAsJsonObject($$0, "advances");
        for (Map.Entry $$3 : $$2.entrySet()) {
            int[] $$4 = ((String)$$3.getKey()).codePoints().toArray();
            if ($$4.length != 1) {
                throw new JsonParseException("Expected single codepoint, got " + Arrays.toString((int[])$$4));
            }
            float $$5 = GsonHelper.convertToFloat((JsonElement)$$3.getValue(), "advance");
            $$1.put($$4[0], $$5);
        }
        return arg_0 -> SpaceProvider.lambda$builderFromJson$2((Int2FloatMap)$$1, arg_0);
    }

    private static /* synthetic */ GlyphProvider lambda$builderFromJson$2(Int2FloatMap $$0, ResourceManager $$1) {
        return new SpaceProvider($$0);
    }
}