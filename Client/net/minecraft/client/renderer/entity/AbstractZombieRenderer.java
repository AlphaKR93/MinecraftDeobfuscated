/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>>
extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

    protected AbstractZombieRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2, M $$3) {
        super($$0, $$1, 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, $$2, $$3));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie $$0) {
        return ZOMBIE_LOCATION;
    }

    @Override
    protected boolean isShaking(T $$0) {
        return super.isShaking($$0) || ((Zombie)$$0).isUnderWaterConverting();
    }
}