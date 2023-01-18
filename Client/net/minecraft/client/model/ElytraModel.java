/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ElytraModel<T extends LivingEntity>
extends AgeableListModel<T> {
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ElytraModel(ModelPart $$0) {
        this.leftWing = $$0.getChild("left_wing");
        this.rightWing = $$0.getChild("right_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(1.0f);
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(22, 0).addBox(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, $$2), PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, $$2), PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.leftWing, (Object)this.rightWing);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = 0.2617994f;
        float $$7 = -0.2617994f;
        float $$8 = 0.0f;
        float $$9 = 0.0f;
        if (((LivingEntity)$$0).isFallFlying()) {
            float $$10 = 1.0f;
            Vec3 $$11 = ((Entity)$$0).getDeltaMovement();
            if ($$11.y < 0.0) {
                Vec3 $$12 = $$11.normalize();
                $$10 = 1.0f - (float)Math.pow((double)(-$$12.y), (double)1.5);
            }
            $$6 = $$10 * 0.34906584f + (1.0f - $$10) * $$6;
            $$7 = $$10 * -1.5707964f + (1.0f - $$10) * $$7;
        } else if (((Entity)$$0).isCrouching()) {
            $$6 = 0.6981317f;
            $$7 = -0.7853982f;
            $$8 = 3.0f;
            $$9 = 0.08726646f;
        }
        this.leftWing.y = $$8;
        if ($$0 instanceof AbstractClientPlayer) {
            AbstractClientPlayer $$13 = (AbstractClientPlayer)$$0;
            $$13.elytraRotX += ($$6 - $$13.elytraRotX) * 0.1f;
            $$13.elytraRotY += ($$9 - $$13.elytraRotY) * 0.1f;
            $$13.elytraRotZ += ($$7 - $$13.elytraRotZ) * 0.1f;
            this.leftWing.xRot = $$13.elytraRotX;
            this.leftWing.yRot = $$13.elytraRotY;
            this.leftWing.zRot = $$13.elytraRotZ;
        } else {
            this.leftWing.xRot = $$6;
            this.leftWing.zRot = $$7;
            this.leftWing.yRot = $$9;
        }
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}