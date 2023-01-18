/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.HttpURLConnection
 *  java.net.URL
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class HttpTexture
extends SimpleTexture {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SKIN_WIDTH = 64;
    private static final int SKIN_HEIGHT = 64;
    private static final int LEGACY_SKIN_HEIGHT = 32;
    @Nullable
    private final File file;
    private final String urlString;
    private final boolean processLegacySkin;
    @Nullable
    private final Runnable onDownloaded;
    @Nullable
    private CompletableFuture<?> future;
    private boolean uploaded;

    public HttpTexture(@Nullable File $$0, String $$1, ResourceLocation $$2, boolean $$3, @Nullable Runnable $$4) {
        super($$2);
        this.file = $$0;
        this.urlString = $$1;
        this.processLegacySkin = $$3;
        this.onDownloaded = $$4;
    }

    private void loadCallback(NativeImage $$0) {
        if (this.onDownloaded != null) {
            this.onDownloaded.run();
        }
        Minecraft.getInstance().execute(() -> {
            this.uploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.upload($$0));
            } else {
                this.upload($$0);
            }
        });
    }

    private void upload(NativeImage $$0) {
        TextureUtil.prepareImage(this.getId(), $$0.getWidth(), $$0.getHeight());
        $$0.upload(0, 0, 0, true);
    }

    @Override
    public void load(ResourceManager $$0) throws IOException {
        NativeImage $$3;
        Minecraft.getInstance().execute(() -> {
            if (!this.uploaded) {
                try {
                    super.load($$0);
                }
                catch (IOException $$1) {
                    LOGGER.warn("Failed to load texture: {}", (Object)this.location, (Object)$$1);
                }
                this.uploaded = true;
            }
        });
        if (this.future != null) {
            return;
        }
        if (this.file != null && this.file.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.file);
            FileInputStream $$1 = new FileInputStream(this.file);
            NativeImage $$2 = this.load((InputStream)$$1);
        } else {
            $$3 = null;
        }
        if ($$3 != null) {
            this.loadCallback($$3);
            return;
        }
        this.future = CompletableFuture.runAsync(() -> {
            HttpURLConnection $$0 = null;
            LOGGER.debug("Downloading http texture from {} to {}", (Object)this.urlString, (Object)this.file);
            try {
                InputStream $$2;
                $$0 = (HttpURLConnection)new URL(this.urlString).openConnection(Minecraft.getInstance().getProxy());
                $$0.setDoInput(true);
                $$0.setDoOutput(false);
                $$0.connect();
                if ($$0.getResponseCode() / 100 != 2) {
                    return;
                }
                if (this.file != null) {
                    FileUtils.copyInputStreamToFile((InputStream)$$0.getInputStream(), (File)this.file);
                    FileInputStream $$1 = new FileInputStream(this.file);
                } else {
                    $$2 = $$0.getInputStream();
                }
                Minecraft.getInstance().execute(() -> {
                    NativeImage $$1 = this.load($$2);
                    if ($$1 != null) {
                        this.loadCallback($$1);
                    }
                });
            }
            catch (Exception $$3) {
                LOGGER.error("Couldn't download http texture", (Throwable)$$3);
            }
            finally {
                if ($$0 != null) {
                    $$0.disconnect();
                }
            }
        }, (Executor)Util.backgroundExecutor());
    }

    @Nullable
    private NativeImage load(InputStream $$0) {
        NativeImage $$1 = null;
        try {
            $$1 = NativeImage.read($$0);
            if (this.processLegacySkin) {
                $$1 = this.processLegacySkin($$1);
            }
        }
        catch (Exception $$2) {
            LOGGER.warn("Error while loading the skin texture", (Throwable)$$2);
        }
        return $$1;
    }

    @Nullable
    private NativeImage processLegacySkin(NativeImage $$0) {
        boolean $$3;
        int $$1 = $$0.getHeight();
        int $$2 = $$0.getWidth();
        if ($$2 != 64 || $$1 != 32 && $$1 != 64) {
            $$0.close();
            LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", new Object[]{$$2, $$1, this.urlString});
            return null;
        }
        boolean bl = $$3 = $$1 == 32;
        if ($$3) {
            NativeImage $$4 = new NativeImage(64, 64, true);
            $$4.copyFrom($$0);
            $$0.close();
            $$0 = $$4;
            $$0.fillRect(0, 32, 64, 32, 0);
            $$0.copyRect(4, 16, 16, 32, 4, 4, true, false);
            $$0.copyRect(8, 16, 16, 32, 4, 4, true, false);
            $$0.copyRect(0, 20, 24, 32, 4, 12, true, false);
            $$0.copyRect(4, 20, 16, 32, 4, 12, true, false);
            $$0.copyRect(8, 20, 8, 32, 4, 12, true, false);
            $$0.copyRect(12, 20, 16, 32, 4, 12, true, false);
            $$0.copyRect(44, 16, -8, 32, 4, 4, true, false);
            $$0.copyRect(48, 16, -8, 32, 4, 4, true, false);
            $$0.copyRect(40, 20, 0, 32, 4, 12, true, false);
            $$0.copyRect(44, 20, -8, 32, 4, 12, true, false);
            $$0.copyRect(48, 20, -16, 32, 4, 12, true, false);
            $$0.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        HttpTexture.setNoAlpha($$0, 0, 0, 32, 16);
        if ($$3) {
            HttpTexture.doNotchTransparencyHack($$0, 32, 0, 64, 32);
        }
        HttpTexture.setNoAlpha($$0, 0, 16, 64, 32);
        HttpTexture.setNoAlpha($$0, 16, 48, 48, 64);
        return $$0;
    }

    private static void doNotchTransparencyHack(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$3; ++$$5) {
            for (int $$6 = $$2; $$6 < $$4; ++$$6) {
                int $$7 = $$0.getPixelRGBA($$5, $$6);
                if (($$7 >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int $$8 = $$1; $$8 < $$3; ++$$8) {
            for (int $$9 = $$2; $$9 < $$4; ++$$9) {
                $$0.setPixelRGBA($$8, $$9, $$0.getPixelRGBA($$8, $$9) & 0xFFFFFF);
            }
        }
    }

    private static void setNoAlpha(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$3; ++$$5) {
            for (int $$6 = $$2; $$6 < $$4; ++$$6) {
                $$0.setPixelRGBA($$5, $$6, $$0.getPixelRGBA($$5, $$6) | 0xFF000000);
            }
        }
    }
}