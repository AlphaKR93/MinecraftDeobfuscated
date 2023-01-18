/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public static final int LEASH_RENDER_STEPS = 24;

    public MobRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected boolean shouldShowName(T $$0) {
        return super.shouldShowName($$0) && (((LivingEntity)$$0).shouldShowName() || ((Entity)$$0).hasCustomName() && $$0 == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    public boolean shouldRender(T $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        if (super.shouldRender($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        Entity $$5 = ((Mob)$$0).getLeashHolder();
        if ($$5 != null) {
            return $$1.isVisible($$5.getBoundingBoxForCulling());
        }
        return false;
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        Entity $$6 = ((Mob)$$0).getLeashHolder();
        if ($$6 == null) {
            return;
        }
        this.renderLeash($$0, $$2, $$3, $$4, $$6);
    }

    private <E extends Entity> void renderLeash(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, E $$4) {
        $$2.pushPose();
        Vec3 $$5 = $$4.getRopeHoldPosition($$1);
        double $$6 = (double)(Mth.lerp($$1, ((Mob)$$0).yBodyRotO, ((Mob)$$0).yBodyRot) * ((float)Math.PI / 180)) + 1.5707963267948966;
        Vec3 $$7 = ((Entity)$$0).getLeashOffset($$1);
        double $$8 = Math.cos((double)$$6) * $$7.z + Math.sin((double)$$6) * $$7.x;
        double $$9 = Math.sin((double)$$6) * $$7.z - Math.cos((double)$$6) * $$7.x;
        double $$10 = Mth.lerp((double)$$1, ((Mob)$$0).xo, ((Entity)$$0).getX()) + $$8;
        double $$11 = Mth.lerp((double)$$1, ((Mob)$$0).yo, ((Entity)$$0).getY()) + $$7.y;
        double $$12 = Mth.lerp((double)$$1, ((Mob)$$0).zo, ((Entity)$$0).getZ()) + $$9;
        $$2.translate($$8, $$7.y, $$9);
        float $$13 = (float)($$5.x - $$10);
        float $$14 = (float)($$5.y - $$11);
        float $$15 = (float)($$5.z - $$12);
        float $$16 = 0.025f;
        VertexConsumer $$17 = $$3.getBuffer(RenderType.leash());
        Matrix4f $$18 = $$2.last().pose();
        float $$19 = Mth.fastInvSqrt($$13 * $$13 + $$15 * $$15) * 0.025f / 2.0f;
        float $$20 = $$15 * $$19;
        float $$21 = $$13 * $$19;
        BlockPos $$22 = new BlockPos(((Entity)$$0).getEyePosition($$1));
        BlockPos $$23 = new BlockPos($$4.getEyePosition($$1));
        int $$24 = this.getBlockLightLevel($$0, $$22);
        int $$25 = this.entityRenderDispatcher.getRenderer($$4).getBlockLightLevel($$4, $$23);
        int $$26 = ((Mob)$$0).level.getBrightness(LightLayer.SKY, $$22);
        int $$27 = ((Mob)$$0).level.getBrightness(LightLayer.SKY, $$23);
        for (int $$28 = 0; $$28 <= 24; ++$$28) {
            MobRenderer.addVertexPair($$17, $$18, $$13, $$14, $$15, $$24, $$25, $$26, $$27, 0.025f, 0.025f, $$20, $$21, $$28, false);
        }
        for (int $$29 = 24; $$29 >= 0; --$$29) {
            MobRenderer.addVertexPair($$17, $$18, $$13, $$14, $$15, $$24, $$25, $$26, $$27, 0.025f, 0.0f, $$20, $$21, $$29, true);
        }
        $$2.popPose();
    }

    private static void addVertexPair(VertexConsumer $$0, Matrix4f $$1, float $$2, float $$3, float $$4, int $$5, int $$6, int $$7, int $$8, float $$9, float $$10, float $$11, float $$12, int $$13, boolean $$14) {
        float $$15 = (float)$$13 / 24.0f;
        int $$16 = (int)Mth.lerp($$15, $$5, $$6);
        int $$17 = (int)Mth.lerp($$15, $$7, $$8);
        int $$18 = LightTexture.pack($$16, $$17);
        float $$19 = $$13 % 2 == ($$14 ? 1 : 0) ? 0.7f : 1.0f;
        float $$20 = 0.5f * $$19;
        float $$21 = 0.4f * $$19;
        float $$22 = 0.3f * $$19;
        float $$23 = $$2 * $$15;
        float $$24 = $$3 > 0.0f ? $$3 * $$15 * $$15 : $$3 - $$3 * (1.0f - $$15) * (1.0f - $$15);
        float $$25 = $$4 * $$15;
        $$0.vertex($$1, $$23 - $$11, $$24 + $$10, $$25 + $$12).color($$20, $$21, $$22, 1.0f).uv2($$18).endVertex();
        $$0.vertex($$1, $$23 + $$11, $$24 + $$9 - $$10, $$25 - $$12).color($$20, $$21, $$22, 1.0f).uv2($$18).endVertex();
    }
}