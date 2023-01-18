/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Map<Integer, Path> pathMap = Maps.newHashMap();
    private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
    private final Map<Integer, Long> creationMap = Maps.newHashMap();
    private static final long TIMEOUT = 5000L;
    private static final float MAX_RENDER_DIST = 80.0f;
    private static final boolean SHOW_OPEN_CLOSED = true;
    private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
    private static final boolean SHOW_GROUND_LABELS = true;
    private static final float TEXT_SCALE = 0.02f;

    public void addPath(int $$0, Path $$1, float $$2) {
        this.pathMap.put((Object)$$0, (Object)$$1);
        this.creationMap.put((Object)$$0, (Object)Util.getMillis());
        this.pathMaxDist.put((Object)$$0, (Object)Float.valueOf((float)$$2));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        if (this.pathMap.isEmpty()) {
            return;
        }
        long $$5 = Util.getMillis();
        for (Integer $$6 : this.pathMap.keySet()) {
            Path $$7 = (Path)this.pathMap.get((Object)$$6);
            float $$8 = ((Float)this.pathMaxDist.get((Object)$$6)).floatValue();
            PathfindingRenderer.renderPath($$7, $$8, true, true, $$2, $$3, $$4);
        }
        for (Integer $$9 : (Integer[])this.creationMap.keySet().toArray((Object[])new Integer[0])) {
            if ($$5 - (Long)this.creationMap.get((Object)$$9) <= 5000L) continue;
            this.pathMap.remove((Object)$$9);
            this.creationMap.remove((Object)$$9);
        }
    }

    public static void renderPath(Path $$0, float $$1, boolean $$2, boolean $$3, double $$4, double $$5, double $$6) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(6.0f);
        PathfindingRenderer.doRenderPath($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        RenderSystem.disableBlend();
    }

    private static void doRenderPath(Path $$0, float $$1, boolean $$2, boolean $$3, double $$4, double $$5, double $$6) {
        PathfindingRenderer.renderPathLine($$0, $$4, $$5, $$6);
        BlockPos $$7 = $$0.getTarget();
        if (PathfindingRenderer.distanceToCamera($$7, $$4, $$5, $$6) <= 80.0f) {
            DebugRenderer.renderFilledBox(new AABB((float)$$7.getX() + 0.25f, (float)$$7.getY() + 0.25f, (double)$$7.getZ() + 0.25, (float)$$7.getX() + 0.75f, (float)$$7.getY() + 0.75f, (float)$$7.getZ() + 0.75f).move(-$$4, -$$5, -$$6), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int $$8 = 0; $$8 < $$0.getNodeCount(); ++$$8) {
                Node $$9 = $$0.getNode($$8);
                if (!(PathfindingRenderer.distanceToCamera($$9.asBlockPos(), $$4, $$5, $$6) <= 80.0f)) continue;
                float $$10 = $$8 == $$0.getNextNodeIndex() ? 1.0f : 0.0f;
                float $$11 = $$8 == $$0.getNextNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.renderFilledBox(new AABB((float)$$9.x + 0.5f - $$1, (float)$$9.y + 0.01f * (float)$$8, (float)$$9.z + 0.5f - $$1, (float)$$9.x + 0.5f + $$1, (float)$$9.y + 0.25f + 0.01f * (float)$$8, (float)$$9.z + 0.5f + $$1).move(-$$4, -$$5, -$$6), $$10, 0.0f, $$11, 0.5f);
            }
        }
        if ($$2) {
            for (Node $$12 : $$0.getClosedSet()) {
                if (!(PathfindingRenderer.distanceToCamera($$12.asBlockPos(), $$4, $$5, $$6) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox(new AABB((float)$$12.x + 0.5f - $$1 / 2.0f, (float)$$12.y + 0.01f, (float)$$12.z + 0.5f - $$1 / 2.0f, (float)$$12.x + 0.5f + $$1 / 2.0f, (double)$$12.y + 0.1, (float)$$12.z + 0.5f + $$1 / 2.0f).move(-$$4, -$$5, -$$6), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (Node $$13 : $$0.getOpenSet()) {
                if (!(PathfindingRenderer.distanceToCamera($$13.asBlockPos(), $$4, $$5, $$6) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox(new AABB((float)$$13.x + 0.5f - $$1 / 2.0f, (float)$$13.y + 0.01f, (float)$$13.z + 0.5f - $$1 / 2.0f, (float)$$13.x + 0.5f + $$1 / 2.0f, (double)$$13.y + 0.1, (float)$$13.z + 0.5f + $$1 / 2.0f).move(-$$4, -$$5, -$$6), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if ($$3) {
            for (int $$14 = 0; $$14 < $$0.getNodeCount(); ++$$14) {
                Node $$15 = $$0.getNode($$14);
                if (!(PathfindingRenderer.distanceToCamera($$15.asBlockPos(), $$4, $$5, $$6) <= 80.0f)) continue;
                DebugRenderer.renderFloatingText(String.valueOf((Object)((Object)$$15.type)), (double)$$15.x + 0.5, (double)$$15.y + 0.75, (double)$$15.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.renderFloatingText(String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)$$15.costMalus)}), (double)$$15.x + 0.5, (double)$$15.y + 0.25, (double)$$15.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void renderPathLine(Path $$0, double $$1, double $$2, double $$3) {
        Tesselator $$4 = Tesselator.getInstance();
        BufferBuilder $$5 = $$4.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        $$5.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int $$6 = 0; $$6 < $$0.getNodeCount(); ++$$6) {
            Node $$7 = $$0.getNode($$6);
            if (PathfindingRenderer.distanceToCamera($$7.asBlockPos(), $$1, $$2, $$3) > 80.0f) continue;
            float $$8 = (float)$$6 / (float)$$0.getNodeCount() * 0.33f;
            int $$9 = $$6 == 0 ? 0 : Mth.hsvToRgb($$8, 0.9f, 0.9f);
            int $$10 = $$9 >> 16 & 0xFF;
            int $$11 = $$9 >> 8 & 0xFF;
            int $$12 = $$9 & 0xFF;
            $$5.vertex((double)$$7.x - $$1 + 0.5, (double)$$7.y - $$2 + 0.5, (double)$$7.z - $$3 + 0.5).color($$10, $$11, $$12, 255).endVertex();
        }
        $$4.end();
    }

    private static float distanceToCamera(BlockPos $$0, double $$1, double $$2, double $$3) {
        return (float)(Math.abs((double)((double)$$0.getX() - $$1)) + Math.abs((double)((double)$$0.getY() - $$2)) + Math.abs((double)((double)$$0.getZ() - $$3)));
    }
}