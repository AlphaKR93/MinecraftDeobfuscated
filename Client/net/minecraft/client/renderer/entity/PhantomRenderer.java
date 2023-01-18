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
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomRenderer
extends MobRenderer<Phantom, PhantomModel<Phantom>> {
    private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

    public PhantomRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PhantomModel($$0.bakeLayer(ModelLayers.PHANTOM)), 0.75f);
        this.addLayer(new PhantomEyesLayer<Phantom>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Phantom $$0) {
        return PHANTOM_LOCATION;
    }

    @Override
    protected void scale(Phantom $$0, PoseStack $$1, float $$2) {
        int $$3 = $$0.getPhantomSize();
        float $$4 = 1.0f + 0.15f * (float)$$3;
        $$1.scale($$4, $$4, $$4);
        $$1.translate(0.0f, 1.3125f, 0.1875f);
    }

    @Override
    protected void setupRotations(Phantom $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        $$1.mulPose(Axis.XP.rotationDegrees($$0.getXRot()));
    }
}