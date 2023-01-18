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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DragonFireballRenderer
extends EntityRenderer<DragonFireball> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public DragonFireballRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    protected int getBlockLightLevel(DragonFireball $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public void render(DragonFireball $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        $$3.scale(2.0f, 2.0f, 2.0f);
        $$3.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
        PoseStack.Pose $$6 = $$3.last();
        Matrix4f $$7 = $$6.pose();
        Matrix3f $$8 = $$6.normal();
        VertexConsumer $$9 = $$4.getBuffer(RENDER_TYPE);
        DragonFireballRenderer.vertex($$9, $$7, $$8, $$5, 0.0f, 0, 0, 1);
        DragonFireballRenderer.vertex($$9, $$7, $$8, $$5, 1.0f, 0, 1, 1);
        DragonFireballRenderer.vertex($$9, $$7, $$8, $$5, 1.0f, 1, 1, 0);
        DragonFireballRenderer.vertex($$9, $$7, $$8, $$5, 0.0f, 1, 0, 0);
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static void vertex(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, int $$3, float $$4, int $$5, int $$6, int $$7) {
        $$0.vertex($$1, $$4 - 0.5f, (float)$$5 - 0.25f, 0.0f).color(255, 255, 255, 255).uv($$6, $$7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$3).normal($$2, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(DragonFireball $$0) {
        return TEXTURE_LOCATION;
    }
}