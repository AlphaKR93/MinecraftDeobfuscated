/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class ArrowRenderer<T extends AbstractArrow>
extends EntityRenderer<T> {
    public ArrowRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        $$3.mulPose(Axis.YP.rotationDegrees(Mth.lerp($$2, ((AbstractArrow)$$0).yRotO, ((Entity)$$0).getYRot()) - 90.0f));
        $$3.mulPose(Axis.ZP.rotationDegrees(Mth.lerp($$2, ((AbstractArrow)$$0).xRotO, ((Entity)$$0).getXRot())));
        boolean $$6 = false;
        float $$7 = 0.0f;
        float $$8 = 0.5f;
        float $$9 = 0.0f;
        float $$10 = 0.15625f;
        float $$11 = 0.0f;
        float $$12 = 0.15625f;
        float $$13 = 0.15625f;
        float $$14 = 0.3125f;
        float $$15 = 0.05625f;
        float $$16 = (float)((AbstractArrow)$$0).shakeTime - $$2;
        if ($$16 > 0.0f) {
            float $$17 = -Mth.sin($$16 * 3.0f) * $$16;
            $$3.mulPose(Axis.ZP.rotationDegrees($$17));
        }
        $$3.mulPose(Axis.XP.rotationDegrees(45.0f));
        $$3.scale(0.05625f, 0.05625f, 0.05625f);
        $$3.translate(-4.0f, 0.0f, 0.0f);
        VertexConsumer $$18 = $$4.getBuffer(RenderType.entityCutout(this.getTextureLocation($$0)));
        PoseStack.Pose $$19 = $$3.last();
        Matrix4f $$20 = $$19.pose();
        Matrix3f $$21 = $$19.normal();
        this.vertex($$20, $$21, $$18, -7, -2, -2, 0.0f, 0.15625f, -1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, 2, -2, 0.0f, 0.3125f, -1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, 2, -2, 0.0f, 0.15625f, 1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, $$5);
        this.vertex($$20, $$21, $$18, -7, -2, -2, 0.0f, 0.3125f, 1, 0, 0, $$5);
        for (int $$22 = 0; $$22 < 4; ++$$22) {
            $$3.mulPose(Axis.XP.rotationDegrees(90.0f));
            this.vertex($$20, $$21, $$18, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, $$5);
            this.vertex($$20, $$21, $$18, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, $$5);
            this.vertex($$20, $$21, $$18, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, $$5);
            this.vertex($$20, $$21, $$18, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, $$5);
        }
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void vertex(Matrix4f $$0, Matrix3f $$1, VertexConsumer $$2, int $$3, int $$4, int $$5, float $$6, float $$7, int $$8, int $$9, int $$10, int $$11) {
        $$2.vertex($$0, $$3, $$4, $$5).color(255, 255, 255, 255).uv($$6, $$7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$11).normal($$1, $$8, $$10, $$9).endVertex();
    }
}