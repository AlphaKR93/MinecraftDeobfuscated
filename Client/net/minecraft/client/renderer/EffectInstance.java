/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InvalidClassException
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.IntSupplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class EffectInstance
implements Effect,
AutoCloseable {
    private static final String EFFECT_SHADER_PATH = "shaders/program/";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    private static final boolean ALWAYS_REAPPLY = true;
    private static EffectInstance lastAppliedEffect;
    private static int lastProgramId;
    private final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
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
    private final EffectProgram vertexProgram;
    private final EffectProgram fragmentProgram;

    public EffectInstance(ResourceManager $$0, String $$1) throws IOException {
        ResourceLocation $$2 = new ResourceLocation(EFFECT_SHADER_PATH + $$1 + ".json");
        this.name = $$1;
        Resource $$3 = $$0.getResourceOrThrow($$2);
        try (BufferedReader $$4 = $$3.openAsReader();){
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
                for (Iterator $$15 : $$13) {
                    try {
                        this.attributeNames.add((Object)GsonHelper.convertToString((JsonElement)$$15, "attribute"));
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
            this.blend = EffectInstance.parseBlendNode(GsonHelper.getAsJsonObject($$5, "blend", null));
            this.vertexProgram = EffectInstance.getOrCreate($$0, Program.Type.VERTEX, $$6);
            this.fragmentProgram = EffectInstance.getOrCreate($$0, Program.Type.FRAGMENT, $$7);
            this.programId = ProgramManager.createProgram();
            ProgramManager.linkShader(this);
            this.updateLocations();
            if (this.attributeNames != null) {
                for (String $$23 : this.attributeNames) {
                    int $$24 = Uniform.glGetAttribLocation(this.programId, $$23);
                    this.attributes.add((Object)$$24);
                }
            }
        }
        catch (Exception $$25) {
            ChainedJsonException $$26 = ChainedJsonException.forException($$25);
            $$26.setFilenameAndFlush($$2.getPath() + " (" + $$3.sourcePackId() + ")");
            throw $$26;
        }
        this.markDirty();
    }

    public static EffectProgram getOrCreate(ResourceManager $$0, Program.Type $$1, String $$2) throws IOException {
        EffectProgram $$9;
        Program $$3 = (Program)$$1.getPrograms().get((Object)$$2);
        if ($$3 != null && !($$3 instanceof EffectProgram)) {
            throw new InvalidClassException("Program is not of type EffectProgram");
        }
        if ($$3 == null) {
            ResourceLocation $$4 = new ResourceLocation(EFFECT_SHADER_PATH + $$2 + $$1.getExtension());
            Resource $$5 = $$0.getResourceOrThrow($$4);
            try (InputStream $$6 = $$5.open();){
                EffectProgram $$7 = EffectProgram.compileShader($$1, $$2, $$6, $$5.sourcePackId());
            }
        } else {
            $$9 = (EffectProgram)$$3;
        }
        return $$9;
    }

    public static BlendMode parseBlendNode(@Nullable JsonObject $$0) {
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
        lastAppliedEffect = null;
        for (int $$0 = 0; $$0 < this.samplerLocations.size(); ++$$0) {
            if (this.samplerMap.get(this.samplerNames.get($$0)) == null) continue;
            GlStateManager._activeTexture(33984 + $$0);
            GlStateManager._bindTexture(0);
        }
    }

    public void apply() {
        RenderSystem.assertOnGameThread();
        this.dirty = false;
        lastAppliedEffect = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }
        for (int $$0 = 0; $$0 < this.samplerLocations.size(); ++$$0) {
            String $$1 = (String)this.samplerNames.get($$0);
            IntSupplier $$2 = (IntSupplier)this.samplerMap.get((Object)$$1);
            if ($$2 == null) continue;
            RenderSystem.activeTexture(33984 + $$0);
            int $$3 = $$2.getAsInt();
            if ($$3 == -1) continue;
            RenderSystem.bindTexture($$3);
            Uniform.uploadInteger((Integer)this.samplerLocations.get($$0), $$0);
        }
        for (Uniform $$4 : this.uniforms) {
            $$4.upload();
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
            this.samplerNames.remove($$0.getInt($$4));
        }
        for (Uniform $$5 : this.uniforms) {
            String $$6 = $$5.getName();
            int $$7 = Uniform.glGetUniformLocation(this.programId, $$6);
            if ($$7 == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", (Object)this.name, (Object)$$6);
                continue;
            }
            this.uniformLocations.add((Object)$$7);
            $$5.setLocation($$7);
            this.uniformMap.put((Object)$$6, (Object)$$5);
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

    public void setSampler(String $$0, IntSupplier $$1) {
        if (this.samplerMap.containsKey((Object)$$0)) {
            this.samplerMap.remove((Object)$$0);
        }
        this.samplerMap.put((Object)$$0, (Object)$$1);
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
            $$12.set($$5);
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
        this.fragmentProgram.attachToEffect(this);
        this.vertexProgram.attachToEffect(this);
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