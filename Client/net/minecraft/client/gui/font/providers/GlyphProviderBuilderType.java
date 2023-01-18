/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.SpaceProvider;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.client.gui.font.providers.LegacyUnicodeBitmapsProvider;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderBuilder;

public enum GlyphProviderBuilderType {
    BITMAP("bitmap", (Function<JsonObject, GlyphProviderBuilder>)((Function)BitmapProvider.Builder::fromJson)),
    TTF("ttf", (Function<JsonObject, GlyphProviderBuilder>)((Function)TrueTypeGlyphProviderBuilder::fromJson)),
    SPACE("space", (Function<JsonObject, GlyphProviderBuilder>)((Function)SpaceProvider::builderFromJson)),
    LEGACY_UNICODE("legacy_unicode", (Function<JsonObject, GlyphProviderBuilder>)((Function)LegacyUnicodeBitmapsProvider.Builder::fromJson));

    private static final Map<String, GlyphProviderBuilderType> BY_NAME;
    private final String name;
    private final Function<JsonObject, GlyphProviderBuilder> factory;

    private GlyphProviderBuilderType(String $$0, Function<JsonObject, GlyphProviderBuilder> $$1) {
        this.name = $$0;
        this.factory = $$1;
    }

    public static GlyphProviderBuilderType byName(String $$0) {
        GlyphProviderBuilderType $$1 = (GlyphProviderBuilderType)((Object)BY_NAME.get((Object)$$0));
        if ($$1 == null) {
            throw new IllegalArgumentException("Invalid type: " + $$0);
        }
        return $$1;
    }

    public GlyphProviderBuilder create(JsonObject $$0) {
        return (GlyphProviderBuilder)this.factory.apply((Object)$$0);
    }

    static {
        BY_NAME = (Map)Util.make(Maps.newHashMap(), $$0 -> {
            for (GlyphProviderBuilderType $$1 : GlyphProviderBuilderType.values()) {
                $$0.put((Object)$$1.name, (Object)$$1);
            }
        });
    }
}