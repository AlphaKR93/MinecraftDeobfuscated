/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class HuskRenderer
extends ZombieRenderer {
    private static final ResourceLocation HUSK_LOCATION = new ResourceLocation("textures/entity/zombie/husk.png");

    public HuskRenderer(EntityRendererProvider.Context $$0) {
        super($$0, ModelLayers.HUSK, ModelLayers.HUSK_INNER_ARMOR, ModelLayers.HUSK_OUTER_ARMOR);
    }

    @Override
    protected void scale(Zombie $$0, PoseStack $$1, float $$2) {
        float $$3 = 1.0625f;
        $$1.scale(1.0625f, 1.0625f, 1.0625f);
        super.scale($$0, $$1, $$2);
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie $$0) {
        return HUSK_LOCATION;
    }
}