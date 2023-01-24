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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class ThrownItemRenderer<T extends Entity>
extends EntityRenderer<T> {
    private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public ThrownItemRenderer(EntityRendererProvider.Context $$0, float $$1, boolean $$2) {
        super($$0);
        this.itemRenderer = $$0.getItemRenderer();
        this.scale = $$1;
        this.fullBright = $$2;
    }

    public ThrownItemRenderer(EntityRendererProvider.Context $$0) {
        this($$0, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        return this.fullBright ? 15 : super.getBlockLightLevel($$0, $$1);
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if (((Entity)$$0).tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr((Entity)$$0) < 12.25) {
            return;
        }
        $$3.pushPose();
        $$3.scale(this.scale, this.scale, this.scale);
        $$3.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f));
        this.itemRenderer.renderStatic(((ItemSupplier)$$0).getItem(), ItemTransforms.TransformType.GROUND, $$5, OverlayTexture.NO_OVERLAY, $$3, $$4, ((Entity)$$0).level, ((Entity)$$0).getId());
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}