/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class GoalSelectorDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private final Minecraft minecraft;
    private final Map<Integer, List<DebugGoal>> goalSelectors = Maps.newHashMap();

    @Override
    public void clear() {
        this.goalSelectors.clear();
    }

    public void addGoalSelector(int $$0, List<DebugGoal> $$1) {
        this.goalSelectors.put((Object)$$0, $$1);
    }

    public void removeGoalSelector(int $$0) {
        this.goalSelectors.remove((Object)$$0);
    }

    public GoalSelectorDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$12, double $$22, double $$3, double $$4) {
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos $$6 = new BlockPos($$5.getPosition().x, 0.0, $$5.getPosition().z);
        this.goalSelectors.forEach(($$1, $$2) -> {
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                DebugGoal $$4 = (DebugGoal)$$2.get($$3);
                if (!$$6.closerThan($$4.pos, 160.0)) continue;
                double $$5 = (double)$$4.pos.getX() + 0.5;
                double $$6 = (double)$$4.pos.getY() + 2.0 + (double)$$3 * 0.25;
                double $$7 = (double)$$4.pos.getZ() + 0.5;
                int $$8 = $$4.isRunning ? -16711936 : -3355444;
                DebugRenderer.renderFloatingText($$4.name, $$5, $$6, $$7, $$8);
            }
        });
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
    }

    public static class DebugGoal {
        public final BlockPos pos;
        public final int priority;
        public final String name;
        public final boolean isRunning;

        public DebugGoal(BlockPos $$0, int $$1, String $$2, boolean $$3) {
            this.pos = $$0;
            this.priority = $$1;
            this.name = $$2;
            this.isRunning = $$3;
        }
    }
}