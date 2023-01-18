/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer
extends MobRenderer<Slime, SlimeModel<Slime>> {
    private static final ResourceLocation SLIME_LOCATION = new ResourceLocation("textures/entity/slime/slime.png");

    public SlimeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SlimeModel($$0.bakeLayer(ModelLayers.SLIME)), 0.25f);
        this.addLayer(new SlimeOuterLayer<Slime>(this, $$0.getModelSet()));
    }

    @Override
    public void render(Slime $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        this.shadowRadius = 0.25f * (float)$$0.getSize();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void scale(Slime $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.999f;
        $$1.scale(0.999f, 0.999f, 0.999f);
        $$1.translate(0.0f, 0.001f, 0.0f);
        float $$4 = $$0.getSize();
        float $$5 = Mth.lerp($$2, $$0.oSquish, $$0.squish) / ($$4 * 0.5f + 1.0f);
        float $$6 = 1.0f / ($$5 + 1.0f);
        $$1.scale($$6 * $$4, 1.0f / $$6 * $$4, $$6 * $$4);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime $$0) {
        return SLIME_LOCATION;
    }
}