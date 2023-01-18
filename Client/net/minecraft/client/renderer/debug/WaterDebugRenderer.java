/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public class WaterDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public WaterDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        BlockPos $$5 = this.minecraft.player.blockPosition();
        Level $$6 = this.minecraft.player.level;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.lineWidth(6.0f);
        for (BlockPos $$7 : BlockPos.betweenClosed($$5.offset(-10, -10, -10), $$5.offset(10, 10, 10))) {
            FluidState $$8 = $$6.getFluidState($$7);
            if (!$$8.is(FluidTags.WATER)) continue;
            double $$9 = (float)$$7.getY() + $$8.getHeight($$6, $$7);
            DebugRenderer.renderFilledBox(new AABB((float)$$7.getX() + 0.01f, (float)$$7.getY() + 0.01f, (float)$$7.getZ() + 0.01f, (float)$$7.getX() + 0.99f, $$9, (float)$$7.getZ() + 0.99f).move(-$$2, -$$3, -$$4), 1.0f, 1.0f, 1.0f, 0.2f);
        }
        for (BlockPos $$10 : BlockPos.betweenClosed($$5.offset(-10, -10, -10), $$5.offset(10, 10, 10))) {
            FluidState $$11 = $$6.getFluidState($$10);
            if (!$$11.is(FluidTags.WATER)) continue;
            DebugRenderer.renderFloatingText(String.valueOf((int)$$11.getAmount()), (double)$$10.getX() + 0.5, (float)$$10.getY() + $$11.getHeight($$6, $$10), (double)$$10.getZ() + 0.5, -16777216);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}