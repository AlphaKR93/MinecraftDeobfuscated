/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderer
extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");

    public ArmorStandRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ArmorStandModel($$0.bakeLayer(ModelLayers.ARMOR_STAND)), 0.0f);
        this.addLayer(new HumanoidArmorLayer<ArmorStand, ArmorStandArmorModel, ArmorStandArmorModel>(this, new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)), $$0.getModelManager()));
        this.addLayer(new ItemInHandLayer<ArmorStand, ArmorStandArmorModel>(this, $$0.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<ArmorStand, ArmorStandArmorModel>(this, $$0.getModelSet()));
        this.addLayer(new CustomHeadLayer<ArmorStand, ArmorStandArmorModel>(this, $$0.getModelSet(), $$0.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorStand $$0) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    protected void setupRotations(ArmorStand $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        $$1.mulPose(Axis.YP.rotationDegrees(180.0f - $$3));
        float $$5 = (float)($$0.level.getGameTime() - $$0.lastHit) + $$4;
        if ($$5 < 5.0f) {
            $$1.mulPose(Axis.YP.rotationDegrees(Mth.sin($$5 / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean shouldShowName(ArmorStand $$0) {
        float $$2;
        double $$1 = this.entityRenderDispatcher.distanceToSqr($$0);
        float f = $$2 = $$0.isCrouching() ? 32.0f : 64.0f;
        if ($$1 >= (double)($$2 * $$2)) {
            return false;
        }
        return $$0.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderType getRenderType(ArmorStand $$0, boolean $$1, boolean $$2, boolean $$3) {
        if (!$$0.isMarker()) {
            return super.getRenderType($$0, $$1, $$2, $$3);
        }
        ResourceLocation $$4 = this.getTextureLocation($$0);
        if ($$2) {
            return RenderType.entityTranslucent($$4, false);
        }
        if ($$1) {
            return RenderType.entityCutoutNoCull($$4, false);
        }
        return null;
    }
}