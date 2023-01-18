/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
    private final RenderLayerParent<T, M> renderer;

    public RenderLayer(RenderLayerParent<T, M> $$0) {
        this.renderer = $$0;
    }

    protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> $$0, EntityModel<T> $$1, ResourceLocation $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, T $$6, float $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14, float $$15) {
        if (!$$6.isInvisible()) {
            $$0.copyPropertiesTo($$1);
            $$1.prepareMobModel($$6, $$7, $$8, $$12);
            $$1.setupAnim($$6, $$7, $$8, $$9, $$10, $$11);
            RenderLayer.renderColoredCutoutModel($$1, $$2, $$3, $$4, $$5, $$6, $$13, $$14, $$15);
        }
    }

    protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> $$0, ResourceLocation $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, T $$5, float $$6, float $$7, float $$8) {
        VertexConsumer $$9 = $$3.getBuffer(RenderType.entityCutoutNoCull($$1));
        $$0.renderToBuffer($$2, $$9, $$4, LivingEntityRenderer.getOverlayCoords($$5, 0.0f), $$6, $$7, $$8, 1.0f);
    }

    public M getParentModel() {
        return this.renderer.getModel();
    }

    protected ResourceLocation getTextureLocation(T $$0) {
        return this.renderer.getTextureLocation($$0);
    }

    public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}