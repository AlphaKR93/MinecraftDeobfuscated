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
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer
extends MobRenderer<Salmon, SalmonModel<Salmon>> {
    private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

    public SalmonRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SalmonModel($$0.bakeLayer(ModelLayers.SALMON)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Salmon $$0) {
        return SALMON_LOCATION;
    }

    @Override
    protected void setupRotations(Salmon $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        float $$5 = 1.0f;
        float $$6 = 1.0f;
        if (!$$0.isInWater()) {
            $$5 = 1.3f;
            $$6 = 1.7f;
        }
        float $$7 = $$5 * 4.3f * Mth.sin($$6 * 0.6f * $$2);
        $$1.mulPose(Axis.YP.rotationDegrees($$7));
        $$1.translate(0.0f, 0.0f, -0.4f);
        if (!$$0.isInWater()) {
            $$1.translate(0.2f, 0.1f, 0.0f);
            $$1.mulPose(Axis.ZP.rotationDegrees(90.0f));
        }
    }
}