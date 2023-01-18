/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringDecomposer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Font {
    private static final float EFFECT_DEPTH = 0.01f;
    private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0f, 0.0f, 0.03f);
    public static final int ALPHA_CUTOFF = 8;
    public final int lineHeight = 9;
    public final RandomSource random = RandomSource.create();
    private final Function<ResourceLocation, FontSet> fonts;
    final boolean filterFishyGlyphs;
    private final StringSplitter splitter;

    public Font(Function<ResourceLocation, FontSet> $$02, boolean $$12) {
        this.fonts = $$02;
        this.filterFishyGlyphs = $$12;
        this.splitter = new StringSplitter(($$0, $$1) -> this.getFontSet($$1.getFont()).getGlyphInfo($$0, this.filterFishyGlyphs).getAdvance($$1.isBold()));
    }

    FontSet getFontSet(ResourceLocation $$0) {
        return (FontSet)this.fonts.apply((Object)$$0);
    }

    public int drawShadow(PoseStack $$0, String $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1, $$2, $$3, $$4, $$0.last().pose(), true, this.isBidirectional());
    }

    public int drawShadow(PoseStack $$0, String $$1, float $$2, float $$3, int $$4, boolean $$5) {
        return this.drawInternal($$1, $$2, $$3, $$4, $$0.last().pose(), true, $$5);
    }

    public int draw(PoseStack $$0, String $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1, $$2, $$3, $$4, $$0.last().pose(), false, this.isBidirectional());
    }

    public int drawShadow(PoseStack $$0, FormattedCharSequence $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1, $$2, $$3, $$4, $$0.last().pose(), true);
    }

    public int drawShadow(PoseStack $$0, Component $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1.getVisualOrderText(), $$2, $$3, $$4, $$0.last().pose(), true);
    }

    public int draw(PoseStack $$0, FormattedCharSequence $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1, $$2, $$3, $$4, $$0.last().pose(), false);
    }

    public int draw(PoseStack $$0, Component $$1, float $$2, float $$3, int $$4) {
        return this.drawInternal($$1.getVisualOrderText(), $$2, $$3, $$4, $$0.last().pose(), false);
    }

    public String bidirectionalShaping(String $$0) {
        try {
            Bidi $$1 = new Bidi(new ArabicShaping(8).shape($$0), 127);
            $$1.setReorderingMode(0);
            return $$1.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return $$0;
        }
    }

    private int drawInternal(String $$0, float $$1, float $$2, int $$3, Matrix4f $$4, boolean $$5, boolean $$6) {
        if ($$0 == null) {
            return 0;
        }
        MultiBufferSource.BufferSource $$7 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int $$8 = this.drawInBatch($$0, $$1, $$2, $$3, $$5, $$4, $$7, false, 0, 0xF000F0, $$6);
        $$7.endBatch();
        return $$8;
    }

    private int drawInternal(FormattedCharSequence $$0, float $$1, float $$2, int $$3, Matrix4f $$4, boolean $$5) {
        MultiBufferSource.BufferSource $$6 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int $$7 = this.drawInBatch($$0, $$1, $$2, $$3, $$5, $$4, (MultiBufferSource)$$6, false, 0, 0xF000F0);
        $$6.endBatch();
        return $$7;
    }

    public int drawInBatch(String $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        return this.drawInBatch($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, this.isBidirectional());
    }

    public int drawInBatch(String $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9, boolean $$10) {
        return this.drawInternal($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
    }

    public int drawInBatch(Component $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        return this.drawInBatch($$0.getVisualOrderText(), $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
    }

    public int drawInBatch(FormattedCharSequence $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        return this.drawInternal($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
    }

    public void drawInBatch8xOutline(FormattedCharSequence $$0, float $$1, float $$2, int $$3, int $$4, Matrix4f $$5, MultiBufferSource $$62, int $$72) {
        int $$82 = Font.adjustColor($$4);
        StringRenderOutput $$9 = new StringRenderOutput($$62, 0.0f, 0.0f, $$82, false, $$5, DisplayMode.NORMAL, $$72);
        for (int $$10 = -1; $$10 <= 1; ++$$10) {
            for (int $$11 = -1; $$11 <= 1; ++$$11) {
                if ($$10 == 0 && $$11 == 0) continue;
                float[] $$12 = new float[]{$$1};
                int $$13 = $$10;
                int $$14 = $$11;
                $$0.accept(($$6, $$7, $$8) -> {
                    boolean $$9 = $$7.isBold();
                    FontSet $$10 = this.getFontSet($$7.getFont());
                    GlyphInfo $$11 = $$10.getGlyphInfo($$8, this.filterFishyGlyphs);
                    $$0.x = $$12[0] + (float)$$13 * $$11.getShadowOffset();
                    $$0.y = $$2 + (float)$$14 * $$11.getShadowOffset();
                    $$1[0] = $$12[0] + $$11.getAdvance($$9);
                    return $$9.accept($$6, $$7.withColor($$82), $$8);
                });
            }
        }
        StringRenderOutput $$15 = new StringRenderOutput($$62, $$1, $$2, Font.adjustColor($$3), false, $$5, DisplayMode.POLYGON_OFFSET, $$72);
        $$0.accept($$15);
        $$15.finish(0, $$1);
    }

    private static int adjustColor(int $$0) {
        if (($$0 & 0xFC000000) == 0) {
            return $$0 | 0xFF000000;
        }
        return $$0;
    }

    private int drawInternal(String $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9, boolean $$10) {
        if ($$10) {
            $$0 = this.bidirectionalShaping($$0);
        }
        $$3 = Font.adjustColor($$3);
        Matrix4f $$11 = new Matrix4f((Matrix4fc)$$5);
        if ($$4) {
            this.renderText($$0, $$1, $$2, $$3, true, $$5, $$6, $$7, $$8, $$9);
            $$11.translate((Vector3fc)SHADOW_OFFSET);
        }
        $$1 = this.renderText($$0, $$1, $$2, $$3, false, $$11, $$6, $$7, $$8, $$9);
        return (int)$$1 + ($$4 ? 1 : 0);
    }

    private int drawInternal(FormattedCharSequence $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        $$3 = Font.adjustColor($$3);
        Matrix4f $$10 = new Matrix4f((Matrix4fc)$$5);
        if ($$4) {
            this.renderText($$0, $$1, $$2, $$3, true, $$5, $$6, $$7, $$8, $$9);
            $$10.translate((Vector3fc)SHADOW_OFFSET);
        }
        $$1 = this.renderText($$0, $$1, $$2, $$3, false, $$10, $$6, $$7, $$8, $$9);
        return (int)$$1 + ($$4 ? 1 : 0);
    }

    private float renderText(String $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        StringRenderOutput $$10 = new StringRenderOutput($$6, $$1, $$2, $$3, $$4, $$5, $$7, $$9);
        StringDecomposer.iterateFormatted($$0, Style.EMPTY, (FormattedCharSink)$$10);
        return $$10.finish($$8, $$1);
    }

    private float renderText(FormattedCharSequence $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, boolean $$7, int $$8, int $$9) {
        StringRenderOutput $$10 = new StringRenderOutput($$6, $$1, $$2, $$3, $$4, $$5, $$7, $$9);
        $$0.accept($$10);
        return $$10.finish($$8, $$1);
    }

    void renderChar(BakedGlyph $$0, boolean $$1, boolean $$2, float $$3, float $$4, float $$5, Matrix4f $$6, VertexConsumer $$7, float $$8, float $$9, float $$10, float $$11, int $$12) {
        $$0.render($$2, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12);
        if ($$1) {
            $$0.render($$2, $$4 + $$3, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12);
        }
    }

    public int width(String $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public int width(FormattedText $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public int width(FormattedCharSequence $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public String plainSubstrByWidth(String $$0, int $$1, boolean $$2) {
        return $$2 ? this.splitter.plainTailByWidth($$0, $$1, Style.EMPTY) : this.splitter.plainHeadByWidth($$0, $$1, Style.EMPTY);
    }

    public String plainSubstrByWidth(String $$0, int $$1) {
        return this.splitter.plainHeadByWidth($$0, $$1, Style.EMPTY);
    }

    public FormattedText substrByWidth(FormattedText $$0, int $$1) {
        return this.splitter.headByWidth($$0, $$1, Style.EMPTY);
    }

    public void drawWordWrap(FormattedText $$0, int $$1, int $$2, int $$3, int $$4) {
        Matrix4f $$5 = Transformation.identity().getMatrix();
        for (FormattedCharSequence $$6 : this.split($$0, $$3)) {
            this.drawInternal($$6, $$1, $$2, $$4, $$5, false);
            $$2 += 9;
        }
    }

    public int wordWrapHeight(String $$0, int $$1) {
        return 9 * this.splitter.splitLines($$0, $$1, Style.EMPTY).size();
    }

    public int wordWrapHeight(FormattedText $$0, int $$1) {
        return 9 * this.splitter.splitLines($$0, $$1, Style.EMPTY).size();
    }

    public List<FormattedCharSequence> split(FormattedText $$0, int $$1) {
        return Language.getInstance().getVisualOrder(this.splitter.splitLines($$0, $$1, Style.EMPTY));
    }

    public boolean isBidirectional() {
        return Language.getInstance().isDefaultRightToLeft();
    }

    public StringSplitter getSplitter() {
        return this.splitter;
    }

    class StringRenderOutput
    implements FormattedCharSink {
        final MultiBufferSource bufferSource;
        private final boolean dropShadow;
        private final float dimFactor;
        private final float r;
        private final float g;
        private final float b;
        private final float a;
        private final Matrix4f pose;
        private final DisplayMode mode;
        private final int packedLightCoords;
        float x;
        float y;
        @Nullable
        private List<BakedGlyph.Effect> effects;

        private void addEffect(BakedGlyph.Effect $$0) {
            if (this.effects == null) {
                this.effects = Lists.newArrayList();
            }
            this.effects.add((Object)$$0);
        }

        public StringRenderOutput(MultiBufferSource $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, boolean $$6, int $$7) {
            this($$0, $$1, $$2, $$3, $$4, $$5, $$6 ? DisplayMode.SEE_THROUGH : DisplayMode.NORMAL, $$7);
        }

        public StringRenderOutput(MultiBufferSource $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, DisplayMode $$6, int $$7) {
            this.bufferSource = $$0;
            this.x = $$1;
            this.y = $$2;
            this.dropShadow = $$4;
            this.dimFactor = $$4 ? 0.25f : 1.0f;
            this.r = (float)($$3 >> 16 & 0xFF) / 255.0f * this.dimFactor;
            this.g = (float)($$3 >> 8 & 0xFF) / 255.0f * this.dimFactor;
            this.b = (float)($$3 & 0xFF) / 255.0f * this.dimFactor;
            this.a = (float)($$3 >> 24 & 0xFF) / 255.0f;
            this.pose = $$5;
            this.mode = $$6;
            this.packedLightCoords = $$7;
        }

        @Override
        public boolean accept(int $$0, Style $$1, int $$2) {
            float $$20;
            float $$15;
            float $$14;
            float $$13;
            FontSet $$3 = Font.this.getFontSet($$1.getFont());
            GlyphInfo $$4 = $$3.getGlyphInfo($$2, Font.this.filterFishyGlyphs);
            BakedGlyph $$5 = $$1.isObfuscated() && $$2 != 32 ? $$3.getRandomGlyph($$4) : $$3.getGlyph($$2);
            boolean $$6 = $$1.isBold();
            float $$7 = this.a;
            TextColor $$8 = $$1.getColor();
            if ($$8 != null) {
                int $$9 = $$8.getValue();
                float $$10 = (float)($$9 >> 16 & 0xFF) / 255.0f * this.dimFactor;
                float $$11 = (float)($$9 >> 8 & 0xFF) / 255.0f * this.dimFactor;
                float $$12 = (float)($$9 & 0xFF) / 255.0f * this.dimFactor;
            } else {
                $$13 = this.r;
                $$14 = this.g;
                $$15 = this.b;
            }
            if (!($$5 instanceof EmptyGlyph)) {
                float $$16 = $$6 ? $$4.getBoldOffset() : 0.0f;
                float $$17 = this.dropShadow ? $$4.getShadowOffset() : 0.0f;
                VertexConsumer $$18 = this.bufferSource.getBuffer($$5.renderType(this.mode));
                Font.this.renderChar($$5, $$6, $$1.isItalic(), $$16, this.x + $$17, this.y + $$17, this.pose, $$18, $$13, $$14, $$15, $$7, this.packedLightCoords);
            }
            float $$19 = $$4.getAdvance($$6);
            float f = $$20 = this.dropShadow ? 1.0f : 0.0f;
            if ($$1.isStrikethrough()) {
                this.addEffect(new BakedGlyph.Effect(this.x + $$20 - 1.0f, this.y + $$20 + 4.5f, this.x + $$20 + $$19, this.y + $$20 + 4.5f - 1.0f, 0.01f, $$13, $$14, $$15, $$7));
            }
            if ($$1.isUnderlined()) {
                this.addEffect(new BakedGlyph.Effect(this.x + $$20 - 1.0f, this.y + $$20 + 9.0f, this.x + $$20 + $$19, this.y + $$20 + 9.0f - 1.0f, 0.01f, $$13, $$14, $$15, $$7));
            }
            this.x += $$19;
            return true;
        }

        public float finish(int $$0, float $$1) {
            if ($$0 != 0) {
                float $$2 = (float)($$0 >> 24 & 0xFF) / 255.0f;
                float $$3 = (float)($$0 >> 16 & 0xFF) / 255.0f;
                float $$4 = (float)($$0 >> 8 & 0xFF) / 255.0f;
                float $$5 = (float)($$0 & 0xFF) / 255.0f;
                this.addEffect(new BakedGlyph.Effect($$1 - 1.0f, this.y + 9.0f, this.x + 1.0f, this.y - 1.0f, 0.01f, $$3, $$4, $$5, $$2));
            }
            if (this.effects != null) {
                BakedGlyph $$6 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
                VertexConsumer $$7 = this.bufferSource.getBuffer($$6.renderType(this.mode));
                for (BakedGlyph.Effect $$8 : this.effects) {
                    $$6.renderEffect($$8, this.pose, $$7, this.packedLightCoords);
                }
            }
            return this.x;
        }
    }

    public static enum DisplayMode {
        NORMAL,
        SEE_THROUGH,
        POLYGON_OFFSET;

    }
}