/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UUIDTypeAdapter
 *  java.awt.image.BufferedImage
 *  java.awt.image.RenderedImage
 *  java.io.ByteArrayInputStream
 *  java.io.ByteArrayOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Boolean
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.net.HttpURLConnection
 *  java.net.URL
 *  java.nio.IntBuffer
 *  java.util.Map
 *  java.util.UUID
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  javax.imageio.ImageIO
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.lwjgl.BufferUtils
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.SkinProcessor;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;

public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.newHashMap();
    static final Map<String, String> FETCHED_SKINS = Maps.newHashMap();
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

    public static void bindWorldTemplate(String $$0, @Nullable String $$1) {
        if ($$1 == null) {
            RenderSystem.setShaderTexture(0, TEMPLATE_ICON_LOCATION);
            return;
        }
        int $$2 = RealmsTextureManager.getTextureId($$0, $$1);
        RenderSystem.setShaderTexture(0, $$2);
    }

    public static void withBoundFace(String $$0, Runnable $$1) {
        RealmsTextureManager.bindFace($$0);
        $$1.run();
    }

    private static void bindDefaultFace(UUID $$0) {
        RenderSystem.setShaderTexture(0, DefaultPlayerSkin.getDefaultSkin($$0));
    }

    private static void bindFace(final String $$0) {
        UUID $$1 = UUIDTypeAdapter.fromString((String)$$0);
        if (TEXTURES.containsKey((Object)$$0)) {
            int $$2 = ((RealmsTexture)RealmsTextureManager.TEXTURES.get((Object)$$0)).textureId;
            RenderSystem.setShaderTexture(0, $$2);
            return;
        }
        if (SKIN_FETCH_STATUS.containsKey((Object)$$0)) {
            if (!((Boolean)SKIN_FETCH_STATUS.get((Object)$$0)).booleanValue()) {
                RealmsTextureManager.bindDefaultFace($$1);
            } else if (FETCHED_SKINS.containsKey((Object)$$0)) {
                int $$3 = RealmsTextureManager.getTextureId($$0, (String)FETCHED_SKINS.get((Object)$$0));
                RenderSystem.setShaderTexture(0, $$3);
            } else {
                RealmsTextureManager.bindDefaultFace($$1);
            }
            return;
        }
        SKIN_FETCH_STATUS.put((Object)$$0, (Object)false);
        RealmsTextureManager.bindDefaultFace($$1);
        Thread $$4 = new Thread("Realms Texture Downloader"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                block17: {
                    block16: {
                        BufferedImage $$6;
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> $$02 = RealmsUtil.getTextures($$0);
                        if (!$$02.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) break block16;
                        MinecraftProfileTexture $$1 = (MinecraftProfileTexture)$$02.get((Object)MinecraftProfileTexture.Type.SKIN);
                        String $$2 = $$1.getUrl();
                        HttpURLConnection $$3 = null;
                        LOGGER.debug("Downloading http texture from {}", (Object)$$2);
                        try {
                            $$3 = (HttpURLConnection)new URL($$2).openConnection(Minecraft.getInstance().getProxy());
                            $$3.setDoInput(true);
                            $$3.setDoOutput(false);
                            $$3.connect();
                            if ($$3.getResponseCode() / 100 != 2) {
                                SKIN_FETCH_STATUS.remove((Object)$$0);
                                return;
                            }
                            try {
                                BufferedImage $$4 = ImageIO.read((InputStream)$$3.getInputStream());
                            }
                            catch (Exception $$5) {
                                SKIN_FETCH_STATUS.remove((Object)$$0);
                                if ($$3 != null) {
                                    $$3.disconnect();
                                }
                                return;
                            }
                            finally {
                                IOUtils.closeQuietly((InputStream)$$3.getInputStream());
                            }
                            $$6 = new SkinProcessor().process($$6);
                        }
                        catch (Exception $$8) {
                            LOGGER.error("Couldn't download http texture", (Throwable)$$8);
                            SKIN_FETCH_STATUS.remove((Object)$$0);
                        }
                        finally {
                            if ($$3 != null) {
                                $$3.disconnect();
                            }
                        }
                        ByteArrayOutputStream $$7 = new ByteArrayOutputStream();
                        ImageIO.write((RenderedImage)$$6, (String)"png", (OutputStream)$$7);
                        FETCHED_SKINS.put((Object)$$0, (Object)new Base64().encodeToString($$7.toByteArray()));
                        SKIN_FETCH_STATUS.put((Object)$$0, (Object)true);
                        break block17;
                    }
                    SKIN_FETCH_STATUS.put((Object)$$0, (Object)true);
                }
            }
        };
        $$4.setDaemon(true);
        $$4.start();
    }

    private static int getTextureId(String $$0, String $$1) {
        int $$4;
        RealmsTexture $$2 = (RealmsTexture)TEXTURES.get((Object)$$0);
        if ($$2 != null && $$2.image.equals((Object)$$1)) {
            return $$2.textureId;
        }
        if ($$2 != null) {
            int $$3 = $$2.textureId;
        } else {
            $$4 = GlStateManager._genTexture();
        }
        TextureData $$5 = TextureData.load($$1);
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTextureForSetup($$4);
        TextureUtil.initTexture($$5.data, $$5.width, $$5.height);
        TEXTURES.put((Object)$$0, (Object)new RealmsTexture($$1, $$4));
        return $$4;
    }

    public static class RealmsTexture {
        final String image;
        final int textureId;

        public RealmsTexture(String $$0, int $$1) {
            this.image = $$0;
            this.textureId = $$1;
        }
    }

    static class TextureData {
        final int width;
        final int height;
        final IntBuffer data;
        private static final Supplier<TextureData> MISSING = Suppliers.memoize(() -> {
            int $$0 = 16;
            int $$1 = 16;
            IntBuffer $$2 = BufferUtils.createIntBuffer((int)256);
            int $$3 = -16777216;
            int $$4 = -524040;
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                for (int $$6 = 0; $$6 < 16; ++$$6) {
                    if ($$5 < 8 ^ $$6 < 8) {
                        $$2.put($$6 + $$5 * 16, -524040);
                        continue;
                    }
                    $$2.put($$6 + $$5 * 16, -16777216);
                }
            }
            return new TextureData(16, 16, $$2);
        });

        private TextureData(int $$0, int $$1, IntBuffer $$2) {
            this.width = $$0;
            this.height = $$1;
            this.data = $$2;
        }

        public static TextureData load(String $$0) {
            try {
                ByteArrayInputStream $$1 = new ByteArrayInputStream(new Base64().decode($$0));
                BufferedImage $$2 = ImageIO.read((InputStream)$$1);
                if ($$2 != null) {
                    int $$3 = $$2.getWidth();
                    int $$4 = $$2.getHeight();
                    int[] $$5 = new int[$$3 * $$4];
                    $$2.getRGB(0, 0, $$3, $$4, $$5, 0, $$3);
                    IntBuffer $$6 = BufferUtils.createIntBuffer((int)($$3 * $$4));
                    $$6.put($$5);
                    $$6.flip();
                    return new TextureData($$3, $$4, $$6);
                }
                LOGGER.warn("Unknown image format: {}", (Object)$$0);
            }
            catch (IOException $$7) {
                LOGGER.warn("Failed to load world image: {}", (Object)$$0, (Object)$$7);
            }
            return (TextureData)MISSING.get();
        }
    }
}