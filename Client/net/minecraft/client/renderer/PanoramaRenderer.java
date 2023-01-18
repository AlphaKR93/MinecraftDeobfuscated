/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.util.Mth;

public class PanoramaRenderer {
    private final Minecraft minecraft;
    private final CubeMap cubeMap;
    private float spin;
    private float bob;

    public PanoramaRenderer(CubeMap $$0) {
        this.cubeMap = $$0;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(float $$0, float $$1) {
        float $$2 = (float)((double)$$0 * this.minecraft.options.panoramaSpeed().get());
        this.spin = PanoramaRenderer.wrap(this.spin + $$2 * 0.1f, 360.0f);
        this.bob = PanoramaRenderer.wrap(this.bob + $$2 * 0.001f, (float)Math.PI * 2);
        this.cubeMap.render(this.minecraft, Mth.sin(this.bob) * 5.0f + 25.0f, -this.spin, $$1);
    }

    private static float wrap(float $$0, float $$1) {
        return $$0 > $$1 ? $$0 - $$1 : $$0;
    }
}