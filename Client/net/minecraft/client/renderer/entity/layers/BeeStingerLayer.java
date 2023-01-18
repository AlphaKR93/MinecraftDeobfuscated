/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeeStingerLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends StuckInBodyLayer<T, M> {
    private static final ResourceLocation BEE_STINGER_LOCATION = new ResourceLocation("textures/entity/bee/bee_stinger.png");

    public BeeStingerLayer(LivingEntityRenderer<T, M> $$0) {
        super($$0);
    }

    @Override
    protected int numStuck(T $$0) {
        return ((LivingEntity)$$0).getStingerCount();
    }

    @Override
    protected void renderStuckItem(PoseStack $$0, MultiBufferSource $$1, int $$2, Entity $$3, float $$4, float $$5, float $$6, float $$7) {
        float $$8 = Mth.sqrt($$4 * $$4 + $$6 * $$6);
        float $$9 = (float)(Math.atan2((double)$$4, (double)$$6) * 57.2957763671875);
        float $$10 = (float)(Math.atan2((double)$$5, (double)$$8) * 57.2957763671875);
        $$0.translate(0.0f, 0.0f, 0.0f);
        $$0.mulPose(Axis.YP.rotationDegrees($$9 - 90.0f));
        $$0.mulPose(Axis.ZP.rotationDegrees($$10));
        float $$11 = 0.0f;
        float $$12 = 0.125f;
        float $$13 = 0.0f;
        float $$14 = 0.0625f;
        float $$15 = 0.03125f;
        $$0.mulPose(Axis.XP.rotationDegrees(45.0f));
        $$0.scale(0.03125f, 0.03125f, 0.03125f);
        $$0.translate(2.5f, 0.0f, 0.0f);
        VertexConsumer $$16 = $$1.getBuffer(RenderType.entityCutoutNoCull(BEE_STINGER_LOCATION));
        for (int $$17 = 0; $$17 < 4; ++$$17) {
            $$0.mulPose(Axis.XP.rotationDegrees(90.0f));
            PoseStack.Pose $$18 = $$0.last();
            Matrix4f $$19 = $$18.pose();
            Matrix3f $$20 = $$18.normal();
            BeeStingerLayer.vertex($$16, $$19, $$20, -4.5f, -1, 0.0f, 0.0f, $$2);
            BeeStingerLayer.vertex($$16, $$19, $$20, 4.5f, -1, 0.125f, 0.0f, $$2);
            BeeStingerLayer.vertex($$16, $$19, $$20, 4.5f, 1, 0.125f, 0.0625f, $$2);
            BeeStingerLayer.vertex($$16, $$19, $$20, -4.5f, 1, 0.0f, 0.0625f, $$2);
        }
    }

    private static void vertex(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, float $$3, int $$4, float $$5, float $$6, int $$7) {
        $$0.vertex($$1, $$3, $$4, 0.0f).color(255, 255, 255, 255).uv($$5, $$6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$7).normal($$2, 0.0f, 1.0f, 0.0f).endVertex();
    }
}