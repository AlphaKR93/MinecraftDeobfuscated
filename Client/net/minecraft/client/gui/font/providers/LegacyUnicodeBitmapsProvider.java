/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.IllegalFormatException
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class LegacyUnicodeBitmapsProvider
implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int UNICODE_SHEETS = 256;
    private static final int CODEPOINTS_PER_SHEET = 256;
    private static final int TEXTURE_SIZE = 256;
    private static final byte NO_GLYPH = 0;
    private static final int TOTAL_CODEPOINTS = 65536;
    private final byte[] sizes;
    private final Sheet[] sheets = new Sheet[256];

    public LegacyUnicodeBitmapsProvider(ResourceManager $$0, byte[] $$1, String $$2) {
        this.sizes = $$1;
        HashSet $$32 = new HashSet();
        for (int $$4 = 0; $$4 < 256; ++$$4) {
            int $$5 = $$4 * 256;
            $$32.add((Object)LegacyUnicodeBitmapsProvider.getSheetLocation($$2, $$5));
        }
        String $$6 = LegacyUnicodeBitmapsProvider.getCommonSearchPrefix((Set<ResourceLocation>)$$32);
        HashMap $$7 = new HashMap();
        $$0.listResources($$6, (Predicate<ResourceLocation>)((Predicate)arg_0 -> ((Set)$$32).contains(arg_0))).forEach((arg_0, arg_1) -> LegacyUnicodeBitmapsProvider.lambda$new$1((Map)$$7, arg_0, arg_1));
        ArrayList $$8 = new ArrayList(256);
        for (int $$9 = 0; $$9 < 256; ++$$9) {
            int $$10 = $$9 * 256;
            int $$11 = $$9;
            ResourceLocation $$12 = LegacyUnicodeBitmapsProvider.getSheetLocation($$2, $$10);
            CompletableFuture $$13 = (CompletableFuture)$$7.get((Object)$$12);
            if ($$13 == null) continue;
            $$8.add((Object)$$13.thenAcceptAsync($$3 -> {
                if ($$3 == null) {
                    return;
                }
                if ($$3.getWidth() == 256 && $$3.getHeight() == 256) {
                    for (int $$4 = 0; $$4 < 256; ++$$4) {
                        byte $$5 = $$1[$$10 + $$4];
                        if ($$5 == 0 || LegacyUnicodeBitmapsProvider.getLeft($$5) <= LegacyUnicodeBitmapsProvider.getRight($$5)) continue;
                        $$0[$$1 + $$4] = 0;
                    }
                    this.sheets[$$2] = new Sheet($$1, (NativeImage)$$3);
                } else {
                    $$3.close();
                    Arrays.fill((byte[])$$1, (int)$$10, (int)($$10 + 256), (byte)0);
                }
            }, (Executor)Util.backgroundExecutor()));
        }
        CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$8.toArray(CompletableFuture[]::new))).join();
    }

    private static String getCommonSearchPrefix(Set<ResourceLocation> $$0) {
        String $$1 = StringUtils.getCommonPrefix((String[])((String[])$$0.stream().map(ResourceLocation::getPath).toArray(String[]::new)));
        int $$2 = $$1.lastIndexOf("/");
        if ($$2 == -1) {
            return "";
        }
        return $$1.substring(0, $$2);
    }

    @Override
    public void close() {
        for (Sheet $$0 : this.sheets) {
            if ($$0 == null) continue;
            $$0.close();
        }
    }

    private static ResourceLocation getSheetLocation(String $$0, int $$1) {
        String $$2 = String.format((Locale)Locale.ROOT, (String)"%02x", (Object[])new Object[]{$$1 / 256});
        ResourceLocation $$3 = new ResourceLocation(String.format((Locale)Locale.ROOT, (String)$$0, (Object[])new Object[]{$$2}));
        return $$3.withPrefix("textures/");
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        if ($$0 < 0 || $$0 >= this.sizes.length) {
            return null;
        }
        int $$1 = $$0 / 256;
        Sheet $$2 = this.sheets[$$1];
        return $$2 != null ? $$2.getGlyph($$0) : null;
    }

    @Override
    public IntSet getSupportedGlyphs() {
        IntOpenHashSet $$0 = new IntOpenHashSet();
        for (int $$1 = 0; $$1 < this.sizes.length; ++$$1) {
            if (this.sizes[$$1] == 0) continue;
            $$0.add($$1);
        }
        return $$0;
    }

    static int getLeft(byte $$0) {
        return $$0 >> 4 & 0xF;
    }

    static int getRight(byte $$0) {
        return ($$0 & 0xF) + 1;
    }

    private static /* synthetic */ void lambda$new$1(Map $$0, ResourceLocation $$1, Resource $$2) {
        $$0.put((Object)$$1, (Object)CompletableFuture.supplyAsync(() -> {
            NativeImage nativeImage;
            block8: {
                InputStream $$2 = $$2.open();
                try {
                    nativeImage = NativeImage.read(NativeImage.Format.RGBA, $$2);
                    if ($$2 == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if ($$2 != null) {
                            try {
                                $$2.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException $$3) {
                        LOGGER.error("Failed to read resource {} from pack {}", (Object)$$1, (Object)$$2.sourcePackId());
                        return null;
                    }
                }
                $$2.close();
            }
            return nativeImage;
        }, (Executor)Util.backgroundExecutor()));
    }

    static class Sheet
    implements AutoCloseable {
        private final byte[] sizes;
        private final NativeImage source;

        Sheet(byte[] $$0, NativeImage $$1) {
            this.sizes = $$0;
            this.source = $$1;
        }

        public void close() {
            this.source.close();
        }

        @Nullable
        public GlyphInfo getGlyph(int $$0) {
            byte $$1 = this.sizes[$$0];
            if ($$1 != 0) {
                int $$2 = LegacyUnicodeBitmapsProvider.getLeft($$1);
                return new Glyph($$0 % 16 * 16 + $$2, ($$0 & 0xFF) / 16 * 16, LegacyUnicodeBitmapsProvider.getRight($$1) - $$2, 16, this.source);
            }
            return null;
        }
    }

    record Glyph(int sourceX, int sourceY, int width, int height, NativeImage source) implements GlyphInfo
    {
        @Override
        public float getAdvance() {
            return this.width / 2 + 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return (BakedGlyph)$$0.apply((Object)new SheetGlyphInfo(){

                @Override
                public float getOversample() {
                    return 2.0f;
                }

                @Override
                public int getPixelWidth() {
                    return width;
                }

                @Override
                public int getPixelHeight() {
                    return height;
                }

                @Override
                public void upload(int $$0, int $$1) {
                    source.upload(0, $$0, $$1, sourceX, sourceY, width, height, false, false);
                }

                @Override
                public boolean isColored() {
                    return source.format().components() > 1;
                }
            });
        }
    }

    public static class Builder
    implements GlyphProviderBuilder {
        private final ResourceLocation metadata;
        private final String texturePattern;

        public Builder(ResourceLocation $$0, String $$1) {
            this.metadata = $$0;
            this.texturePattern = $$1;
        }

        public static GlyphProviderBuilder fromJson(JsonObject $$0) {
            return new Builder(new ResourceLocation(GsonHelper.getAsString($$0, "sizes")), Builder.getTemplate($$0));
        }

        private static String getTemplate(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "template");
            try {
                String.format((Locale)Locale.ROOT, (String)$$1, (Object[])new Object[]{""});
            }
            catch (IllegalFormatException $$2) {
                throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + $$1);
            }
            return $$1;
        }

        @Override
        @Nullable
        public GlyphProvider create(ResourceManager $$0) {
            LegacyUnicodeBitmapsProvider legacyUnicodeBitmapsProvider;
            block8: {
                InputStream $$1 = Minecraft.getInstance().getResourceManager().open(this.metadata);
                try {
                    byte[] $$2 = $$1.readNBytes(65536);
                    legacyUnicodeBitmapsProvider = new LegacyUnicodeBitmapsProvider($$0, $$2, this.texturePattern);
                    if ($$1 == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
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
                    catch (IOException $$3) {
                        LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.metadata);
                        return null;
                    }
                }
                $$1.close();
            }
            return legacyUnicodeBitmapsProvider;
        }
    }
}