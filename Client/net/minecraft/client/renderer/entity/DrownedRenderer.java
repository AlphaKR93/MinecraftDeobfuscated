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
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedRenderer
extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
    private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

    public DrownedRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)));
        this.addLayer(new DrownedOuterLayer<Drowned>(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie $$0) {
        return DROWNED_LOCATION;
    }

    @Override
    protected void setupRotations(Drowned $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        float $$5 = $$0.getSwimAmount($$4);
        if ($$5 > 0.0f) {
            $$1.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$5, $$0.getXRot(), -10.0f - $$0.getXRot())));
        }
    }
}