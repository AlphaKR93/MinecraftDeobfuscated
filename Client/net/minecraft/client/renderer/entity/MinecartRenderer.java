/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartRenderer<T extends AbstractMinecart>
extends EntityRenderer<T> {
    private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
    protected final EntityModel<T> model;
    private final BlockRenderDispatcher blockRenderer;

    public MinecartRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1) {
        super($$0);
        this.shadowRadius = 0.7f;
        this.model = new MinecartModel($$0.bakeLayer($$1));
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        $$3.pushPose();
        long $$6 = (long)((Entity)$$0).getId() * 493286711L;
        $$6 = $$6 * $$6 * 4392167121L + $$6 * 98761L;
        float $$7 = (((float)($$6 >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float $$8 = (((float)($$6 >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float $$9 = (((float)($$6 >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        $$3.translate($$7, $$8, $$9);
        double $$10 = Mth.lerp((double)$$2, ((AbstractMinecart)$$0).xOld, ((Entity)$$0).getX());
        double $$11 = Mth.lerp((double)$$2, ((AbstractMinecart)$$0).yOld, ((Entity)$$0).getY());
        double $$12 = Mth.lerp((double)$$2, ((AbstractMinecart)$$0).zOld, ((Entity)$$0).getZ());
        double $$13 = 0.3f;
        Vec3 $$14 = ((AbstractMinecart)$$0).getPos($$10, $$11, $$12);
        float $$15 = Mth.lerp($$2, ((AbstractMinecart)$$0).xRotO, ((Entity)$$0).getXRot());
        if ($$14 != null) {
            Vec3 $$16 = ((AbstractMinecart)$$0).getPosOffs($$10, $$11, $$12, 0.3f);
            Vec3 $$17 = ((AbstractMinecart)$$0).getPosOffs($$10, $$11, $$12, -0.3f);
            if ($$16 == null) {
                $$16 = $$14;
            }
            if ($$17 == null) {
                $$17 = $$14;
            }
            $$3.translate($$14.x - $$10, ($$16.y + $$17.y) / 2.0 - $$11, $$14.z - $$12);
            Vec3 $$18 = $$17.add(-$$16.x, -$$16.y, -$$16.z);
            if ($$18.length() != 0.0) {
                $$18 = $$18.normalize();
                $$1 = (float)(Math.atan2((double)$$18.z, (double)$$18.x) * 180.0 / Math.PI);
                $$15 = (float)(Math.atan((double)$$18.y) * 73.0);
            }
        }
        $$3.translate(0.0f, 0.375f, 0.0f);
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f - $$1));
        $$3.mulPose(Axis.ZP.rotationDegrees(-$$15));
        float $$19 = (float)((AbstractMinecart)$$0).getHurtTime() - $$2;
        float $$20 = ((AbstractMinecart)$$0).getDamage() - $$2;
        if ($$20 < 0.0f) {
            $$20 = 0.0f;
        }
        if ($$19 > 0.0f) {
            $$3.mulPose(Axis.XP.rotationDegrees(Mth.sin($$19) * $$19 * $$20 / 10.0f * (float)((AbstractMinecart)$$0).getHurtDir()));
        }
        int $$21 = ((AbstractMinecart)$$0).getDisplayOffset();
        BlockState $$22 = ((AbstractMinecart)$$0).getDisplayBlockState();
        if ($$22.getRenderShape() != RenderShape.INVISIBLE) {
            $$3.pushPose();
            float $$23 = 0.75f;
            $$3.scale(0.75f, 0.75f, 0.75f);
            $$3.translate(-0.5f, (float)($$21 - 8) / 16.0f, 0.5f);
            $$3.mulPose(Axis.YP.rotationDegrees(90.0f));
            this.renderMinecartContents($$0, $$2, $$22, $$3, $$4, $$5);
            $$3.popPose();
        }
        $$3.scale(-1.0f, -1.0f, 1.0f);
        this.model.setupAnim($$0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        VertexConsumer $$24 = $$4.getBuffer(this.model.renderType(this.getTextureLocation($$0)));
        this.model.renderToBuffer($$3, $$24, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$3.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T $$0) {
        return MINECART_LOCATION;
    }

    protected void renderMinecartContents(T $$0, float $$1, BlockState $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        this.blockRenderer.renderSingleBlock($$2, $$3, $$4, $$5, OverlayTexture.NO_OVERLAY);
    }
}