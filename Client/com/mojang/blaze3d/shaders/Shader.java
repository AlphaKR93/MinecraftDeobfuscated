/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.shaders.Program;

public interface Shader {
    public int getId();

    public void markDirty();

    public Program getVertexProgram();

    public Program getFragmentProgram();

    public void attachToProgram();
}