/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  java.util.function.Function
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProvider
implements GlyphProvider {
    private final ByteBuffer fontMemory;
    final STBTTFontinfo font;
    final float oversample;
    private final IntSet skip = new IntArraySet();
    final float shiftX;
    final float shiftY;
    final float pointScale;
    final float ascent;

    public TrueTypeGlyphProvider(ByteBuffer $$0, STBTTFontinfo $$1, float $$2, float $$3, float $$4, float $$5, String $$6) {
        this.fontMemory = $$0;
        this.font = $$1;
        this.oversample = $$3;
        $$6.codePoints().forEach(arg_0 -> ((IntSet)this.skip).add(arg_0));
        this.shiftX = $$4 * $$3;
        this.shiftY = $$5 * $$3;
        this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight((STBTTFontinfo)$$1, (float)($$2 * $$3));
        try (MemoryStack $$7 = MemoryStack.stackPush();){
            IntBuffer $$8 = $$7.mallocInt(1);
            IntBuffer $$9 = $$7.mallocInt(1);
            IntBuffer $$10 = $$7.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics((STBTTFontinfo)$$1, (IntBuffer)$$8, (IntBuffer)$$9, (IntBuffer)$$10);
            this.ascent = (float)$$8.get(0) * this.pointScale;
        }
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        if (this.skip.contains($$0)) {
            return null;
        }
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            int $$2 = STBTruetype.stbtt_FindGlyphIndex((STBTTFontinfo)this.font, (int)$$0);
            if ($$2 == 0) {
                GlyphInfo glyphInfo = null;
                return glyphInfo;
            }
            IntBuffer $$3 = $$1.mallocInt(1);
            IntBuffer $$4 = $$1.mallocInt(1);
            IntBuffer $$5 = $$1.mallocInt(1);
            IntBuffer $$6 = $$1.mallocInt(1);
            IntBuffer $$7 = $$1.mallocInt(1);
            IntBuffer $$8 = $$1.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics((STBTTFontinfo)this.font, (int)$$2, (IntBuffer)$$7, (IntBuffer)$$8);
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel((STBTTFontinfo)this.font, (int)$$2, (float)this.pointScale, (float)this.pointScale, (float)this.shiftX, (float)this.shiftY, (IntBuffer)$$3, (IntBuffer)$$4, (IntBuffer)$$5, (IntBuffer)$$6);
            float $$9 = (float)$$7.get(0) * this.pointScale;
            int $$10 = $$5.get(0) - $$3.get(0);
            int $$11 = $$6.get(0) - $$4.get(0);
            if ($$10 <= 0 || $$11 <= 0) {
                GlyphInfo.SpaceGlyphInfo spaceGlyphInfo = () -> $$9 / this.oversample;
                return spaceGlyphInfo;
            }
            Glyph glyph = new Glyph($$3.get(0), $$5.get(0), -$$4.get(0), -$$6.get(0), $$9, (float)$$8.get(0) * this.pointScale, $$2);
            return glyph;
        }
    }

    @Override
    public void close() {
        this.font.free();
        MemoryUtil.memFree((Buffer)this.fontMemory);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return (IntSet)IntStream.range((int)0, (int)65535).filter($$0 -> !this.skip.contains($$0)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    class Glyph
    implements GlyphInfo {
        final int width;
        final int height;
        final float bearingX;
        final float bearingY;
        private final float advance;
        final int index;

        Glyph(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5, int $$6) {
            this.width = $$1 - $$0;
            this.height = $$2 - $$3;
            this.advance = $$4 / TrueTypeGlyphProvider.this.oversample;
            this.bearingX = ($$5 + (float)$$0 + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
            this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)$$2 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
            this.index = $$6;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return (BakedGlyph)$$0.apply((Object)new SheetGlyphInfo(){

                @Override
                public int getPixelWidth() {
                    return Glyph.this.width;
                }

                @Override
                public int getPixelHeight() {
                    return Glyph.this.height;
                }

                @Override
                public float getOversample() {
                    return TrueTypeGlyphProvider.this.oversample;
                }

                @Override
                public float getBearingX() {
                    return Glyph.this.bearingX;
                }

                @Override
                public float getBearingY() {
                    return Glyph.this.bearingY;
                }

                @Override
                public void upload(int $$0, int $$1) {
                    NativeImage $$2 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
                    $$2.copyFromFont(TrueTypeGlyphProvider.this.font, Glyph.this.index, Glyph.this.width, Glyph.this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
                    $$2.upload(0, $$0, $$1, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
                }

                @Override
                public boolean isColored() {
                    return false;
                }
            });
        }
    }
}