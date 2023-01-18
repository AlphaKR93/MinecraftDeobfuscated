/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;

public class EffectProgram
extends Program {
    private static final GlslPreprocessor PREPROCESSOR = new GlslPreprocessor(){

        @Override
        public String applyImport(boolean $$0, String $$1) {
            return "#error Import statement not supported";
        }
    };
    private int references;

    private EffectProgram(Program.Type $$0, int $$1, String $$2) {
        super($$0, $$1, $$2);
    }

    public void attachToEffect(Effect $$0) {
        RenderSystem.assertOnRenderThread();
        ++this.references;
        this.attachToShader($$0);
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThread();
        --this.references;
        if (this.references <= 0) {
            super.close();
        }
    }

    public static EffectProgram compileShader(Program.Type $$0, String $$1, InputStream $$2, String $$3) throws IOException {
        RenderSystem.assertOnRenderThread();
        int $$4 = EffectProgram.compileShaderInternal($$0, $$1, $$2, $$3, PREPROCESSOR);
        EffectProgram $$5 = new EffectProgram($$0, $$4, $$1);
        $$0.getPrograms().put((Object)$$1, (Object)$$5);
        return $$5;
    }
}