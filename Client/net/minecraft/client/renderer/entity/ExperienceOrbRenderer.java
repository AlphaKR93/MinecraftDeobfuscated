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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ExperienceOrbRenderer
extends EntityRenderer<ExperienceOrb> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

    public ExperienceOrbRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    protected int getBlockLightLevel(ExperienceOrb $$0, BlockPos $$1) {
        return Mth.clamp(super.getBlockLightLevel($$0, $$1) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrb $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        int $$6 = $$0.getIcon();
        float $$7 = (float)($$6 % 4 * 16 + 0) / 64.0f;
        float $$8 = (float)($$6 % 4 * 16 + 16) / 64.0f;
        float $$9 = (float)($$6 / 4 * 16 + 0) / 64.0f;
        float $$10 = (float)($$6 / 4 * 16 + 16) / 64.0f;
        float $$11 = 1.0f;
        float $$12 = 0.5f;
        float $$13 = 0.25f;
        float $$14 = 255.0f;
        float $$15 = ((float)$$0.tickCount + $$2) / 2.0f;
        int $$16 = (int)((Mth.sin($$15 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int $$17 = 255;
        int $$18 = (int)((Mth.sin($$15 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        $$3.translate(0.0f, 0.1f, 0.0f);
        $$3.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
        float $$19 = 0.3f;
        $$3.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer $$20 = $$4.getBuffer(RENDER_TYPE);
        PoseStack.Pose $$21 = $$3.last();
        Matrix4f $$22 = $$21.pose();
        Matrix3f $$23 = $$21.normal();
        ExperienceOrbRenderer.vertex($$20, $$22, $$23, -0.5f, -0.25f, $$16, 255, $$18, $$7, $$10, $$5);
        ExperienceOrbRenderer.vertex($$20, $$22, $$23, 0.5f, -0.25f, $$16, 255, $$18, $$8, $$10, $$5);
        ExperienceOrbRenderer.vertex($$20, $$22, $$23, 0.5f, 0.75f, $$16, 255, $$18, $$8, $$9, $$5);
        ExperienceOrbRenderer.vertex($$20, $$22, $$23, -0.5f, 0.75f, $$16, 255, $$18, $$7, $$9, $$5);
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static void vertex(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, float $$3, float $$4, int $$5, int $$6, int $$7, float $$8, float $$9, int $$10) {
        $$0.vertex($$1, $$3, $$4, 0.0f).color($$5, $$6, $$7, 128).uv($$8, $$9).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$10).normal($$2, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ExperienceOrb $$0) {
        return EXPERIENCE_ORB_LOCATION;
    }
}