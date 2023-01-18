/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;

public class WitchRenderer
extends MobRenderer<Witch, WitchModel<Witch>> {
    private static final ResourceLocation WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");

    public WitchRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WitchModel($$0.bakeLayer(ModelLayers.WITCH)), 0.5f);
        this.addLayer(new WitchItemLayer<Witch>(this, $$0.getItemInHandRenderer()));
    }

    @Override
    public void render(Witch $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        ((WitchModel)this.model).setHoldingItem(!$$0.getMainHandItem().isEmpty());
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(Witch $$0) {
        return WITCH_LOCATION;
    }

    @Override
    protected void scale(Witch $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.9375f;
        $$1.scale(0.9375f, 0.9375f, 0.9375f);
    }
}