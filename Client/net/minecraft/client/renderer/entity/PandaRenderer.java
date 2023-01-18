/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer
extends MobRenderer<Panda, PandaModel<Panda>> {
    private static final Map<Panda.Gene, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), $$0 -> {
        $$0.put((Enum)Panda.Gene.NORMAL, (Object)new ResourceLocation("textures/entity/panda/panda.png"));
        $$0.put((Enum)Panda.Gene.LAZY, (Object)new ResourceLocation("textures/entity/panda/lazy_panda.png"));
        $$0.put((Enum)Panda.Gene.WORRIED, (Object)new ResourceLocation("textures/entity/panda/worried_panda.png"));
        $$0.put((Enum)Panda.Gene.PLAYFUL, (Object)new ResourceLocation("textures/entity/panda/playful_panda.png"));
        $$0.put((Enum)Panda.Gene.BROWN, (Object)new ResourceLocation("textures/entity/panda/brown_panda.png"));
        $$0.put((Enum)Panda.Gene.WEAK, (Object)new ResourceLocation("textures/entity/panda/weak_panda.png"));
        $$0.put((Enum)Panda.Gene.AGGRESSIVE, (Object)new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PandaModel($$0.bakeLayer(ModelLayers.PANDA)), 0.9f);
        this.addLayer(new PandaHoldsItemLayer(this, $$0.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Panda $$0) {
        return (ResourceLocation)TEXTURES.getOrDefault((Object)$$0.getVariant(), (Object)((ResourceLocation)TEXTURES.get((Object)Panda.Gene.NORMAL)));
    }

    @Override
    protected void setupRotations(Panda $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        float $$26;
        float $$24;
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        if ($$0.rollCounter > 0) {
            float $$8;
            int $$5 = $$0.rollCounter;
            int $$6 = $$5 + 1;
            float $$7 = 7.0f;
            float f = $$8 = $$0.isBaby() ? 0.3f : 0.8f;
            if ($$5 < 8) {
                float $$9 = (float)(90 * $$5) / 7.0f;
                float $$10 = (float)(90 * $$6) / 7.0f;
                float $$11 = this.getAngle($$9, $$10, $$6, $$4, 8.0f);
                $$1.translate(0.0f, ($$8 + 0.2f) * ($$11 / 90.0f), 0.0f);
                $$1.mulPose(Axis.XP.rotationDegrees(-$$11));
            } else if ($$5 < 16) {
                float $$12 = ((float)$$5 - 8.0f) / 7.0f;
                float $$13 = 90.0f + 90.0f * $$12;
                float $$14 = 90.0f + 90.0f * ((float)$$6 - 8.0f) / 7.0f;
                float $$15 = this.getAngle($$13, $$14, $$6, $$4, 16.0f);
                $$1.translate(0.0f, $$8 + 0.2f + ($$8 - 0.2f) * ($$15 - 90.0f) / 90.0f, 0.0f);
                $$1.mulPose(Axis.XP.rotationDegrees(-$$15));
            } else if ((float)$$5 < 24.0f) {
                float $$16 = ((float)$$5 - 16.0f) / 7.0f;
                float $$17 = 180.0f + 90.0f * $$16;
                float $$18 = 180.0f + 90.0f * ((float)$$6 - 16.0f) / 7.0f;
                float $$19 = this.getAngle($$17, $$18, $$6, $$4, 24.0f);
                $$1.translate(0.0f, $$8 + $$8 * (270.0f - $$19) / 90.0f, 0.0f);
                $$1.mulPose(Axis.XP.rotationDegrees(-$$19));
            } else if ($$5 < 32) {
                float $$20 = ((float)$$5 - 24.0f) / 7.0f;
                float $$21 = 270.0f + 90.0f * $$20;
                float $$22 = 270.0f + 90.0f * ((float)$$6 - 24.0f) / 7.0f;
                float $$23 = this.getAngle($$21, $$22, $$6, $$4, 32.0f);
                $$1.translate(0.0f, $$8 * ((360.0f - $$23) / 90.0f), 0.0f);
                $$1.mulPose(Axis.XP.rotationDegrees(-$$23));
            }
        }
        if (($$24 = $$0.getSitAmount($$4)) > 0.0f) {
            $$1.translate(0.0f, 0.8f * $$24, 0.0f);
            $$1.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$24, $$0.getXRot(), $$0.getXRot() + 90.0f)));
            $$1.translate(0.0f, -1.0f * $$24, 0.0f);
            if ($$0.isScared()) {
                float $$25 = (float)(Math.cos((double)((double)$$0.tickCount * 1.25)) * Math.PI * (double)0.05f);
                $$1.mulPose(Axis.YP.rotationDegrees($$25));
                if ($$0.isBaby()) {
                    $$1.translate(0.0f, 0.8f, 0.55f);
                }
            }
        }
        if (($$26 = $$0.getLieOnBackAmount($$4)) > 0.0f) {
            float $$27 = $$0.isBaby() ? 0.5f : 1.3f;
            $$1.translate(0.0f, $$27 * $$26, 0.0f);
            $$1.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$26, $$0.getXRot(), $$0.getXRot() + 180.0f)));
        }
    }

    private float getAngle(float $$0, float $$1, int $$2, float $$3, float $$4) {
        if ((float)$$2 < $$4) {
            return Mth.lerp($$3, $$0, $$1);
        }
        return $$0;
    }
}