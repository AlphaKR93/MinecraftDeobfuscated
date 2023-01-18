/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;

public class SaddleLayer<T extends Entity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private final ResourceLocation textureLocation;
    private final M model;

    public SaddleLayer(RenderLayerParent<T, M> $$0, M $$1, ResourceLocation $$2) {
        super($$0);
        this.model = $$1;
        this.textureLocation = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!((Saddleable)$$3).isSaddled()) {
            return;
        }
        ((EntityModel)this.getParentModel()).copyPropertiesTo(this.model);
        ((EntityModel)this.model).prepareMobModel($$3, $$4, $$5, $$6);
        ((EntityModel)this.model).setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        VertexConsumer $$10 = $$1.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
        ((Model)this.model).renderToBuffer($$0, $$10, $$2, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}