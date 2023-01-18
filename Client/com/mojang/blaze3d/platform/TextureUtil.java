/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.FileInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  java.nio.channels.Channels
 *  java.nio.channels.FileChannel
 *  java.nio.channels.ReadableByteChannel
 *  java.nio.file.Path
 *  java.util.concurrent.ThreadLocalRandom
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.SharedConstants;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class TextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static int generateTextureId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            int[] $$0 = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManager._genTextures($$0);
            int $$1 = GlStateManager._genTexture();
            GlStateManager._deleteTextures($$0);
            return $$1;
        }
        return GlStateManager._genTexture();
    }

    public static void releaseTextureId(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._deleteTexture($$0);
    }

    public static void prepareImage(int $$0, int $$1, int $$2) {
        TextureUtil.prepareImage(NativeImage.InternalGlFormat.RGBA, $$0, 0, $$1, $$2);
    }

    public static void prepareImage(NativeImage.InternalGlFormat $$0, int $$1, int $$2, int $$3) {
        TextureUtil.prepareImage($$0, $$1, 0, $$2, $$3);
    }

    public static void prepareImage(int $$0, int $$1, int $$2, int $$3) {
        TextureUtil.prepareImage(NativeImage.InternalGlFormat.RGBA, $$0, $$1, $$2, $$3);
    }

    public static void prepareImage(NativeImage.InternalGlFormat $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.assertOnRenderThreadOrInit();
        TextureUtil.bind($$1);
        if ($$2 >= 0) {
            GlStateManager._texParameter(3553, 33085, $$2);
            GlStateManager._texParameter(3553, 33082, 0);
            GlStateManager._texParameter(3553, 33083, $$2);
            GlStateManager._texParameter(3553, 34049, 0.0f);
        }
        for (int $$5 = 0; $$5 <= $$2; ++$$5) {
            GlStateManager._texImage2D(3553, $$5, $$0.glFormat(), $$3 >> $$5, $$4 >> $$5, 0, 6408, 5121, null);
        }
    }

    private static void bind(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._bindTexture($$0);
    }

    public static ByteBuffer readResource(InputStream $$0) throws IOException {
        ByteBuffer $$4;
        if ($$0 instanceof FileInputStream) {
            FileInputStream $$1 = (FileInputStream)$$0;
            FileChannel $$2 = $$1.getChannel();
            ByteBuffer $$3 = MemoryUtil.memAlloc((int)((int)$$2.size() + 1));
            while ($$2.read($$3) != -1) {
            }
        } else {
            $$4 = MemoryUtil.memAlloc((int)8192);
            ReadableByteChannel $$5 = Channels.newChannel((InputStream)$$0);
            while ($$5.read($$4) != -1) {
                if ($$4.remaining() != 0) continue;
                $$4 = MemoryUtil.memRealloc((ByteBuffer)$$4, (int)($$4.capacity() * 2));
            }
        }
        return $$4;
    }

    public static void writeAsPNG(Path $$0, String $$1, int $$2, int $$3, int $$4, int $$5) {
        RenderSystem.assertOnRenderThread();
        TextureUtil.bind($$2);
        for (int $$6 = 0; $$6 <= $$3; ++$$6) {
            int $$7 = $$4 >> $$6;
            int $$8 = $$5 >> $$6;
            try (NativeImage $$9 = new NativeImage($$7, $$8, false);){
                $$9.downloadTexture($$6, false);
                Path $$10 = $$0.resolve($$1 + "_" + $$6 + ".png");
                $$9.writeToFile($$10);
                LOGGER.debug("Exported png to: {}", (Object)$$10.toAbsolutePath());
                continue;
            }
            catch (IOException $$11) {
                LOGGER.debug("Unable to write: ", (Throwable)$$11);
            }
        }
    }

    public static void initTexture(IntBuffer $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GL11.glPixelStorei((int)3312, (int)0);
        GL11.glPixelStorei((int)3313, (int)0);
        GL11.glPixelStorei((int)3314, (int)0);
        GL11.glPixelStorei((int)3315, (int)0);
        GL11.glPixelStorei((int)3316, (int)0);
        GL11.glPixelStorei((int)3317, (int)4);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)$$1, (int)$$2, (int)0, (int)32993, (int)33639, (IntBuffer)$$0);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
    }

    public static Path getDebugTexturePath(Path $$0) {
        return $$0.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath(Path.of((String)".", (String[])new String[0]));
    }
}