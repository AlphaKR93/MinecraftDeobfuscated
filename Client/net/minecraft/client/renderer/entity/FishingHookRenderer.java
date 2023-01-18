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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class FishingHookRenderer
extends EntityRenderer<FishingHook> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final double VIEW_BOBBING_SCALE = 960.0;

    public FishingHookRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(FishingHook $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        float $$29;
        double $$28;
        double $$27;
        double $$26;
        Player $$6 = $$0.getPlayerOwner();
        if ($$6 == null) {
            return;
        }
        $$3.pushPose();
        $$3.pushPose();
        $$3.scale(0.5f, 0.5f, 0.5f);
        $$3.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
        PoseStack.Pose $$7 = $$3.last();
        Matrix4f $$8 = $$7.pose();
        Matrix3f $$9 = $$7.normal();
        VertexConsumer $$10 = $$4.getBuffer(RENDER_TYPE);
        FishingHookRenderer.vertex($$10, $$8, $$9, $$5, 0.0f, 0, 0, 1);
        FishingHookRenderer.vertex($$10, $$8, $$9, $$5, 1.0f, 0, 1, 1);
        FishingHookRenderer.vertex($$10, $$8, $$9, $$5, 1.0f, 1, 1, 0);
        FishingHookRenderer.vertex($$10, $$8, $$9, $$5, 0.0f, 1, 0, 0);
        $$3.popPose();
        int $$11 = $$6.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack $$12 = $$6.getMainHandItem();
        if (!$$12.is(Items.FISHING_ROD)) {
            $$11 = -$$11;
        }
        float $$13 = $$6.getAttackAnim($$2);
        float $$14 = Mth.sin(Mth.sqrt($$13) * (float)Math.PI);
        float $$15 = Mth.lerp($$2, $$6.yBodyRotO, $$6.yBodyRot) * ((float)Math.PI / 180);
        double $$16 = Mth.sin($$15);
        double $$17 = Mth.cos($$15);
        double $$18 = (double)$$11 * 0.35;
        double $$19 = 0.8;
        if (this.entityRenderDispatcher.options != null && !this.entityRenderDispatcher.options.getCameraType().isFirstPerson() || $$6 != Minecraft.getInstance().player) {
            double $$20 = Mth.lerp((double)$$2, $$6.xo, $$6.getX()) - $$17 * $$18 - $$16 * 0.8;
            double $$21 = $$6.yo + (double)$$6.getEyeHeight() + ($$6.getY() - $$6.yo) * (double)$$2 - 0.45;
            double $$22 = Mth.lerp((double)$$2, $$6.zo, $$6.getZ()) - $$16 * $$18 + $$17 * 0.8;
            float $$23 = $$6.isCrouching() ? -0.1875f : 0.0f;
        } else {
            double $$24 = 960.0 / (double)this.entityRenderDispatcher.options.fov().get().intValue();
            Vec3 $$25 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)$$11 * 0.525f, -0.1f);
            $$25 = $$25.scale($$24);
            $$25 = $$25.yRot($$14 * 0.5f);
            $$25 = $$25.xRot(-$$14 * 0.7f);
            $$26 = Mth.lerp((double)$$2, $$6.xo, $$6.getX()) + $$25.x;
            $$27 = Mth.lerp((double)$$2, $$6.yo, $$6.getY()) + $$25.y;
            $$28 = Mth.lerp((double)$$2, $$6.zo, $$6.getZ()) + $$25.z;
            $$29 = $$6.getEyeHeight();
        }
        double $$30 = Mth.lerp((double)$$2, $$0.xo, $$0.getX());
        double $$31 = Mth.lerp((double)$$2, $$0.yo, $$0.getY()) + 0.25;
        double $$32 = Mth.lerp((double)$$2, $$0.zo, $$0.getZ());
        float $$33 = (float)($$26 - $$30);
        float $$34 = (float)($$27 - $$31) + $$29;
        float $$35 = (float)($$28 - $$32);
        VertexConsumer $$36 = $$4.getBuffer(RenderType.lineStrip());
        PoseStack.Pose $$37 = $$3.last();
        int $$38 = 16;
        for (int $$39 = 0; $$39 <= 16; ++$$39) {
            FishingHookRenderer.stringVertex($$33, $$34, $$35, $$36, $$37, FishingHookRenderer.fraction($$39, 16), FishingHookRenderer.fraction($$39 + 1, 16));
        }
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static float fraction(int $$0, int $$1) {
        return (float)$$0 / (float)$$1;
    }

    private static void vertex(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, int $$3, float $$4, int $$5, int $$6, int $$7) {
        $$0.vertex($$1, $$4 - 0.5f, (float)$$5 - 0.5f, 0.0f).color(255, 255, 255, 255).uv($$6, $$7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$3).normal($$2, 0.0f, 1.0f, 0.0f).endVertex();
    }

    private static void stringVertex(float $$0, float $$1, float $$2, VertexConsumer $$3, PoseStack.Pose $$4, float $$5, float $$6) {
        float $$7 = $$0 * $$5;
        float $$8 = $$1 * ($$5 * $$5 + $$5) * 0.5f + 0.25f;
        float $$9 = $$2 * $$5;
        float $$10 = $$0 * $$6 - $$7;
        float $$11 = $$1 * ($$6 * $$6 + $$6) * 0.5f + 0.25f - $$8;
        float $$12 = $$2 * $$6 - $$9;
        float $$13 = Mth.sqrt($$10 * $$10 + $$11 * $$11 + $$12 * $$12);
        $$3.vertex($$4.pose(), $$7, $$8, $$9).color(0, 0, 0, 255).normal($$4.normal(), $$10 /= $$13, $$11 /= $$13, $$12 /= $$13).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(FishingHook $$0) {
        return TEXTURE_LOCATION;
    }
}