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
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GuardianRenderer
extends MobRenderer<Guardian, GuardianModel> {
    private static final ResourceLocation GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public GuardianRenderer(EntityRendererProvider.Context $$0) {
        this($$0, 0.5f, ModelLayers.GUARDIAN);
    }

    protected GuardianRenderer(EntityRendererProvider.Context $$0, float $$1, ModelLayerLocation $$2) {
        super($$0, new GuardianModel($$0.bakeLayer($$2)), $$1);
    }

    @Override
    public boolean shouldRender(Guardian $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        LivingEntity $$5;
        if (super.shouldRender($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if ($$0.hasActiveAttackTarget() && ($$5 = $$0.getActiveAttackTarget()) != null) {
            Vec3 $$6 = this.getPosition($$5, (double)$$5.getBbHeight() * 0.5, 1.0f);
            Vec3 $$7 = this.getPosition($$0, $$0.getEyeHeight(), 1.0f);
            return $$1.isVisible(new AABB($$7.x, $$7.y, $$7.z, $$6.x, $$6.y, $$6.z));
        }
        return false;
    }

    private Vec3 getPosition(LivingEntity $$0, double $$1, float $$2) {
        double $$3 = Mth.lerp((double)$$2, $$0.xOld, $$0.getX());
        double $$4 = Mth.lerp((double)$$2, $$0.yOld, $$0.getY()) + $$1;
        double $$5 = Mth.lerp((double)$$2, $$0.zOld, $$0.getZ());
        return new Vec3($$3, $$4, $$5);
    }

    @Override
    public void render(Guardian $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        LivingEntity $$6 = $$0.getActiveAttackTarget();
        if ($$6 != null) {
            float $$7 = $$0.getAttackAnimationScale($$2);
            float $$8 = (float)$$0.level.getGameTime() + $$2;
            float $$9 = $$8 * 0.5f % 1.0f;
            float $$10 = $$0.getEyeHeight();
            $$3.pushPose();
            $$3.translate(0.0f, $$10, 0.0f);
            Vec3 $$11 = this.getPosition($$6, (double)$$6.getBbHeight() * 0.5, $$2);
            Vec3 $$12 = this.getPosition($$0, $$10, $$2);
            Vec3 $$13 = $$11.subtract($$12);
            float $$14 = (float)($$13.length() + 1.0);
            $$13 = $$13.normalize();
            float $$15 = (float)Math.acos((double)$$13.y);
            float $$16 = (float)Math.atan2((double)$$13.z, (double)$$13.x);
            $$3.mulPose(Axis.YP.rotationDegrees((1.5707964f - $$16) * 57.295776f));
            $$3.mulPose(Axis.XP.rotationDegrees($$15 * 57.295776f));
            boolean $$17 = true;
            float $$18 = $$8 * 0.05f * -1.5f;
            float $$19 = $$7 * $$7;
            int $$20 = 64 + (int)($$19 * 191.0f);
            int $$21 = 32 + (int)($$19 * 191.0f);
            int $$22 = 128 - (int)($$19 * 64.0f);
            float $$23 = 0.2f;
            float $$24 = 0.282f;
            float $$25 = Mth.cos($$18 + 2.3561945f) * 0.282f;
            float $$26 = Mth.sin($$18 + 2.3561945f) * 0.282f;
            float $$27 = Mth.cos($$18 + 0.7853982f) * 0.282f;
            float $$28 = Mth.sin($$18 + 0.7853982f) * 0.282f;
            float $$29 = Mth.cos($$18 + 3.926991f) * 0.282f;
            float $$30 = Mth.sin($$18 + 3.926991f) * 0.282f;
            float $$31 = Mth.cos($$18 + 5.4977875f) * 0.282f;
            float $$32 = Mth.sin($$18 + 5.4977875f) * 0.282f;
            float $$33 = Mth.cos($$18 + (float)Math.PI) * 0.2f;
            float $$34 = Mth.sin($$18 + (float)Math.PI) * 0.2f;
            float $$35 = Mth.cos($$18 + 0.0f) * 0.2f;
            float $$36 = Mth.sin($$18 + 0.0f) * 0.2f;
            float $$37 = Mth.cos($$18 + 1.5707964f) * 0.2f;
            float $$38 = Mth.sin($$18 + 1.5707964f) * 0.2f;
            float $$39 = Mth.cos($$18 + 4.712389f) * 0.2f;
            float $$40 = Mth.sin($$18 + 4.712389f) * 0.2f;
            float $$41 = $$14;
            float $$42 = 0.0f;
            float $$43 = 0.4999f;
            float $$44 = -1.0f + $$9;
            float $$45 = $$14 * 2.5f + $$44;
            VertexConsumer $$46 = $$4.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose $$47 = $$3.last();
            Matrix4f $$48 = $$47.pose();
            Matrix3f $$49 = $$47.normal();
            GuardianRenderer.vertex($$46, $$48, $$49, $$33, $$41, $$34, $$20, $$21, $$22, 0.4999f, $$45);
            GuardianRenderer.vertex($$46, $$48, $$49, $$33, 0.0f, $$34, $$20, $$21, $$22, 0.4999f, $$44);
            GuardianRenderer.vertex($$46, $$48, $$49, $$35, 0.0f, $$36, $$20, $$21, $$22, 0.0f, $$44);
            GuardianRenderer.vertex($$46, $$48, $$49, $$35, $$41, $$36, $$20, $$21, $$22, 0.0f, $$45);
            GuardianRenderer.vertex($$46, $$48, $$49, $$37, $$41, $$38, $$20, $$21, $$22, 0.4999f, $$45);
            GuardianRenderer.vertex($$46, $$48, $$49, $$37, 0.0f, $$38, $$20, $$21, $$22, 0.4999f, $$44);
            GuardianRenderer.vertex($$46, $$48, $$49, $$39, 0.0f, $$40, $$20, $$21, $$22, 0.0f, $$44);
            GuardianRenderer.vertex($$46, $$48, $$49, $$39, $$41, $$40, $$20, $$21, $$22, 0.0f, $$45);
            float $$50 = 0.0f;
            if ($$0.tickCount % 2 == 0) {
                $$50 = 0.5f;
            }
            GuardianRenderer.vertex($$46, $$48, $$49, $$25, $$41, $$26, $$20, $$21, $$22, 0.5f, $$50 + 0.5f);
            GuardianRenderer.vertex($$46, $$48, $$49, $$27, $$41, $$28, $$20, $$21, $$22, 1.0f, $$50 + 0.5f);
            GuardianRenderer.vertex($$46, $$48, $$49, $$31, $$41, $$32, $$20, $$21, $$22, 1.0f, $$50);
            GuardianRenderer.vertex($$46, $$48, $$49, $$29, $$41, $$30, $$20, $$21, $$22, 0.5f, $$50);
            $$3.popPose();
        }
    }

    private static void vertex(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, float $$3, float $$4, float $$5, int $$6, int $$7, int $$8, float $$9, float $$10) {
        $$0.vertex($$1, $$3, $$4, $$5).color($$6, $$7, $$8, 255).uv($$9, $$10).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal($$2, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Guardian $$0) {
        return GUARDIAN_LOCATION;
    }
}