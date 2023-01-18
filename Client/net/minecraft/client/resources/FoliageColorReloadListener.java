/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 */
package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.FoliageColor;

public class FoliageColorReloadListener
extends SimplePreparableReloadListener<int[]> {
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/colormap/foliage.png");

    @Override
    protected int[] prepare(ResourceManager $$0, ProfilerFiller $$1) {
        try {
            return LegacyStuffWrapper.getPixels($$0, LOCATION);
        }
        catch (IOException $$2) {
            throw new IllegalStateException("Failed to load foliage color texture", (Throwable)$$2);
        }
    }

    @Override
    protected void apply(int[] $$0, ResourceManager $$1, ProfilerFiller $$2) {
        FoliageColor.init($$0);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }
}