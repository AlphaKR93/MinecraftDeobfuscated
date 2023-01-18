/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.BiConsumer
 *  java.util.function.Supplier
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public abstract class GuiComponent {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    private int blitOffset;

    protected void hLine(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        if ($$2 < $$1) {
            int $$5 = $$1;
            $$1 = $$2;
            $$2 = $$5;
        }
        GuiComponent.fill($$0, $$1, $$3, $$2 + 1, $$3 + 1, $$4);
    }

    protected void vLine(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        if ($$3 < $$2) {
            int $$5 = $$2;
            $$2 = $$3;
            $$3 = $$5;
        }
        GuiComponent.fill($$0, $$1, $$2 + 1, $$1 + 1, $$3, $$4);
    }

    public static void enableScissor(int $$0, int $$1, int $$2, int $$3) {
        Window $$4 = Minecraft.getInstance().getWindow();
        int $$5 = $$4.getHeight();
        double $$6 = $$4.getGuiScale();
        double $$7 = (double)$$0 * $$6;
        double $$8 = (double)$$5 - (double)$$3 * $$6;
        double $$9 = (double)($$2 - $$0) * $$6;
        double $$10 = (double)($$3 - $$1) * $$6;
        RenderSystem.enableScissor((int)$$7, (int)$$8, Math.max((int)0, (int)((int)$$9)), Math.max((int)0, (int)((int)$$10)));
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
    }

    public static void fill(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        GuiComponent.innerFill($$0.last().pose(), $$1, $$2, $$3, $$4, $$5);
    }

    private static void innerFill(Matrix4f $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$1 < $$3) {
            int $$6 = $$1;
            $$1 = $$3;
            $$3 = $$6;
        }
        if ($$2 < $$4) {
            int $$7 = $$2;
            $$2 = $$4;
            $$4 = $$7;
        }
        float $$8 = (float)($$5 >> 24 & 0xFF) / 255.0f;
        float $$9 = (float)($$5 >> 16 & 0xFF) / 255.0f;
        float $$10 = (float)($$5 >> 8 & 0xFF) / 255.0f;
        float $$11 = (float)($$5 & 0xFF) / 255.0f;
        BufferBuilder $$12 = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        $$12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$12.vertex($$0, $$1, $$4, 0.0f).color($$9, $$10, $$11, $$8).endVertex();
        $$12.vertex($$0, $$3, $$4, 0.0f).color($$9, $$10, $$11, $$8).endVertex();
        $$12.vertex($$0, $$3, $$2, 0.0f).color($$9, $$10, $$11, $$8).endVertex();
        $$12.vertex($$0, $$1, $$2, 0.0f).color($$9, $$10, $$11, $$8).endVertex();
        BufferUploader.drawWithShader($$12.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        GuiComponent.fillGradient($$0, $$1, $$2, $$3, $$4, $$5, $$6, this.blitOffset);
    }

    protected static void fillGradient(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Tesselator $$8 = Tesselator.getInstance();
        BufferBuilder $$9 = $$8.getBuilder();
        $$9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        GuiComponent.fillGradient($$0.last().pose(), $$9, $$1, $$2, $$3, $$4, $$7, $$5, $$6);
        $$8.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f $$0, BufferBuilder $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        float $$9 = (float)($$7 >> 24 & 0xFF) / 255.0f;
        float $$10 = (float)($$7 >> 16 & 0xFF) / 255.0f;
        float $$11 = (float)($$7 >> 8 & 0xFF) / 255.0f;
        float $$12 = (float)($$7 & 0xFF) / 255.0f;
        float $$13 = (float)($$8 >> 24 & 0xFF) / 255.0f;
        float $$14 = (float)($$8 >> 16 & 0xFF) / 255.0f;
        float $$15 = (float)($$8 >> 8 & 0xFF) / 255.0f;
        float $$16 = (float)($$8 & 0xFF) / 255.0f;
        $$1.vertex($$0, $$4, $$3, $$6).color($$10, $$11, $$12, $$9).endVertex();
        $$1.vertex($$0, $$2, $$3, $$6).color($$10, $$11, $$12, $$9).endVertex();
        $$1.vertex($$0, $$2, $$5, $$6).color($$14, $$15, $$16, $$13).endVertex();
        $$1.vertex($$0, $$4, $$5, $$6).color($$14, $$15, $$16, $$13).endVertex();
    }

    public static void drawCenteredString(PoseStack $$0, Font $$1, String $$2, int $$3, int $$4, int $$5) {
        $$1.drawShadow($$0, $$2, (float)($$3 - $$1.width($$2) / 2), (float)$$4, $$5);
    }

    public static void drawCenteredString(PoseStack $$0, Font $$1, Component $$2, int $$3, int $$4, int $$5) {
        FormattedCharSequence $$6 = $$2.getVisualOrderText();
        $$1.drawShadow($$0, $$6, (float)($$3 - $$1.width($$6) / 2), (float)$$4, $$5);
    }

    public static void drawCenteredString(PoseStack $$0, Font $$1, FormattedCharSequence $$2, int $$3, int $$4, int $$5) {
        $$1.drawShadow($$0, $$2, (float)($$3 - $$1.width($$2) / 2), (float)$$4, $$5);
    }

    public static void drawString(PoseStack $$0, Font $$1, String $$2, int $$3, int $$4, int $$5) {
        $$1.drawShadow($$0, $$2, (float)$$3, (float)$$4, $$5);
    }

    public static void drawString(PoseStack $$0, Font $$1, FormattedCharSequence $$2, int $$3, int $$4, int $$5) {
        $$1.drawShadow($$0, $$2, (float)$$3, (float)$$4, $$5);
    }

    public static void drawString(PoseStack $$0, Font $$1, Component $$2, int $$3, int $$4, int $$5) {
        $$1.drawShadow($$0, $$2, (float)$$3, (float)$$4, $$5);
    }

    public void blitOutlineBlack(int $$0, int $$1, BiConsumer<Integer, Integer> $$2) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        $$2.accept((Object)($$0 + 1), (Object)$$1);
        $$2.accept((Object)($$0 - 1), (Object)$$1);
        $$2.accept((Object)$$0, (Object)($$1 + 1));
        $$2.accept((Object)$$0, (Object)($$1 - 1));
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        $$2.accept((Object)$$0, (Object)$$1);
    }

    public static void blit(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, TextureAtlasSprite $$6) {
        GuiComponent.innerBlit($$0.last().pose(), $$1, $$1 + $$4, $$2, $$2 + $$5, $$3, $$6.getU0(), $$6.getU1(), $$6.getV0(), $$6.getV1());
    }

    public void blit(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        GuiComponent.blit($$0, $$1, $$2, this.blitOffset, $$3, $$4, $$5, $$6, 256, 256);
    }

    public static void blit(PoseStack $$0, int $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9) {
        GuiComponent.innerBlit($$0, $$1, $$1 + $$6, $$2, $$2 + $$7, $$3, $$6, $$7, $$4, $$5, $$8, $$9);
    }

    public static void blit(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, float $$5, float $$6, int $$7, int $$8, int $$9, int $$10) {
        GuiComponent.innerBlit($$0, $$1, $$1 + $$3, $$2, $$2 + $$4, 0, $$7, $$8, $$5, $$6, $$9, $$10);
    }

    public static void blit(PoseStack $$0, int $$1, int $$2, float $$3, float $$4, int $$5, int $$6, int $$7, int $$8) {
        GuiComponent.blit($$0, $$1, $$2, $$5, $$6, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    private static void innerBlit(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, float $$8, float $$9, int $$10, int $$11) {
        GuiComponent.innerBlit($$0.last().pose(), $$1, $$2, $$3, $$4, $$5, ($$8 + 0.0f) / (float)$$10, ($$8 + (float)$$6) / (float)$$10, ($$9 + 0.0f) / (float)$$11, ($$9 + (float)$$7) / (float)$$11);
    }

    private static void innerBlit(Matrix4f $$0, int $$1, int $$2, int $$3, int $$4, int $$5, float $$6, float $$7, float $$8, float $$9) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        BufferBuilder $$10 = Tesselator.getInstance().getBuilder();
        $$10.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$10.vertex($$0, $$1, $$4, $$5).uv($$6, $$9).endVertex();
        $$10.vertex($$0, $$2, $$4, $$5).uv($$7, $$9).endVertex();
        $$10.vertex($$0, $$2, $$3, $$5).uv($$7, $$8).endVertex();
        $$10.vertex($$0, $$1, $$3, $$5).uv($$6, $$8).endVertex();
        BufferUploader.drawWithShader($$10.end());
    }

    public int getBlitOffset() {
        return this.blitOffset;
    }

    public void setBlitOffset(int $$0) {
        this.blitOffset = $$0;
    }
}