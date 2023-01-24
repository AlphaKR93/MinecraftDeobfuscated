/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.IntUnaryOperator
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import org.slf4j.Logger;

public class PalettedPermutations
implements SpriteSource {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<PalettedPermutations> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter($$0 -> $$0.textures), (App)ResourceLocation.CODEC.fieldOf("palette_key").forGetter($$0 -> $$0.paletteKey), (App)Codec.unboundedMap((Codec)Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter($$0 -> $$0.permutations)).apply((Applicative)$$02, PalettedPermutations::new));
    private final List<ResourceLocation> textures;
    private final Map<String, ResourceLocation> permutations;
    private final ResourceLocation paletteKey;

    private PalettedPermutations(List<ResourceLocation> $$0, ResourceLocation $$1, Map<String, ResourceLocation> $$2) {
        this.textures = $$0;
        this.permutations = $$2;
        this.paletteKey = $$1;
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        Supplier $$2 = Suppliers.memoize(() -> PalettedPermutations.loadPaletteEntryFromImage($$0, this.paletteKey));
        HashMap $$3 = new HashMap();
        this.permutations.forEach((arg_0, arg_1) -> PalettedPermutations.lambda$run$6((Map)$$3, (java.util.function.Supplier)$$2, $$0, arg_0, arg_1));
        for (ResourceLocation $$4 : this.textures) {
            ResourceLocation $$5 = TEXTURE_ID_CONVERTER.idToFile($$4);
            Optional $$6 = $$0.getResource($$5);
            if ($$6.isEmpty()) {
                LOGGER.warn("Unable to find texture {}", (Object)$$5);
                continue;
            }
            LazyLoadedImage $$7 = new LazyLoadedImage($$5, (Resource)$$6.get(), $$3.size());
            for (Map.Entry $$8 : $$3.entrySet()) {
                ResourceLocation $$9 = $$4.withSuffix("_" + (String)$$8.getKey());
                $$1.add($$9, new PalettedSpriteSupplier($$7, (java.util.function.Supplier<IntUnaryOperator>)((java.util.function.Supplier)$$8.getValue()), $$9));
            }
        }
    }

    private static IntUnaryOperator createPaletteMapping(int[] $$0, int[] $$1) {
        if ($$1.length != $$0.length) {
            LOGGER.warn("Palette mapping has different sizes: {} and {}", (Object)$$0.length, (Object)$$1.length);
            throw new IllegalArgumentException();
        }
        Int2IntOpenHashMap $$2 = new Int2IntOpenHashMap($$1.length);
        for (int $$3 = 0; $$3 < $$0.length; ++$$3) {
            int $$4 = $$0[$$3];
            if (FastColor.ABGR32.alpha($$4) == 0) continue;
            $$2.put(FastColor.ABGR32.bgr($$4), $$1[$$3]);
        }
        return arg_0 -> PalettedPermutations.lambda$createPaletteMapping$7((Int2IntMap)$$2, arg_0);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static int[] loadPaletteEntryFromImage(ResourceManager $$0, ResourceLocation $$1) {
        Optional $$2 = $$0.getResource(TEXTURE_ID_CONVERTER.idToFile($$1));
        if ($$2.isEmpty()) {
            LOGGER.error("Failed to load palette image {}", (Object)$$1);
            throw new IllegalArgumentException();
        }
        try (InputStream $$3 = ((Resource)$$2.get()).open();){
            int[] nArray;
            block15: {
                NativeImage $$4 = NativeImage.read($$3);
                try {
                    nArray = $$4.getPixelsRGBA();
                    if ($$4 == null) break block15;
                }
                catch (Throwable throwable) {
                    if ($$4 != null) {
                        try {
                            $$4.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                $$4.close();
            }
            return nArray;
        }
        catch (Exception $$5) {
            LOGGER.error("Couldn't load texture {}", (Object)$$1, (Object)$$5);
            throw new IllegalArgumentException();
        }
    }

    @Override
    public SpriteSourceType type() {
        return SpriteSources.PALETTED_PERMUTATIONS;
    }

    private static /* synthetic */ int lambda$createPaletteMapping$7(Int2IntMap $$0, int $$1) {
        int $$2 = FastColor.ABGR32.alpha($$1);
        if ($$2 == 0) {
            return $$1;
        }
        int $$3 = FastColor.ABGR32.bgr($$1);
        int $$4 = $$0.getOrDefault($$3, $$3);
        int $$5 = FastColor.ABGR32.alpha($$4);
        return FastColor.ABGR32.color($$2 * $$5 / 255, $$4);
    }

    private static /* synthetic */ void lambda$run$6(Map $$0, java.util.function.Supplier $$1, ResourceManager $$2, String $$3, ResourceLocation $$4) {
        $$0.put((Object)$$3, (Object)Suppliers.memoize(() -> PalettedPermutations.lambda$run$5((java.util.function.Supplier)$$1, $$2, $$4)));
    }

    private static /* synthetic */ IntUnaryOperator lambda$run$5(java.util.function.Supplier $$0, ResourceManager $$1, ResourceLocation $$2) {
        return PalettedPermutations.createPaletteMapping((int[])$$0.get(), PalettedPermutations.loadPaletteEntryFromImage($$1, $$2));
    }

    record PalettedSpriteSupplier(LazyLoadedImage baseImage, java.util.function.Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier
    {
        @Nullable
        public SpriteContents get() {
            try {
                NativeImage $$0 = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
                SpriteContents spriteContents = new SpriteContents(this.permutationLocation, new FrameSize($$0.getWidth(), $$0.getHeight()), $$0, AnimationMetadataSection.EMPTY);
                return spriteContents;
            }
            catch (IOException | IllegalArgumentException $$1) {
                LOGGER.error("unable to apply palette to {}", (Object)this.permutationLocation, (Object)$$1);
                SpriteContents spriteContents = null;
                return spriteContents;
            }
            finally {
                this.baseImage.release();
            }
        }

        @Override
        public void discard() {
            this.baseImage.release();
        }
    }
}