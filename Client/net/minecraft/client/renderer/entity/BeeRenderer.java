/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public class BeeRenderer
extends MobRenderer<Bee, BeeModel<Bee>> {
    private static final ResourceLocation ANGRY_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry.png");
    private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry_nectar.png");
    private static final ResourceLocation BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee.png");
    private static final ResourceLocation NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_nectar.png");

    public BeeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BeeModel($$0.bakeLayer(ModelLayers.BEE)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Bee $$0) {
        if ($$0.isAngry()) {
            if ($$0.hasNectar()) {
                return ANGRY_NECTAR_BEE_TEXTURE;
            }
            return ANGRY_BEE_TEXTURE;
        }
        if ($$0.hasNectar()) {
            return NECTAR_BEE_TEXTURE;
        }
        return BEE_TEXTURE;
    }
}