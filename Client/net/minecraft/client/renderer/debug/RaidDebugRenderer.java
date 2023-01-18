/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class RaidDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private static final float TEXT_SCALE = 0.04f;
    private final Minecraft minecraft;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void setRaidCenters(Collection<BlockPos> $$0) {
        this.raidCenters = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        BlockPos $$5 = this.getCamera().getBlockPosition();
        for (BlockPos $$6 : this.raidCenters) {
            if (!$$5.closerThan($$6, 160.0)) continue;
            RaidDebugRenderer.highlightRaidCenter($$6);
        }
    }

    private static void highlightRaidCenter(BlockPos $$0) {
        DebugRenderer.renderFilledBox($$0.offset(-0.5, -0.5, -0.5), $$0.offset(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        int $$1 = -65536;
        RaidDebugRenderer.renderTextOverBlock("Raid center", $$0, -65536);
    }

    private static void renderTextOverBlock(String $$0, BlockPos $$1, int $$2) {
        double $$3 = (double)$$1.getX() + 0.5;
        double $$4 = (double)$$1.getY() + 1.3;
        double $$5 = (double)$$1.getZ() + 0.5;
        DebugRenderer.renderFloatingText($$0, $$3, $$4, $$5, $$2, 0.04f, true, 0.0f, true);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }
}