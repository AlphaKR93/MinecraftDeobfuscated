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
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer
extends EntityRenderer<EvokerFangs> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsModel<EvokerFangs> model;

    public EvokerFangsRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new EvokerFangsModel($$0.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangs $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        float $$6 = $$0.getAnimationProgress($$2);
        if ($$6 == 0.0f) {
            return;
        }
        float $$7 = 2.0f;
        if ($$6 > 0.9f) {
            $$7 *= (1.0f - $$6) / 0.1f;
        }
        $$3.pushPose();
        $$3.mulPose(Axis.YP.rotationDegrees(90.0f - $$0.getYRot()));
        $$3.scale(-$$7, -$$7, $$7);
        float $$8 = 0.03125f;
        $$3.translate(0.0, -0.626, 0.0);
        $$3.scale(0.5f, 0.5f, 0.5f);
        this.model.setupAnim($$0, $$6, 0.0f, 0.0f, $$0.getYRot(), $$0.getXRot());
        VertexConsumer $$9 = $$4.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer($$3, $$9, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(EvokerFangs $$0) {
        return TEXTURE_LOCATION;
    }
}