/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, M extends HorseModel<T>>
extends MobRenderer<T, M> {
    private final float scale;

    public AbstractHorseRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        super($$0, $$1, 0.75f);
        this.scale = $$2;
    }

    @Override
    protected void scale(T $$0, PoseStack $$1, float $$2) {
        $$1.scale(this.scale, this.scale, this.scale);
        super.scale($$0, $$1, $$2);
    }
}