/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Void
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Supplier
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CubeMap {
    private static final int SIDES = 6;
    private final ResourceLocation[] images = new ResourceLocation[6];

    public CubeMap(ResourceLocation $$0) {
        for (int $$1 = 0; $$1 < 6; ++$$1) {
            this.images[$$1] = $$0.withPath($$0.getPath() + "_" + $$1 + ".png");
        }
    }

    public void render(Minecraft $$0, float $$1, float $$2, float $$3) {
        Tesselator $$4 = Tesselator.getInstance();
        BufferBuilder $$5 = $$4.getBuilder();
        Matrix4f $$6 = new Matrix4f().setPerspective(1.4835298f, (float)$$0.getWindow().getWidth() / (float)$$0.getWindow().getHeight(), 0.05f, 10.0f);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix($$6);
        PoseStack $$7 = RenderSystem.getModelViewStack();
        $$7.pushPose();
        $$7.setIdentity();
        $$7.mulPose(Axis.XP.rotationDegrees(180.0f));
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexColorShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        int $$8 = 2;
        for (int $$9 = 0; $$9 < 4; ++$$9) {
            $$7.pushPose();
            float $$10 = ((float)($$9 % 2) / 2.0f - 0.5f) / 256.0f;
            float $$11 = ((float)($$9 / 2) / 2.0f - 0.5f) / 256.0f;
            float $$12 = 0.0f;
            $$7.translate($$10, $$11, 0.0f);
            $$7.mulPose(Axis.XP.rotationDegrees($$1));
            $$7.mulPose(Axis.YP.rotationDegrees($$2));
            RenderSystem.applyModelViewMatrix();
            for (int $$13 = 0; $$13 < 6; ++$$13) {
                RenderSystem.setShaderTexture(0, this.images[$$13]);
                $$5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                int $$14 = Math.round((float)(255.0f * $$3)) / ($$9 + 1);
                if ($$13 == 0) {
                    $$5.vertex(-1.0, -1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                if ($$13 == 1) {
                    $$5.vertex(1.0, -1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                if ($$13 == 2) {
                    $$5.vertex(1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                if ($$13 == 3) {
                    $$5.vertex(-1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                if ($$13 == 4) {
                    $$5.vertex(-1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                if ($$13 == 5) {
                    $$5.vertex(-1.0, 1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, $$14).endVertex();
                }
                $$4.end();
            }
            $$7.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.restoreProjectionMatrix();
        $$7.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public CompletableFuture<Void> preload(TextureManager $$0, Executor $$1) {
        CompletableFuture[] $$2 = new CompletableFuture[6];
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            $$2[$$3] = $$0.preload(this.images[$$3], $$1);
        }
        return CompletableFuture.allOf((CompletableFuture[])$$2);
    }
}