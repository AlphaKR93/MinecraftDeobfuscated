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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolidFaceRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public SolidFaceRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Level $$5 = this.minecraft.player.level;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.depthMask(false);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        BlockPos $$6 = new BlockPos($$2, $$3, $$4);
        for (BlockPos $$7 : BlockPos.betweenClosed($$6.offset(-6, -6, -6), $$6.offset(6, 6, 6))) {
            BlockState $$8 = $$5.getBlockState($$7);
            if ($$8.is(Blocks.AIR)) continue;
            VoxelShape $$9 = $$8.getShape($$5, $$7);
            for (AABB $$10 : $$9.toAabbs()) {
                AABB $$11 = $$10.move($$7).inflate(0.002).move(-$$2, -$$3, -$$4);
                double $$12 = $$11.minX;
                double $$13 = $$11.minY;
                double $$14 = $$11.minZ;
                double $$15 = $$11.maxX;
                double $$16 = $$11.maxY;
                double $$17 = $$11.maxZ;
                float $$18 = 1.0f;
                float $$19 = 0.0f;
                float $$20 = 0.0f;
                float $$21 = 0.5f;
                if ($$8.isFaceSturdy($$5, $$7, Direction.WEST)) {
                    Tesselator $$22 = Tesselator.getInstance();
                    BufferBuilder $$23 = $$22.getBuilder();
                    $$23.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                    $$23.vertex($$12, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$23.vertex($$12, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$23.vertex($$12, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$23.vertex($$12, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$22.end();
                }
                if ($$8.isFaceSturdy($$5, $$7, Direction.SOUTH)) {
                    Tesselator $$24 = Tesselator.getInstance();
                    BufferBuilder $$25 = $$24.getBuilder();
                    $$25.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                    $$25.vertex($$12, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$25.vertex($$12, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$25.vertex($$15, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$25.vertex($$15, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$24.end();
                }
                if ($$8.isFaceSturdy($$5, $$7, Direction.EAST)) {
                    Tesselator $$26 = Tesselator.getInstance();
                    BufferBuilder $$27 = $$26.getBuilder();
                    $$27.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                    $$27.vertex($$15, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$27.vertex($$15, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$27.vertex($$15, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$27.vertex($$15, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$26.end();
                }
                if ($$8.isFaceSturdy($$5, $$7, Direction.NORTH)) {
                    Tesselator $$28 = Tesselator.getInstance();
                    BufferBuilder $$29 = $$28.getBuilder();
                    $$29.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                    $$29.vertex($$15, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$29.vertex($$15, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$29.vertex($$12, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$29.vertex($$12, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$28.end();
                }
                if ($$8.isFaceSturdy($$5, $$7, Direction.DOWN)) {
                    Tesselator $$30 = Tesselator.getInstance();
                    BufferBuilder $$31 = $$30.getBuilder();
                    $$31.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                    $$31.vertex($$12, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$31.vertex($$15, $$13, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$31.vertex($$12, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$31.vertex($$15, $$13, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    $$30.end();
                }
                if (!$$8.isFaceSturdy($$5, $$7, Direction.UP)) continue;
                Tesselator $$32 = Tesselator.getInstance();
                BufferBuilder $$33 = $$32.getBuilder();
                $$33.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                $$33.vertex($$12, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$33.vertex($$12, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$33.vertex($$15, $$16, $$14).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$33.vertex($$15, $$16, $$17).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                $$32.end();
            }
        }
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}