/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphInfo;
import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;

public interface GlyphProvider
extends AutoCloseable {
    default public void close() {
    }

    @Nullable
    default public GlyphInfo getGlyph(int $$0) {
        return null;
    }

    public IntSet getSupportedGlyphs();
}