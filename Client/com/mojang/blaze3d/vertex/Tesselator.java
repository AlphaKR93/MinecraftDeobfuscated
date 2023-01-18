/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;

public class Tesselator {
    private static final int MAX_MEMORY_USE = 0x800000;
    private static final int MAX_FLOATS = 0x200000;
    private final BufferBuilder builder;
    private static final Tesselator INSTANCE = new Tesselator();

    public static Tesselator getInstance() {
        RenderSystem.assertOnGameThreadOrInit();
        return INSTANCE;
    }

    public Tesselator(int $$0) {
        this.builder = new BufferBuilder($$0);
    }

    public Tesselator() {
        this(0x200000);
    }

    public void end() {
        BufferUploader.drawWithShader(this.builder.end());
    }

    public BufferBuilder getBuilder() {
        return this.builder;
    }
}