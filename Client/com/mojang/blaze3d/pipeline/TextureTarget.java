/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;

public class TextureTarget
extends RenderTarget {
    public TextureTarget(int $$0, int $$1, boolean $$2, boolean $$3) {
        super($$2);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize($$0, $$1, $$3);
    }
}