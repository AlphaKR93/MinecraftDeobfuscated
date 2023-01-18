/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class BitmapProvider
implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private final NativeImage image;
    private final Int2ObjectMap<Glyph> glyphs;

    BitmapProvider(NativeImage $$0, Int2ObjectMap<Glyph> $$1) {
        this.image = $$0;
        this.glyphs = $$1;
    }

    @Override
    public void close() {
        this.image.close();
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

    record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo
    {
        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return (BakedGlyph)$$0.apply((Object)new SheetGlyphInfo(){

                @Override
                public float getOversample() {
                    return 1.0f / scale;
                }

                @Override
                public int getPixelWidth() {
                    return width;
                }

                @Override
                public int getPixelHeight() {
                    return height;
                }

                @Override
                public float getBearingY() {
                    return SheetGlyphInfo.super.getBearingY() + 7.0f - (float)ascent;
                }

                @Override
                public void upload(int $$0, int $$1) {
                    image.upload(0, $$0, $$1, offsetX, offsetY, width, height, false, false);
                }

                @Override
                public boolean isColored() {
                    return image.format().components() > 1;
                }
            });
        }
    }

    public static class Builder
    implements GlyphProviderBuilder {
        private final ResourceLocation texture;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Builder(ResourceLocation $$0, int $$1, int $$2, List<int[]> $$3) {
            this.texture = $$0.withPrefix("textures/");
            this.chars = $$3;
            this.height = $$1;
            this.ascent = $$2;
        }

        public static Builder fromJson(JsonObject $$0) {
            int $$1 = GsonHelper.getAsInt($$0, "height", 8);
            int $$2 = GsonHelper.getAsInt($$0, "ascent");
            if ($$2 > $$1) {
                throw new JsonParseException("Ascent " + $$2 + " higher than height " + $$1);
            }
            ArrayList $$3 = Lists.newArrayList();
            JsonArray $$4 = GsonHelper.getAsJsonArray($$0, "chars");
            for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                int $$8;
                String $$6 = GsonHelper.convertToString($$4.get($$5), "chars[" + $$5 + "]");
                int[] $$7 = $$6.codePoints().toArray();
                if ($$5 > 0 && $$7.length != ($$8 = ((int[])$$3.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + $$7.length + ", expected: " + $$8 + "), pad with space or \\u0000");
                }
                $$3.add((Object)$$7);
            }
            if ($$3.isEmpty() || ((int[])$$3.get(0)).length == 0) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Builder(new ResourceLocation(GsonHelper.getAsString($$0, "file")), $$1, $$2, (List<int[]>)$$3);
        }

        @Override
        @Nullable
        public GlyphProvider create(ResourceManager $$0) {
            BitmapProvider bitmapProvider;
            block10: {
                InputStream $$1 = $$0.open(this.texture);
                try {
                    NativeImage $$2 = NativeImage.read(NativeImage.Format.RGBA, $$1);
                    int $$3 = $$2.getWidth();
                    int $$4 = $$2.getHeight();
                    int $$5 = $$3 / ((int[])this.chars.get(0)).length;
                    int $$6 = $$4 / this.chars.size();
                    float $$7 = (float)this.height / (float)$$6;
                    Int2ObjectOpenHashMap $$8 = new Int2ObjectOpenHashMap();
                    for (int $$9 = 0; $$9 < this.chars.size(); ++$$9) {
                        int $$10 = 0;
                        for (int $$11 : (int[])this.chars.get($$9)) {
                            int $$13;
                            Glyph $$14;
                            int $$12 = $$10++;
                            if ($$11 == 0 || ($$14 = (Glyph)$$8.put($$11, (Object)new Glyph($$7, $$2, $$12 * $$5, $$9 * $$6, $$5, $$6, (int)(0.5 + (double)((float)($$13 = this.getActualGlyphWidth($$2, $$5, $$6, $$12, $$9)) * $$7)) + 1, this.ascent))) == null) continue;
                            LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString((int)$$11), (Object)this.texture);
                        }
                    }
                    bitmapProvider = new BitmapProvider($$2, (Int2ObjectMap<Glyph>)$$8);
                    if ($$1 == null) break block10;
                }
                catch (Throwable throwable) {
                    try {
                        if ($$1 != null) {
                            try {
                                $$1.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException $$15) {
                        throw new RuntimeException($$15.getMessage());
                    }
                }
                $$1.close();
            }
            return bitmapProvider;
        }

        private int getActualGlyphWidth(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
            int $$5;
            for ($$5 = $$1 - 1; $$5 >= 0; --$$5) {
                int $$6 = $$3 * $$1 + $$5;
                for (int $$7 = 0; $$7 < $$2; ++$$7) {
                    int $$8 = $$4 * $$2 + $$7;
                    if ($$0.getLuminanceOrAlpha($$6, $$8) == 0) continue;
                    return $$5 + 1;
                }
            }
            return $$5 + 1;
        }
    }
}