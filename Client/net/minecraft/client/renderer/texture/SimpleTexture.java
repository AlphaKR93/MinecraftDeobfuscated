/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.Closeable
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SimpleTexture
extends AbstractTexture {
    static final Logger LOGGER = LogUtils.getLogger();
    protected final ResourceLocation location;

    public SimpleTexture(ResourceLocation $$0) {
        this.location = $$0;
    }

    @Override
    public void load(ResourceManager $$0) throws IOException {
        boolean $$6;
        boolean $$5;
        TextureImage $$1 = this.getTextureImage($$0);
        $$1.throwIfError();
        TextureMetadataSection $$2 = $$1.getTextureMetadata();
        if ($$2 != null) {
            boolean $$3 = $$2.isBlur();
            boolean $$4 = $$2.isClamp();
        } else {
            $$5 = false;
            $$6 = false;
        }
        NativeImage $$7 = $$1.getImage();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.doLoad($$7, $$5, $$6));
        } else {
            this.doLoad($$7, $$5, $$6);
        }
    }

    private void doLoad(NativeImage $$0, boolean $$1, boolean $$2) {
        TextureUtil.prepareImage(this.getId(), 0, $$0.getWidth(), $$0.getHeight());
        $$0.upload(0, 0, 0, 0, 0, $$0.getWidth(), $$0.getHeight(), $$1, $$2, false, true);
    }

    protected TextureImage getTextureImage(ResourceManager $$0) {
        return TextureImage.load($$0, this.location);
    }

    protected static class TextureImage
    implements Closeable {
        @Nullable
        private final TextureMetadataSection metadata;
        @Nullable
        private final NativeImage image;
        @Nullable
        private final IOException exception;

        public TextureImage(IOException $$0) {
            this.exception = $$0;
            this.metadata = null;
            this.image = null;
        }

        public TextureImage(@Nullable TextureMetadataSection $$0, NativeImage $$1) {
            this.exception = null;
            this.metadata = $$0;
            this.image = $$1;
        }

        /*
         * WARNING - void declaration
         */
        public static TextureImage load(ResourceManager $$0, ResourceLocation $$1) {
            try {
                void $$5;
                Resource $$2 = $$0.getResourceOrThrow($$1);
                try (InputStream $$3 = $$2.open();){
                    NativeImage $$4 = NativeImage.read($$3);
                }
                TextureMetadataSection $$6 = null;
                try {
                    $$6 = (TextureMetadataSection)$$2.metadata().getSection(TextureMetadataSection.SERIALIZER).orElse(null);
                }
                catch (RuntimeException $$7) {
                    LOGGER.warn("Failed reading metadata of: {}", (Object)$$1, (Object)$$7);
                }
                return new TextureImage($$6, (NativeImage)$$5);
            }
            catch (IOException $$8) {
                return new TextureImage($$8);
            }
        }

        @Nullable
        public TextureMetadataSection getTextureMetadata() {
            return this.metadata;
        }

        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }

        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }

        public void throwIfError() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}