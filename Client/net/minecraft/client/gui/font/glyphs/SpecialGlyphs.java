/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;

public enum SpecialGlyphs implements GlyphInfo
{
    WHITE((Supplier<NativeImage>)((Supplier)() -> SpecialGlyphs.generate(5, 8, ($$0, $$1) -> -1))),
    MISSING((Supplier<NativeImage>)((Supplier)() -> {
        int $$02 = 5;
        int $$12 = 8;
        return SpecialGlyphs.generate(5, 8, ($$0, $$1) -> {
            boolean $$2 = $$0 == 0 || $$0 + 1 == 5 || $$1 == 0 || $$1 + 1 == 8;
            return $$2 ? -1 : 0;
        });
    }));

    final NativeImage image;

    private static NativeImage generate(int $$0, int $$1, PixelProvider $$2) {
        NativeImage $$3 = new NativeImage(NativeImage.Format.RGBA, $$0, $$1, false);
        for (int $$4 = 0; $$4 < $$1; ++$$4) {
            for (int $$5 = 0; $$5 < $$0; ++$$5) {
                $$3.setPixelRGBA($$5, $$4, $$2.getColor($$5, $$4));
            }
        }
        $$3.untrack();
        return $$3;
    }

    private SpecialGlyphs(Supplier<NativeImage> $$0) {
        this.image = (NativeImage)$$0.get();
    }

    @Override
    public float getAdvance() {
        return this.image.getWidth() + 1;
    }

    @Override
    public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
        return (BakedGlyph)$$0.apply((Object)new SheetGlyphInfo(){

            @Override
            public int getPixelWidth() {
                return SpecialGlyphs.this.image.getWidth();
            }

            @Override
            public int getPixelHeight() {
                return SpecialGlyphs.this.image.getHeight();
            }

            @Override
            public float getOversample() {
                return 1.0f;
            }

            @Override
            public void upload(int $$0, int $$1) {
                SpecialGlyphs.this.image.upload(0, $$0, $$1, false);
            }

            @Override
            public boolean isColored() {
                return true;
            }
        });
    }

    @FunctionalInterface
    static interface PixelProvider {
        public int getColor(int var1, int var2);
    }
}