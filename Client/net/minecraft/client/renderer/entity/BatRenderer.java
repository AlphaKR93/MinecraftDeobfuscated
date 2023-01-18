/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer
extends MobRenderer<Bat, BatModel> {
    private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

    public BatRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BatModel($$0.bakeLayer(ModelLayers.BAT)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(Bat $$0) {
        return BAT_LOCATION;
    }

    @Override
    protected void scale(Bat $$0, PoseStack $$1, float $$2) {
        $$1.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void setupRotations(Bat $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        if ($$0.isResting()) {
            $$1.translate(0.0f, -0.1f, 0.0f);
        } else {
            $$1.translate(0.0f, Mth.cos($$2 * 0.3f) * 0.1f, 0.0f);
        }
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
    }
}