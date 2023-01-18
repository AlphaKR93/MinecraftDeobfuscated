/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Object
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import org.slf4j.Logger;

public class ProgramManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void glUseProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram($$0);
    }

    public static void releaseProgram(Shader $$0) {
        RenderSystem.assertOnRenderThread();
        $$0.getFragmentProgram().close();
        $$0.getVertexProgram().close();
        GlStateManager.glDeleteProgram($$0.getId());
    }

    public static int createProgram() throws IOException {
        RenderSystem.assertOnRenderThread();
        int $$0 = GlStateManager.glCreateProgram();
        if ($$0 <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + $$0 + ")");
        }
        return $$0;
    }

    public static void linkShader(Shader $$0) {
        RenderSystem.assertOnRenderThread();
        $$0.attachToProgram();
        GlStateManager.glLinkProgram($$0.getId());
        int $$1 = GlStateManager.glGetProgrami($$0.getId(), 35714);
        if ($$1 == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", (Object)$$0.getVertexProgram().getName(), (Object)$$0.getFragmentProgram().getName());
            LOGGER.warn(GlStateManager.glGetProgramInfoLog($$0.getId(), 32768));
        }
    }
}