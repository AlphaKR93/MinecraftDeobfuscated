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
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;

public class CodRenderer
extends MobRenderer<Cod, CodModel<Cod>> {
    private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

    public CodRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CodModel($$0.bakeLayer(ModelLayers.COD)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Cod $$0) {
        return COD_LOCATION;
    }

    @Override
    protected void setupRotations(Cod $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        float $$5 = 4.3f * Mth.sin(0.6f * $$2);
        $$1.mulPose(Axis.YP.rotationDegrees($$5));
        if (!$$0.isInWater()) {
            $$1.translate(0.1f, 0.1f, -0.1f);
            $$1.mulPose(Axis.ZP.rotationDegrees(90.0f));
        }
    }
}