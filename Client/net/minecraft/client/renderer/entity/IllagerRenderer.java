/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer<T extends AbstractIllager>
extends MobRenderer<T, IllagerModel<T>> {
    protected IllagerRenderer(EntityRendererProvider.Context $$0, IllagerModel<T> $$1, float $$2) {
        super($$0, $$1, $$2);
        this.addLayer(new CustomHeadLayer(this, $$0.getModelSet(), $$0.getItemInHandRenderer()));
    }

    @Override
    protected void scale(T $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.9375f;
        $$1.scale(0.9375f, 0.9375f, 0.9375f);
    }
}