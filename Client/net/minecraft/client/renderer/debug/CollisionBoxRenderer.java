/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Double
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionBoxRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> shapes = Collections.emptyList();

    public CollisionBoxRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        double $$5 = Util.getNanos();
        if ($$5 - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = $$5;
            Entity $$6 = this.minecraft.gameRenderer.getMainCamera().getEntity();
            this.shapes = ImmutableList.copyOf((Iterable)$$6.level.getCollisions($$6, $$6.getBoundingBox().inflate(6.0)));
        }
        VertexConsumer $$7 = $$1.getBuffer(RenderType.lines());
        for (VoxelShape $$8 : this.shapes) {
            LevelRenderer.renderVoxelShape($$0, $$7, $$8, -$$2, -$$3, -$$4, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}