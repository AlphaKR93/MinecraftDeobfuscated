/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.Mob;

public abstract class HumanoidMobRenderer<T extends Mob, M extends HumanoidModel<T>>
extends MobRenderer<T, M> {
    public HumanoidMobRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        this($$0, $$1, $$2, 1.0f, 1.0f, 1.0f);
    }

    public HumanoidMobRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2, float $$3, float $$4, float $$5) {
        super($$0, $$1, $$2);
        this.addLayer(new CustomHeadLayer(this, $$0.getModelSet(), $$3, $$4, $$5, $$0.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer(this, $$0.getModelSet()));
        this.addLayer(new ItemInHandLayer(this, $$0.getItemInHandRenderer()));
    }
}