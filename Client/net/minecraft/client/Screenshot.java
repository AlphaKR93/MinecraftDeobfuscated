/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.DataOutputStream
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  java.util.function.Consumer
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.slf4j.Logger;

public class Screenshot {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SCREENSHOT_DIR = "screenshots";
    private int rowHeight;
    private final DataOutputStream outputStream;
    private final byte[] bytes;
    private final int width;
    private final int height;
    private File file;

    public static void grab(File $$0, RenderTarget $$1, Consumer<Component> $$2) {
        Screenshot.grab($$0, null, $$1, $$2);
    }

    public static void grab(File $$0, @Nullable String $$1, RenderTarget $$2, Consumer<Component> $$3) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> Screenshot._grab($$0, $$1, $$2, $$3));
        } else {
            Screenshot._grab($$0, $$1, $$2, $$3);
        }
    }

    private static void _grab(File $$0, @Nullable String $$1, RenderTarget $$2, Consumer<Component> $$3) {
        File $$7;
        NativeImage $$4 = Screenshot.takeScreenshot($$2);
        File $$5 = new File($$0, SCREENSHOT_DIR);
        $$5.mkdir();
        if ($$1 == null) {
            File $$6 = Screenshot.getFile($$5);
        } else {
            $$7 = new File($$5, $$1);
        }
        Util.ioPool().execute(() -> {
            try {
                $$4.writeToFile($$7);
                MutableComponent $$3 = Component.literal($$7.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, $$7.getAbsolutePath()))));
                $$3.accept((Object)Component.translatable("screenshot.success", $$3));
            }
            catch (Exception $$4) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)$$4);
                $$3.accept((Object)Component.translatable("screenshot.failure", $$4.getMessage()));
            }
            finally {
                $$4.close();
            }
        });
    }

    public static NativeImage takeScreenshot(RenderTarget $$0) {
        int $$1 = $$0.width;
        int $$2 = $$0.height;
        NativeImage $$3 = new NativeImage($$1, $$2, false);
        RenderSystem.bindTexture($$0.getColorTextureId());
        $$3.downloadTexture(0, true);
        $$3.flipY();
        return $$3;
    }

    private static File getFile(File $$0) {
        String $$1 = Util.getFilenameFormattedDateTime();
        int $$2 = 1;
        File $$3;
        while (($$3 = new File($$0, $$1 + ($$2 == 1 ? "" : "_" + $$2) + ".png")).exists()) {
            ++$$2;
        }
        return $$3;
    }

    public Screenshot(File $$0, int $$1, int $$2, int $$3) throws IOException {
        this.width = $$1;
        this.height = $$2;
        this.rowHeight = $$3;
        File $$4 = new File($$0, SCREENSHOT_DIR);
        $$4.mkdir();
        String $$5 = "huge_" + Util.getFilenameFormattedDateTime();
        int $$6 = 1;
        while ((this.file = new File($$4, $$5 + ($$6 == 1 ? "" : "_" + $$6) + ".tga")).exists()) {
            ++$$6;
        }
        byte[] $$7 = new byte[18];
        $$7[2] = 2;
        $$7[12] = (byte)($$1 % 256);
        $$7[13] = (byte)($$1 / 256);
        $$7[14] = (byte)($$2 % 256);
        $$7[15] = (byte)($$2 / 256);
        $$7[16] = 24;
        this.bytes = new byte[$$1 * $$3 * 3];
        this.outputStream = new DataOutputStream((OutputStream)new FileOutputStream(this.file));
        this.outputStream.write($$7);
    }

    public void addRegion(ByteBuffer $$0, int $$1, int $$2, int $$3, int $$4) {
        int $$5 = $$3;
        int $$6 = $$4;
        if ($$5 > this.width - $$1) {
            $$5 = this.width - $$1;
        }
        if ($$6 > this.height - $$2) {
            $$6 = this.height - $$2;
        }
        this.rowHeight = $$6;
        for (int $$7 = 0; $$7 < $$6; ++$$7) {
            $$0.position(($$4 - $$6) * $$3 * 3 + $$7 * $$3 * 3);
            int $$8 = ($$1 + $$7 * this.width) * 3;
            $$0.get(this.bytes, $$8, $$5 * 3);
        }
    }

    public void saveRow() throws IOException {
        this.outputStream.write(this.bytes, 0, this.width * 3 * this.rowHeight);
    }

    public File close() throws IOException {
        this.outputStream.close();
        return this.file;
    }
}