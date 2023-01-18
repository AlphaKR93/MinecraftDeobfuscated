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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SlimeOuterLayer<T extends LivingEntity>
extends RenderLayer<T, SlimeModel<T>> {
    private final EntityModel<T> model;

    public SlimeOuterLayer(RenderLayerParent<T, SlimeModel<T>> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new SlimeModel($$1.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        VertexConsumer $$13;
        boolean $$11;
        Minecraft $$10 = Minecraft.getInstance();
        boolean bl = $$11 = $$10.shouldEntityAppearGlowing((Entity)$$3) && ((Entity)$$3).isInvisible();
        if (((Entity)$$3).isInvisible() && !$$11) {
            return;
        }
        if ($$11) {
            VertexConsumer $$12 = $$1.getBuffer(RenderType.outline(this.getTextureLocation($$3)));
        } else {
            $$13 = $$1.getBuffer(RenderType.entityTranslucent(this.getTextureLocation($$3)));
        }
        ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.prepareMobModel($$3, $$4, $$5, $$6);
        this.model.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        this.model.renderToBuffer($$0, $$13, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
    }
}