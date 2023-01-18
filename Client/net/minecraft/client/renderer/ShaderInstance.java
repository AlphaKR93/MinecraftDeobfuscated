/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ShaderInstance
implements Shader,
AutoCloseable {
    public static final String SHADER_PATH = "shaders";
    private static final String SHADER_CORE_PATH = "shaders/core/";
    private static final String SHADER_INCLUDE_PATH = "shaders/include/";
    static final Logger LOGGER = LogUtils.getLogger();
    private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    private static final boolean ALWAYS_REAPPLY = true;
    private static ShaderInstance lastAppliedShader;
    private static int lastProgramId;
    private final Map<String, Object> samplerMap = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerLocations = Lists.newArrayList();
    private final List<Uniform> uniforms = Lists.newArrayList();
    private final List<Integer> uniformLocations = Lists.newArrayList();
    private final Map<String, Uniform> uniformMap = Maps.newHashMap();
    private final int programId;
    private final String name;
    private boolean dirty;
    private final BlendMode blend;
    private final List<Integer> attributes;
    private final List<String> attributeNames;
    private final Program vertexProgram;
    private final Program fragmentProgram;
    private final VertexFormat vertexFormat;
    @Nullable
    public final Uniform MODEL_VIEW_MATRIX;
    @Nullable
    public final Uniform PROJECTION_MATRIX;
    @Nullable
    public final Uniform INVERSE_VIEW_ROTATION_MATRIX;
    @Nullable
    public final Uniform TEXTURE_MATRIX;
    @Nullable
    public final Uniform SCREEN_SIZE;
    @Nullable
    public final Uniform COLOR_MODULATOR;
    @Nullable
    public final Uniform LIGHT0_DIRECTION;
    @Nullable
    public final Uniform LIGHT1_DIRECTION;
    @Nullable
    public final Uniform FOG_START;
    @Nullable
    public final Uniform FOG_END;
    @Nullable
    public final Uniform FOG_COLOR;
    @Nullable
    public final Uniform FOG_SHAPE;
    @Nullable
    public final Uniform LINE_WIDTH;
    @Nullable
    public final Uniform GAME_TIME;
    @Nullable
    public final Uniform CHUNK_OFFSET;

    public ShaderInstance(ResourceProvider $$0, String $$1, VertexFormat $$2) throws IOException {
        this.name = $$1;
        this.vertexFormat = $$2;
        ResourceLocation $$3 = new ResourceLocation(SHADER_CORE_PATH + $$1 + ".json");
        try (BufferedReader $$4 = $$0.openAsReader($$3);){
            JsonArray $$18;
            JsonArray $$13;
            JsonObject $$5 = GsonHelper.parse((Reader)$$4);
            String $$6 = GsonHelper.getAsString($$5, "vertex");
            String $$7 = GsonHelper.getAsString($$5, "fragment");
            JsonArray $$8 = GsonHelper.getAsJsonArray($$5, "samplers", null);
            if ($$8 != null) {
                int $$9 = 0;
                for (JsonElement $$10 : $$8) {
                    try {
                        this.parseSamplerNode($$10);
                    }
                    catch (Exception $$11) {
                        ChainedJsonException $$12 = ChainedJsonException.forException($$11);
                        $$12.prependJsonKey("samplers[" + $$9 + "]");
                        throw $$12;
                    }
                    ++$$9;
                }
            }
            if (($$13 = GsonHelper.getAsJsonArray($$5, "attributes", null)) != null) {
                int $$14 = 0;
                this.attributes = Lists.newArrayListWithCapacity((int)$$13.size());
                this.attributeNames = Lists.newArrayListWithCapacity((int)$$13.size());
                for (JsonElement $$15 : $$13) {
                    try {
                        this.attributeNames.add((Object)GsonHelper.convertToString($$15, "attribute"));
                    }
                    catch (Exception $$16) {
                        ChainedJsonException $$17 = ChainedJsonException.forException($$16);
                        $$17.prependJsonKey("attributes[" + $$14 + "]");
                        throw $$17;
                    }
                    ++$$14;
                }
            } else {
                this.attributes = null;
                this.attributeNames = null;
            }
            if (($$18 = GsonHelper.getAsJsonArray($$5, "uniforms", null)) != null) {
                int $$19 = 0;
                for (JsonElement $$20 : $$18) {
                    try {
                        this.parseUniformNode($$20);
                    }
                    catch (Exception $$21) {
                        ChainedJsonException $$22 = ChainedJsonException.forException($$21);
                        $$22.prependJsonKey("uniforms[" + $$19 + "]");
                        throw $$22;
                    }
                    ++$$19;
                }
            }
            this.blend = ShaderInstance.parseBlendNode(GsonHelper.getAsJsonObject($$5, "blend", null));
            this.vertexProgram = ShaderInstance.getOrCreate($$0, Program.Type.VERTEX, $$6);
            this.fragmentProgram = ShaderInstance.getOrCreate($$0, Program.Type.FRAGMENT, $$7);
            this.programId = ProgramManager.createProgram();
            if (this.attributeNames != null) {
                int $$23 = 0;
                for (String $$24 : $$2.getElementAttributeNames()) {
                    Uniform.glBindAttribLocation(this.programId, $$23, $$24);
                    this.attributes.add((Object)$$23);
                    ++$$23;
                }
            }
            ProgramManager.linkShader(this);
            this.updateLocations();
        }
        catch (Exception $$26) {
            ChainedJsonException $$27 = ChainedJsonException.forException($$26);
            $$27.setFilenameAndFlush($$3.getPath());
            throw $$27;
        }
        this.markDirty();
        this.MODEL_VIEW_MATRIX = this.getUniform("ModelViewMat");
        this.PROJECTION_MATRIX = this.getUniform("ProjMat");
        this.INVERSE_VIEW_ROTATION_MATRIX = this.getUniform("IViewRotMat");
        this.TEXTURE_MATRIX = this.getUniform("TextureMat");
        this.SCREEN_SIZE = this.getUniform("ScreenSize");
        this.COLOR_MODULATOR = this.getUniform("ColorModulator");
        this.LIGHT0_DIRECTION = this.getUniform("Light0_Direction");
        this.LIGHT1_DIRECTION = this.getUniform("Light1_Direction");
        this.FOG_START = this.getUniform("FogStart");
        this.FOG_END = this.getUniform("FogEnd");
        this.FOG_COLOR = this.getUniform("FogColor");
        this.FOG_SHAPE = this.getUniform("FogShape");
        this.LINE_WIDTH = this.getUniform("LineWidth");
        this.GAME_TIME = this.getUniform("GameTime");
        this.CHUNK_OFFSET = this.getUniform("ChunkOffset");
    }

    private static Program getOrCreate(final ResourceProvider $$0, Program.Type $$1, String $$2) throws IOException {
        Program $$10;
        Program $$3 = (Program)$$1.getPrograms().get((Object)$$2);
        if ($$3 == null) {
            String $$4 = SHADER_CORE_PATH + $$2 + $$1.getExtension();
            Resource $$5 = $$0.getResourceOrThrow(new ResourceLocation($$4));
            try (InputStream $$6 = $$5.open();){
                final String $$7 = FileUtil.getFullResourcePath($$4);
                Program $$8 = Program.compileShader($$1, $$2, $$6, $$5.sourcePackId(), new GlslPreprocessor(){
                    private final Set<String> importedPaths = Sets.newHashSet();

                    @Override
                    public String applyImport(boolean $$02, String $$1) {
                        String string;
                        block9: {
                            $$1 = FileUtil.normalizeResourcePath(($$02 ? $$7 : ShaderInstance.SHADER_INCLUDE_PATH) + $$1);
                            if (!this.importedPaths.add((Object)$$1)) {
                                return null;
                            }
                            ResourceLocation $$2 = new ResourceLocation($$1);
                            BufferedReader $$3 = $$0.openAsReader($$2);
                            try {
                                string = IOUtils.toString((Reader)$$3);
                                if ($$3 == null) break block9;
                            }
                            catch (Throwable throwable) {
                                try {
                                    if ($$3 != null) {
                                        try {
                                            $$3.close();
                                        }
                                        catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                }
                                catch (IOException $$4) {
                                    LOGGER.error("Could not open GLSL import {}: {}", (Object)$$1, (Object)$$4.getMessage());
                                    return "#error " + $$4.getMessage();
                                }
                            }
                            $$3.close();
                        }
                        return string;
                    }
                });
            }
        } else {
            $$10 = $$3;
        }
        return $$10;
    }

    public static BlendMode parseBlendNode(JsonObject $$0) {
        if ($$0 == null) {
            return new BlendMode();
        }
        int $$1 = 32774;
        int $$2 = 1;
        int $$3 = 0;
        int $$4 = 1;
        int $$5 = 0;
        boolean $$6 = true;
        boolean $$7 = false;
        if (GsonHelper.isStringValue($$0, "func") && ($$1 = BlendMode.stringToBlendFunc($$0.get("func").getAsString())) != 32774) {
            $$6 = false;
        }
        if (GsonHelper.isStringValue($$0, "srcrgb") && ($$2 = BlendMode.stringToBlendFactor($$0.get("srcrgb").getAsString())) != 1) {
            $$6 = false;
        }
        if (GsonHelper.isStringValue($$0, "dstrgb") && ($$3 = BlendMode.stringToBlendFactor($$0.get("dstrgb").getAsString())) != 0) {
            $$6 = false;
        }
        if (GsonHelper.isStringValue($$0, "srcalpha")) {
            $$4 = BlendMode.stringToBlendFactor($$0.get("srcalpha").getAsString());
            if ($$4 != 1) {
                $$6 = false;
            }
            $$7 = true;
        }
        if (GsonHelper.isStringValue($$0, "dstalpha")) {
            $$5 = BlendMode.stringToBlendFactor($$0.get("dstalpha").getAsString());
            if ($$5 != 0) {
                $$6 = false;
            }
            $$7 = true;
        }
        if ($$6) {
            return new BlendMode();
        }
        if ($$7) {
            return new BlendMode($$2, $$3, $$4, $$5, $$1);
        }
        return new BlendMode($$2, $$3, $$1);
    }

    public void close() {
        for (Uniform $$0 : this.uniforms) {
            $$0.close();
        }
        ProgramManager.releaseProgram(this);
    }

    public void clear() {
        RenderSystem.assertOnRenderThread();
        ProgramManager.glUseProgram(0);
        lastProgramId = -1;
        lastAppliedShader = null;
        int $$0 = GlStateManager._getActiveTexture();
        for (int $$1 = 0; $$1 < this.samplerLocations.size(); ++$$1) {
            if (this.samplerMap.get(this.samplerNames.get($$1)) == null) continue;
            GlStateManager._activeTexture(33984 + $$1);
            GlStateManager._bindTexture(0);
        }
        GlStateManager._activeTexture($$0);
    }

    public void apply() {
        RenderSystem.assertOnRenderThread();
        this.dirty = false;
        lastAppliedShader = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }
        int $$0 = GlStateManager._getActiveTexture();
        for (int $$1 = 0; $$1 < this.samplerLocations.size(); ++$$1) {
            String $$2 = (String)this.samplerNames.get($$1);
            if (this.samplerMap.get((Object)$$2) == null) continue;
            int $$3 = Uniform.glGetUniformLocation(this.programId, $$2);
            Uniform.uploadInteger($$3, $$1);
            RenderSystem.activeTexture(33984 + $$1);
            RenderSystem.enableTexture();
            Object $$4 = this.samplerMap.get((Object)$$2);
            int $$5 = -1;
            if ($$4 instanceof RenderTarget) {
                $$5 = ((RenderTarget)$$4).getColorTextureId();
            } else if ($$4 instanceof AbstractTexture) {
                $$5 = ((AbstractTexture)$$4).getId();
            } else if ($$4 instanceof Integer) {
                $$5 = (Integer)$$4;
            }
            if ($$5 == -1) continue;
            RenderSystem.bindTexture($$5);
        }
        GlStateManager._activeTexture($$0);
        for (Uniform $$6 : this.uniforms) {
            $$6.upload();
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public Uniform getUniform(String $$0) {
        RenderSystem.assertOnRenderThread();
        return (Uniform)this.uniformMap.get((Object)$$0);
    }

    public AbstractUniform safeGetUniform(String $$0) {
        RenderSystem.assertOnGameThread();
        Uniform $$1 = this.getUniform($$0);
        return $$1 == null ? DUMMY_UNIFORM : $$1;
    }

    private void updateLocations() {
        RenderSystem.assertOnRenderThread();
        IntArrayList $$0 = new IntArrayList();
        for (int $$1 = 0; $$1 < this.samplerNames.size(); ++$$1) {
            String $$2 = (String)this.samplerNames.get($$1);
            int $$3 = Uniform.glGetUniformLocation(this.programId, $$2);
            if ($$3 == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)$$2);
                this.samplerMap.remove((Object)$$2);
                $$0.add($$1);
                continue;
            }
            this.samplerLocations.add((Object)$$3);
        }
        for (int $$4 = $$0.size() - 1; $$4 >= 0; --$$4) {
            int $$5 = $$0.getInt($$4);
            this.samplerNames.remove($$5);
        }
        for (Uniform $$6 : this.uniforms) {
            String $$7 = $$6.getName();
            int $$8 = Uniform.glGetUniformLocation(this.programId, $$7);
            if ($$8 == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", (Object)this.name, (Object)$$7);
                continue;
            }
            this.uniformLocations.add((Object)$$8);
            $$6.setLocation($$8);
            this.uniformMap.put((Object)$$7, (Object)$$6);
        }
    }

    private void parseSamplerNode(JsonElement $$0) {
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "sampler");
        String $$2 = GsonHelper.getAsString($$1, "name");
        if (!GsonHelper.isStringValue($$1, "file")) {
            this.samplerMap.put((Object)$$2, null);
            this.samplerNames.add((Object)$$2);
            return;
        }
        this.samplerNames.add((Object)$$2);
    }

    public void setSampler(String $$0, Object $$1) {
        this.samplerMap.put((Object)$$0, $$1);
        this.markDirty();
    }

    private void parseUniformNode(JsonElement $$0) throws ChainedJsonException {
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "uniform");
        String $$2 = GsonHelper.getAsString($$1, "name");
        int $$3 = Uniform.getTypeFromString(GsonHelper.getAsString($$1, "type"));
        int $$4 = GsonHelper.getAsInt($$1, "count");
        float[] $$5 = new float[Math.max((int)$$4, (int)16)];
        JsonArray $$6 = GsonHelper.getAsJsonArray($$1, "values");
        if ($$6.size() != $$4 && $$6.size() > 1) {
            throw new ChainedJsonException("Invalid amount of values specified (expected " + $$4 + ", found " + $$6.size() + ")");
        }
        int $$7 = 0;
        for (JsonElement $$8 : $$6) {
            try {
                $$5[$$7] = GsonHelper.convertToFloat($$8, "value");
            }
            catch (Exception $$9) {
                ChainedJsonException $$10 = ChainedJsonException.forException($$9);
                $$10.prependJsonKey("values[" + $$7 + "]");
                throw $$10;
            }
            ++$$7;
        }
        if ($$4 > 1 && $$6.size() == 1) {
            while ($$7 < $$4) {
                $$5[$$7] = $$5[0];
                ++$$7;
            }
        }
        int $$11 = $$4 > 1 && $$4 <= 4 && $$3 < 8 ? $$4 - 1 : 0;
        Uniform $$12 = new Uniform($$2, $$3 + $$11, $$4, this);
        if ($$3 <= 3) {
            $$12.setSafe((int)$$5[0], (int)$$5[1], (int)$$5[2], (int)$$5[3]);
        } else if ($$3 <= 7) {
            $$12.setSafe($$5[0], $$5[1], $$5[2], $$5[3]);
        } else {
            $$12.set(Arrays.copyOfRange((float[])$$5, (int)0, (int)$$4));
        }
        this.uniforms.add((Object)$$12);
    }

    @Override
    public Program getVertexProgram() {
        return this.vertexProgram;
    }

    @Override
    public Program getFragmentProgram() {
        return this.fragmentProgram;
    }

    @Override
    public void attachToProgram() {
        this.fragmentProgram.attachToShader(this);
        this.vertexProgram.attachToShader(this);
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getId() {
        return this.programId;
    }

    static {
        lastProgramId = -1;
    }
}