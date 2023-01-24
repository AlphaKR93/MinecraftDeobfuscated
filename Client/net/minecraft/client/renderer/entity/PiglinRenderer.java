/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinRenderer
extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
    private static final Map<EntityType<?>, ResourceLocation> TEXTURES = ImmutableMap.of(EntityType.PIGLIN, (Object)new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, (Object)new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.PIGLIN_BRUTE, (Object)new ResourceLocation("textures/entity/piglin/piglin_brute.png"));
    private static final float PIGLIN_CUSTOM_HEAD_SCALE = 1.0019531f;

    public PiglinRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, ModelLayerLocation $$3, boolean $$4) {
        super($$0, PiglinRenderer.createModel($$0.getModelSet(), $$1, $$4), 0.5f, 1.0019531f, 1.0f, 1.0019531f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel($$0.bakeLayer($$2)), new HumanoidModel($$0.bakeLayer($$3)), $$0.getModelManager()));
    }

    private static PiglinModel<Mob> createModel(EntityModelSet $$0, ModelLayerLocation $$1, boolean $$2) {
        PiglinModel<Mob> $$3 = new PiglinModel<Mob>($$0.bakeLayer($$1));
        if ($$2) {
            $$3.rightEar.visible = false;
        }
        return $$3;
    }

    @Override
    public ResourceLocation getTextureLocation(Mob $$0) {
        ResourceLocation $$1 = (ResourceLocation)TEXTURES.get($$0.getType());
        if ($$1 == null) {
            throw new IllegalArgumentException("I don't know what texture to use for " + $$0.getType());
        }
        return $$1;
    }

    @Override
    protected boolean isShaking(Mob $$0) {
        return super.isShaking($$0) || $$0 instanceof AbstractPiglin && ((AbstractPiglin)$$0).isConverting();
    }
}