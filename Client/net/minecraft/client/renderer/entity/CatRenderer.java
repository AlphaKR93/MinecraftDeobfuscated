/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class CatRenderer
extends MobRenderer<Cat, CatModel<Cat>> {
    public CatRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CatModel($$0.bakeLayer(ModelLayers.CAT)), 0.4f);
        this.addLayer(new CatCollarLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Cat $$0) {
        return $$0.getResourceLocation();
    }

    @Override
    protected void scale(Cat $$0, PoseStack $$1, float $$2) {
        super.scale($$0, $$1, $$2);
        $$1.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void setupRotations(Cat $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        float $$5 = $$0.getLieDownAmount($$4);
        if ($$5 > 0.0f) {
            $$1.translate(0.4f * $$5, 0.15f * $$5, 0.1f * $$5);
            $$1.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp($$5, 0.0f, 90.0f)));
            BlockPos $$6 = $$0.blockPosition();
            List $$7 = $$0.level.getEntitiesOfClass(Player.class, new AABB($$6).inflate(2.0, 2.0, 2.0));
            for (Player $$8 : $$7) {
                if (!$$8.isSleeping()) continue;
                $$1.translate(0.15f * $$5, 0.0f, 0.0f);
                break;
            }
        }
    }
}