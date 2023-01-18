/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.util.Map
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class Program {
    private static final int MAX_LOG_LENGTH = 32768;
    private final Type type;
    private final String name;
    private int id;

    protected Program(Type $$0, int $$1, String $$2) {
        this.type = $$0;
        this.id = $$1;
        this.name = $$2;
    }

    public void attachToShader(Shader $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.glAttachShader($$0.getId(), this.getId());
    }

    public void close() {
        if (this.id == -1) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.id);
        this.id = -1;
        this.type.getPrograms().remove((Object)this.name);
    }

    public String getName() {
        return this.name;
    }

    public static Program compileShader(Type $$0, String $$1, InputStream $$2, String $$3, GlslPreprocessor $$4) throws IOException {
        RenderSystem.assertOnRenderThread();
        int $$5 = Program.compileShaderInternal($$0, $$1, $$2, $$3, $$4);
        Program $$6 = new Program($$0, $$5, $$1);
        $$0.getPrograms().put((Object)$$1, (Object)$$6);
        return $$6;
    }

    protected static int compileShaderInternal(Type $$0, String $$1, InputStream $$2, String $$3, GlslPreprocessor $$4) throws IOException {
        String $$5 = IOUtils.toString((InputStream)$$2, (Charset)StandardCharsets.UTF_8);
        if ($$5 == null) {
            throw new IOException("Could not load program " + $$0.getName());
        }
        int $$6 = GlStateManager.glCreateShader($$0.getGlType());
        GlStateManager.glShaderSource($$6, $$4.process($$5));
        GlStateManager.glCompileShader($$6);
        if (GlStateManager.glGetShaderi($$6, 35713) == 0) {
            String $$7 = StringUtils.trim((String)GlStateManager.glGetShaderInfoLog($$6, 32768));
            throw new IOException("Couldn't compile " + $$0.getName() + " program (" + $$3 + ", " + $$1 + ") : " + $$7);
        }
        return $$6;
    }

    protected int getId() {
        return this.id;
    }

    public static enum Type {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);

        private final String name;
        private final String extension;
        private final int glType;
        private final Map<String, Program> programs = Maps.newHashMap();

        private Type(String $$0, String $$1, int $$2) {
            this.name = $$0;
            this.extension = $$1;
            this.glType = $$2;
        }

        public String getName() {
            return this.name;
        }

        public String getExtension() {
            return this.extension;
        }

        int getGlType() {
            return this.glType;
        }

        public Map<String, Program> getPrograms() {
            return this.programs;
        }
    }
}