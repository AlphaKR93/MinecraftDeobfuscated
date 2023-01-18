/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

public class PostChain
implements AutoCloseable {
    private static final String MAIN_RENDER_TARGET = "minecraft:main";
    private final RenderTarget screenTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostPass> passes = Lists.newArrayList();
    private final Map<String, RenderTarget> customRenderTargets = Maps.newHashMap();
    private final List<RenderTarget> fullSizedTargets = Lists.newArrayList();
    private Matrix4f shaderOrthoMatrix;
    private int screenWidth;
    private int screenHeight;
    private float time;
    private float lastStamp;

    public PostChain(TextureManager $$0, ResourceManager $$1, RenderTarget $$2, ResourceLocation $$3) throws IOException, JsonSyntaxException {
        this.resourceManager = $$1;
        this.screenTarget = $$2;
        this.time = 0.0f;
        this.lastStamp = 0.0f;
        this.screenWidth = $$2.viewWidth;
        this.screenHeight = $$2.viewHeight;
        this.name = $$3.toString();
        this.updateOrthoMatrix();
        this.load($$0, $$3);
    }

    private void load(TextureManager $$0, ResourceLocation $$1) throws IOException, JsonSyntaxException {
        block15: {
            Resource $$2 = this.resourceManager.getResourceOrThrow($$1);
            try (BufferedReader $$3 = $$2.openAsReader();){
                JsonObject $$4 = GsonHelper.parse((Reader)$$3);
                if (GsonHelper.isArrayNode($$4, "targets")) {
                    JsonArray $$5 = $$4.getAsJsonArray("targets");
                    int $$6 = 0;
                    for (JsonElement $$7 : $$5) {
                        try {
                            this.parseTargetNode($$7);
                        }
                        catch (Exception $$8) {
                            ChainedJsonException $$9 = ChainedJsonException.forException($$8);
                            $$9.prependJsonKey("targets[" + $$6 + "]");
                            throw $$9;
                        }
                        ++$$6;
                    }
                }
                if (!GsonHelper.isArrayNode($$4, "passes")) break block15;
                JsonArray $$10 = $$4.getAsJsonArray("passes");
                int $$11 = 0;
                for (JsonElement $$12 : $$10) {
                    try {
                        this.parsePassNode($$0, $$12);
                    }
                    catch (Exception $$13) {
                        ChainedJsonException $$14 = ChainedJsonException.forException($$13);
                        $$14.prependJsonKey("passes[" + $$11 + "]");
                        throw $$14;
                    }
                    ++$$11;
                }
            }
            catch (Exception $$15) {
                ChainedJsonException $$16 = ChainedJsonException.forException($$15);
                $$16.setFilenameAndFlush($$1.getPath() + " (" + $$2.sourcePackId() + ")");
                throw $$16;
            }
        }
    }

    private void parseTargetNode(JsonElement $$0) throws ChainedJsonException {
        if (GsonHelper.isStringValue($$0)) {
            this.addTempTarget($$0.getAsString(), this.screenWidth, this.screenHeight);
        } else {
            JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "target");
            String $$2 = GsonHelper.getAsString($$1, "name");
            int $$3 = GsonHelper.getAsInt($$1, "width", this.screenWidth);
            int $$4 = GsonHelper.getAsInt($$1, "height", this.screenHeight);
            if (this.customRenderTargets.containsKey((Object)$$2)) {
                throw new ChainedJsonException($$2 + " is already defined");
            }
            this.addTempTarget($$2, $$3, $$4);
        }
    }

    private void parsePassNode(TextureManager $$0, JsonElement $$1) throws IOException {
        JsonArray $$27;
        JsonObject $$2 = GsonHelper.convertToJsonObject($$1, "pass");
        String $$3 = GsonHelper.getAsString($$2, "name");
        String $$4 = GsonHelper.getAsString($$2, "intarget");
        String $$5 = GsonHelper.getAsString($$2, "outtarget");
        RenderTarget $$6 = this.getRenderTarget($$4);
        RenderTarget $$7 = this.getRenderTarget($$5);
        if ($$6 == null) {
            throw new ChainedJsonException("Input target '" + $$4 + "' does not exist");
        }
        if ($$7 == null) {
            throw new ChainedJsonException("Output target '" + $$5 + "' does not exist");
        }
        PostPass $$8 = this.addPass($$3, $$6, $$7);
        JsonArray $$9 = GsonHelper.getAsJsonArray($$2, "auxtargets", null);
        if ($$9 != null) {
            int $$10 = 0;
            for (JsonElement $$11 : $$9) {
                try {
                    String $$18;
                    boolean $$17;
                    JsonObject $$12 = GsonHelper.convertToJsonObject($$11, "auxtarget");
                    String $$13 = GsonHelper.getAsString($$12, "name");
                    String $$14 = GsonHelper.getAsString($$12, "id");
                    if ($$14.endsWith(":depth")) {
                        boolean $$15 = true;
                        String $$16 = $$14.substring(0, $$14.lastIndexOf(58));
                    } else {
                        $$17 = false;
                        $$18 = $$14;
                    }
                    RenderTarget $$19 = this.getRenderTarget($$18);
                    if ($$19 == null) {
                        if ($$17) {
                            throw new ChainedJsonException("Render target '" + $$18 + "' can't be used as depth buffer");
                        }
                        ResourceLocation $$20 = new ResourceLocation("textures/effect/" + $$18 + ".png");
                        this.resourceManager.getResource($$20).orElseThrow(() -> new ChainedJsonException("Render target or texture '" + $$18 + "' does not exist"));
                        RenderSystem.setShaderTexture(0, $$20);
                        $$0.bindForSetup($$20);
                        AbstractTexture $$21 = $$0.getTexture($$20);
                        int $$22 = GsonHelper.getAsInt($$12, "width");
                        int $$23 = GsonHelper.getAsInt($$12, "height");
                        boolean $$24 = GsonHelper.getAsBoolean($$12, "bilinear");
                        if ($$24) {
                            RenderSystem.texParameter(3553, 10241, 9729);
                            RenderSystem.texParameter(3553, 10240, 9729);
                        } else {
                            RenderSystem.texParameter(3553, 10241, 9728);
                            RenderSystem.texParameter(3553, 10240, 9728);
                        }
                        $$8.addAuxAsset($$13, $$21::getId, $$22, $$23);
                    } else if ($$17) {
                        $$8.addAuxAsset($$13, $$19::getDepthTextureId, $$19.width, $$19.height);
                    } else {
                        $$8.addAuxAsset($$13, $$19::getColorTextureId, $$19.width, $$19.height);
                    }
                }
                catch (Exception $$25) {
                    ChainedJsonException $$26 = ChainedJsonException.forException($$25);
                    $$26.prependJsonKey("auxtargets[" + $$10 + "]");
                    throw $$26;
                }
                ++$$10;
            }
        }
        if (($$27 = GsonHelper.getAsJsonArray($$2, "uniforms", null)) != null) {
            int $$28 = 0;
            for (JsonElement $$29 : $$27) {
                try {
                    this.parseUniformNode($$29);
                }
                catch (Exception $$30) {
                    ChainedJsonException $$31 = ChainedJsonException.forException($$30);
                    $$31.prependJsonKey("uniforms[" + $$28 + "]");
                    throw $$31;
                }
                ++$$28;
            }
        }
    }

    private void parseUniformNode(JsonElement $$0) throws ChainedJsonException {
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "uniform");
        String $$2 = GsonHelper.getAsString($$1, "name");
        Uniform $$3 = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform($$2);
        if ($$3 == null) {
            throw new ChainedJsonException("Uniform '" + $$2 + "' does not exist");
        }
        float[] $$4 = new float[4];
        int $$5 = 0;
        JsonArray $$6 = GsonHelper.getAsJsonArray($$1, "values");
        for (JsonElement $$7 : $$6) {
            try {
                $$4[$$5] = GsonHelper.convertToFloat($$7, "value");
            }
            catch (Exception $$8) {
                ChainedJsonException $$9 = ChainedJsonException.forException($$8);
                $$9.prependJsonKey("values[" + $$5 + "]");
                throw $$9;
            }
            ++$$5;
        }
        switch ($$5) {
            case 0: {
                break;
            }
            case 1: {
                $$3.set($$4[0]);
                break;
            }
            case 2: {
                $$3.set($$4[0], $$4[1]);
                break;
            }
            case 3: {
                $$3.set($$4[0], $$4[1], $$4[2]);
                break;
            }
            case 4: {
                $$3.set($$4[0], $$4[1], $$4[2], $$4[3]);
            }
        }
    }

    public RenderTarget getTempTarget(String $$0) {
        return (RenderTarget)this.customRenderTargets.get((Object)$$0);
    }

    public void addTempTarget(String $$0, int $$1, int $$2) {
        TextureTarget $$3 = new TextureTarget($$1, $$2, true, Minecraft.ON_OSX);
        $$3.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.customRenderTargets.put((Object)$$0, (Object)$$3);
        if ($$1 == this.screenWidth && $$2 == this.screenHeight) {
            this.fullSizedTargets.add((Object)$$3);
        }
    }

    public void close() {
        for (RenderTarget $$0 : this.customRenderTargets.values()) {
            $$0.destroyBuffers();
        }
        for (PostPass $$1 : this.passes) {
            $$1.close();
        }
        this.passes.clear();
    }

    public PostPass addPass(String $$0, RenderTarget $$1, RenderTarget $$2) throws IOException {
        PostPass $$3 = new PostPass(this.resourceManager, $$0, $$1, $$2);
        this.passes.add(this.passes.size(), (Object)$$3);
        return $$3;
    }

    private void updateOrthoMatrix() {
        this.shaderOrthoMatrix = new Matrix4f().setOrtho(0.0f, (float)this.screenTarget.width, 0.0f, (float)this.screenTarget.height, 0.1f, 1000.0f);
    }

    public void resize(int $$0, int $$1) {
        this.screenWidth = this.screenTarget.width;
        this.screenHeight = this.screenTarget.height;
        this.updateOrthoMatrix();
        for (PostPass $$2 : this.passes) {
            $$2.setOrthoMatrix(this.shaderOrthoMatrix);
        }
        for (RenderTarget $$3 : this.fullSizedTargets) {
            $$3.resize($$0, $$1, Minecraft.ON_OSX);
        }
    }

    public void process(float $$0) {
        if ($$0 < this.lastStamp) {
            this.time += 1.0f - this.lastStamp;
            this.time += $$0;
        } else {
            this.time += $$0 - this.lastStamp;
        }
        this.lastStamp = $$0;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (PostPass $$1 : this.passes) {
            $$1.process(this.time / 20.0f);
        }
    }

    public final String getName() {
        return this.name;
    }

    @Nullable
    private RenderTarget getRenderTarget(@Nullable String $$0) {
        if ($$0 == null) {
            return null;
        }
        if ($$0.equals((Object)MAIN_RENDER_TARGET)) {
            return this.screenTarget;
        }
        return (RenderTarget)this.customRenderTargets.get((Object)$$0);
    }
}