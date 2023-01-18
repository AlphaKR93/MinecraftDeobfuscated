/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class SpriteLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;

    public SpriteLoader(ResourceLocation $$0, int $$1) {
        this.location = $$0;
        this.maxSupportedTextureSize = $$1;
    }

    public static SpriteLoader create(TextureAtlas $$0) {
        return new SpriteLoader($$0.location(), $$0.maxSupportedTextureSize());
    }

    public Preparations stitch(List<SpriteContents> $$02, int $$1, Executor $$2) {
        CompletableFuture $$19;
        int $$12;
        int $$3 = this.maxSupportedTextureSize;
        Stitcher<SpriteContents> $$4 = new Stitcher<SpriteContents>($$3, $$3, $$1);
        int $$5 = Integer.MAX_VALUE;
        int $$6 = 1 << $$1;
        for (SpriteContents $$7 : $$02) {
            $$5 = Math.min((int)$$5, (int)Math.min((int)$$7.width(), (int)$$7.height()));
            int $$8 = Math.min((int)Integer.lowestOneBit((int)$$7.width()), (int)Integer.lowestOneBit((int)$$7.height()));
            if ($$8 < $$6) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{$$7.name(), $$7.width(), $$7.height(), Mth.log2($$6), Mth.log2($$8)});
                $$6 = $$8;
            }
            $$4.registerSprite($$7);
        }
        int $$9 = Math.min((int)$$5, (int)$$6);
        int $$10 = Mth.log2($$9);
        if ($$10 < $$1) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.location, $$1, $$10, $$9});
            int $$11 = $$10;
        } else {
            $$12 = $$1;
        }
        try {
            $$4.stitch();
        }
        catch (StitcherException $$13) {
            CrashReport $$14 = CrashReport.forThrowable((Throwable)$$13, "Stitching");
            CrashReportCategory $$15 = $$14.addCategory("Stitcher");
            $$15.setDetail("Sprites", $$13.getAllSprites().stream().map($$0 -> String.format((Locale)Locale.ROOT, (String)"%s[%dx%d]", (Object[])new Object[]{$$0.name(), $$0.width(), $$0.height()})).collect(Collectors.joining((CharSequence)",")));
            $$15.setDetail("Max Texture Size", $$3);
            throw new ReportedException($$14);
        }
        Map<ResourceLocation, TextureAtlasSprite> $$16 = this.getStitchedSprites($$4);
        TextureAtlasSprite $$17 = (TextureAtlasSprite)$$16.get((Object)MissingTextureAtlasSprite.getLocation());
        if ($$12 > 0) {
            CompletableFuture $$18 = CompletableFuture.runAsync(() -> $$16.values().forEach($$1 -> $$1.contents().increaseMipLevel($$12)), (Executor)$$2);
        } else {
            $$19 = CompletableFuture.completedFuture(null);
        }
        return new Preparations($$4.getWidth(), $$4.getHeight(), $$12, $$17, $$16, (CompletableFuture<Void>)$$19);
    }

    public static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(List<Supplier<SpriteContents>> $$02, Executor $$12) {
        List $$2 = $$02.stream().map($$1 -> CompletableFuture.supplyAsync((Supplier)$$1, (Executor)$$12)).toList();
        return Util.sequence($$2).thenApply($$0 -> $$0.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<Preparations> loadAndStitch(ResourceManager $$0, ResourceLocation $$12, int $$22, Executor $$3) {
        return CompletableFuture.supplyAsync(() -> SpriteResourceLoader.load($$0, $$12).list($$0), (Executor)$$3).thenCompose($$1 -> SpriteLoader.runSpriteSuppliers((List<Supplier<SpriteContents>>)$$1, $$3)).thenApply($$2 -> this.stitch((List<SpriteContents>)$$2, $$22, $$3));
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static SpriteContents loadSprite(ResourceLocation $$0, Resource $$1) {
        void $$9;
        void $$4;
        try {
            AnimationMetadataSection $$2 = (AnimationMetadataSection)$$1.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse((Object)AnimationMetadataSection.EMPTY);
        }
        catch (Exception $$3) {
            LOGGER.error("Unable to parse metadata from {}", (Object)$$0, (Object)$$3);
            return null;
        }
        try (InputStream $$5 = $$1.open();){
            NativeImage $$6 = NativeImage.read($$5);
        }
        catch (IOException $$8) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)$$0, (Object)$$8);
            return null;
        }
        FrameSize $$10 = $$4.calculateFrameSize($$9.getWidth(), $$9.getHeight());
        if (!Mth.isDivisionInteger($$9.getWidth(), $$10.width()) || !Mth.isDivisionInteger($$9.getHeight(), $$10.height())) {
            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{$$0, $$9.getWidth(), $$9.getHeight(), $$10.width(), $$10.height()});
            $$9.close();
            return null;
        }
        return new SpriteContents($$0, $$10, (NativeImage)$$9, (AnimationMetadataSection)$$4);
    }

    private Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> $$0) {
        HashMap $$1 = new HashMap();
        int $$2 = $$0.getWidth();
        int $$3 = $$0.getHeight();
        $$0.gatherSprites((arg_0, arg_1, arg_2) -> this.lambda$getStitchedSprites$8((Map)$$1, $$2, $$3, arg_0, arg_1, arg_2));
        return $$1;
    }

    private /* synthetic */ void lambda$getStitchedSprites$8(Map $$0, int $$1, int $$2, SpriteContents $$3, int $$4, int $$5) {
        $$0.put((Object)$$3.name(), (Object)new TextureAtlasSprite(this.location, $$3, $$1, $$2, $$4, $$5));
    }

    public record Preparations(int width, int height, int mipLevel, TextureAtlasSprite missing, Map<ResourceLocation, TextureAtlasSprite> regions, CompletableFuture<Void> readyForUpload) {
        public CompletableFuture<Preparations> waitForUpload() {
            return this.readyForUpload.thenApply($$0 -> this);
        }
    }
}