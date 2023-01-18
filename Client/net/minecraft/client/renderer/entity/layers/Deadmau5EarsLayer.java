/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;

public class Deadmau5EarsLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public Deadmau5EarsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!"deadmau5".equals((Object)$$3.getName().getString()) || !$$3.isSkinLoaded() || $$3.isInvisible()) {
            return;
        }
        VertexConsumer $$10 = $$1.getBuffer(RenderType.entitySolid($$3.getSkinTextureLocation()));
        int $$11 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
        for (int $$12 = 0; $$12 < 2; ++$$12) {
            float $$13 = Mth.lerp($$6, $$3.yRotO, $$3.getYRot()) - Mth.lerp($$6, $$3.yBodyRotO, $$3.yBodyRot);
            float $$14 = Mth.lerp($$6, $$3.xRotO, $$3.getXRot());
            $$0.pushPose();
            $$0.mulPose(Axis.YP.rotationDegrees($$13));
            $$0.mulPose(Axis.XP.rotationDegrees($$14));
            $$0.translate(0.375f * (float)($$12 * 2 - 1), 0.0f, 0.0f);
            $$0.translate(0.0f, -0.375f, 0.0f);
            $$0.mulPose(Axis.XP.rotationDegrees(-$$14));
            $$0.mulPose(Axis.YP.rotationDegrees(-$$13));
            float $$15 = 1.3333334f;
            $$0.scale(1.3333334f, 1.3333334f, 1.3333334f);
            ((PlayerModel)this.getParentModel()).renderEars($$0, $$10, $$2, $$11);
            $$0.popPose();
        }
    }
}