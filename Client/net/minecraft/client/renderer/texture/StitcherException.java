/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Locale
 */
package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.renderer.texture.Stitcher;

public class StitcherException
extends RuntimeException {
    private final Collection<Stitcher.Entry> allSprites;

    public StitcherException(Stitcher.Entry $$0, Collection<Stitcher.Entry> $$1) {
        super(String.format((Locale)Locale.ROOT, (String)"Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", (Object[])new Object[]{$$0.name(), $$0.width(), $$0.height()}));
        this.allSprites = $$1;
    }

    public Collection<Stitcher.Entry> getAllSprites() {
        return this.allSprites;
    }
}