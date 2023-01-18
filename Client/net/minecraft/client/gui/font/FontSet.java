/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FontSet
implements AutoCloseable {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final float LARGE_FORWARD_ADVANCE = 32.0f;
    private final TextureManager textureManager;
    private final ResourceLocation name;
    private BakedGlyph missingGlyph;
    private BakedGlyph whiteGlyph;
    private final List<GlyphProvider> providers = Lists.newArrayList();
    private final Int2ObjectMap<BakedGlyph> glyphs = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<GlyphInfoFilter> glyphInfos = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
    private final List<FontTexture> textures = Lists.newArrayList();

    public FontSet(TextureManager $$0, ResourceLocation $$1) {
        this.textureManager = $$0;
        this.name = $$1;
    }

    public void reload(List<GlyphProvider> $$0) {
        this.closeProviders();
        this.closeTextures();
        this.glyphs.clear();
        this.glyphInfos.clear();
        this.glyphsByWidth.clear();
        this.missingGlyph = SpecialGlyphs.MISSING.bake((Function<SheetGlyphInfo, BakedGlyph>)((Function)this::stitch));
        this.whiteGlyph = SpecialGlyphs.WHITE.bake((Function<SheetGlyphInfo, BakedGlyph>)((Function)this::stitch));
        IntOpenHashSet $$1 = new IntOpenHashSet();
        for (GlyphProvider $$2 : $$0) {
            $$1.addAll((IntCollection)$$2.getSupportedGlyphs());
        }
        HashSet $$3 = Sets.newHashSet();
        $$1.forEach(arg_0 -> this.lambda$reload$1($$0, (Set)$$3, arg_0));
        $$0.stream().filter(arg_0 -> ((Set)$$3).contains(arg_0)).forEach(arg_0 -> this.providers.add(arg_0));
    }

    public void close() {
        this.closeProviders();
        this.closeTextures();
    }

    private void closeProviders() {
        for (GlyphProvider $$0 : this.providers) {
            $$0.close();
        }
        this.providers.clear();
    }

    private void closeTextures() {
        for (FontTexture $$0 : this.textures) {
            $$0.close();
        }
        this.textures.clear();
    }

    private static boolean hasFishyAdvance(GlyphInfo $$0) {
        float $$1 = $$0.getAdvance(false);
        if ($$1 < 0.0f || $$1 > 32.0f) {
            return true;
        }
        float $$2 = $$0.getAdvance(true);
        return $$2 < 0.0f || $$2 > 32.0f;
    }

    private GlyphInfoFilter computeGlyphInfo(int $$0) {
        GlyphInfo $$1 = null;
        for (GlyphProvider $$2 : this.providers) {
            GlyphInfo $$3 = $$2.getGlyph($$0);
            if ($$3 == null) continue;
            if ($$1 == null) {
                $$1 = $$3;
            }
            if (FontSet.hasFishyAdvance($$3)) continue;
            return new GlyphInfoFilter($$1, $$3);
        }
        if ($$1 != null) {
            return new GlyphInfoFilter($$1, SpecialGlyphs.MISSING);
        }
        return GlyphInfoFilter.MISSING;
    }

    public GlyphInfo getGlyphInfo(int $$0, boolean $$1) {
        return ((GlyphInfoFilter)((Object)this.glyphInfos.computeIfAbsent($$0, this::computeGlyphInfo))).select($$1);
    }

    private BakedGlyph computeBakedGlyph(int $$0) {
        for (GlyphProvider $$1 : this.providers) {
            GlyphInfo $$2 = $$1.getGlyph($$0);
            if ($$2 == null) continue;
            return $$2.bake((Function<SheetGlyphInfo, BakedGlyph>)((Function)this::stitch));
        }
        return this.missingGlyph;
    }

    public BakedGlyph getGlyph(int $$0) {
        return (BakedGlyph)this.glyphs.computeIfAbsent($$0, this::computeBakedGlyph);
    }

    private BakedGlyph stitch(SheetGlyphInfo $$02) {
        for (FontTexture $$1 : this.textures) {
            BakedGlyph $$2 = $$1.add($$02);
            if ($$2 == null) continue;
            return $$2;
        }
        FontTexture $$3 = new FontTexture(this.name.withPath((UnaryOperator<String>)((UnaryOperator)$$0 -> $$0 + "/" + this.textures.size())), $$02.isColored());
        this.textures.add((Object)$$3);
        this.textureManager.register($$3.getName(), $$3);
        BakedGlyph $$4 = $$3.add($$02);
        return $$4 == null ? this.missingGlyph : $$4;
    }

    public BakedGlyph getRandomGlyph(GlyphInfo $$0) {
        IntList $$1 = (IntList)this.glyphsByWidth.get(Mth.ceil($$0.getAdvance(false)));
        if ($$1 != null && !$$1.isEmpty()) {
            return this.getGlyph($$1.getInt(RANDOM.nextInt($$1.size())));
        }
        return this.missingGlyph;
    }

    public BakedGlyph whiteGlyph() {
        return this.whiteGlyph;
    }

    private /* synthetic */ void lambda$reload$1(List $$02, Set $$1, int $$2) {
        for (GlyphProvider $$3 : $$02) {
            GlyphInfo $$4 = $$3.getGlyph($$2);
            if ($$4 == null) continue;
            $$1.add((Object)$$3);
            if ($$4 == SpecialGlyphs.MISSING) break;
            ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil($$4.getAdvance(false)), $$0 -> new IntArrayList())).add($$2);
            break;
        }
    }

    record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
        static final GlyphInfoFilter MISSING = new GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);

        GlyphInfo select(boolean $$0) {
            return $$0 ? this.glyphInfoNotFishy : this.glyphInfo;
        }
    }
}