/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Tadpole;

public class TadpoleRenderer
extends MobRenderer<Tadpole, TadpoleModel<Tadpole>> {
    private static final ResourceLocation TADPOLE_TEXTURE = new ResourceLocation("textures/entity/tadpole/tadpole.png");

    public TadpoleRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new TadpoleModel($$0.bakeLayer(ModelLayers.TADPOLE)), 0.14f);
    }

    @Override
    public ResourceLocation getTextureLocation(Tadpole $$0) {
        return TADPOLE_TEXTURE;
    }
}