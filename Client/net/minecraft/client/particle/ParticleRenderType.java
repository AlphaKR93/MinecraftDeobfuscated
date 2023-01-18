/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Supplier;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public interface ParticleRenderType {
    public static final ParticleRenderType TERRAIN_SHEET = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator $$0) {
            $$0.end();
        }

        public String toString() {
            return "TERRAIN_SHEET";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getParticleShader));
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator $$0) {
            $$0.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator $$0) {
            $$0.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    public static final ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator $$0) {
            $$0.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }
    };
    public static final ParticleRenderType CUSTOM = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        @Override
        public void end(Tesselator $$0) {
        }

        public String toString() {
            return "CUSTOM";
        }
    };
    public static final ParticleRenderType NO_RENDER = new ParticleRenderType(){

        @Override
        public void begin(BufferBuilder $$0, TextureManager $$1) {
        }

        @Override
        public void end(Tesselator $$0) {
        }

        public String toString() {
            return "NO_RENDER";
        }
    };

    public void begin(BufferBuilder var1, TextureManager var2);

    public void end(Tesselator var1);
}