/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer
extends MobRenderer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
    private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    private static final ResourceLocation WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

    public WolfRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WolfModel($$0.bakeLayer(ModelLayers.WOLF)), 0.5f);
        this.addLayer(new WolfCollarLayer(this));
    }

    @Override
    protected float getBob(Wolf $$0, float $$1) {
        return $$0.getTailAngle();
    }

    @Override
    public void render(Wolf $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if ($$0.isWet()) {
            float $$6 = $$0.getWetShade($$2);
            ((WolfModel)this.model).setColor($$6, $$6, $$6);
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        if ($$0.isWet()) {
            ((WolfModel)this.model).setColor(1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf $$0) {
        if ($$0.isTame()) {
            return WOLF_TAME_LOCATION;
        }
        if ($$0.isAngry()) {
            return WOLF_ANGRY_LOCATION;
        }
        return WOLF_LOCATION;
    }
}