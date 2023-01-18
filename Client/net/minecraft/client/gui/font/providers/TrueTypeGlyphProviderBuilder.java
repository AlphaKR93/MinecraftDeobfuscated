/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  javax.annotation.Nullable
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class TrueTypeGlyphProviderBuilder
implements GlyphProviderBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation location;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String skip;

    public TrueTypeGlyphProviderBuilder(ResourceLocation $$0, float $$1, float $$2, float $$3, float $$4, String $$5) {
        this.location = $$0;
        this.size = $$1;
        this.oversample = $$2;
        this.shiftX = $$3;
        this.shiftY = $$4;
        this.skip = $$5;
    }

    public static GlyphProviderBuilder fromJson(JsonObject $$0) {
        float $$1 = 0.0f;
        float $$2 = 0.0f;
        if ($$0.has("shift")) {
            JsonArray $$3 = $$0.getAsJsonArray("shift");
            if ($$3.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + $$3.size());
            }
            $$1 = GsonHelper.convertToFloat($$3.get(0), "shift[0]");
            $$2 = GsonHelper.convertToFloat($$3.get(1), "shift[1]");
        }
        StringBuilder $$4 = new StringBuilder();
        if ($$0.has("skip")) {
            JsonElement $$5 = $$0.get("skip");
            if ($$5.isJsonArray()) {
                JsonArray $$6 = GsonHelper.convertToJsonArray($$5, "skip");
                for (int $$7 = 0; $$7 < $$6.size(); ++$$7) {
                    $$4.append(GsonHelper.convertToString($$6.get($$7), "skip[" + $$7 + "]"));
                }
            } else {
                $$4.append(GsonHelper.convertToString($$5, "skip"));
            }
        }
        return new TrueTypeGlyphProviderBuilder(new ResourceLocation(GsonHelper.getAsString($$0, "file")), GsonHelper.getAsFloat($$0, "size", 11.0f), GsonHelper.getAsFloat($$0, "oversample", 1.0f), $$1, $$2, $$4.toString());
    }

    @Override
    @Nullable
    public GlyphProvider create(ResourceManager $$0) {
        TrueTypeGlyphProvider trueTypeGlyphProvider;
        block10: {
            STBTTFontinfo $$1 = null;
            ByteBuffer $$2 = null;
            InputStream $$3 = $$0.open(this.location.withPrefix("font/"));
            try {
                LOGGER.debug("Loading font {}", (Object)this.location);
                $$1 = STBTTFontinfo.malloc();
                $$2 = TextureUtil.readResource($$3);
                $$2.flip();
                LOGGER.debug("Reading font {}", (Object)this.location);
                if (!STBTruetype.stbtt_InitFont((STBTTFontinfo)$$1, (ByteBuffer)$$2)) {
                    throw new IOException("Invalid ttf");
                }
                trueTypeGlyphProvider = new TrueTypeGlyphProvider($$2, $$1, this.size, this.oversample, this.shiftX, this.shiftY, this.skip);
                if ($$3 == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if ($$3 != null) {
                        try {
                            $$3.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception $$4) {
                    LOGGER.error("Couldn't load truetype font {}", (Object)this.location, (Object)$$4);
                    if ($$1 != null) {
                        $$1.free();
                    }
                    MemoryUtil.memFree($$2);
                    return null;
                }
            }
            $$3.close();
        }
        return trueTypeGlyphProvider;
    }
}