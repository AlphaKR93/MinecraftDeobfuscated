/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class ChunkBorderRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CELL_BORDER = FastColor.ARGB32.color(255, 0, 155, 155);
    private static final int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0);

    public ChunkBorderRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Entity $$5 = this.minecraft.gameRenderer.getMainCamera().getEntity();
        Tesselator $$6 = Tesselator.getInstance();
        BufferBuilder $$7 = $$6.getBuilder();
        double $$8 = (double)this.minecraft.level.getMinBuildHeight() - $$3;
        double $$9 = (double)this.minecraft.level.getMaxBuildHeight() - $$3;
        RenderSystem.disableBlend();
        ChunkPos $$10 = $$5.chunkPosition();
        double $$11 = (double)$$10.getMinBlockX() - $$2;
        double $$12 = (double)$$10.getMinBlockZ() - $$4;
        RenderSystem.lineWidth(1.0f);
        $$7.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int $$13 = -16; $$13 <= 32; $$13 += 16) {
            for (int $$14 = -16; $$14 <= 32; $$14 += 16) {
                $$7.vertex($$11 + (double)$$13, $$8, $$12 + (double)$$14).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
                $$7.vertex($$11 + (double)$$13, $$8, $$12 + (double)$$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$7.vertex($$11 + (double)$$13, $$9, $$12 + (double)$$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$7.vertex($$11 + (double)$$13, $$9, $$12 + (double)$$14).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
            }
        }
        for (int $$15 = 2; $$15 < 16; $$15 += 2) {
            int $$16 = $$15 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$7.vertex($$11 + (double)$$15, $$8, $$12).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11 + (double)$$15, $$8, $$12).color($$16).endVertex();
            $$7.vertex($$11 + (double)$$15, $$9, $$12).color($$16).endVertex();
            $$7.vertex($$11 + (double)$$15, $$9, $$12).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11 + (double)$$15, $$8, $$12 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11 + (double)$$15, $$8, $$12 + 16.0).color($$16).endVertex();
            $$7.vertex($$11 + (double)$$15, $$9, $$12 + 16.0).color($$16).endVertex();
            $$7.vertex($$11 + (double)$$15, $$9, $$12 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int $$17 = 2; $$17 < 16; $$17 += 2) {
            int $$18 = $$17 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$7.vertex($$11, $$8, $$12 + (double)$$17).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11, $$8, $$12 + (double)$$17).color($$18).endVertex();
            $$7.vertex($$11, $$9, $$12 + (double)$$17).color($$18).endVertex();
            $$7.vertex($$11, $$9, $$12 + (double)$$17).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11 + 16.0, $$8, $$12 + (double)$$17).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11 + 16.0, $$8, $$12 + (double)$$17).color($$18).endVertex();
            $$7.vertex($$11 + 16.0, $$9, $$12 + (double)$$17).color($$18).endVertex();
            $$7.vertex($$11 + 16.0, $$9, $$12 + (double)$$17).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int $$19 = this.minecraft.level.getMinBuildHeight(); $$19 <= this.minecraft.level.getMaxBuildHeight(); $$19 += 2) {
            double $$20 = (double)$$19 - $$3;
            int $$21 = $$19 % 8 == 0 ? CELL_BORDER : YELLOW;
            $$7.vertex($$11, $$20, $$12).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            $$7.vertex($$11, $$20, $$12).color($$21).endVertex();
            $$7.vertex($$11, $$20, $$12 + 16.0).color($$21).endVertex();
            $$7.vertex($$11 + 16.0, $$20, $$12 + 16.0).color($$21).endVertex();
            $$7.vertex($$11 + 16.0, $$20, $$12).color($$21).endVertex();
            $$7.vertex($$11, $$20, $$12).color($$21).endVertex();
            $$7.vertex($$11, $$20, $$12).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        $$6.end();
        RenderSystem.lineWidth(2.0f);
        $$7.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int $$22 = 0; $$22 <= 16; $$22 += 16) {
            for (int $$23 = 0; $$23 <= 16; $$23 += 16) {
                $$7.vertex($$11 + (double)$$22, $$8, $$12 + (double)$$23).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
                $$7.vertex($$11 + (double)$$22, $$8, $$12 + (double)$$23).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                $$7.vertex($$11 + (double)$$22, $$9, $$12 + (double)$$23).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                $$7.vertex($$11 + (double)$$22, $$9, $$12 + (double)$$23).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            }
        }
        for (int $$24 = this.minecraft.level.getMinBuildHeight(); $$24 <= this.minecraft.level.getMaxBuildHeight(); $$24 += 16) {
            double $$25 = (double)$$24 - $$3;
            $$7.vertex($$11, $$25, $$12).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            $$7.vertex($$11, $$25, $$12).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            $$7.vertex($$11, $$25, $$12 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            $$7.vertex($$11 + 16.0, $$25, $$12 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            $$7.vertex($$11 + 16.0, $$25, $$12).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            $$7.vertex($$11, $$25, $$12).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            $$7.vertex($$11, $$25, $$12).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
        }
        $$6.end();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
    }
}