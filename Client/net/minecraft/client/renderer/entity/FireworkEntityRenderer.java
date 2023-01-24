/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

public class FireworkEntityRenderer
extends EntityRenderer<FireworkRocketEntity> {
    private final ItemRenderer itemRenderer;

    public FireworkEntityRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemRenderer = $$0.getItemRenderer();
    }

    @Override
    public void render(FireworkRocketEntity $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        $$3.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
        if ($$0.isShotAtAngle()) {
            $$3.mulPose(Axis.ZP.rotationDegrees(180.0f));
            $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
            $$3.mulPose(Axis.XP.rotationDegrees(90.0f));
        }
        this.itemRenderer.renderStatic($$0.getItem(), ItemTransforms.TransformType.GROUND, $$5, OverlayTexture.NO_OVERLAY, $$3, $$4, $$0.level, $$0.getId());
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(FireworkRocketEntity $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}