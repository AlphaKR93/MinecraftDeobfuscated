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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PowerableMob;

public abstract class EnergySwirlLayer<T extends Entity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public EnergySwirlLayer(RenderLayerParent<T, M> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!((PowerableMob)$$3).isPowered()) {
            return;
        }
        float $$10 = (float)((Entity)$$3).tickCount + $$6;
        EntityModel<T> $$11 = this.model();
        $$11.prepareMobModel($$3, $$4, $$5, $$6);
        ((EntityModel)this.getParentModel()).copyPropertiesTo($$11);
        VertexConsumer $$12 = $$1.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset($$10) % 1.0f, $$10 * 0.01f % 1.0f));
        $$11.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        $$11.renderToBuffer($$0, $$12, $$2, OverlayTexture.NO_OVERLAY, 0.5f, 0.5f, 0.5f, 1.0f);
    }

    protected abstract float xOffset(float var1);

    protected abstract ResourceLocation getTextureLocation();

    protected abstract EntityModel<T> model();
}