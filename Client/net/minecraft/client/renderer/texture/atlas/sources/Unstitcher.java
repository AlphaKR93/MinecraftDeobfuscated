/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.Optional
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.concurrent.atomic.AtomicReference
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class Unstitcher
implements SpriteSource {
    static final Logger LOGGER = LogUtils.getLogger();
    private final FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");
    public static final Codec<Unstitcher> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ResourceLocation.CODEC.fieldOf("resource").forGetter($$0 -> $$0.resource), (App)ExtraCodecs.nonEmptyList(Region.CODEC.listOf()).fieldOf("regions").forGetter($$0 -> $$0.regions), (App)Codec.DOUBLE.optionalFieldOf("divisor_x", (Object)1.0).forGetter($$0 -> $$0.xDivisor), (App)Codec.DOUBLE.optionalFieldOf("divisor_y", (Object)1.0).forGetter($$0 -> $$0.yDivisor)).apply((Applicative)$$02, Unstitcher::new));
    private final ResourceLocation resource;
    private final List<Region> regions;
    private final double xDivisor;
    private final double yDivisor;

    public Unstitcher(ResourceLocation $$0, List<Region> $$1, double $$2, double $$3) {
        this.resource = $$0;
        this.regions = $$1;
        this.xDivisor = $$2;
        this.yDivisor = $$3;
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        ResourceLocation $$2 = this.TEXTURE_ID_CONVERTER.idToFile(this.resource);
        Optional $$3 = $$0.getResource($$2);
        if ($$3.isPresent()) {
            LazyLoadedImage $$4 = new LazyLoadedImage($$2, (Resource)$$3.get(), this.regions.size());
            for (Region $$5 : this.regions) {
                $$1.add($$5.sprite, new RegionInstance($$4, $$5, this.xDivisor, this.yDivisor));
            }
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)$$2);
        }
    }

    @Override
    public SpriteSourceType type() {
        return SpriteSources.UNSTITCHER;
    }

    static class LazyLoadedImage {
        private final ResourceLocation id;
        private final Resource resource;
        private final AtomicReference<NativeImage> image = new AtomicReference();
        private final AtomicInteger referenceCount;

        LazyLoadedImage(ResourceLocation $$0, Resource $$1, int $$2) {
            this.id = $$0;
            this.resource = $$1;
            this.referenceCount = new AtomicInteger($$2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public NativeImage get() throws IOException {
            NativeImage $$0 = (NativeImage)this.image.get();
            if ($$0 == null) {
                LazyLoadedImage lazyLoadedImage = this;
                synchronized (lazyLoadedImage) {
                    $$0 = (NativeImage)this.image.get();
                    if ($$0 == null) {
                        try (InputStream $$1 = this.resource.open();){
                            $$0 = NativeImage.read($$1);
                            this.image.set((Object)$$0);
                        }
                        catch (IOException $$2) {
                            throw new IOException("Failed to load image " + this.id, (Throwable)$$2);
                        }
                    }
                }
            }
            return $$0;
        }

        public void release() {
            NativeImage $$1;
            int $$0 = this.referenceCount.decrementAndGet();
            if ($$0 <= 0 && ($$1 = (NativeImage)this.image.getAndSet(null)) != null) {
                $$1.close();
            }
        }
    }

    record Region(ResourceLocation sprite, double x, double y, double width, double height) {
        public static final Codec<Region> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("sprite").forGetter(Region::sprite), (App)Codec.DOUBLE.fieldOf("x").forGetter(Region::x), (App)Codec.DOUBLE.fieldOf("y").forGetter(Region::y), (App)Codec.DOUBLE.fieldOf("width").forGetter(Region::width), (App)Codec.DOUBLE.fieldOf("height").forGetter(Region::height)).apply((Applicative)$$0, Region::new));
    }

    static class RegionInstance
    implements SpriteSource.SpriteSupplier {
        private final LazyLoadedImage image;
        private final Region region;
        private final double xDivisor;
        private final double yDivisor;

        RegionInstance(LazyLoadedImage $$0, Region $$1, double $$2, double $$3) {
            this.image = $$0;
            this.region = $$1;
            this.xDivisor = $$2;
            this.yDivisor = $$3;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public SpriteContents get() {
            try {
                NativeImage $$0 = this.image.get();
                double $$1 = (double)$$0.getWidth() / this.xDivisor;
                double $$2 = (double)$$0.getHeight() / this.yDivisor;
                int $$3 = Mth.floor(this.region.x * $$1);
                int $$4 = Mth.floor(this.region.y * $$2);
                int $$5 = Mth.floor(this.region.width * $$1);
                int $$6 = Mth.floor(this.region.height * $$2);
                NativeImage $$7 = new NativeImage(NativeImage.Format.RGBA, $$5, $$6, false);
                $$0.copyRect($$7, $$3, $$4, 0, 0, $$5, $$6, false, false);
                SpriteContents spriteContents = new SpriteContents(this.region.sprite, new FrameSize($$5, $$6), $$7, AnimationMetadataSection.EMPTY);
                return spriteContents;
            }
            catch (Exception $$8) {
                LOGGER.error("Failed to unstitch region {}", (Object)this.region.sprite, (Object)$$8);
            }
            finally {
                this.image.release();
            }
            return MissingTextureAtlasSprite.create();
        }

        @Override
        public void discard() {
            this.image.release();
        }
    }
}