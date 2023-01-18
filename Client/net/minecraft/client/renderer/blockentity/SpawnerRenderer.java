/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer
implements BlockEntityRenderer<SpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public SpawnerRenderer(BlockEntityRendererProvider.Context $$0) {
        this.entityRenderer = $$0.getEntityRenderer();
    }

    @Override
    public void render(SpawnerBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        $$2.pushPose();
        $$2.translate(0.5f, 0.0f, 0.5f);
        BaseSpawner $$6 = $$0.getSpawner();
        Entity $$7 = $$6.getOrCreateDisplayEntity($$0.getLevel(), $$0.getLevel().getRandom(), $$0.getBlockPos());
        if ($$7 != null) {
            float $$8 = 0.53125f;
            float $$9 = Math.max((float)$$7.getBbWidth(), (float)$$7.getBbHeight());
            if ((double)$$9 > 1.0) {
                $$8 /= $$9;
            }
            $$2.translate(0.0f, 0.4f, 0.0f);
            $$2.mulPose(Axis.YP.rotationDegrees((float)Mth.lerp((double)$$1, $$6.getoSpin(), $$6.getSpin()) * 10.0f));
            $$2.translate(0.0f, -0.2f, 0.0f);
            $$2.mulPose(Axis.XP.rotationDegrees(-30.0f));
            $$2.scale($$8, $$8, $$8);
            this.entityRenderer.render($$7, 0.0, 0.0, 0.0, 0.0f, $$1, $$2, $$3, $$4);
        }
        $$2.popPose();
    }
}