/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.logging.LogUtils
 *  java.io.ByteArrayOutputStream
 *  java.io.File
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.AutoCloseable
 *  java.lang.Deprecated
 *  java.lang.Enum
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.UnsupportedOperationException
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  java.nio.channels.Channels
 *  java.nio.channels.SeekableByteChannel
 *  java.nio.channels.WritableByteChannel
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.nio.file.StandardOpenOption
 *  java.nio.file.attribute.FileAttribute
 *  java.util.Base64
 *  java.util.EnumSet
 *  java.util.Locale
 *  java.util.Set
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public final class NativeImage
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int OFFSET_A = 24;
    private static final int OFFSET_B = 16;
    private static final int OFFSET_G = 8;
    private static final int OFFSET_R = 0;
    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of((Enum)StandardOpenOption.WRITE, (Enum)StandardOpenOption.CREATE, (Enum)StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean useStbFree;
    private long pixels;
    private final long size;

    public NativeImage(int $$0, int $$1, boolean $$2) {
        this(Format.RGBA, $$0, $$1, $$2);
    }

    public NativeImage(Format $$0, int $$1, int $$2, boolean $$3) {
        if ($$1 <= 0 || $$2 <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + $$1 + "x" + $$2);
        }
        this.format = $$0;
        this.width = $$1;
        this.height = $$2;
        this.size = (long)$$1 * (long)$$2 * (long)$$0.components();
        this.useStbFree = false;
        this.pixels = $$3 ? MemoryUtil.nmemCalloc((long)1L, (long)this.size) : MemoryUtil.nmemAlloc((long)this.size);
    }

    private NativeImage(Format $$0, int $$1, int $$2, boolean $$3, long $$4) {
        if ($$1 <= 0 || $$2 <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + $$1 + "x" + $$2);
        }
        this.format = $$0;
        this.width = $$1;
        this.height = $$2;
        this.useStbFree = $$3;
        this.pixels = $$4;
        this.size = (long)$$1 * (long)$$2 * (long)$$0.components();
    }

    public String toString() {
        return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
    }

    private boolean isOutsideBounds(int $$0, int $$1) {
        return $$0 < 0 || $$0 >= this.width || $$1 < 0 || $$1 >= this.height;
    }

    public static NativeImage read(InputStream $$0) throws IOException {
        return NativeImage.read(Format.RGBA, $$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format $$0, InputStream $$1) throws IOException {
        ByteBuffer $$2 = null;
        try {
            $$2 = TextureUtil.readResource($$1);
            $$2.rewind();
            NativeImage nativeImage = NativeImage.read($$0, $$2);
            return nativeImage;
        }
        finally {
            MemoryUtil.memFree((Buffer)$$2);
            IOUtils.closeQuietly((InputStream)$$1);
        }
    }

    public static NativeImage read(ByteBuffer $$0) throws IOException {
        return NativeImage.read(Format.RGBA, $$0);
    }

    public static NativeImage read(@Nullable Format $$0, ByteBuffer $$1) throws IOException {
        if ($$0 != null && !$$0.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to read format " + $$0);
        }
        if (MemoryUtil.memAddress((ByteBuffer)$$1) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack $$2 = MemoryStack.stackPush();){
            IntBuffer $$3 = $$2.mallocInt(1);
            IntBuffer $$4 = $$2.mallocInt(1);
            IntBuffer $$5 = $$2.mallocInt(1);
            ByteBuffer $$6 = STBImage.stbi_load_from_memory((ByteBuffer)$$1, (IntBuffer)$$3, (IntBuffer)$$4, (IntBuffer)$$5, (int)($$0 == null ? 0 : $$0.components));
            if ($$6 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage($$0 == null ? Format.getStbFormat($$5.get(0)) : $$0, $$3.get(0), $$4.get(0), true, MemoryUtil.memAddress((ByteBuffer)$$6));
            return nativeImage;
        }
    }

    private static void setFilter(boolean $$0, boolean $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        if ($$0) {
            GlStateManager._texParameter(3553, 10241, $$1 ? 9987 : 9729);
            GlStateManager._texParameter(3553, 10240, 9729);
        } else {
            GlStateManager._texParameter(3553, 10241, $$1 ? 9986 : 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
        }
    }

    private void checkAllocated() {
        if (this.pixels == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    public void close() {
        if (this.pixels != 0L) {
            if (this.useStbFree) {
                STBImage.nstbi_image_free((long)this.pixels);
            } else {
                MemoryUtil.nmemFree((long)this.pixels);
            }
        }
        this.pixels = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format format() {
        return this.format;
    }

    public int getPixelRGBA(int $$0, int $$1) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"getPixelRGBA only works on RGBA images; have %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        this.checkAllocated();
        long $$2 = ((long)$$0 + (long)$$1 * (long)this.width) * 4L;
        return MemoryUtil.memGetInt((long)(this.pixels + $$2));
    }

    public void setPixelRGBA(int $$0, int $$1, int $$2) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"getPixelRGBA only works on RGBA images; have %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        this.checkAllocated();
        long $$3 = ((long)$$0 + (long)$$1 * (long)this.width) * 4L;
        MemoryUtil.memPutInt((long)(this.pixels + $$3), (int)$$2);
    }

    public void setPixelLuminance(int $$0, int $$1, byte $$2) {
        RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminance()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"setPixelLuminance only works on image with luminance; have %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        this.checkAllocated();
        long $$3 = ((long)$$0 + (long)$$1 * (long)this.width) * (long)this.format.components() + (long)(this.format.luminanceOffset() / 8);
        MemoryUtil.memPutByte((long)(this.pixels + $$3), (byte)$$2);
    }

    public byte getRedOrLuminance(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrRed()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"no red or luminance in %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        int $$2 = ($$0 + $$1 * this.width) * this.format.components() + this.format.luminanceOrRedOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)$$2));
    }

    public byte getGreenOrLuminance(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrGreen()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"no green or luminance in %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        int $$2 = ($$0 + $$1 * this.width) * this.format.components() + this.format.luminanceOrGreenOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)$$2));
    }

    public byte getBlueOrLuminance(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        if (!this.format.hasLuminanceOrBlue()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"no blue or luminance in %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        int $$2 = ($$0 + $$1 * this.width) * this.format.components() + this.format.luminanceOrBlueOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)$$2));
    }

    public byte getLuminanceOrAlpha(int $$0, int $$1) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"no luminance or alpha in %s", (Object[])new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"(%s, %s) outside of image bounds (%s, %s)", (Object[])new Object[]{$$0, $$1, this.width, this.height}));
        }
        int $$2 = ($$0 + $$1 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)$$2));
    }

    public void blendPixel(int $$0, int $$1, int $$2) {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        int $$3 = this.getPixelRGBA($$0, $$1);
        float $$4 = (float)NativeImage.getA($$2) / 255.0f;
        float $$5 = (float)NativeImage.getB($$2) / 255.0f;
        float $$6 = (float)NativeImage.getG($$2) / 255.0f;
        float $$7 = (float)NativeImage.getR($$2) / 255.0f;
        float $$8 = (float)NativeImage.getA($$3) / 255.0f;
        float $$9 = (float)NativeImage.getB($$3) / 255.0f;
        float $$10 = (float)NativeImage.getG($$3) / 255.0f;
        float $$11 = (float)NativeImage.getR($$3) / 255.0f;
        float $$12 = $$4;
        float $$13 = 1.0f - $$4;
        float $$14 = $$4 * $$12 + $$8 * $$13;
        float $$15 = $$5 * $$12 + $$9 * $$13;
        float $$16 = $$6 * $$12 + $$10 * $$13;
        float $$17 = $$7 * $$12 + $$11 * $$13;
        if ($$14 > 1.0f) {
            $$14 = 1.0f;
        }
        if ($$15 > 1.0f) {
            $$15 = 1.0f;
        }
        if ($$16 > 1.0f) {
            $$16 = 1.0f;
        }
        if ($$17 > 1.0f) {
            $$17 = 1.0f;
        }
        int $$18 = (int)($$14 * 255.0f);
        int $$19 = (int)($$15 * 255.0f);
        int $$20 = (int)($$16 * 255.0f);
        int $$21 = (int)($$17 * 255.0f);
        this.setPixelRGBA($$0, $$1, NativeImage.combine($$18, $$19, $$20, $$21));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] $$0 = new int[this.getWidth() * this.getHeight()];
        for (int $$1 = 0; $$1 < this.getHeight(); ++$$1) {
            for (int $$2 = 0; $$2 < this.getWidth(); ++$$2) {
                int $$8;
                int $$3 = this.getPixelRGBA($$2, $$1);
                int $$4 = NativeImage.getA($$3);
                int $$5 = NativeImage.getB($$3);
                int $$6 = NativeImage.getG($$3);
                int $$7 = NativeImage.getR($$3);
                $$0[$$2 + $$1 * this.getWidth()] = $$8 = $$4 << 24 | $$7 << 16 | $$6 << 8 | $$5;
            }
        }
        return $$0;
    }

    public void upload(int $$0, int $$1, int $$2, boolean $$3) {
        this.upload($$0, $$1, $$2, 0, 0, this.width, this.height, false, $$3);
    }

    public void upload(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8) {
        this.upload($$0, $$1, $$2, $$3, $$4, $$5, $$6, false, false, $$7, $$8);
    }

    public void upload(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8, boolean $$9, boolean $$10) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._upload($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10));
        } else {
            this._upload($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
        }
    }

    private void _upload(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8, boolean $$9, boolean $$10) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.checkAllocated();
        NativeImage.setFilter($$7, $$9);
        if ($$5 == this.getWidth()) {
            GlStateManager._pixelStore(3314, 0);
        } else {
            GlStateManager._pixelStore(3314, this.getWidth());
        }
        GlStateManager._pixelStore(3316, $$3);
        GlStateManager._pixelStore(3315, $$4);
        this.format.setUnpackPixelStoreState();
        GlStateManager._texSubImage2D(3553, $$0, $$1, $$2, $$5, $$6, this.format.glFormat(), 5121, this.pixels);
        if ($$8) {
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
        }
        if ($$10) {
            this.close();
        }
    }

    public void downloadTexture(int $$0, boolean $$1) {
        RenderSystem.assertOnRenderThread();
        this.checkAllocated();
        this.format.setPackPixelStoreState();
        GlStateManager._getTexImage(3553, $$0, this.format.glFormat(), 5121, this.pixels);
        if ($$1 && this.format.hasAlpha()) {
            for (int $$2 = 0; $$2 < this.getHeight(); ++$$2) {
                for (int $$3 = 0; $$3 < this.getWidth(); ++$$3) {
                    this.setPixelRGBA($$3, $$2, this.getPixelRGBA($$3, $$2) | 255 << this.format.alphaOffset());
                }
            }
        }
    }

    public void downloadDepthBuffer(float $$0) {
        RenderSystem.assertOnRenderThread();
        if (this.format.components() != 1) {
            throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
        }
        this.checkAllocated();
        this.format.setPackPixelStoreState();
        GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pixels);
    }

    public void drawPixels() {
        RenderSystem.assertOnRenderThread();
        this.format.setUnpackPixelStoreState();
        GlStateManager._glDrawPixels(this.width, this.height, this.format.glFormat(), 5121, this.pixels);
    }

    public void writeToFile(File $$0) throws IOException {
        this.writeToFile($$0.toPath());
    }

    public void copyFromFont(STBTTFontinfo $$0, int $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7, int $$8, int $$9) {
        if ($$8 < 0 || $$8 + $$2 > this.getWidth() || $$9 < 0 || $$9 + $$3 > this.getHeight()) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", (Object[])new Object[]{$$8, $$9, $$2, $$3, this.getWidth(), this.getHeight()}));
        }
        if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel((long)$$0.address(), (long)(this.pixels + (long)$$8 + (long)($$9 * this.getWidth())), (int)$$2, (int)$$3, (int)this.getWidth(), (float)$$4, (float)$$5, (float)$$6, (float)$$7, (int)$$1);
    }

    public void writeToFile(Path $$0) throws IOException {
        if (!this.format.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to write format " + this.format);
        }
        this.checkAllocated();
        try (SeekableByteChannel $$1 = Files.newByteChannel((Path)$$0, OPEN_OPTIONS, (FileAttribute[])new FileAttribute[0]);){
            if (!this.writeToChannel((WritableByteChannel)$$1)) {
                throw new IOException("Could not write image to the PNG file \"" + $$0.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
        }
    }

    public byte[] asByteArray() throws IOException {
        try (ByteArrayOutputStream $$0 = new ByteArrayOutputStream();){
            byte[] byArray;
            block12: {
                WritableByteChannel $$1 = Channels.newChannel((OutputStream)$$0);
                try {
                    if (!this.writeToChannel($$1)) {
                        throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
                    }
                    byArray = $$0.toByteArray();
                    if ($$1 == null) break block12;
                }
                catch (Throwable throwable) {
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
                $$1.close();
            }
            return byArray;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean writeToChannel(WritableByteChannel $$0) throws IOException {
        WriteCallback $$1 = new WriteCallback($$0);
        try {
            int $$2 = Math.min((int)this.getHeight(), (int)(Integer.MAX_VALUE / this.getWidth() / this.format.components()));
            if ($$2 < this.getHeight()) {
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)$$2);
            }
            if (STBImageWrite.nstbi_write_png_to_func((long)$$1.address(), (long)0L, (int)this.getWidth(), (int)$$2, (int)this.format.components(), (long)this.pixels, (int)0) == 0) {
                boolean bl = false;
                return bl;
            }
            $$1.throwIfException();
            boolean bl = true;
            return bl;
        }
        finally {
            $$1.free();
        }
    }

    public void copyFrom(NativeImage $$0) {
        if ($$0.format() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int $$1 = this.format.components();
        this.checkAllocated();
        $$0.checkAllocated();
        if (this.width == $$0.width) {
            MemoryUtil.memCopy((long)$$0.pixels, (long)this.pixels, (long)Math.min((long)this.size, (long)$$0.size));
        } else {
            int $$2 = Math.min((int)this.getWidth(), (int)$$0.getWidth());
            int $$3 = Math.min((int)this.getHeight(), (int)$$0.getHeight());
            for (int $$4 = 0; $$4 < $$3; ++$$4) {
                int $$5 = $$4 * $$0.getWidth() * $$1;
                int $$6 = $$4 * this.getWidth() * $$1;
                MemoryUtil.memCopy((long)($$0.pixels + (long)$$5), (long)(this.pixels + (long)$$6), (long)$$2);
            }
        }
    }

    public void fillRect(int $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$1 + $$3; ++$$5) {
            for (int $$6 = $$0; $$6 < $$0 + $$2; ++$$6) {
                this.setPixelRGBA($$6, $$5, $$4);
            }
        }
    }

    public void copyRect(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, boolean $$6, boolean $$7) {
        this.copyRect(this, $$0, $$1, $$0 + $$2, $$1 + $$3, $$4, $$5, $$6, $$7);
    }

    public void copyRect(NativeImage $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8) {
        for (int $$9 = 0; $$9 < $$6; ++$$9) {
            for (int $$10 = 0; $$10 < $$5; ++$$10) {
                int $$11 = $$7 ? $$5 - 1 - $$10 : $$10;
                int $$12 = $$8 ? $$6 - 1 - $$9 : $$9;
                int $$13 = this.getPixelRGBA($$1 + $$10, $$2 + $$9);
                $$0.setPixelRGBA($$3 + $$11, $$4 + $$12, $$13);
            }
        }
    }

    public void flipY() {
        this.checkAllocated();
        try (MemoryStack $$0 = MemoryStack.stackPush();){
            int $$1 = this.format.components();
            int $$2 = this.getWidth() * $$1;
            long $$3 = $$0.nmalloc($$2);
            for (int $$4 = 0; $$4 < this.getHeight() / 2; ++$$4) {
                int $$5 = $$4 * this.getWidth() * $$1;
                int $$6 = (this.getHeight() - 1 - $$4) * this.getWidth() * $$1;
                MemoryUtil.memCopy((long)(this.pixels + (long)$$5), (long)$$3, (long)$$2);
                MemoryUtil.memCopy((long)(this.pixels + (long)$$6), (long)(this.pixels + (long)$$5), (long)$$2);
                MemoryUtil.memCopy((long)$$3, (long)(this.pixels + (long)$$6), (long)$$2);
            }
        }
    }

    public void resizeSubRectTo(int $$0, int $$1, int $$2, int $$3, NativeImage $$4) {
        this.checkAllocated();
        if ($$4.format() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int $$5 = this.format.components();
        STBImageResize.nstbir_resize_uint8((long)(this.pixels + (long)(($$0 + $$1 * this.getWidth()) * $$5)), (int)$$2, (int)$$3, (int)(this.getWidth() * $$5), (long)$$4.pixels, (int)$$4.getWidth(), (int)$$4.getHeight(), (int)0, (int)$$5);
    }

    public void untrack() {
        DebugMemoryUntracker.untrack(this.pixels);
    }

    public static NativeImage fromBase64(String $$0) throws IOException {
        byte[] $$1 = Base64.getDecoder().decode($$0.replaceAll("\n", "").getBytes(Charsets.UTF_8));
        try (MemoryStack $$2 = MemoryStack.stackPush();){
            ByteBuffer $$3 = $$2.malloc($$1.length);
            $$3.put($$1);
            $$3.rewind();
            NativeImage nativeImage = NativeImage.read($$3);
            return nativeImage;
        }
    }

    public static int getA(int $$0) {
        return $$0 >> 24 & 0xFF;
    }

    public static int getR(int $$0) {
        return $$0 >> 0 & 0xFF;
    }

    public static int getG(int $$0) {
        return $$0 >> 8 & 0xFF;
    }

    public static int getB(int $$0) {
        return $$0 >> 16 & 0xFF;
    }

    public static int combine(int $$0, int $$1, int $$2, int $$3) {
        return ($$0 & 0xFF) << 24 | ($$1 & 0xFF) << 16 | ($$2 & 0xFF) << 8 | ($$3 & 0xFF) << 0;
    }

    public static enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        final int components;
        private final int glFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceOffset;
        private final int alphaOffset;
        private final boolean supportedByStb;

        private Format(int $$0, int $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6, int $$7, int $$8, int $$9, int $$10, int $$11, boolean $$12) {
            this.components = $$0;
            this.glFormat = $$1;
            this.hasRed = $$2;
            this.hasGreen = $$3;
            this.hasBlue = $$4;
            this.hasLuminance = $$5;
            this.hasAlpha = $$6;
            this.redOffset = $$7;
            this.greenOffset = $$8;
            this.blueOffset = $$9;
            this.luminanceOffset = $$10;
            this.alphaOffset = $$11;
            this.supportedByStb = $$12;
        }

        public int components() {
            return this.components;
        }

        public void setPackPixelStoreState() {
            RenderSystem.assertOnRenderThread();
            GlStateManager._pixelStore(3333, this.components());
        }

        public void setUnpackPixelStoreState() {
            RenderSystem.assertOnRenderThreadOrInit();
            GlStateManager._pixelStore(3317, this.components());
        }

        public int glFormat() {
            return this.glFormat;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int redOffset() {
            return this.redOffset;
        }

        public int greenOffset() {
            return this.greenOffset;
        }

        public int blueOffset() {
            return this.blueOffset;
        }

        public int luminanceOffset() {
            return this.luminanceOffset;
        }

        public int alphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasLuminanceOrRed() {
            return this.hasLuminance || this.hasRed;
        }

        public boolean hasLuminanceOrGreen() {
            return this.hasLuminance || this.hasGreen;
        }

        public boolean hasLuminanceOrBlue() {
            return this.hasLuminance || this.hasBlue;
        }

        public boolean hasLuminanceOrAlpha() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int luminanceOrRedOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.redOffset;
        }

        public int luminanceOrGreenOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
        }

        public int luminanceOrBlueOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
        }

        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean supportedByStb() {
            return this.supportedByStb;
        }

        static Format getStbFormat(int $$0) {
            switch ($$0) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }
    }

    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel output;
        @Nullable
        private IOException exception;

        WriteCallback(WritableByteChannel $$0) {
            this.output = $$0;
        }

        public void invoke(long $$0, long $$1, int $$2) {
            ByteBuffer $$3 = WriteCallback.getData((long)$$1, (int)$$2);
            try {
                this.output.write($$3);
            }
            catch (IOException $$4) {
                this.exception = $$4;
            }
        }

        public void throwIfException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }

    public static enum InternalGlFormat {
        RGBA(6408),
        RGB(6407),
        RG(33319),
        RED(6403);

        private final int glFormat;

        private InternalGlFormat(int $$0) {
            this.glFormat = $$0;
        }

        public int glFormat() {
            return this.glFormat;
        }
    }
}