/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer
extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
    private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))));
        this.addLayer(new VillagerProfessionLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>(this, $$0.getResourceManager(), "zombie_villager"));
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieVillager $$0) {
        return ZOMBIE_VILLAGER_LOCATION;
    }

    @Override
    protected boolean isShaking(ZombieVillager $$0) {
        return super.isShaking($$0) || $$0.isConverting();
    }
}