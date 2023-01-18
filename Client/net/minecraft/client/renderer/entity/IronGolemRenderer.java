/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer
extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public IronGolemRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new IronGolemModel($$0.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7f);
        this.addLayer(new IronGolemCrackinessLayer(this));
        this.addLayer(new IronGolemFlowerLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem $$0) {
        return GOLEM_LOCATION;
    }

    @Override
    protected void setupRotations(IronGolem $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        if ((double)$$0.animationSpeed < 0.01) {
            return;
        }
        float $$5 = 13.0f;
        float $$6 = $$0.animationPosition - $$0.animationSpeed * (1.0f - $$4) + 6.0f;
        float $$7 = (Math.abs((float)($$6 % 13.0f - 6.5f)) - 3.25f) / 3.25f;
        $$1.mulPose(Axis.ZP.rotationDegrees(6.5f * $$7));
    }
}