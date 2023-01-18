/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class WorldGenAttemptRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final List<BlockPos> toRender = Lists.newArrayList();
    private final List<Float> scales = Lists.newArrayList();
    private final List<Float> alphas = Lists.newArrayList();
    private final List<Float> reds = Lists.newArrayList();
    private final List<Float> greens = Lists.newArrayList();
    private final List<Float> blues = Lists.newArrayList();

    public void addPos(BlockPos $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.toRender.add((Object)$$0);
        this.scales.add((Object)Float.valueOf((float)$$1));
        this.alphas.add((Object)Float.valueOf((float)$$5));
        this.reds.add((Object)Float.valueOf((float)$$2));
        this.greens.add((Object)Float.valueOf((float)$$3));
        this.blues.add((Object)Float.valueOf((float)$$4));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Tesselator $$5 = Tesselator.getInstance();
        BufferBuilder $$6 = $$5.getBuilder();
        $$6.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int $$7 = 0; $$7 < this.toRender.size(); ++$$7) {
            BlockPos $$8 = (BlockPos)this.toRender.get($$7);
            Float $$9 = (Float)this.scales.get($$7);
            float $$10 = $$9.floatValue() / 2.0f;
            LevelRenderer.addChainedFilledBoxVertices($$6, (double)((float)$$8.getX() + 0.5f - $$10) - $$2, (double)((float)$$8.getY() + 0.5f - $$10) - $$3, (double)((float)$$8.getZ() + 0.5f - $$10) - $$4, (double)((float)$$8.getX() + 0.5f + $$10) - $$2, (double)((float)$$8.getY() + 0.5f + $$10) - $$3, (double)((float)$$8.getZ() + 0.5f + $$10) - $$4, ((Float)this.reds.get($$7)).floatValue(), ((Float)this.greens.get($$7)).floatValue(), ((Float)this.blues.get($$7)).floatValue(), ((Float)this.alphas.get($$7)).floatValue());
        }
        $$5.end();
        RenderSystem.enableTexture();
    }
}