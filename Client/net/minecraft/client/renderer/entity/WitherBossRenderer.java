/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer
extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

    public WitherBossRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WitherBossModel($$0.bakeLayer(ModelLayers.WITHER)), 1.0f);
        this.addLayer(new WitherArmorLayer(this, $$0.getModelSet()));
    }

    @Override
    protected int getBlockLightLevel(WitherBoss $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(WitherBoss $$0) {
        int $$1 = $$0.getInvulnerableTicks();
        if ($$1 <= 0 || $$1 <= 80 && $$1 / 5 % 2 == 1) {
            return WITHER_LOCATION;
        }
        return WITHER_INVULNERABLE_LOCATION;
    }

    @Override
    protected void scale(WitherBoss $$0, PoseStack $$1, float $$2) {
        float $$3 = 2.0f;
        int $$4 = $$0.getInvulnerableTicks();
        if ($$4 > 0) {
            $$3 -= ((float)$$4 - $$2) / 220.0f * 0.5f;
        }
        $$1.scale($$3, $$3, $$3);
    }
}