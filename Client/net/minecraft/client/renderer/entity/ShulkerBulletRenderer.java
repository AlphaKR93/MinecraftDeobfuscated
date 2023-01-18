/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer
extends EntityRenderer<ShulkerBullet> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    private final ShulkerBulletModel<ShulkerBullet> model;

    public ShulkerBulletRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new ShulkerBulletModel($$0.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLightLevel(ShulkerBullet $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public void render(ShulkerBullet $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        float $$6 = Mth.rotlerp($$0.yRotO, $$0.getYRot(), $$2);
        float $$7 = Mth.lerp($$2, $$0.xRotO, $$0.getXRot());
        float $$8 = (float)$$0.tickCount + $$2;
        $$3.translate(0.0f, 0.15f, 0.0f);
        $$3.mulPose(Axis.YP.rotationDegrees(Mth.sin($$8 * 0.1f) * 180.0f));
        $$3.mulPose(Axis.XP.rotationDegrees(Mth.cos($$8 * 0.1f) * 180.0f));
        $$3.mulPose(Axis.ZP.rotationDegrees(Mth.sin($$8 * 0.15f) * 360.0f));
        $$3.scale(-0.5f, -0.5f, 0.5f);
        this.model.setupAnim($$0, 0.0f, 0.0f, 0.0f, $$6, $$7);
        VertexConsumer $$9 = $$4.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer($$3, $$9, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$3.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer $$10 = $$4.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer($$3, $$10, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.15f);
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkerBullet $$0) {
        return TEXTURE_LOCATION;
    }
}